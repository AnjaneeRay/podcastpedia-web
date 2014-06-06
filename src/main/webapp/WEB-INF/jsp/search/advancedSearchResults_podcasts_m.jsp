<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<div id="search_feed_link">
	<c:url value="/static/images/feed-icon-14x14.png" var="urlFeedPic"/>
	<c:url value="/static/images/atomfeed14.png" var="urlFeedPicAtom"/>
	<c:url value="/feeds/search/podcasts.rss?${queryString}&amp;currentPage=${advancedSearchResult.currentPage}" var="rssURL"/> 
	<c:url value="/feeds/search/podcasts.atom?${queryString}&amp;currentPage=${advancedSearchResult.currentPage}" var="atomURL"/>
	<spring:message code="search.subscribe_via_feed"/> 
	<a href="${atomURL}" target="_blank" class="icon-feed-atom">Atom</a>
	<a href="${rssURL}" target="_blank" class="icon-feed-rss">RSS</a>

</div>	
<div class="clear"></div>	
	
<div class="results_list">
	<c:forEach items="${advancedSearchResult.podcasts}" var="podcast" varStatus="loop"> 
		<div class="bg_color shadowy item_wrapper">	
	    	<div class="title-and-pub-date">
				<c:choose>
					<c:when test="${podcast.mediaType == 'Audio'}">
						<div class="icon-audio-episode"></div>					
					</c:when>
					<c:otherwise>
						<div class="icon-video-episode"></div>		
					</c:otherwise>
				</c:choose>			
				<c:choose>
					<c:when test="${podcast.identifier == null}">
						<c:set var="urlPodcast" value="/podcasts/${podcast.podcastId}/${podcast.titleInUrl}"/>
					</c:when>
					<c:otherwise>
						<c:set var="urlPodcast" value="/${podcast.identifier}"/>
					</c:otherwise>
				</c:choose>													 						    				    	
				<a class="item_title" href="${urlPodcast}">				
					<c:out value="${podcast.title}"/>
				</a>
				<div class="pub_date_media_type">
					<div class="pub_date">
						<fmt:formatDate pattern="yyyy-MM-dd" value="${podcast.publicationDate}" />
					</div>	
				</div>	
			<div class="clear"></div>
			</div>
			<hr>						
			<div class="pod_desc">		
				<a class="item_desc" href="${urlPodcast}">						
					${fn:substring(podcast.description,0,320)}
				</a> 					
			</div>	
			<div class="clear"></div>
			<div class="social_and_download">
				<a href="#${loop.index}" class="icon-share-podcast btn-share">Share </a>
				<c:choose>
					<c:when test="${podcast.identifier == null}">
						<c:set var="podcast_link" value="http://www.podcastpedia.org/podcasts/${podcast.podcastId}/${podcast.titleInUrl}"/>
					</c:when>
					<c:otherwise>
						<c:set var="podcast_link" value="http://www.podcastpedia.org/${podcast.identifier}"/>
					</c:otherwise>
				</c:choose>									
				<span class="item_url">${podcast_link}</span>							
			</div>	
			<div class="clear"></div>																																			 														
		</div>
	</c:forEach>
</div>	
	
<div class="pagination pagination_bottom">
	<%@ include file="pagination_page_podcasts.jsp" %>
</div>	
<div class="clear"></div>	

<!-- javascript libraries required -->
<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
<!-- include loading dynamic player page -->
<%@ include file="/WEB-INF/jsp/common/load_player_dynamically.jsp" %>
<!-- dynamic social share -->
<%@ include file="/WEB-INF/jsp/common/social_share_dynamically_pods.jsp" %>