package org.podcastpedia.web.podcasts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.podcastpedia.common.domain.Episode;
import org.podcastpedia.common.domain.Podcast;
import org.podcastpedia.common.exception.BusinessException;
import org.podcastpedia.common.types.ErrorCodeType;
import org.podcastpedia.web.searching.SearchData;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;



/**
 * Annotation-driven controller that handles requests to display podcasts in different forms.
 * 
 *
 */
@Controller
@RequestMapping("/podcasts") // it will handle all requests having "podcasts" in it
public class PodcastController {

	protected static Logger LOG = Logger.getLogger(PodcastController.class);
	
	@Autowired
	private PodcastService podcastService;	
	
	/**
	 * Add an empty searchData object to the model 
	 */
	@ModelAttribute
	public void addDataToModel(ModelMap model){
		SearchData dataForSearchBar = new SearchData();
		dataForSearchBar.setSearchMode("natural");
		dataForSearchBar.setCurrentPage(1);
		dataForSearchBar.setQueryText(null);
		dataForSearchBar.setNumberResultsPerPage(10);
		model.put("advancedSearchData", dataForSearchBar);
	}	
	
	    	  
	  /**
	   * Custom handler for displaying a podcast.
	   * 
	   * @param podcastId
	   * @param episode
	   * @return
	 * @throws BusinessException 
	   */
	  @RequestMapping(value="{podcastId}/*", method=RequestMethod.GET)
	  public String getPodcastDetails(@PathVariable("podcastId") int podcastId,
			  						 ModelMap model) throws BusinessException{
		  
		  	LOG.debug("------ getPodcastDetails : Received request to show details for podcast id "	+ podcastId + " ------");
		  	
		  	Podcast  podcast = podcastService.getPodcastById(podcastId);
		  			  			  	
		  	//add the last episodes to be displayed under the podcast metadata
		  	List<Episode> lastEpisodes = null;
		  	if(podcast.getEpisodes().size() > 5){
		  		lastEpisodes = podcast.getEpisodes().subList(0, 5);
		  	} else {
		  		lastEpisodes = podcast.getEpisodes(); 
		  	}
		  	model.addAttribute("lastEpisodes", lastEpisodes);	  		
		  	model.addAttribute("nr_divs_with_ratings", lastEpisodes.size());	
			if(podcast.getRating() == null) podcast.setRating(10f);
			model.addAttribute("roundedRatingScore", Math.round(podcast.getRating()));
			model.addAttribute("podcast", podcast);
			
			return "m_podcastDetails";						
	  }
	  
	  		   			  	  
	  @ExceptionHandler({NoSuchRequestHandlingMethodException.class, ConversionNotSupportedException.class})
	  @ResponseStatus(value = HttpStatus.NOT_FOUND)	 	  
	  public String handleResourceNotFound(){
		  return "resourceNotFound";
	  }
	  
	  @ExceptionHandler(BusinessException.class)
	  @ResponseStatus(value = HttpStatus.NOT_FOUND)	  
	  public String handleBusinessException(BusinessException ex, HttpServletRequest request){
		  if(ex.getErrorCode() == ErrorCodeType.PODCAST_NOT_FOUND){
			 return "error_podcast_not_found_in_DB";
		  }  else {
		     return "resourceNotFound";			  
		  } 	  
	  }
	  
		/*** Diverse setters used for injection ***/
		public void setPodcastService(PodcastService podcastService) {
			this.podcastService = podcastService;
		}	
}
