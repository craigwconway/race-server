package com.bibsmobile.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.Test;

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
	}

	@Test
	public void testCalculateSplitTime_position_1() {
		// given
		AbstractTimer timer = new TestTimer();
		long bibtime = System.currentTimeMillis();
		Event event = new Event();
		RaceResult result = new RaceResult();
		result.setEvent(event);
		int position = 1;
		// when
		result = timer.calculateSplitTime(result, bibtime, position);
		// then
		assertTrue(result.getTimestart() == 0);
		assertTrue(result.getTimesplit().equals(bibtime+""));
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
		int position = 2;
		// when
		result = timer.calculateSplitTime(result, bibtime, position);
		// then
		assertTrue(result.getTimestart() == 0);
		assertTrue(result.getTimesplit().split(",")[position-1].equals(bibtime+""));
		assertTrue(result.getTimesplit().split(",").length == position);
		assertTrue(result.getTimeofficial() == bibtime);
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
		// when
		result = timer.calculateSplitTime(result, bibtime, position);
		// then
		assertTrue(result.getTimestart() == 0);
		assertFalse(result.getTimesplit().equals(splits));
		assertTrue(result.getTimesplit().split(",")[position-1].equals(bibtime+""));
		assertTrue(result.getTimesplit().split(",").length == position);
		assertTrue(result.getTimeofficial() == bibtime);
	}

}
