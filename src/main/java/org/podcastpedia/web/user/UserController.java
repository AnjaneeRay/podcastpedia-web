package org.podcastpedia.web.user;

import java.util.List;

import org.apache.log4j.Logger;
import org.podcastpedia.common.domain.Episode;
import org.podcastpedia.common.domain.Podcast;
import org.podcastpedia.web.searching.SearchData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("")
public class UserController {
	
	protected static Logger LOG = Logger.getLogger(UserController.class);
	
	@Autowired
	UserService userService;
	
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

    @RequestMapping(value="users/subscriptions", method=RequestMethod.GET)
    public String getPodcastSubscriptions(ModelMap model) {

        LOG.debug("------ Returns the podcasts the user has subscribed to ------");
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.debug("got request from " + userDetails.getUsername() + " and password "+ userDetails.getPassword());

        List<Podcast> subscriptions = userService.getSubscriptions(userDetails.getUsername());
        model.addAttribute("subscriptions", subscriptions);

        return "podcast_subscriptions_def";
    }
	  
    @RequestMapping(value="users/subscriptions/latest-episodes", method=RequestMethod.GET)
    public String getLatestEpisodesFromPodcastSubscriptions(ModelMap model) {

        LOG.debug("------ Returns the podcasts the user has subscribed to ------");
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Episode> latestEpisodes = userService.getLatestEpisodesFromSubscriptions(userDetails.getUsername());
        model.addAttribute("latestEpisodes", latestEpisodes);

        return "latest_episodes_for_podcast_subscriptions_def";
    }

}
