package com.bibsmobile.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.TimerConfig;

public class AbstractTimerTest {
	
	class TestTimer extends AbstractTimer{
		@Override
		public void setTimerConfig(TimerConfig timerConfig) { }
		@Override
		public int getStatus() { return 0; }
		@Override
		public long getDateTime() { return 0; }
		@Override
		public void connect() throws Exception { }
		@Override
		public void disconnect() { }
		@Override
		public void startReader() { }
		@Override
		public void stopReader() { }
		@Override
		public void write(long bib) throws Exception { }
		@Override
		public void clearTimesByEvent(long eventId) { }
		@Override
		public void emptyBuffer() {
			// TODO Auto-generated method stub
			
		} 
	}

	@Test
	public void testCalculateSplitTime_position_1() {
		// given
		AbstractTimer timer = new TestTimer();
		long bibtime = System.currentTimeMillis();
		Event event = new Event();
		RaceResult result = new RaceResult();
		result.setEvent(event);
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setPosition(1);
		// when
		result = timer.calculateOfficialTime(result, bibtime, timerConfig);
		// then
		assertTrue(result.getTimestart() == 0);
		assertTrue(result.getTimesplit() == null);
		assertTrue(result.getTimeofficial() == bibtime);
	}

	@Test
	public void testCalculateSplitTime_null_split_position_2() {
		// given
		AbstractTimer timer = new TestTimer();
		long bibtime = System.currentTimeMillis();
		Event event = new Event();
		RaceResult result = new RaceResult();
		result.setEvent(event);
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setPosition(2);
		// when
		result = timer.calculateOfficialTime(result, bibtime, timerConfig);
		// then
		assertTrue(result.getTimestart() == 0);
		assertTrue(result.getTimesplit().split(",")[timerConfig.getPosition()-2].equals(bibtime+""));
		assertTrue(result.getTimesplit().split(",").length == timerConfig.getPosition() - 1);
		assertTrue(result.getTimeofficial() == 0);
	}

	@Test
	public void testCalculateSplitTime_null_split_position_2_exists() {
		// given
		AbstractTimer timer = new TestTimer();
		long bibtime = System.currentTimeMillis();
		Event event = new Event();
		RaceResult result = new RaceResult();
		result.setEvent(event);
		int position = 2;
		String splits = "123,456";
		result.setTimesplit(splits);
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setPosition(position);
		// when
		result = timer.calculateOfficialTime(result, bibtime, timerConfig);
		// then
		assertTrue(result.getTimestart() == 0);
		assertFalse(result.getTimesplit().equals(splits));
		assertTrue(result.getTimesplit().split(",")[position-2].equals(bibtime+""));
		assertTrue(result.getTimesplit().split(",").length == splits.split(",").length);
		assertTrue(result.getTimeofficial() == 0);
	}



	@Test
	public void testTimerRead() {
		// given
		AbstractTimer timer = new TestTimer();
		Random random = new Random();
		TimerConfig config = new TimerConfig();
		for(int i=0;i<100;i++){
			int bib = random.nextInt(5000);
			int position = random.nextInt(3)+1;
			config.setPosition(position);
			timer.logTimerRead(String.valueOf(bib),String.valueOf(position));
		}
		// when
		String report = timer.createReport();
		// then
		assertTrue(report.contains("Unique bibs:"));
		assertTrue(report.contains("Total reads:"));
		
	}
}
