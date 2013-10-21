/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibsmobile.service;

/**
 *
 * @author mythinkhouse
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Date;
import java.util.HashMap;

import com.thingmagic.Gen2;
import com.thingmagic.ReadListener;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.TagData;
import com.thingmagic.TagFilter;
import com.thingmagic.TagReadData;

public class ThingMagicTimer implements Timer{
		private int status;
		private Reader r;
		private ReadListener bibListener;
		private String readerURI;
		private HashMap <Integer, Long> times; // Bibnumber vs ms after epoch
		private HashMap <Integer, Long> startTimes;
		public ThingMagicTimer() {
			status = 0;
			readerURI = getReaderURI();
			times = new HashMap <Integer, Long> ();
			startTimes = new HashMap <Integer, Long>();
            this.connect();
            bibListener = null;
            
		}
		
		public void purge(){
			times = new HashMap<Integer,Long>();
		}
		
		public void init(){
        	if(getStatus() < 1)
			this.connect();
		}
		
		public void cleanup(){
			this.disconnect();
		}
		
		public int getStatus() {
			return status;
		}
		public void connect() {
			try {
				r = Reader.create(readerURI);
                                //if (Reader.Region.UNSPEC == (Reader.Region)r.paramGet("/reader/region/id")) {
                                //    Reader.Region[] supportedRegions = (Reader.Region[])r.paramGet(TMConstants.TMR_PARAM_REGION_SUPPORTEDREGIONS);
                                //    if (supportedRegions.length < 1)
                                //        {
                                //            throw new Exception("Reader doesn't support any regions");
                                //        }
                                //    else
                                //    {
                                //        r.paramSet("/reader/region/id", supportedRegions[0]);
                                //    }
                                //}
                                r.connect();
                                status = 1;
                                System.out.println("connected");
			}
                        catch( Exception ex){
                            System.err.println(ex);
                        }
		}
		public String getReaderURI() {
			return readerURI;
		}

		public void setReaderURI(String readerURI) {
			this.readerURI = readerURI;
		}

		public void disconnect() {
			if(status != 0)
				r.destroy();
			status = 0;
		}

		public void start(){
			if(status == 3) {
				this.disconnect();
				this.connect();
			}
			if(status == 1) {
				ReadListener bibListener = new BibListener();
				r.addReadListener(bibListener);
				r.startReading();
				status = 2;
			}
		}
		public void stop(){
			if (status == 2) {
				r.stopReading();
				r.removeReadListener(bibListener);
				status = 1;
			}
		}
		
		public HashMap <Integer,Long> getTimes() {
			return times;
		}
		
		public void writeTag(int num) throws Exception{
			status = 3; //mark low power write mode
			TagReadData[] bibSeen;
			byte [] bibdata = new byte []{
					(byte) (num >>> 24),
					(byte) (num >>> 16),
					(byte) (num >>> 8),
					(byte) (num)
			};
			bibSeen = r.read(250);
			if(bibSeen.length < 1) {
				throw new Exception("0" + " tags seen, could not write");
			}
			if(bibSeen.length > 1) {
				throw new Exception(bibSeen.length + "tags seen, could not write");
			}
				
			TagFilter target = null;
			TagData bibinf = new TagData(bibdata);
			try {
				  //r.writeTag(null, bibinf);
			      Gen2.TagData epc = new Gen2.TagData(bibdata);
			      Gen2.WriteTag tagop = new Gen2.WriteTag(epc);
			      r.executeTagOp(tagop, target);
			} catch (ReaderException e) {
				e.printStackTrace();
			}
		}
		
		class BibListener implements ReadListener
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
		      System.out.println("New tag: " + t.toString());
		      if (!times.containsKey(bibnum))
		      {
		          //System.out.println("New tag: " + t.toString());
		          times.put(new Integer(bibnum), new Long(bibtime));
		          //TODO: Call announcer
		          //TODO: Add
		          }
		    }
		  }

		@Override
		public long getTime() throws Exception {
			Date readerDate = new Date();
			long time;
			if(status < 1) {
				throw new Exception("Could not connect.");
			}
			try {
				readerDate = (Date) r.paramGet("/reader/currentTime");
			} catch (ReaderException e) {
				e.printStackTrace();
				throw new Exception("Time not found");
			}
			return readerDate.getTime();
		}

}
