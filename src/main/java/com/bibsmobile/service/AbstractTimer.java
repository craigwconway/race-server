package com.bibsmobile.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.TimerConfig;

public abstract class AbstractTimer implements Timer {

    private final Map<String, Integer> bibsByReader = new HashMap<String, Integer>(); // position,
                                                                                      // count
    private final List<String> bibCache = new ArrayList<String>();
    private final Set<String> uniqueBibs = new TreeSet<String>();
    private final Map<String, Long> bibTimes = new HashMap<String, Long>();

    @Override
    public void logTime(final int bibnum, long bibtime, final TimerConfig timerConfig) {
        bibtime = bibtime / 1000; // microseconds
        final String slog = Thread.currentThread().getName() + " " + getClass().getName();
        final String cacheKey = bibnum + "-" + timerConfig.getPosition();
        System.out.println(slog + " logging '" + bibnum + "' @ " + bibtime + ", position " + timerConfig.getPosition());
        logUnregisteredBib(String.valueOf(bibnum), timerConfig.getUrl());// test
                                                                         // logging
        if (!this.bibTimes.containsKey(cacheKey))
            this.bibTimes.put(cacheKey, bibtime);

        if (bibtime < (this.bibTimes.get(cacheKey) + (timerConfig.getReadTimeout() * 1000))) {
            List<Event> events = Event.findEventsByRunning();
            System.out.println(slog + " running events " + events.size());
            for (Event event : events) {
                try {
                    // if event started
                    if (null == event.getGunTime())
                        continue;

                    System.out.println(slog + " EVENT: " + event.getId() + " start:" + event.getTimeStart() + " gun:" + event.getGunTimeStart());
                    RaceResult result = RaceResult.findRaceResultsByEventAndBibEquals(event, bibnum + "").getSingleResult();
                    System.out.println(slog + " RUNNER: " + result.getId() + " start:" + result.getTimestart() + " finish:" + result.getTimeofficial());

                    // starting line
                    if (timerConfig.getPosition() == 0) {
                        long cTimestart = 0;
                        if (result.getTimestart() > 0) {
                            cTimestart = result.getTimestart();
                            if (bibtime > cTimestart + (timerConfig.getReadTimeout() * 1000)) {
                                System.out.println(slog + " START EXISTS ");
                                continue; // don't update
                            }
                        }
                        result.setTimestart(bibtime);

                        // finish line
                    } else {
                        long cTimeofficial = 0;
                        if (result.getTimeofficial() > 0) {
                            cTimeofficial = result.getTimeofficial();
                            if (bibtime > cTimeofficial + (timerConfig.getReadTimeout() * 1000)) {
                                System.out.println(slog + " FINISH EXISTS ");
                                continue; // don't update
                            }
                        }
                        // bib vs chip start
                        long starttime = 0l;
                        if (result.getTimestart() > 0) {
                            starttime = Long.valueOf(result.getTimestart());
                        } else {
                            starttime = event.getGunTime().getTime();
                            result.setTimestart(starttime);
                        }
                        final String strTime = RaceResult.toHumanTime(starttime, bibtime);
                        result.setTimeofficial(bibtime);
                        result.setTimeofficialdisplay(strTime);
                    }

                    System.out.println(slog + " UPDATE " + result.getId() + " start:" + result.getTimestart() + " finish:" + result.getTimeofficial());

                    result.merge();

                } catch (Exception e) {
                    System.out.println(slog + " ERROR " + e.getMessage());
                }
            }
        } else {
            System.out.println(slog + " TIMEOUT bib " + bibnum + " @ reader " + timerConfig.getPosition());
        }
    }

    public synchronized void logUnregisteredBib(String bib, String reader) {
        System.out.println("UNREGISTERED '" + bib + "' @ " + reader);
        this.uniqueBibs.add(bib);
        if (!this.bibCache.contains(bib + "-" + reader)) {
            this.bibCache.add(bib + "-" + reader);
            if (this.bibsByReader.containsKey(reader)) {
                this.bibsByReader.put(reader, this.bibsByReader.get(reader) + 1);
            } else {
                this.bibsByReader.put(reader, 1);
            }
        }
    }

    @Override
    public String createReport() {
        // create file
        StringBuilder sb = new StringBuilder();
        for (String reader : this.bibsByReader.keySet()) {
            sb.append("Reader " + reader + ": " + this.bibsByReader.get(reader) + "          \t<br/>\n");
        }
        sb.append("Unique bibs: " + this.uniqueBibs.size() + "          \t<br/>\n");
        sb.append("Total reads: " + this.bibCache.size());

        // write file
        // Writer writer = null;
        // try {
        // writer = new BufferedWriter(new OutputStreamWriter(
        // new FileOutputStream("/data/bib-report.txt"), "utf-8"));
        // writer.write(sb.toString());
        // } catch (IOException ex) {
        // System.out.println("ERROR WRITING FILE");
        // } finally {
        // try {writer.close();} catch (Exception ex) {}
        // }

        return sb.toString();
    }

    @Override
    public void clearReport() {
        // clear out cache
        this.bibTimes.clear();
        this.bibCache.clear();
        this.bibsByReader.clear();
        this.uniqueBibs.clear();
    }

    // public static void main(String[] args){
    // AbstractTimer timer = new AbstractTimer();
    // Random random = new Random();
    // TimerConfig config = new TimerConfig();
    // for(int i=0;i<100;i++){
    // int bib = random.nextInt(5000);
    // int position = random.nextInt(3)+1;
    // config.setPosition(position);
    // timer.logUnregisteredBib(String.valueOf(bib),position);
    // }
    // timer.creatReport();
    // }

}
