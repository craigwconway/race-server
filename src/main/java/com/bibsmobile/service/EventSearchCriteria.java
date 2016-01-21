/**
 * 
 */
package com.bibsmobile.service;

/**
 * This is a search API used to create searches on properties in an event.
 * @author galen
 * @since 2016-1-16
 *
 */
public class EventSearchCriteria {
	/**
	 * Search radius defaults to 80km.
	 */
	public final static double DEFAULT_SEARCH_RADIUS = 80;
	
	/**
	 * Used to search events on the encompassing racetypes.
	 */
	private RacetypeCriteria racetypeCriteria;
	
	/**
	 * Used to search events on the encompassing distance.
	 */
	private DistanceCriteria distanceCriteria;
	
	/**
	 * Used to query ranges of distances in events. Currently unused.
	 */
	private DistanceRangeCriteria distanceRangeCriteria;
	
	/**
	 * Used to search events on the encompassing name.
	 */
	private NameCriteria nameCriteria;
	
	/**
	 * Used to search events on the encompassing geospatial data.
	 */
	private GeospatialCriteria geospatialCriteria;
	
	/**
	 * Adds racetype criteria to the search encompassed {@link com.bibsmobile.model.EventType types} for using the supplied racetype.
	 * @param racetype {@link com.bibsmobile.model.EventType #getRacetype() racetype} to search for.
	 */
	public void addRacetypeCriteria(String racetype) {
		this.racetypeCriteria = new RacetypeCriteria(racetype);
	}
	
	/**
	 * Adds geospatial criteria to search an event using a default {@link #DEFAULT_SEARCH_RADIUS radius}.
	 * @param longitude Longitude to search
	 * @param latitude Latitude to search
	 */
	public void addGeospatialCriteria(Double longitude, Double latitude) {
		this.geospatialCriteria = new GeospatialCriteria(longitude, latitude);
	}

	/**
	 * Adds geospatial criteria to search an event using a custom radius.
	 * @param longitude Longitude to search
	 * @param latitude Latitude to search
	 * @param radius Radius to search
	 */
	public void addGeospatialCriteria(Double longitude, Double latitude, Double radius) {
		this.geospatialCriteria = new GeospatialCriteria(longitude, latitude);
	}
	
	/**
	 * Adds name critera to search an event on a name.
	 * @param name Name to search for
	 */
	public void addNameCriteria(String name) {
		this.nameCriteria = new NameCriteria(name);
	}
	
	/**
	 * Adds distance critera to search an event by distance.
	 * @param distance distance string to search for (e.g. 5k, 10mi, Half Marathon)
	 */
	public void addDistanceCriteria(String distance) {
		this.distanceCriteria = new DistanceCriteria(distance);
	}
	
	/**
	 * Racetype criteria used in searches
	 * @author galen
	 *
	 */
	protected class RacetypeCriteria {
		String racetype;
		
		/**
		 * @return the racetype
		 */
		public String getRacetype() {
			return racetype;
		}

		RacetypeCriteria(String racetype) {
			this.racetype = racetype;
		}
	}

	/**
	 * Distance range criteria used in searches
	 * @author galen
	 *
	 */
	protected class DistanceRangeCriteria {
		Long lowDistance;
		Long highDistance;
		
		/**
		 * @return the lowDistance
		 */
		public Long getLowDistance() {
			return lowDistance;
		}

		/**
		 * @return the highDistance
		 */
		public Long getHighDistance() {
			return highDistance;
		}

		DistanceRangeCriteria(Long lowDistance, Long highDistance) {
			this.lowDistance = lowDistance;
			this.highDistance = highDistance;
		}
	}	
	
	/**
	 * Distance string criteria used in searches
	 * @author galen
	 *
	 */
	protected class DistanceCriteria {
		String distance;
		
		/**
		 * @return the distance
		 */
		public String getDistance() {
			return distance;
		}

		DistanceCriteria(String distance) {
			this.distance = distance;
		}
	}
	
	/**
	 * Name criteria used in searches
	 * @author galen
	 *
	 */
	protected class NameCriteria {
		String name;
		
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		NameCriteria(String name) {
			this.name = name;
		}
	}
	
	/**
	 * Geospatial criteria used in searches
	 * @author galen
	 *
	 */
	protected class GeospatialCriteria {
		Double longitude;
		Double latitude;
		Double radius;
		
		/**
		 * @return the longitude
		 */
		public Double getLongitude() {
			return longitude;
		}

		/**
		 * @return the latitude
		 */
		public Double getLatitude() {
			return latitude;
		}

		/**
		 * @return the radius
		 */
		public Double getRadius() {
			return radius;
		}

		GeospatialCriteria(Double longitude, Double latitude, Double radius) {
			this.radius = radius;
			this.longitude = longitude;
			this.latitude = latitude;
		}
		
		GeospatialCriteria(Double longitude, Double latitude) {
			this.radius = DEFAULT_SEARCH_RADIUS;
			this.longitude = longitude;
			this.latitude = latitude;
		}
		
	}

	/**
	 * @return the racetypeCriteria
	 */
	public RacetypeCriteria getRacetypeCriteria() {
		return racetypeCriteria;
	}

	/**
	 * @return the distanceCriteria
	 */
	public DistanceCriteria getDistanceCriteria() {
		return distanceCriteria;
	}

	/**
	 * @return the distanceRangeCriteria
	 */
	public DistanceRangeCriteria getDistanceRangeCriteria() {
		return distanceRangeCriteria;
	}

	/**
	 * @return the nameCriteria
	 */
	public NameCriteria getNameCriteria() {
		return nameCriteria;
	}

	/**
	 * @return the geospatialCriteria
	 */
	public GeospatialCriteria getGeospatialCriteria() {
		return geospatialCriteria;
	}
}
