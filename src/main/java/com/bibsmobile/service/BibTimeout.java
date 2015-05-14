/**
 * 
 */
package com.bibsmobile.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;



/**
 * Some pretentious asshole ported us to ehcache so we could store bib timeout in a centralized location
 * and have mortality in our data.
 * @author galen
 *
 */
public class BibTimeout {
	/**
	 * Internal cache creation method with ehcache.
	 * @param position
	 * @return
	 */
	private Cache createCacheForPosition(int position) {
		CacheConfiguration cacheConfiguration = new CacheConfiguration("timeout" + String.valueOf(position), 0)
		.eternal(true);
		Cache cache = new Cache(cacheConfiguration);
		CacheManager cm = CacheManager.getInstance();
		cm.addCache(cache);
		return cache;
	}
	/**
	 * Get timeout by split position and bib number.
	 * Start = 0, Finish = 1, Split n = n-1 (Split 1 = 2, etc)
	 * @param position Position of timer to get
	 * @param bib Bib number
	 * @return Timeout if exists, null if not yet present in cache.
	 */
	public Long getTimeout(int position, long bib) {
		Cache timerCache = CacheManager.getInstance().getCache("timeout"+String.valueOf(position));
		if(timerCache == null) {
			return null;
		}
		Element timeout = timerCache.get(bib);
		return timeout == null ? null : (Long) timeout.getObjectValue();
	}
	/**
	 * Set timeout for a bib by position, bib number and epoch time.
	 * Start = 0, Finish = 1, Split n = n-1 (Split 1 = 2, etc)
	 * @param position 
	 * @param bib
	 * @param time
	 */
	public void setTimeout(int position, long bib, long time) {
		Cache timerCache = CacheManager.getInstance().getCache("timeout" + String.valueOf(position));
		if(timerCache == null) {
			timerCache = createCacheForPosition(position);
		}
		timerCache.put(new Element(bib, time));
	}
	/**
	 * Clear the timeout cache for a bib by split position.
	 * Start = 0, Finish = 1, Split n = n-1 (Split 1 = 2, etc)
	 * @param bib
	 * @param position
	 */
	public void clearBib(long bib, int position) {
		Cache timerCache = CacheManager.getInstance().getCache("timeout" + String.valueOf(position));
		if(timerCache != null) {
			timerCache.remove(bib);
		}
	}
	/**
	 * Clear the timeout cache for a bib in all split/start positions.
	 * @param bib
	 */
	public void clearBib(long bib) {
		for(String name : CacheManager.getInstance().getCacheNames()) {
			if(name.contains("timeout")) {
				CacheManager.getInstance().getCache(name).remove(bib);
			}
		}
	}
}
