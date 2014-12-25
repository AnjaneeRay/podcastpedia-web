package org.podcastpedia.web.api.podcast;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.podcastpedia.common.domain.Episode;
import org.podcastpedia.common.domain.Podcast;
import org.podcastpedia.common.exception.BusinessException;
import org.podcastpedia.web.episodes.EpisodeService;
import org.podcastpedia.web.podcasts.PodcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/podcasts")
public class PodcastResource {

	@Autowired
	private PodcastService podcastService;
	
	@Autowired
	private EpisodeService episodeService;
	
	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getPodcastById(@PathParam("id") Integer podcastId) throws BusinessException {
		Podcast podcast = podcastService.getPodcastById(podcastId);
		return Response.status(200)
				.entity(podcast)
				.build();
	}
	
	@GET
	@Path("names/{name}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getPodcastByName(@PathParam("name") String name) throws BusinessException {
		Podcast podcast = podcastService.getPodcastForIdentifier(name);
		return Response.status(200)
				.entity(podcast)
				.build();
	}
	
	@GET
	@Path("{podcastId}/episodes/latest-episodes")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<Episode> getLatestEpisodes(@PathParam("podcastId") Integer podcastId, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit) throws BusinessException {
		List<Episode> latestEpisodesPaginated = episodeService.getLatestEpisodes(podcastId, offset, limit);
		
		return latestEpisodesPaginated;
	}	
	
	@GET
	@Path("{podcastId}/episodes/{episodeId}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getEpisode(@PathParam("podcastId") Integer podcastId, @PathParam("episodeId") Integer episodeId) throws BusinessException {
		Episode episode = episodeService.getEpisodeById(podcastId, episodeId);
		return Response.status(200)
				.entity(episode)
				.build();
	}	
}
