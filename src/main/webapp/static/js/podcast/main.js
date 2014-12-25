$(function (){
	var moreEpisodesBtn = $('#more-episodes');	
	moreEpisodesBtn.on('click', function(){

		var $lastEpisodes=$('.results_list');
		var podcastId=$('#sub_podcastId').val();
		
		var offsetDataId=$('#offset-data-id');
		var offset=parseInt(offsetDataId.val());
		var limit = 5; 
		
		$.ajax({
			headers: {"Accept":"application/json"},
			type: 'GET',
			url: '/api/podcasts/'+ podcastId + '/episodes/latest-episodes?offset='+ offset + '&limit=' + limit,
			success: function(episodes){
				if(episodes.length == 0){
					moreEpisodesBtn.attr("disabled","disabled");
				} else {
					var newoffset = offset + 5;
					offsetDataId.val(newoffset);//update offset hidden field value 
					$.each(episodes, function(i, episode){
						var episodeDiv = buildEpisodeDiv(episode, i, offset);					
						$lastEpisodes.append(episodeDiv).fadeIn(800);										
					});
					
					bindDynamicPlaying();
					bindDynamicSocialSharing();
					bindDynamicSharringCurrentEpisode();
					console.log("success", episodes);					
				}
			} 
		});		
		
	});

	function buildEpisodeDiv(episode, i, offset){
		var episodeDiv=""
			+ "<div class='bg_color shadowy item_wrapper'>"		
			+ "<div class='title-and-pub-date'>";
			if(episode.mediaType == 'Audio'){
				episodeDiv += "<div class='icon-audio-episode'></div>";
			} else {
				episodeDiv += "<div class='icon-video-episode'></div>";
			}
			episodeDiv += "<a href='#' class='item_title'>" + episode.title + '</a>';
			
			episodeDiv +='<div class="pub_date">';
			episodeDiv += episode.publicationDate;
			if(episode.isNew === 1){
				episodeDiv +='<span class="ep_is_new"><spring:message code="new"/></span>';
			}
			episodeDiv +='</div>';
				
			episodeDiv += '<div class="clear"></div>';				
			episodeDiv +='</div>';					
			episodeDiv +='<hr>';
			episodeDiv +='<div class="ep_desc">';
			episodeDiv +='<a href="#" class="item_desc">';
			episodeDiv += episode.description.substring(0,280);
			episodeDiv +='</a>';
			episodeDiv +='</div>';
			  		
			episodeDiv +='<div class="ep_desc_bigger">';
			episodeDiv +='<a href="#" class="item_desc">';
			episodeDiv += episode.description.substring(0,600);
			episodeDiv +='</a>';
			episodeDiv +='</div>';
			
			episodeDiv +='<div class="clear"></div>';
			
			episodeDiv +='<div class="not_shown">';
			episodeDiv +='<div id=mediaspace'+parseInt(offset+i) +'>Flashplayer not supported</div>';
			
			
			//change with requireJs sometime soon
			episodeDiv +="<script type='text/javascript'>";
			if(episode.mediaType == 'Audio'){					
				episodeDiv +="jwplayer(mediaspace"+ parseInt(offset+i) + ").setup({'controlbar': 'bottom',  'width': '100%',   'aspectratio': '16:5', 'file': '" + episode.mediaUrl+ "'});";					
			} else {
				episodeDiv +="jwplayer(mediaspace"+ parseInt(offset+i) + ").setup({'controlbar': 'bottom',  'width': '100%',   'aspectratio': '16:9', 'file': '" + episode.mediaUrl+ "'});";
			}	
			episodeDiv +="</script>";
			episodeDiv +='</div>';				
		
			episodeDiv +='<div class="social_and_download">';
			episodeDiv +='<a href="#' + parseInt(2*(offset+i)) + '" class="icon-play-episode btn-share">Play </a>';
			episodeDiv +='<a href="#' + parseInt(2*(offset+i)+1) + '" class="icon-share-episode btn-share">Share </a>';
			episodeDiv+= '<a class="icon-download-ep btn-share" href="'+ episode.mediaUrl +'" target="_blank">';
			episodeDiv+= 'download';
			episodeDiv +='</a>';
			episodeDiv +='<span class="item_url">http://www.podcastpedia.org/podcasts/' + episode.podcastId + '/' + episode.podcast.titleInUrl + '/episodes/' + episode.episodeId + '/' + episode.titleInUrl;
			episodeDiv +='</div>';	
			
			return episodeDiv;
	}	
	
	
	bindDynamicPlaying();
	//delegate again...
	function bindDynamicPlaying(){
		$('.item_wrapper').delegate('.icon-play-episode', 'click',  function () {
			var currentDiv=$(this).closest("div.item_wrapper");
			var playerDiv = currentDiv.find("div.not_shown")
			playerDiv.removeClass('not_shown').addClass('shown');
			var jwpId = currentDiv.find("div.jwplayer ").attr("id")
			
			// if we load the player, the div containing the player will set the distance to the share, play and download buttons
			currentDiv.find("div.ep_desc").css("margin-bottom","5px");
			
			//and also widen the distance to the share buttonsif they are available
			var shareButtonsDiv = currentDiv.find("div.share_buttons");
			if(shareButtonsDiv.length > 0){
				playerDiv.css("margin-bottom","75px");
			}
			
			$('html, body').animate({
				scrollTop: currentDiv.offset().top
			}, 150);
			if(typeof jwpId != 'undefined'){
				jwplayer(jwpId).play();	
			}			
		 });
	}

	
	bindDynamicSocialSharing();	
	function bindDynamicSocialSharing(){
		$('.item_wrapper').on('click', '.icon-share-episode', function (e) {
			var currentDiv=$(this).closest("div.item_wrapper");
			var epUrl= currentDiv.find("span.item_url").text();
			
			//verify existence of player to manipulate distance to div containing sharing buttons
			var playerDiv = currentDiv.find("div.shown");
			if(playerDiv.length > 0){
				playerDiv.css("margin-bottom","75px");
			} else {
				//player not shown widen the distance to insert the sharing buttons 
				currentDiv.find("div.ep_desc").css("margin-bottom","75px");
				currentDiv.find("div.ep_desc_bigger").css("margin-bottom","75px");	
				currentDiv.find("div.pod_desc").css("margin-bottom","55px");
				currentDiv.find("div.pod_desc_bigger").css("margin-bottom","55px");
			}
			//the share button is being replaced with social media buttons
			$(e.target).remove();
			var socialAndDownload = currentDiv.find("div.social_and_download");
			socialAndDownload.prepend(
				 "<div class='share_buttons'>" 
					+ "<div class='fb_like'>"
					+ " <div class='fb-like' data-href='"+ epUrl + " data-send='false' data-layout='button_count' data-width='70' data-height='72'></div>"
					+ "</div> "						
					+ "<div class='twitter_share'> "
					+ " <a href='https://twitter.com/share' class='twitter-share-button' data-url='" + epUrl + "' data-hashtags='podcastpedia'>Tweet</a>"	
					+ "</div>"
					+ "<div class='google_share'> "					
					+ "  <div class='g-plusone' data-size='medium' data-annotation='bubble' data-href='" + epUrl + "'></div>"						
				    + "</div>"
			    + "</div>"
			);
			
			loadTwitter();
			loadFacebook();
			loadGooglePlus();
			
		 });		
	}

	bindDynamicSharringCurrentEpisode();
	function bindDynamicSharringCurrentEpisode(){
		$('#social_and_download_curr_ep').on('click', '.icon-share-episode', function (e) {
			var currentDiv=$(this).closest("div#social_and_download_curr_ep");
			var epUrl= currentDiv.find("span.item_url_ep").text();
			
			//widen the distance to insert the sharing buttons 
			$('#current_episode').find(".ep_desc").css("margin-bottom","75px");
			$('#current_episode').find(".ep_desc_bigger").css("margin-bottom","75px");	

			//the share button is being replaced with social media buttons
			$(e.target).remove();
			currentDiv.prepend(
				 "<div class='share_buttons'>" 
					+ "<div class='fb_like'>"
					+ " <div class='fb-like' data-href='"+ epUrl + " data-send='false' data-layout='button_count' data-width='70' data-height='72'></div>"
					+ "</div> "						
					+ "<div class='twitter_share'> "
					+ " <a href='https://twitter.com/share' class='twitter-share-button' data-url='" + epUrl + "' data-hashtags='podcastpedia'>Tweet</a>"	
					+ "</div>"
					+ "<div class='google_share'> "					
					+ "  <div class='g-plusone' data-size='medium' data-annotation='bubble' data-href='" + epUrl + "'></div>"						
				    + "</div>"
			    + "</div>"
			);
			
			loadTwitter();
			loadFacebook();
			loadGooglePlus();
			
		 });		
		
	}
				
	function loadTwitter()
	{
	    if (typeof (twttr) != 'undefined'){
	        twttr.widgets.load();
	    } else {
	        $.getScript('http://platform.twitter.com/widgets.js');
	    }
	}		
	
	function loadFacebook()
	{
	    if (typeof (FB) != 'undefined') {
	        FB.init({ status: true, cookie: true, xfbml: true });
	    } else {
	        $.getScript("http://connect.facebook.net/en_US/all.js#xfbml=1", function () {
	            FB.init({ status: true, cookie: true, xfbml: true });
	        });
	    }
	}		
	
	function loadGooglePlus()
	{
		var gbuttons = $(".g-plusone");
		if (gbuttons.length > 0) {
		    if (typeof (gapi) != 'undefined') {
		        gbuttons.each(function () {
		            gapi.plusone.render($(this).get(0));
		        });
		    } else {
		        $.getScript('https://apis.google.com/js/plusone.js');
		    }
		}
	}	

});

