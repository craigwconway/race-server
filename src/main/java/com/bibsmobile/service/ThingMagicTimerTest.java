package com.bibsmobile.service;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class ThingMagicTimerTest {

	@Test
	public void test() {
		//fail("Not yet implemented");
		Timer galen = new ThingMagicTimer();
		galen.setReaderURI("tmr://10.0.1.7");
		galen.connect();
		System.out.println("Status: " + galen.getStatus());
		long time;
		try {
			//time = galen.getTime();
			System.out.println(galen.getTime());
			//System.out.println(new Date().getTime());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert(true);
	}

}
