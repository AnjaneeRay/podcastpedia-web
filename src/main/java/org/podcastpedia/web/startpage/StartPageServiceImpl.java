package org.podcastpedia.web.startpage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.podcastpedia.common.domain.Episode;
import org.podcastpedia.common.domain.Podcast;
import org.podcastpedia.common.types.LanguageCode;
import org.podcastpedia.web.episodes.EpisodeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;


/**
 * This class implements the PodcastService interface to offer the database functionality when adding podcasts
 * 
 */
public class StartPageServiceImpl implements StartPageService {

   private static final Integer NUMBER_OF_PODCASTS_IN_CHART = 5;
   
   @Autowired
   private EpisodeDao episodeDao;
   
   @Autowired
   private StartPageDao startPageDao;
   
   @Cacheable(value="newestAndRecommendedPodcasts", key="#root.method.name")   
   public  List<Podcast> getLastUpdatedPodcasts() {
	   List<Podcast> newestPodcasts = startPageDao.getLastUpdatedPodcasts(NUMBER_OF_PODCASTS_IN_CHART);
	   for(Podcast p : newestPodcasts) {
		   Episode lastEpisode = episodeDao.getLastEpisodeForPodcast(p.getPodcastId());
		   p.setLastEpisode(lastEpisode);
	   }
	   
	   return newestPodcasts;
   }
   
   @Cacheable(value="newestAndRecommendedPodcasts", key="#root.method.name")   
   public  List<Podcast> getNewEntries() {
	   List<Podcast> newEntries = startPageDao.getNewEntries(NUMBER_OF_PODCASTS_IN_CHART);
	   for(Podcast p : newEntries) {
		   Episode lastEpisode = episodeDao.getLastEpisodeForPodcast(p.getPodcastId());
		   p.setLastEpisode(lastEpisode);
	   }
	   
	   return newEntries;
   }   
         
   @Cacheable(value="newestAndRecommendedPodcasts", key="T(java.lang.String).valueOf(#languageCode.code).concat('-').concat(#root.method.name)")    
   public  List<Podcast> getLastUpdatedPodcasts(LanguageCode languageCode) {
	   Map<String, Object> params = new HashMap<String, Object>();
	   params.put("lang", languageCode);
	   params.put("limit", NUMBER_OF_PODCASTS_IN_CHART);  
	   List<Podcast> newestPodcasts = startPageDao.getLastUpdatedPodcastsWithLanguage(params);
	   for(Podcast p : newestPodcasts) {
		   Episode lastEpisode = episodeDao.getLastEpisodeForPodcast(p.getPodcastId());
		   p.setLastEpisode(lastEpisode);
	   }
	   
	   return newestPodcasts;
   }   
   
   @Cacheable(value="newestAndRecommendedPodcasts", key="#root.method.name")
   public List<Podcast> getRecommendedPodcasts() {
	   List<Podcast> recommendedPodcasts = startPageDao.getRecommendedPodcasts(NUMBER_OF_PODCASTS_IN_CHART);
	   for(Podcast p : recommendedPodcasts) {
		   Episode lastEpisode = episodeDao.getLastEpisodeForPodcast(p.getPodcastId());
		   p.setLastEpisode(lastEpisode);
	   }
	   
	   return recommendedPodcasts;
   }	
   
	/**
	 * Returns the top rated podcasts - TODO should I hold this also in memory ????  
	 */
    @Cacheable(value="randomAndTopRatedPodcasts", key="#root.method.name")
	public List<Podcast> getTopRatedPodcasts(Integer numberOfPodcasts) {
		List<Podcast> response = startPageDao.getTopRatedPodcasts(numberOfPodcasts);
		for(Podcast p : response){
			p.setLastEpisode(episodeDao.getLastEpisodeForPodcast(p.getPodcastId()));
		}
		
		return response;
	}

	@Cacheable(value="randomAndTopRatedPodcasts", key="T(java.lang.String).valueOf(#languageCode.code).concat('-').concat(#root.method.name)")
	public List<Podcast> getTopRatedPodcastsWithLanguage(LanguageCode languageCode, int numberOfResults) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("languageCode", languageCode);
		params.put("numberOfResults", numberOfResults);
		
		List<Podcast> podcasts= startPageDao.getTopRatedPodcastsWithLanguage(params); 
		for(Podcast p : podcasts){
			p.setLastEpisode(episodeDao.getLastEpisodeForPodcast(p.getPodcastId()));
		}
		
		return podcasts; 
	}
	
	//TODO - optimize and get the last episode only when the feed is called 
	@Cacheable(value="randomAndTopRatedPodcasts", key="#root.method.name") 
	public List<Podcast> getRandomPodcasts(Integer numberOfPodcasts) {
		List<Podcast> randomPodcasts = startPageDao.getRandomPodcasts(numberOfPodcasts);
		for(Podcast p : randomPodcasts){
			p.setLastEpisode(episodeDao.getLastEpisodeForPodcast(p.getPodcastId()));
		}		
		return randomPodcasts;
	}

	public void setStartPageDao(StartPageDao startPageDao) {
		this.startPageDao = startPageDao;
	}
   	
}


