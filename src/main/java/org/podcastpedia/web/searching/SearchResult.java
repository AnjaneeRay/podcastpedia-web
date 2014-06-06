package org.podcastpedia.web.searching;


import java.io.Serializable;
import java.util.List;

import org.podcastpedia.common.domain.Episode;
import org.podcastpedia.common.domain.Podcast;

/**
 * Encapsulates either podcasts or episodes found when executing a search.  
 *  
 *
 */
public class SearchResult implements Serializable{

	private static final long serialVersionUID = 8066923834246645687L;

	/**List of podcasts found for search criteria */
	private List<Podcast> podcasts;
	
	/** List of episodes found for search criteria */ 
	private List<Episode> episodes; 
	
	private Integer numberOfItemsFound;
	private Integer numberOfItemsPerPage;
	private Integer currentPage;
	private Integer numberOfPages;
	
	/** placeholder for relative path query string to be used in subsequent calls in pagination */
	private String queryString;

	/** queryText used in Boolean mode to pass it further to the next call*/
	private String booleanQueryText;
	
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	public String getBooleanQueryText() {
		return booleanQueryText;
	}
	public void setBooleanQueryText(String booleanQueryText) {
		this.booleanQueryText = booleanQueryText;
	}
	public int getNumberOfPages() {
		return numberOfPages;
	}
	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}
	public List<Episode> getEpisodes() {
		return episodes;
	}
	public void setEpisodes(List<Episode> episodes) {
		this.episodes = episodes;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPageToDisplay) {
		this.currentPage = currentPageToDisplay;
	}
	public int getNumberOfItemsPerPage() {
		return numberOfItemsPerPage;
	}
	public void setNumberOfItemsPerPage(Integer numberOfItemsPerPage) {
		this.numberOfItemsPerPage = numberOfItemsPerPage;
	}
	public List<Podcast> getPodcasts() {
		return podcasts;
	}
	public void setPodcasts(List<Podcast> podcasts) {
		this.podcasts = podcasts;
	}
	public int getNumberOfItemsFound() {
		return numberOfItemsFound;
	}
	public void setNumberOfItemsFound(Integer numberOfItemsFound) {
		this.numberOfItemsFound = numberOfItemsFound;
	}
	
}
