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
import java.util.HashMap;

import com.thingmagic.ReadListener;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.TagData;
import com.thingmagic.TagFilter;
import com.thingmagic.TagReadData;

public class ThingMagicTimer implements Timer{
		private int status;
		private Reader r;
		private String readerURI;
		private HashMap <Integer, Long> times; // Bibnumber vs ms after epoch
		private HashMap <Integer, Long> startTimes;
		private ReadListener rl;
		public ThingMagicTimer() {
			status = 0;
			r = null;
			readerURI = getReaderURI();
			times = new HashMap <Integer, Long> ();
			startTimes = new HashMap <Integer, Long>();
                        rl = null;
            this.connect();
            
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
			if(status == 1) {
				ReadListener rl = new bibListener();
				r.addReadListener(rl);
				r.startReading();
				status = 2;
			}
		}
		public void stop(){
			if (status == 2) {
				r.stopReading();
                                r.removeReadListener(rl);
				status = 1;
			}
		}
		
		public HashMap <Integer,Long> getTimes() {
			return times;
		}
		
		public void writeTag(int num){
			status = 3; //mark low power write mode
			byte [] bibdata = new byte []{
					(byte) (num >>> 24),
					(byte) (num >>> 16),
					(byte) (num >>> 8),
					(byte) (num)
			};
			TagFilter target = null;
			TagData bibinf = new TagData(bibdata);
			try {
				r.writeTag(target, bibinf);
			} catch (ReaderException e) {
				e.printStackTrace();
			}
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
}