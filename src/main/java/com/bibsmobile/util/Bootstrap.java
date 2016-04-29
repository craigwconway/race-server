package com.bibsmobile.util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import com.bibsmobile.model.AwardCategory;
import com.bibsmobile.model.DeviceInfo;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventAwardsConfig;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.FuseDevice;
import com.bibsmobile.model.License;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.Series;
import com.bibsmobile.model.SeriesRegion;
import com.bibsmobile.model.Split;
import com.bibsmobile.model.SplitTimeType;
import com.bibsmobile.model.TimerConfig;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupType;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.UserGroupUserAuthorityID;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.job.BaseJob;
import com.bibsmobile.job.CartExpiration;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

    
    private static UserAuthority userAuthority;

    private static UserAuthorities userAuthorities;

    private static UserProfile userProfile;
    
    @Autowired
    private StandardPasswordEncoder encoder;


	private static Random r = new Random();
	
	protected static String generateRandomName(){
		int wordSize = Math.abs(r.nextInt(9));
		StringBuilder sb = new StringBuilder(wordSize);
		if(wordSize < 4) wordSize = 4;
		String cons = "bcdfghjklmnpqrstvwxz";
		String vows = "aeiou";
		for(int n=0;n<wordSize;n++){
			int i = Math.abs(r.nextInt(cons.length()));
			int j = Math.abs(r.nextInt(vows.length()));
			sb.append( (n%2==0)?cons.charAt(i):vows.charAt(j));
		}
		return sb.toString();
	}
	
	protected static int generateRandomAge(){
		int age = Math.abs(r.nextInt(89));
		if(age < 14) age = 14;
		return age;
	}
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
    	
    	if(Event.findAllEvents().isEmpty()){
    		TimeZone systemtz = TimeZone.getDefault();
    		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
    		
            Event foo = new Event();
            foo.setName("Kings Canyon Critical Mass");
            foo.setCity("San Francisco");
            foo.setDescription("Official Kings Canyon race! Volunteers along the course will distribute shrugs.");
            foo.setAddress("904 Haight St");
            foo.setOrganization("Bibs Athletic Events");
            foo.setWebsite("http://mybibs.co");
            foo.setZip("94117");
            foo.setState("CA");
            foo.setCountry("United States");
            foo.setLongitude(-122.43723019999999);
            foo.setLatitude(37.771276);
            foo.setTimezone(systemtz);
            foo.setPhone("9253602927");
            foo.setCharity("American Red Cross");
            foo.setTimeStart(new DateTime().toDate());
            foo.setTimeStartLocal(format.format(foo.getTimeStart()));
            //Advanced Fields
            foo.setParking("Parking is available in the lot at Divisadero and Scott. There is a $5 fee payable to the ranger.");
            foo.setGeneral("This run is called King's Canyon to commemorate the trials of one hiker who"
            		+ " shrugged his way across the wilderness preserve. It has a 5k run for runners and a 1 mile run for"
            		+ " beginners. Volunteers from years past will haunt the course and distribute water at a halfway point for athletes.");
            foo.setHashtag("runwithWOW");
            foo.setCourseRules("Runners should stick to the trail and yeild to any horses or angry ghosts.");
            
            
            foo.persist();
            
            EventType type = new EventType();
            type.setEvent(foo);
            type.setStartTime(new DateTime().toDate());
            type.setTimeStartLocal(format.format(type.getStartTime()));
            type.setDistance("5k");
            type.setRacetype("Running");
            type.setMeters(new Long(5000));
            type.setTypeName("Run with the Lizards 5k");
            type.setGunTime(new DateTime().toDate());
            type.setGunFired(true);
            type.persist();
            
            EventType type2 = new EventType();
            type2.setEvent(foo);
            type2.setStartTime(new DateTime().toDate());
            type2.setTimeStartLocal(format.format(type.getStartTime()));
            type2.setDistance("1 mi");
            type2.setRacetype("Running");
            type2.setMeters(new Long(1760));
            type2.setTypeName("Run with the Wizards 1 Miler");
            type2.setGunTime(new DateTime().toDate());
            type2.setGunFired(true);
            type2.persist();
            Map<Integer, String> teams = new HashMap<Integer, String>();
            teams.put(0, "Pacific Run Club");
            teams.put(1, "Face Punchers");
            teams.put(2, "Face Punchees");
            teams.put(3, "Dirt Shovellers");
            teams.put(4, "Grinches");
            teams.put(5, "Santas");
            teams.put(6, "Black Bears");
            teams.put(7, "Beet Farmers");
            for(long i = 1; i < 300; i++){
            	RaceResult user = new RaceResult();
            	user.setBib(i);
            	user.setEvent(foo);
            	if(i %3 == 0) {
            		user.setEventType(type2);
            	} else {
            		user.setEventType(type);
            	}
            	
            	user.setTeam(teams.get(((int)i)%10));
            	System.out.println("Team: " + user.getTeam());
            	user.setAge(generateRandomAge());
            	user.setGender( (i%2==0) ? "M" : "F");
            	user.setTimeofficial(Math.abs(
            			 new DateTime().plusMinutes(r.nextInt(60)).plusMinutes(15).plusSeconds(10).plusSeconds(r.nextInt(39)).toDate().getTime()));
            	user.setTimestart(user.getEventType().getGunTime().getTime());
            	user.setTimeofficialdisplay(
            			RaceResult.toHumanTime(user.getTimestart(), user.getTimeofficial()));
            	user.setFirstname(generateRandomName());
            	user.setLastname(generateRandomName());
            	user.setCity("San Francisco");
            	user.setState("CA");
            	if( i== 1) {
            		Map<String, String> customs = new HashMap<String,String>();
            		customs.put("beers", "3");
            		customs.put("beer-adjusted-time", "00:33:22");
            		Map<Integer, Split> splits = new HashMap<Integer, Split>();
            		Split split1 = new Split();
            		split1.setTime(10000);
            		split1.setType(SplitTimeType.DISCRETE);
            		Split split2 = new Split();
            		split2.setTime(20000);
            		split2.setType(SplitTimeType.DISCRETE);
            		splits.put(1, split1);
            		splits.put(2, split2);
            		user.setSplits(splits);
            		user.setCustomFields(customs);
            	}
            	if(i==2) {
            		Map<String, String> customs = new HashMap<String,String>();
            		customs.put("bears", "3");
            		customs.put("bear-adjusted-time", "00:33:22");
            		Map<Integer, Split> splits = new HashMap<Integer, Split>();
            		Split split1 = new Split();
            		split1.setTime(10000);
            		split1.setType(SplitTimeType.CUMULATIVE);
            		Split split2 = new Split();
            		split2.setTime(30000);
            		split2.setType(SplitTimeType.CUMULATIVE);
            		splits.put(1, split1);
            		splits.put(2, split2);
            		user.setSplits(splits);
            		user.setCustomFields(customs);            		
            	}
            	if(i==3) {
            		Map<String, String> customs = new HashMap<String,String>();
            		customs.put("bears", "3");
            		customs.put("bear-adjusted-time", "00:33:22");
            		Map<Integer, Split> splits = new HashMap<Integer, Split>();
            		Split split1 = new Split();
            		split1.setTime(user.getTimestart()+10000);
            		split1.setType(SplitTimeType.TIMESTAMP);
            		Split split2 = new Split();
            		split2.setTime(user.getTimestart()+30000);
            		split2.setType(SplitTimeType.TIMESTAMP);
            		splits.put(1, split1);
            		splits.put(2, split2);
            		user.setSplits(splits);
            		user.setCustomFields(customs);            		
            	}
            	user.persist();
            }
            
            if(Series.findAllSeries().isEmpty()) {
            	Series zapposRaceSeries = new Series();
            	zapposRaceSeries.setName("Zappos Race Series");
            	zapposRaceSeries.setTitleSponsor("Zappos");
            	zapposRaceSeries.persist();
            	Set <SeriesRegion> seriesRegions = new HashSet<SeriesRegion>();
            	SeriesRegion sf = new SeriesRegion();
            	sf.setName("San Francisco");
            	seriesRegions.add(sf);
            	SeriesRegion charlotte = new SeriesRegion();
            	charlotte.setName("Charlotte");
            	seriesRegions.add(charlotte);
            	SeriesRegion ny = new SeriesRegion();
            	ny.setName("New York");
            	seriesRegions.add(ny);
            	SeriesRegion boston = new SeriesRegion();
            	boston.setName("Boston");
            	seriesRegions.add(boston);
            	SeriesRegion atlanta = new SeriesRegion();
            	atlanta.setName("Atlanta");
            	seriesRegions.add(atlanta);
            	SeriesRegion portland = new SeriesRegion();
            	portland.setName("Portland");
            	seriesRegions.add(portland);
            	SeriesRegion seattle = new SeriesRegion();
            	seattle.setName("Seattle");
            	seriesRegions.add(seattle);
            	SeriesRegion austin = new SeriesRegion();
            	austin.setName("Austin");
            	seriesRegions.add(austin);
            	zapposRaceSeries.setRegions(seriesRegions);
            	zapposRaceSeries.merge();
            }
            // default awards categories
            AwardCategory.createDefaultMedals(type);
            AwardCategory.createAgeGenderRankings(type, 
            		AwardCategory.MIN_AGE, AwardCategory.MAX_AGE, 
            		AwardCategory.DEFAULT_AGE_SPAN, AwardCategory.DEFAULT_LIST_SIZE);
            
            AwardCategory.createDefaultMedals(type2);
            AwardCategory.createAgeGenderRankings(type2, 
            		AwardCategory.MIN_AGE, AwardCategory.MAX_AGE, 
            		AwardCategory.DEFAULT_AGE_SPAN, AwardCategory.DEFAULT_LIST_SIZE);
            
        }
    	
    	if(FuseDevice.countFuseDevices() < 1) {
        	FuseDevice fuseDevice = new FuseDevice("dirt");
        	fuseDevice.setSecret("vn+KIMN1Ca6ouJgQ8rU0CluOyBrnKLs29oAgCluoIVk=");
        	fuseDevice.persist();
    	}

    	
        // expire leftover carts at startup
        try {
            BaseJob.scheduleNow(CartExpiration.class);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        //store default readers
        if (TimerConfig.countTimerConfigs() < 1) {
            TimerConfig timerConfig = new TimerConfig();
            timerConfig.setUrl("tmr://bibs001.bibsmobile.com");
            if(BuildTypeUtil.usesRfid()) {
                timerConfig.setType(1);
            }
            timerConfig.persist(); // reader 1
            timerConfig = new TimerConfig();
            timerConfig.setUrl("tmr://bibs002.bibsmobile.com");
            timerConfig.setPosition(1);
            if(BuildTypeUtil.usesRfid()) {
                timerConfig.setType(1);
            }
            timerConfig.persist(); // reader 2
        }

        //  store default users
        if (UserProfile.countUserProfiles() < 1) {

            userProfile = new UserProfile();
            userProfile.setUsername("admin");
            userProfile.setPassword(encoder.encode("admin"));
            userProfile.setFirstname("System");
            userProfile.setLastname("Administrator");
            userProfile.persist();

            userProfile = new UserProfile();
            userProfile.setUsername("eventadmin");
            userProfile.setPassword(encoder.encode("eventadmin"));
            userProfile.setFirstname("Event");
            userProfile.setLastname("Administrator");
            userProfile.persist();  
            
            userProfile = new UserProfile();
            userProfile.setUsername("useradmin");
            userProfile.setPassword(encoder.encode("useradmin"));
            userProfile.setFirstname("User");
            userProfile.setLastname("Administrator");
            userProfile.persist();

            userProfile = new UserProfile();
            userProfile.setUsername("user");
            userProfile.setPassword(encoder.encode("user"));
            userProfile.setFirstname("Bibs");
            userProfile.setLastname("User");
            userProfile.persist();
        }
        //Generate a deviceinfo if there is not yet one in the system
        if(null == DeviceInfo.findDeviceInfo(new Long(1))) {
        	DeviceInfo info = new DeviceInfo();
        	info.setRunnersUsed(new Long(0));
        	info.persist();
        	// If we have no deviceinfo, we also do not have any licenses:
        	License newLicense = new License();
        	byte[] token = new byte[64];
        	newLicense.setToken(token);
        	newLicense.persist();
        }
        
        
        //store default roles
        if (UserAuthority.countUserAuthoritys() < 1) {
            for (String authorityName : new String[] { UserAuthority.SYS_ADMIN, UserAuthority.EVENT_ADMIN, UserAuthority.USER_ADMIN, UserAuthority.USER }) {
                userAuthority = new UserAuthority();
                userAuthority.setAuthority(authorityName);
                userAuthority.persist();
            }
        }

        if (UserAuthorities.countUserAuthoritieses() < 1) {

            UserProfile tmpUserProfile = UserProfile.findUserProfilesByUsernameEquals("admin").getSingleResult();
            UserAuthority userAuthority1 = UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_SYS_ADMIN").getSingleResult();
            UserAuthoritiesID id = new UserAuthoritiesID();
            if (tmpUserProfile != null && userAuthority1 != null) {
                id.setUserAuthority(userAuthority1);
                id.setUserProfile(tmpUserProfile);
                if (UserAuthorities.findUserAuthorities(id) == null) {
                    userAuthorities = new UserAuthorities();
                    userAuthorities.setId(id);
                    userAuthorities.persist();
                }
            }

            tmpUserProfile = UserProfile.findUserProfilesByUsernameEquals("eventadmin").getSingleResult();
            userAuthority1 = UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_EVENT_ADMIN").getSingleResult();
            if (tmpUserProfile != null && userAuthority1 != null) {
                id.setUserAuthority(userAuthority1);
                id.setUserProfile(tmpUserProfile);
                if (UserAuthorities.findUserAuthorities(id) == null) {
                    userAuthorities = new UserAuthorities();
                    userAuthorities.setId(id);
                    userAuthorities.persist();
                }
            }
            
            tmpUserProfile = UserProfile.findUserProfilesByUsernameEquals("useradmin").getSingleResult();
            userAuthority1 = UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_EVENT_ADMIN").getSingleResult();
            if (tmpUserProfile != null && userAuthority1 != null) {
                id.setUserAuthority(userAuthority1);
                id.setUserProfile(tmpUserProfile);
                if (UserAuthorities.findUserAuthorities(id) == null) {
                    userAuthorities = new UserAuthorities();
                    userAuthorities.setId(id);
                    userAuthorities.persist();
                }
            }

            tmpUserProfile = UserProfile.findUserProfilesByUsernameEquals("user").getSingleResult();
            userAuthority1 = UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_USER").getSingleResult();
            if (tmpUserProfile != null && userAuthority != null) {
                id.setUserAuthority(userAuthority1);
                id.setUserProfile(tmpUserProfile);
                if (UserAuthorities.findUserAuthorities(id) == null) {
                    userAuthorities = new UserAuthorities();
                    userAuthorities.setId(id);
                    userAuthorities.persist();
                }
            }
        }
    }
}