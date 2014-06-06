package org.podcastpedia.web.searching;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.podcastpedia.common.controllers.propertyeditors.MediaTypeEditor;
import org.podcastpedia.common.domain.Category;
import org.podcastpedia.common.types.LanguageCode;
import org.podcastpedia.common.types.MediaType;
import org.podcastpedia.common.types.OrderByOption;
import org.podcastpedia.common.types.SearchModeType;
import org.podcastpedia.web.categories.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Annotation-driven controller that handles searching for podcasts.
 * 
 * "advancedSearchData" is an object passed along the session to navigate from
 * search bar to advanced search results and from one page result to the other.
 * 
 * @author Ama
 * 
 */
@Controller
@RequestMapping("/search")
public class SearchController {

	protected static Logger LOG = Logger.getLogger(SearchController.class);

	@Autowired
	private SearchService searchService;

	@Autowired
	private CategoryService categoryService;

	/**
	 * If the users decides to write "http://localhost:8080/search/" into the
	 * browser she should get redirected to the advanced search formular
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String redirectToAdvancedSearch() {
		return "redirect:/search/advanced_search";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {

		binder.registerCustomEditor(MediaType.class, new MediaTypeEditor(
				MediaType.class));
	}

	@RequestMapping(value = "/advanced_search", method = RequestMethod.GET)
	public String prepareFormForAdvancedSearch(
			@RequestParam(value = "noResultsFound", required = false) Boolean noResultsFound,
			SearchData advancedSearchData, Model model) {

		LOG.debug("------ prepareFormForAdvancedSearch : Received request to show advanced search form -----");

		SearchData formSearchData = new SearchData();
		formSearchData.setNumberResultsPerPage(10);
		formSearchData.setSearchMode("natural");
		formSearchData.setCurrentPage(1);

		List<Category> categoriesThatHavePodcasts = categoryService
				.getCategoriesOrderedByNoOfPodcasts();

		model.addAttribute("allCategories", categoriesThatHavePodcasts);
		model.addAttribute("isAdvancedSearchPage", true);
		model.addAttribute("mediaTypes", MediaType.values());
		model.addAttribute("languageCodes", LanguageCode.values());
		model.addAttribute("orderByOptions", OrderByOption.values());

		// it could be redirected here if no results are found and a proper
		// message should be displayed to try again
		if (null != noResultsFound && noResultsFound) {
			model.addAttribute("noResultsFound", noResultsFound);
		}

		// set radio button checked by default for searchInNaturalMode
		formSearchData.setSearchMode(SearchModeType.NATURAL_MODE.getValue());

		// set default podcasts the default target to look in
		formSearchData.setSearchTarget("episodes");

		model.addAttribute("advancedSearchData", formSearchData);

		return "m_advancedSearchForm";

	}

	/**
	 * Returns results from the advanced search form, from search form under the
	 * menu in start page, from categories or from tags AND from links to
	 * next/previous page and so on.
	 * 
	 * @param numberOfResults
	 * @param numberOfPages
	 * @param advancedSearchData
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/advanced_search/results", method = RequestMethod.GET)
	public ModelAndView getAdvancedSearchResults(
			@ModelAttribute("advancedSearchData") SearchData advancedSearchData,
			BindingResult bindingResult, Model model,
			HttpServletRequest httpRequest) throws UnsupportedEncodingException {

		LOG.debug("------ getAdvancedSearchResults : get SEARCH RESULTS based on advanced search form -----");

		if (advancedSearchData.getSearchTarget() == null)
			advancedSearchData.setSearchTarget("episodes");
		SearchResult results = searchService
				.getResultsForSearchCriteria(advancedSearchData);

		String redirectUrl = null;
		String tilesDef = null;
		ModelAndView mv = null;

		// no results found
		if (results.getNumberOfItemsFound() < 1) {
			bindingResult.rejectValue("queryText", "notFound", "not found");
			redirectUrl = "/search/advanced_search?noResultsFound=true";
		} else if (results.getNumberOfItemsFound() > 1) {
			// more than one result - prepare for pagination
			String query = httpRequest.getQueryString();
			query = query.substring(0, query.lastIndexOf("&currentPage="));
			if (advancedSearchData.getNrOfResults() == null) {
				query += "&nrOfResults=" + results.getNumberOfItemsFound()
						+ "&nrResultPages=" + results.getNumberOfPages();
			}
			model.addAttribute("queryString", query.replaceAll("&", "&amp;"));
			model.addAttribute("numberOfResults",
					results.getNumberOfItemsFound());
			model.addAttribute("numberOfPages", results.getNumberOfPages());
			model.addAttribute("advancedSearchResult", results);
			if (advancedSearchData.getSearchTarget().equals("podcasts")) {
				model.addAttribute("nr_divs_with_ratings", results
						.getPodcasts().size());

				tilesDef = "m_advancedSearchResults_podcasts_def";
			} else {
				model.addAttribute("nr_divs_with_ratings", results
						.getEpisodes().size());

				tilesDef = "m_advancedSearchResults_episodes_def";
			}
		} else {
			// exactly one result found (either podcast or episode)
			if (advancedSearchData.getSearchTarget().equals("podcasts")) {
				redirectUrl = "/podcasts/"
						+ results.getPodcasts().get(0).getPodcastId() + "/"
						+ results.getPodcasts().get(0).getTitleInUrl();
			} else {
				redirectUrl = "/podcasts/"
						+ results.getEpisodes().get(0).getPodcastId() + "/"
						+ results.getEpisodes().get(0).getPodcast().getTitleInUrl()
						+ "/episodes/"
						+ results.getEpisodes().get(0).getEpisodeId() + "/"
						+ results.getEpisodes().get(0).getTitleInUrl();
			}
		}

		if (tilesDef != null) {
			mv = new ModelAndView();
			mv.setViewName(tilesDef);
		} else {
			// must be a redirect
			RedirectView rv = new RedirectView();
			rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
			rv.setUrl(redirectUrl);
			mv = new ModelAndView(rv);
		}

		return mv;

	}

	public List<Integer> transformStringListToIntegerList(List<String> list) {
		List<Integer> intList = new ArrayList<Integer>();
		for (String s : list) {
			intList.add(Integer.parseInt(s));
		}

		return intList;
	}

}
