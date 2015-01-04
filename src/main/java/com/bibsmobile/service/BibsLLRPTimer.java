package com.bibsmobile.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javassist.tools.rmi.RemoteException;

import org.jdom.JDOMException;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.enumerations.AISpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.AirProtocols;
import org.llrp.ltk.generated.enumerations.KeepaliveTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecStartTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecState;
import org.llrp.ltk.generated.enumerations.ROSpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.StatusCode;
import org.llrp.ltk.generated.interfaces.SpecParameter;
import org.llrp.ltk.generated.messages.ADD_ROSPEC;
import org.llrp.ltk.generated.messages.ADD_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.CLOSE_CONNECTION;
import org.llrp.ltk.generated.messages.CLOSE_CONNECTION_RESPONSE;
import org.llrp.ltk.generated.messages.DELETE_ROSPEC;
import org.llrp.ltk.generated.messages.DELETE_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.ENABLE_EVENTS_AND_REPORTS;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.ERROR_MESSAGE;
import org.llrp.ltk.generated.messages.GET_REPORT;
import org.llrp.ltk.generated.messages.KEEPALIVE;
import org.llrp.ltk.generated.messages.READER_EVENT_NOTIFICATION;
import org.llrp.ltk.generated.messages.RO_ACCESS_REPORT;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG_RESPONSE;
import org.llrp.ltk.generated.messages.START_ROSPEC;
import org.llrp.ltk.generated.messages.START_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.STOP_ROSPEC;
import org.llrp.ltk.generated.messages.STOP_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.parameters.AISpec;
import org.llrp.ltk.generated.parameters.AISpecStopTrigger;
import org.llrp.ltk.generated.parameters.ConnectionAttemptEvent;
import org.llrp.ltk.generated.parameters.EPCData;
import org.llrp.ltk.generated.parameters.EPC_96;
import org.llrp.ltk.generated.parameters.EventsAndReports;
import org.llrp.ltk.generated.parameters.InventoryParameterSpec;
import org.llrp.ltk.generated.parameters.KeepaliveSpec;
import org.llrp.ltk.generated.parameters.ROBoundarySpec;
import org.llrp.ltk.generated.parameters.ROSpec;
import org.llrp.ltk.generated.parameters.ROSpecStartTrigger;
import org.llrp.ltk.generated.parameters.ROSpecStopTrigger;
import org.llrp.ltk.generated.parameters.TagReportData;
import org.llrp.ltk.generated.parameters.UTCTimestamp;
import org.llrp.ltk.net.LLRPConnection;
import org.llrp.ltk.net.LLRPConnectionAttemptFailedException;
import org.llrp.ltk.net.LLRPConnector;
import org.llrp.ltk.net.LLRPEndpoint;
import org.llrp.ltk.types.Bit;
import org.llrp.ltk.types.BitArray_HEX;
import org.llrp.ltk.types.LLRPMessage;
import org.llrp.ltk.types.LLRPParameter;
import org.llrp.ltk.types.UnsignedByte;
import org.llrp.ltk.types.UnsignedInteger;
import org.llrp.ltk.types.UnsignedShort;
import org.llrp.ltk.types.UnsignedShortArray;
import org.llrp.ltk.util.Util;

import com.bibsmobile.model.TimerConfig;

public class BibsLLRPTimer extends AbstractTimer implements LLRPEndpoint, Timer {

	private int status;
	private LLRPConnection reader;
	private TimerConfig timerConfig;
    private ROSpec rospec;
    private int MessageID=1;
    private long usReaderOffset;
    private long usCurrentOffset;
    private Thread watchdog = null; // watchdog is a null thread
    private int alive = 0; // initialize alive count to zero.
    
    public BibsLLRPTimer(TimerConfig timerConfig){
    	this.timerConfig = timerConfig;
    }
    
    private UnsignedInteger getUniqueMessageID() {
        return new UnsignedInteger(MessageID++);
    }

	public void errorOccured(String arg0) {
		System.out.println(arg0);
		
	}
	
    private ADD_ROSPEC buildROSpecFromFile(String filename) {
        try {
            LLRPMessage addRospec = Util.loadXMLLLRPMessage(new File(filename));
            System.out.println("file: "+filename+", ROSPEC:" + addRospec.getResponseType());

            String[] ports = timerConfig.getPorts().split(",");
             for(SpecParameter param: ((ADD_ROSPEC) addRospec).getROSpec().getSpecParameterList()){
             	if(param instanceof AISpec){
             		UnsignedShortArray array = new UnsignedShortArray();
             		for(int i=0;i<ports.length;i++){
             			array.add(new UnsignedShort(ports[i]));
             		}
             		((AISpec)param).setAntennaIDs(array);
             	}
             }
             
            // TODO make sure this is an ADD_ROSPEC message 
            return (ADD_ROSPEC) addRospec;
        } catch (FileNotFoundException ex) {
            System.out.println("Could not find file");
            System.exit(1);
        } catch (IOException ex) {
            System.out.println("IO Exception on file");
            System.exit(1);
        } catch (JDOMException ex) {
            System.out.println("Enable to convert LTK-XML to DOM");
            System.exit(1);
        } catch (InvalidLLRPMessageException ex) {
            System.out.println("Enable to convert LTK-XML to Internal Object");
            System.exit(1);
        }
        return null;
    }	
	
    private ADD_ROSPEC buildROSpecFromObjects() {
        System.out.println("Building ADD_ROSPEC message from scratch ...");
        ADD_ROSPEC addRoSpec = new ADD_ROSPEC();
        addRoSpec.setMessageID(getUniqueMessageID());

        rospec = new ROSpec();

        // set up the basic info for the RO Spec.
        rospec.setCurrentState(new ROSpecState(ROSpecState.Disabled));
        rospec.setPriority(new UnsignedByte(0));
        rospec.setROSpecID(new UnsignedInteger(12345));

        // set the start and stop conditions for the ROSpec.
        // For now, we will start and stop manually 
        ROBoundarySpec boundary = new ROBoundarySpec();
        ROSpecStartTrigger start = new ROSpecStartTrigger();
        ROSpecStopTrigger stop = new ROSpecStopTrigger();
        start.setROSpecStartTriggerType(new ROSpecStartTriggerType(ROSpecStartTriggerType.Null));
        stop.setROSpecStopTriggerType(new ROSpecStopTriggerType(ROSpecStopTriggerType.Null));
        stop.setDurationTriggerValue(new UnsignedInteger(0));
        boundary.setROSpecStartTrigger(start);
        boundary.setROSpecStopTrigger(stop);
        rospec.setROBoundarySpec(boundary);

        // set up what we want to do in the ROSpec. In this case
        // build the simples inventory on all channels using defaults
        AISpec aispec = new AISpec();

        // what antennas to use.
        UnsignedShortArray ants = new UnsignedShortArray();
        if(null == timerConfig.getPorts() || timerConfig.getPorts().isEmpty()){
        	ants.add(new UnsignedShort(0)); // 0 means all antennas
        }else{
        	String[] ports = timerConfig.getPorts().split(",");
        	System.out.println("ports "+ports);
        	for(int i=0;i<ports.length;i++){
            	ants.add(new UnsignedShort(ports[i]));
        	}
        }
        aispec.setAntennaIDs(ants);

        // set up the AISpec stop condition and options for inventory
        AISpecStopTrigger aistop = new AISpecStopTrigger();
        aistop.setAISpecStopTriggerType(new AISpecStopTriggerType(AISpecStopTriggerType.Null));
        aistop.setDurationTrigger(new UnsignedInteger(0));
        aispec.setAISpecStopTrigger(aistop);

        // set up any override configuration.  none in this case
        InventoryParameterSpec ispec = new InventoryParameterSpec();
        ispec.setAntennaConfigurationList(null);
        ispec.setInventoryParameterSpecID(new UnsignedShort(23));
        ispec.setProtocolID(new AirProtocols(AirProtocols.EPCGlobalClass1Gen2));
        List<InventoryParameterSpec> ilist = new ArrayList<InventoryParameterSpec>();
        ilist.add(ispec);

        aispec.setInventoryParameterSpecList(ilist);
        List<SpecParameter> slist = new ArrayList<SpecParameter>();
        slist.add(aispec);
        rospec.setSpecParameterList(slist);

        addRoSpec.setROSpec(rospec);

        return addRoSpec;
    }

    private void LLRPAddROSPEC() {
    	if(status < 1) {
    		return;
    	}
        LLRPMessage response;

        ADD_ROSPEC addRospec = null;

        if(null !=timerConfig.getFilename() && !timerConfig.getFilename().isEmpty()) {
        	addRospec = buildROSpecFromFile(timerConfig.getFilename());
        } else {
        addRospec = buildROSpecFromObjects();
        }
        addRospec.setMessageID(getUniqueMessageID());
        rospec = addRospec.getROSpec();
        
        System.out.println("Sending ADD_ROSPEC message  ...");
        try {
            response =  reader.transact(addRospec, timerConfig.getConnectionTimeout() * 1000);

            // check whether ROSpec addition was successful
            StatusCode status = ((ADD_ROSPEC_RESPONSE)response).getLLRPStatus().getStatusCode();
            if (status.intValue() == StatusCode.M_Success) {
                    System.out.println("ADD_ROSPEC was successful");
                    alive = 3;
            }
            else {
                    System.out.println("ADD_ROSPEC failures");
            }
        } catch (TimeoutException ex) {
            System.out.println("Timeout waiting for ADD_ROSPEC response");
            System.out.println("Crashing the server...");
        }
    }

	private void LLRPSetReaderConfiguration() {
		LLRPMessage response;

		System.out.println("[LLRP][TIMER "+timerConfig.getId()+"]Loading SET_READER_CONFIG message from file SET_READER_CONFIG.xml ...");
		try {
			String xmlPath = "/properties/LLRP/";
			LLRPMessage setConfigMsg = Util
					.loadXMLLLRPMessage(new File(
							xmlPath+"SET_READER_CONFIG.xml"));
			// TODO make sure this is an SET_READER_CONFIG message

			// touch up the transmit power for max
			SET_READER_CONFIG setConfig = (SET_READER_CONFIG) setConfigMsg;
			
			// Hold Events and Reports
		    EventsAndReports eventsAndReports = new EventsAndReports();
		    eventsAndReports.setHoldEventsAndReportsUponReconnect(new Bit(true));
		    setConfig.setEventsAndReports(eventsAndReports);
		      
			response = reader.transact(setConfig, 10000);

			// check whetherSET_READER_CONFIG addition was successful
			StatusCode status = ((SET_READER_CONFIG_RESPONSE) response)
					.getLLRPStatus().getStatusCode();
			if (status.equals(new StatusCode("M_Success"))) {
				System.out.println("SET_READER_CONFIG was successful");
			} else {
				System.out.println(response.toXMLString());
				System.out.println("SET_READER_CONFIG failures");
				System.exit(1);
			}

		} catch (TimeoutException ex) {
			System.out.println("[LLRP][TIMER "+timerConfig.getId()+"]Timeout waiting for SET_READER_CONFIG response");
			System.exit(1);
		} catch (FileNotFoundException ex) {
			System.out.println("[LLRP][TIMER "+timerConfig.getId()+"]Could not find file");
			System.exit(1);
		} catch (IOException ex) {
			System.out.println("[LLRP][TIMER "+timerConfig.getId()+"]IO Exception on file");
			System.exit(1);
		} catch (JDOMException ex) {
			System.out.println("[LLRP][TIMER "+timerConfig.getId()+"]Unable to convert LTK-XML to DOM");
			System.exit(1);
		} catch (InvalidLLRPMessageException ex) {
			System.out.println("[LLRP][TIMER "+timerConfig.getId()+"]Unable to convert LTK-XML to Internal Object");
			System.exit(1);
		}
	}    
    
    private void LLRPEnableHeartbeat() {                
        // build the keepalive settings
    	if(status < 1) {
    		return;
    	}
        SET_READER_CONFIG sr = new SET_READER_CONFIG();
        KeepaliveSpec ks = new KeepaliveSpec();
        ks.setKeepaliveTriggerType(new KeepaliveTriggerType(KeepaliveTriggerType.Periodic));
        ks.setPeriodicTriggerValue(new UnsignedInteger(timerConfig.getHeartbeatTimeout()));
        LLRPMessage response; //response to handle reader transaction
        
        sr.setKeepaliveSpec(ks); //Apply keepalive message
        sr.setResetToFactoryDefault(new Bit(0)); //don't factory default
        
        //System.out.println("using keepalive period " +  timerConfig.getHeartbeatTimeout());  
        try {
        	System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] Sending SET_READER_CONFIG...");
			response = reader.transact(sr, timerConfig.getHeartbeatTimeout());
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] SET_READER_CONFIG timeout...");
		}
        // run the watch-dog
        watchdog = new Thread(new Runnable() {
                public void run() {
                        System.out.println("[LLRP][WATCHDOG][TIMER "+timerConfig.getId()+"]Enabling watchdog on ");
                        try {
                        		alive = timerConfig.getHeartbeats(); //On watchdog engage, reset the counter
                        		System.out.println("[LLRP][WATCHDOG][TIMER "+timerConfig.getId()+"] Heartbeat count set to: " + alive);
                                while (getStatus() > 0) {
                                        try {
                                                Thread.sleep(timerConfig.getHeartbeatTimeout());
                                                System.out.println("[LLRP][WATCHDOG][TIMER "+timerConfig.getId()+"] wakeup: Alive = " + alive);
                                                if (alive > 0) {
                                                	LLRPGetTagReports();
                                                	alive--;
                                                }
                                                System.out.println("[LLRP][WATCHDOG][TIMER "+timerConfig.getId()+"] wakeup: Alive now = " + alive);                                                
                                                if (0 >= alive) {
                                                        System.out.println("[LLRP][WATCHDOG][TIMER "+timerConfig.getId()+"] connection timed out...");
                                                        disconnect();
                                                        System.out.println("[LLRP][WATCHDOG][TIMER "+timerConfig.getId()+"] connecting");
                                                        try {
                                                        	//Reconnect method instead of connect to avoid spinning up excess threads
                                                        	System.out.println("[LLRP][WATCHDOG][TIMER "+timerConfig.getId()+"] Reconnecting...");
															reconnect();
														} catch (Exception e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
                                                        	System.out.println("[LLRP][WATCHDOG][TIMER "+timerConfig.getId()+"] Error Reconnecting, setting status to 0...");
															status = 0;  //for the app? for the app.
															alive = 0;
														}
                                                }
                                        } catch (InterruptedException e) {
                                                System.out.println("[LLRP][WATCHDOG][TIMER "+timerConfig.getId()+"]received interrupt - stopping watchdog.");
                                                status = 0;
                                                alive = 0;
                                                watchdog.stop();
                                        }
                                }
                        } catch (RemoteException e) {
                                System.out.println("could not connect to the reader with the watchdog. "+ e);
                        }
                        System.out.println("[LLRP][WATCHDOG][TIMER "+timerConfig.getId()+"] Watchdong deactivated...");
                }
        });
        watchdog.start();
}
	
    private void LLRPReenableHeartbeat() {                
        // build the keepalive settings
        SET_READER_CONFIG sr = new SET_READER_CONFIG();
        KeepaliveSpec ks = new KeepaliveSpec();
        ks.setKeepaliveTriggerType(new KeepaliveTriggerType(KeepaliveTriggerType.Periodic));
        ks.setPeriodicTriggerValue(new UnsignedInteger(timerConfig.getHeartbeatTimeout()));
        LLRPMessage response; //response to handle reader transaction
        
        sr.setKeepaliveSpec(ks); //Apply keepalive message
        sr.setResetToFactoryDefault(new Bit(0)); //don't factory default
        
        //System.out.println("using keepalive period " +  timerConfig.getHeartbeatTimeout());  
        try {
        	System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] Sending SET_READER_CONFIG...");
			response = reader.transact(sr, timerConfig.getHeartbeatTimeout());
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] SET_READER_CONFIG timeout...");
		}
}
		
    private void LLRPFactoryDefault() {
        LLRPMessage response;

        try {
            // factory default the reader
            System.out.println("[LLRP][TIMER "+timerConfig.getId()+"]SET_READER_CONFIG with factory default ...");
            SET_READER_CONFIG set = new SET_READER_CONFIG();
            set.setMessageID(getUniqueMessageID());
            set.setResetToFactoryDefault(new Bit(true));
            response =  reader.transact(set, timerConfig.getConnectionTimeout() * 1000);

            // check whether ROSpec addition was successful
            StatusCode status = ((SET_READER_CONFIG_RESPONSE)response).getLLRPStatus().getStatusCode();
            if (status.intValue() == StatusCode.M_Success) {
                    System.out.println("SET_READER_CONFIG Factory Default was successful");
            }
            else {
                    System.out.println(response.toXMLString());
                    System.out.println("SET_READER_CONFIG Factory Default Failure");
                    System.out.println("Crashing the server...");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Crashing the server...");
        }
    }
    
    private void LLRPDeleteROSPEC() {
        LLRPMessage response;
        if(status < 1) {
        	// Don't do anything if the reader is connected
        	return;
        }
        try {
            // factory default the reader
            System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] DELETE_ROSPEC ...");
            DELETE_ROSPEC delete = new DELETE_ROSPEC();
            delete.setMessageID(getUniqueMessageID());
            //get.setROSpecID(rospec.getROSpecID());
            delete.setROSpecID(new UnsignedInteger(1));
            response =  reader.transact(delete, timerConfig.getConnectionTimeout() * 1000);

            // check whether ROSpec addition was successful
            StatusCode status = ((DELETE_ROSPEC_RESPONSE)response).getLLRPStatus().getStatusCode();
            if (status.intValue() == StatusCode.M_Success) {
                    System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] DELETE_ROSPEC was successful");
            }
            else {
                    System.out.println(response.toXMLString());
                    System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] DELETE_ROSPEC failed ");
            }
        } catch (Exception e) {
        	System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] Caught Exception in DELETE_ROSPEC...");
            e.printStackTrace();
        }
    }
    
    
    private void LLRPEnable() {
    	if(status < 1) {
    		return;
    	}
        LLRPMessage response;
        try {
            // factory default the reader
            System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] ENABLE_ROSPEC ...");
            ENABLE_ROSPEC ena = new ENABLE_ROSPEC();
            ena.setMessageID(getUniqueMessageID());
            ena.setROSpecID(rospec.getROSpecID());

            response =  reader.transact(ena, timerConfig.getConnectionTimeout() * 1000);

            // check whether ROSpec addition was successful
            StatusCode status = ((ENABLE_ROSPEC_RESPONSE)response).getLLRPStatus().getStatusCode();
            if (status.intValue() == StatusCode.M_Success) {
                    System.out.println("[LLRP][TIMER "+timerConfig.getId()+"]ENABLE_ROSPEC was successful");
            }
            else {
                    System.out.println(response.toXMLString());
                    System.out.println("[LLRP][TIMER "+timerConfig.getId()+"]ENABLE_ROSPEC_RESPONSE failed ");
            }
        } catch (Exception e) {
        	System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] Caught Exception in ENABLE_ROSPEC...");
            e.printStackTrace();
        }
    }

    private void lineReadHandler(TagReportData tr) {
    	long bibtime = tr.getFirstSeenTimestampUTC().getMicroseconds().toLong()-this.usReaderOffset+this.usCurrentOffset;
        LLRPParameter epcp = (LLRPParameter) tr.getEPCParameter();

        System.out.println("[LLRP] lineReadHandler "+tr.toString());
        // epc is not optional, so we should fail if we can't find it
        String epcString = "EPC: ";
        if(epcp != null) {
            if( epcp.getName().equals("EPC_96")) {
                EPC_96 epc96 = (EPC_96) epcp;
                epcString += epc96.getEPC().toString();
                System.out.println("ERROR Non-Bibs chip read " + epcString);
                logUnassignedBib(epcString, bibtime, timerConfig);
            } else if ( epcp.getName().equals("EPCData")) {
                EPCData epcData = (EPCData) epcp;
                epcString += epcData.getEPC().toString();
                BitArray_HEX bibdata = epcData.getEPC();
            	// long bibtime = tr.getFirstSeenTimestampUTC().getMicroseconds().toLong();
                int bib = Integer.decode("0x"+bibdata.toString());
                System.out.println(" ANTENNE FOUND "+bib+" "+bibtime);
                logTime(bib, bibtime, timerConfig);
            }
        } else {
            System.out.println("Could not find EPC in Tag Report");
        }

        // all of these values are optional, so check their non-nullness first
        if(tr.getAntennaID() != null) {
            epcString += " Antenna: " +
                    tr.getAntennaID().getAntennaID().toString();
        }

        if(tr.getChannelIndex() != null) {
            epcString += " ChanIndex: " +
                    tr.getChannelIndex().getChannelIndex().toString();
        }

        if( tr.getFirstSeenTimestampUTC() != null) {
            epcString += " FirstSeen: " +
                    tr.getFirstSeenTimestampUTC().getMicroseconds().toString();
        }

        if(tr.getInventoryParameterSpecID() != null) {
            epcString += " ParamSpecID: " +
                    tr.getInventoryParameterSpecID().getInventoryParameterSpecID().toString();
        }

        if(tr.getLastSeenTimestampUTC() != null) {
            epcString += " LastTime: " +
                    tr.getLastSeenTimestampUTC().getMicroseconds().toString();
        }

        if(tr.getPeakRSSI() != null) {
            epcString += " RSSI: " +
                    tr.getPeakRSSI().getPeakRSSI().toString();
        }

        if(tr.getROSpecID() != null) {
            epcString += " ROSpecID: " +
                    tr.getROSpecID().getROSpecID().toString();
        }

        if(tr.getTagSeenCount() != null) {
            epcString += " SeenCount: " +
                    tr.getTagSeenCount().getTagCount().toString();
        }

        System.out.println(epcString);
   	
    }
    
	public void messageReceived(LLRPMessage bibRead) {
		if (bibRead instanceof KEEPALIVE) {
			System.out.println("[LLRP][WATCHDOG][TIMER "+timerConfig.getId()+"] Heartbeat Recieved...");
			alive = timerConfig.getHeartbeats(); //If we have a valid keepalive message, reset the watchdog.
		}
        if (bibRead.getTypeNum() == RO_ACCESS_REPORT.TYPENUM) {
            RO_ACCESS_REPORT report = (RO_ACCESS_REPORT) bibRead;
            List<TagReportData> list = report.getTagReportDataList();
            for(TagReportData data:list){
            	lineReadHandler(data);
            }
        }else if (bibRead.getTypeNum() == READER_EVENT_NOTIFICATION.TYPENUM) {
        	READER_EVENT_NOTIFICATION eventNotification = (READER_EVENT_NOTIFICATION) bibRead;
        	ConnectionAttemptEvent conn = eventNotification.getReaderEventNotificationData().getConnectionAttemptEvent();
        	if(conn.getStatus() != null) {
        		//we probably connected
        		this.usCurrentOffset = 1000 * System.currentTimeMillis();
        		this.usReaderOffset = ((UTCTimestamp) eventNotification.getReaderEventNotificationData().getTimestamp()).getMicroseconds().toLong();
        		System.out.println("usCurrentOffset ################ "+usCurrentOffset);
        		System.out.println("usReaderOffset ################ "+usReaderOffset);
            	}
        } else{
    		try {
				System.out.println("bibRead.getTypeNum() "+bibRead.toXMLString());
			} catch (InvalidLLRPMessageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}

	public void setTimerConfig(TimerConfig timerConfig) {
		this.timerConfig = timerConfig;
		
	}

	public int getStatus() {
		// TODO Auto-generated method stub
		return this.status;
	}

	public long getDateTime() {
		// TODO PUNCH PATRICK IN THE FACE
		return System.currentTimeMillis();
	}

	public void LLRPConnect() throws Exception {
		//Generate Reader using LLRP:
		status = 0;
		reader = new LLRPConnector(this, timerConfig.getUrl());
		try {
			((LLRPConnector) reader).connect();
			reader.getHandler().setKeepAliveAck(true);
			reader.getHandler().setKeepAliveForward(true);
			status = 1;

		} catch(Exception e) {
			status = 0;
			throw new Exception("Error: Could Not connect");
		}
	}
	
	
	private void LLRPGetTagReports()
	   {
			try {
				System.out.println("GET_REPORT ...");
				GET_REPORT getReport = new GET_REPORT();
			    getReport.setMessageID(getUniqueMessageID());

				reader.send(getReport);

				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	   }
	
	public void emptyBuffer() {
		if(this.status == 2) {
			LLRPGetTagReports();
		}
	}
	
	   public void LLRPResumeReports()
	   {
		  
			try {
				System.out.println("[LLRP}[TIMER "+timerConfig.getId()+"]ENABLE_EVENTS_AND_REPORTS ...");
				ENABLE_EVENTS_AND_REPORTS enableEventsAndReports = new ENABLE_EVENTS_AND_REPORTS();
			    ERROR_MESSAGE errorMessage = new ERROR_MESSAGE();
				errorMessage.setMessageID(getUniqueMessageID());
				reader.send(enableEventsAndReports);

			} catch (Exception e) {
				e.printStackTrace();
			}
	   }	
	
	
	public void LLRPFactoryReset() {
		
	}
	
	public void connect() throws Exception {
		LLRPConnect();
		LLRPGetTagReports();
		LLRPDeleteROSPEC();
		//LLRPFactoryDefault();
		//LLRPSetReaderConfiguration();
		LLRPEnableHeartbeat();
		LLRPAddROSPEC();
		LLRPEnable();
	}

	public void reconnect() throws Exception {
		LLRPConnect();
		LLRPGetTagReports();
		LLRPDeleteROSPEC();
		//LLRPFactoryDefault();
		//LLRPSetReaderConfiguration();
		LLRPReenableHeartbeat();
		LLRPAddROSPEC();
		LLRPEnable();
	}
	

	public void disconnect() {
		if(reader == null)
			return;
		// Comment this out if you like spinning up a lot of threads
		if (null != watchdog) {
			watchdog.interrupt();
		}
		
        LLRPMessage response;
        CLOSE_CONNECTION close = new CLOSE_CONNECTION();
        close.setMessageID(getUniqueMessageID());
        try {
            // don't wait around too long for close
            response = reader.transact(close, 4000);
            // check whether ROSpec addition was successful
            StatusCode status = ((CLOSE_CONNECTION_RESPONSE)response).getLLRPStatus().getStatusCode();
            if (status.intValue() == StatusCode.M_Success) {
                System.out.println("[LLRP][TIMER "+timerConfig.getId()+"]CLOSE_CONNECTION was successful");
                this.status = 0;
            }
            else {
                System.out.println("[LLRP][TIMER "+timerConfig.getId()+"]Failed to disconnect");
            }
        } catch (TimeoutException ex) {
            System.out.println(ex);
        }
       
	}

	public void startReader() {
		// TODO Auto-generated method stub
        LLRPMessage response;
        try {
            System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] START_ROSPEC ...");
            START_ROSPEC start = new START_ROSPEC();
            start.setMessageID(getUniqueMessageID());
            start.setROSpecID(rospec.getROSpecID());
     
            response =  reader.transact(start, timerConfig.getConnectionTimeout() * 1000);

            // check whether ROSpec addition was successful
            StatusCode status = ((START_ROSPEC_RESPONSE)response).getLLRPStatus().getStatusCode();
            if (status.intValue() == StatusCode.M_Success) {
            	System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] START_ROSPEC was successful");
            	this.status = 2;
            }
            else {
            	System.out.println(response.toXMLString());
            	System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] START_ROSPEC_RESPONSE failed ");
            }
        } catch (Exception e) {
        	System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] Caught exception in START_ROSPEC...");
            e.printStackTrace();
        }		
	}

	public void stopReader() {
        LLRPMessage response;
        try {
            System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] STOP_ROSPEC ...");
            STOP_ROSPEC stop = new STOP_ROSPEC();
            stop.setMessageID(getUniqueMessageID());
            stop.setROSpecID(rospec.getROSpecID());

            response =  reader.transact(stop, timerConfig.getConnectionTimeout() * 1000);

            // check whether ROSpec addition was successful
            StatusCode status = ((STOP_ROSPEC_RESPONSE)response).getLLRPStatus().getStatusCode();
            if (status.intValue() == StatusCode.M_Success) {
                    System.out.println("[LLRP][TIMER "+timerConfig.getId()+"]STOP_ROSPEC was successful");
                    this.status = 1;
            }
            else {
                    System.out.println(response.toXMLString());
                    System.out.println("[LLRP][TIMER "+timerConfig.getId()+"]STOP_ROSPEC_RESPONSE failed ");
                    System.out.println("Crashing the server...");
            }
        } catch (Exception e) {
        	System.out.println("[LLRP][TIMER "+timerConfig.getId()+"] Caught exception in STOP_ROSPEC...");
            e.printStackTrace();
            System.out.println("Crashing the server...");
        }
	}

	public void write(long bib) throws Exception {
		// Punch Patrick in the face
		
	}

	public static void main(String[] args) {
		// Unit test
		// Currently only validates shrugs
		TimerConfig configuration = new TimerConfig();
		configuration.setUrl("192.168.0.102");
		configuration.setPosition(1);
		configuration.setPorts("1");
		BibsLLRPTimer bibs = new BibsLLRPTimer(configuration);
		try {
			bibs.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bibs.startReader();
		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bibs.stopReader();
			bibs.disconnect();
			System.out.println("Disconnected.");
		}
		bibs.stopReader();
		bibs.disconnect();
		System.out.println("Done.");
		System.exit(0);
	}
}