package com.bibsmobile.model;
import java.util.HashMap;
import java.util.Map;

import com.thingmagic.*;

public class RaceTimer {
		private int status;
		private Reader r;
		private String ReaderURI;
		private HashMap <Integer, Long> times; // Bibnumber vs ms after epoch
		private HashMap <Integer, Long> startTimes;
		private ReadListener listener;
		
		public RaceTimer() {
			status = 0;
			r = null;
			ReaderURI = "192.168.1.100";
			times = new HashMap <Integer, Long> ();
			startTimes = new HashMap <Integer, Long>();
		}
		public int getStatus() {
			return status;
		}
		public void connect() {
			try {
				r = Reader.create(ReaderURI);
				status = 1;
			}
			catch( ReaderException ex) {
				System.out.println(ex);
			}
		}
		public void disconnect() {
			if(status != 0)
				r.destroy();
			status = 0;
		}

		public void start(){
			if(status == 1) {
				ReadListener listener = new bibListener();
				r.addReadListener(listener);
				r.startReading();
				status = 2;
			}
		}
		public void stop(){
			if (status == 2) {
				r.stopReading();
				r.removeReadListener(listener);
				status = 1;
			}
		}
		
		public HashMap <Integer,Long> getTimes() {
			return times;
		}
		
		public void writeTag(int num) throws ReaderException{
			status = 3; //mark low power write mode
			byte [] bibdata = new byte []{
					(byte) (num >>> 24),
					(byte) (num >>> 16),
					(byte) (num >>> 8),
					(byte) (num)
			};
			TagFilter target = null;
			TagData bibinf = new TagData(bibdata);
			r.writeTag(target, bibinf);
		}
		
		class bibListener implements ReadListener
		{
			public void tagRead(Reader r, TagReadData tr)
		    {
		      TagData t = tr.getTag();
		      byte [] bibdata = t.epcBytes();
		      long bibtime = tr.getTime();
		      int bibnum = bibdata[3] & 0xFF | 
		    		  (bibdata[2] & 0xFF) << 8 | 
		    		  (bibdata[1] & 0xFF) << 16 |
		    		  (bibdata[0] & 0xFF) << 24;
		      
		      if (!times.containsKey(bibnum))
		      {
		          //System.out.println("New tag: " + t.toString());
		          times.put(new Integer(bibnum), new Long(bibtime));
		          //TODO: Call announcer
		          //TODO: Add
		          }
		    }
		  }
}