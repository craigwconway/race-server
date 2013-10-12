/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibsmobile.service;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mythinkhouse
 */
public class RaceTimerTest {

    private static Timer rt = new ThingMagicTimer();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        System.out.println("Race Timer initial state: " + rt.getStatus());
        System.out.println("Conecting...");
        rt.connect();
        rt.start();
        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                rt.stop();
                rt.disconnect();
                System.exit(1);
            }
        }));
        // listen for bibs
        while(true){
	        try {
				System.out.println("Searching...");
				Thread.sleep(1000);
                rt.getStatus();
				Map <Integer,Long> times = rt.getTimes();
				for(Integer key:times.keySet()){
					System.out.println(key+":"+times.get(key));
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}
