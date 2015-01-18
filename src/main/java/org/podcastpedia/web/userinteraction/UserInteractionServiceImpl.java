package org.podcastpedia.web.userinteraction;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.podcastpedia.common.domain.Rating;
import org.podcastpedia.common.domain.Subscription;
import org.podcastpedia.web.podcasts.PodcastDao;
import org.podcastpedia.web.suggestpodcast.SuggestedPodcast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;

public class UserInteractionServiceImpl implements UserInteractionService {

	@Autowired
	private UserInteractionDao userInteractionDao;

	@Autowired
	private PodcastDao podcastDao;

	public void setUserInteractionDao(UserInteractionDao userInteractionDao) {
		this.userInteractionDao = userInteractionDao;
	}

	public void addSuggestedPodcast(SuggestedPodcast addPodcastFormData) {

		buildMetadataLine(addPodcastFormData);
		addPodcastFormData.setInsertionDate(new Date());

		// call the dao service
		userInteractionDao.insertSuggestedPodcast(addPodcastFormData);

	}

	/**
	 * Creates line with podcast url and podcast metadata
	 * 
	 * @param addPodcastFormData
	 */
	private void buildMetadataLine(SuggestedPodcast addPodcastFormData) {

		StringBuffer metadataLine = new StringBuffer();
		metadataLine.append(addPodcastFormData.getFeedUrl());
		metadataLine.append("; ");

		metadataLine.append(addPodcastFormData.getIdentifier());
		metadataLine.append("; ");

		if (addPodcastFormData.getCategories() != null) {
			// build categories String
			String categoriesStr = "";
			for (String s : addPodcastFormData.getCategories()) {
				categoriesStr += s + ", ";
			}
			// we add the categories separated by comma, and remove the last
			// comma and space
			metadataLine.append(categoriesStr.subSequence(0,
					categoriesStr.length() - 2));
			metadataLine.append("; ");
		}

		// add language code
		metadataLine.append(addPodcastFormData.getLanguageCode().toString());
		metadataLine.append("; ");

		// add media type
		metadataLine.append(addPodcastFormData.getMediaType().toString());
		metadataLine.append("; ");

		// add frequency type
		metadataLine.append(addPodcastFormData.getUpdateFrequency().toString());
		metadataLine.append("; ");

		// add tags/keywords
		metadataLine.append(addPodcastFormData.getSuggestedTags());
		metadataLine.append("; ");

		// add facebook fan page
		metadataLine.append(addPodcastFormData.getFacebookPage());
		metadataLine.append("; ");

		// add twitter fan page
		metadataLine.append(addPodcastFormData.getTwitterPage());
		metadataLine.append("; ");

		// add twitter fan page
		metadataLine.append(addPodcastFormData.getGplusPage());
		metadataLine.append("; ");

		// add name of the visitor that submitted the podcast
		metadataLine.append(addPodcastFormData.getName());
		metadataLine.append("; ");

		// add email of the visitor who suggested the podcast, so that we sent
		// him a thank you email if added - ahaaa
		metadataLine.append(addPodcastFormData.getEmail());

		// set the metadataLine
		addPodcastFormData.setMetadataLine(metadataLine.toString());

	}
}
