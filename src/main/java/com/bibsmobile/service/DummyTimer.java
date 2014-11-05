package com.bibsmobile.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.bibsmobile.model.TimerConfig;

public class DummyTimer extends AbstractTimer implements Timer, Runnable {

    private TimerConfig timerConfig;
    private int status;
    private Thread thread;
    private final String log = this.getClass().getName() + " " + Thread.currentThread().getName();
    private final Map<Integer, Long> bibTimes = new HashMap<>();

    @Override
    public long getDateTime() {
        return new Date().getTime();
    }

    @Override
    public void startReader() {
        System.out.println(this.log + " Start Reading...");
        this.thread = new Thread(this);
        this.thread.start();
        this.status = 2;
        System.out.println(this.log + " Started.");
    }

    @Override
    public void stopReader() {
        System.out.println(this.log + " Stop Reading...");
        this.status = 1;
        this.notify();
        System.out.println(this.log + " Stopped.");
    }

    @Override
    public void write(long bib) throws Exception {
        this.status = 3;
        System.out.println(this.log + " Started Writing...");
    }

    @Override
    public void connect() {
        System.out.println(this.log + " Connecting...");
        this.status = 1;
        System.out.println(this.log + " Connected.");
    }

    @Override
    public void disconnect() {
        System.out.println(this.log + " Disconnecting...");
        this.status = 0;
        try {
            this.notify();
            this.thread = null;
        } catch (Exception e) {
            System.out.println(this.log + " Error Discconnecting.");
        }
        System.out.println(this.log + " Discconnected.");

    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public void setTimerConfig(TimerConfig timerConfig) {
        this.timerConfig = timerConfig;
    }

    @Override
    public void run() {
        System.out.println(this.log + " run...");
        this.bibTimes.clear();
        while (this.status == 2) {
            int bibnum = new Random().nextInt(30);
            long bibtime = new Date().getTime();

            // yay
            this.logTime(bibnum, bibtime, this.timerConfig);

            // wait or conceed
            try {
                Thread.sleep(1000);
                System.out.println(this.log + " Again thread " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
            }
        }
    }
}