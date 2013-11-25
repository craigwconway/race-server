package com.bibsmobile.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.thingmagic.*;

public class DummyTimer implements Timer {
		private String readerURI;
		int status = 0;
		List<Integer> done = new ArrayList<Integer>();
		HashMap <Integer,Long> times = new HashMap <Integer,Long>();
		public void init(){ status = 1;}
		public void cleanup(){ status = 0;}
		public int getStatus(){ return status; }
		public void connect(){ status = 1;}
		public String getReaderURI(){ return "dummy";}
		public void disconnect(){status = 0;}
		public void start(){status = 2;}
		public void stop(){status = 1;}
		public HashMap <Integer,Long> getTimes(){
			java.util.Random r = new java.util.Random();
			times.clear();
			Integer rand = r.nextInt(20);
			if(!done.contains(rand)){
				times.put(rand, new Date().getTime());
				done.add(rand);
			}
			return times;
		}
		public void writeTag(int num){ status = 3; }
		public void setReaderURI(String readerURI) {
			this.readerURI = readerURI;
		}
		@Override
		public long getTime() {
			return new Date().getTime();
		}

		@Override
		public void purge(){ done.clear();}
}