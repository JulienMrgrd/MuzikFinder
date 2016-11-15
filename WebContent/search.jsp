<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
<%@page import="interfaces.MFMusic"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<script src="js/jquery.min.js"></script>
<script src="js/js.cookie.min.js"></script>
<head>
	<meta charset="utf-8">
	<link rel="icon" href="images/favicon.png?2">
	<link rel="stylesheet" href="css/navbar.css">
	<link rel="stylesheet" href="css/bootstrap.min.css">
	<link rel="stylesheet" href="css/ladda-themeless.min.css">
	<link href="css/search.css" rel="stylesheet">
	<title>MuzikFinder</title>
</head>

<body>
	<div id="header"></div>
	
	<div class="container">
		<div class="container-fluid">
			<% if( !((boolean)request.getAttribute("success")) ){ %>
				<div id="errorMessageSearch" class="alert alert-danger fade in">
					<strong id="strongErrorMessageSearch"><%=request.getAttribute("message") %></strong>
				</div>
			<% } else {
					@SuppressWarnings("unchecked")
					Set<MFMusic> musics = (Set<MFMusic>) request.getAttribute("results");
					if(musics == null || musics.isEmpty()){ %>
						<div id="errorMessageSearch" class="alert alert-danger fade in">
							<strong id="strongErrorMessageSearch">No results...</strong>
						</div>
			<%		} else {  		
						String artist = (String) request.getAttribute("artist");
						String tags = (String) request.getAttribute("tags");
						if(artist!=null){
			%>
							<h4>Your research by artist "<%=artist %>" : </h4>
			<%			} else if(tags!=null){
							%>
							<h4>Results for "<%=tags%>" : </h4>
			<%			} else {  	%>
							<h4>Results for your music search : </h4>
			<% 			} %>
							<input id="searchId" type="hidden" name="searchId" 
								value="<%=request.getAttribute("searchId") %>"/>
						<div class="panel-group" id="panel-results">
						
			<%			for(MFMusic music : musics){
							String id = music.getTrackId();
							if(id==null) id="";
							String artistName = music.getArtistName();
							if(artistName==null) artistName="N/C";
							String albumName = music.getAlbumName();
							if(albumName==null) albumName="N/C";
							String trackName = music.getTrackName();
							if(trackName==null) trackName="N/C";
							String genre = music.getMusicGenre();
							if(genre==null) genre="N/C";
							
							String lyricsToDisplay = "";
							String regex = (String) request.getAttribute("tagsRegex");
							if(music.getLyrics()==null || music.getLyrics().getLyricsBody().isEmpty()){
								lyricsToDisplay = "No lyrics available...";
							} else if(regex==null || regex.isEmpty()){
								lyricsToDisplay = music.getLyrics().getLyricsBody();
							} else {
								lyricsToDisplay = music.getLyrics().getLyricsBody().replaceAll(regex, "<font color=\"red\">$1</font>");
							}
			%>
							<div class="panel panel-default">
								<div class="panel-heading">
									<a class="panel-title" data-toggle="collapse" data-parent="#panel-results" 
										href="#panel-element-<%=id%>"><%=artistName+" - "+trackName%></a>
									<div style="float: right;">
										<span class="badge"><i><%=genre%></i></span>
										<a id="<%=id%>" class="btn btn-success btn-outline btn-sm good"
											onclick="confirmGoodMusic(this)">Good !</a>
									</div>
								</div>
								<div id="panel-element-<%=id%>" class="panel-collapse collapse">
									<div class="panel-body">
										Album : <%=albumName %>
										<pre><%=lyricsToDisplay%></pre>
									</div>
								</div>
							</div>
			<%			} 	%>
						</div>
						<div id="showMoreResultsMessage" class="alert alert-danger fade in" style="display:none;">
							<strong>No results...</strong>
						</div>
						<button id="showMoreResults" class="btn moreResults ladda-button" data-spinner-color="#000" 
								data-style="expand-right" >Show more results >></button>
						
			<%		}
				}
			%>
				
		</div>
	</div>
		
	<div id="footer"></div>
</body>

<script> 
(function() { 
	var login = Cookies.get('MUZIKFINDERLOGIN');
	if(login==null) $("#header").load("htmls/header/headerNotConnected.html");
	else $("#header").load("htmls/header/headerConnected.html");
	
	$("#carousel").load("htmls/carousel.html");
	$("#footer").load("htmls/footer.html");
	
	//checks for the button click event
	$("#showMoreResults").click(function(e) {
		e.preventDefault();
		console.log("onClick");
		var laddaButton = Ladda.create(this);
		laddaButton.start();
		
		var searchId = $('input#searchId').val();
		$.ajax({
			type : "POST",
			url : "ShowMoreResultsServlet",
			data : {
				searchId : searchId,
				listMusicsAlreadyDisplayed : generateAlreadyDisplayedMusicsString()
			},
	
			//if received a response from the server
			success : function(data, textStatus, jqXHR) {
				console.log("success");
				laddaButton.stop();
				if (data.success) {
					$("#panel-results").append('<hr>');
					var results = $.parseJSON(data.results);
			      	$.each(results, function(i, item) {
			      		var genre;
			      		if(item.musicGenre==null || item.musicGenre=="") genre = "N/C";
			      		else genre = item.musicGenre;
			      		
			      		var goodButton = "";
			      		if(!$("#showMoreResults").hasClass( "goodMusicAlreadyClicked" )){
			      			goodButton = '<a id=\"'+item.trackId+'\" class=\"btn btn-success btn-outline btn-sm good\" onclick=\"confirmGoodMusic(this)\">Good !</a>';
			      		}
			      		
			      		var panel = '<div class="panel panel-default">'+
										'<div class="panel-heading">'+
											'<a class="panel-title" data-toggle="collapse" data-parent="#panel-results"'+
												'href="#panel-element-'+item.trackId+'">'+item.artistName+' - '+item.trackName+'</a>'+
											'<div style="float: right;">'+
												'<span class="badge" style="margin-right:5px;"><i>'+genre+'</i></span>'+goodButton+
											'</div>'+
										'</div>'+
										'<div id="panel-element-'+item.trackId+'" class="panel-collapse collapse">'+
											'<div class="panel-body">'+
												'Album : '+item.albumName+
												'<pre>'+item.lyrics.lyricsBody+'</pre>'+
											'</div>'+
										'</div>'+
									'</div>'
						$("#panel-results").append(panel);
			      	});
				} else { //display error message
					$("#showMoreResultsMessage").show();
				}
			},
	
			error : function(jqXHR, textStatus, errorThrown) {
				alert("No response from the server");
				laddaButton.stop();
			},
	
			always : function() {
				console.log("always");
				laddaButton.stop();
				$('.good').hide();
			}
		});
		return false;
	});
})();

function generateAlreadyDisplayedMusicsString(){
	var res = "";
	console.log($(".panel-title"));
	$('.panel-title').each(function() {
		res += "["+$(this).text()+"]";
    });
	return res;
}

function confirmGoodMusic(e){
	var idGoodMusic = e.id;
	console.log(idGoodMusic);
	
	$.ajax({
		type : "POST",
		url : "FindGoodMusicServlet",
		data : {
			idGoodMusic : idGoodMusic
		},

		//if received a response from the server
		success : function(data, textStatus, jqXHR) {
			console.log("success");
			if (data.success) {
				$('.good').hide();
				$("#"+idGoodMusic).show();
				$("#"+idGoodMusic).text("Thanks !");
				$("#"+idGoodMusic).css( 'pointer-events', 'none' );
				$("#"+idGoodMusic).css( 'disabled', 'true' );
				
				$("#showMoreResults").addClass('goodMusicAlreadyClicked'); // to know if we have to display a "Good !" button for next "see more results"
			} else { //display error message
				console.log("error on confirmGoodMusic")
			}
		}
	});
	return false;
}

</script>
</body>
</html>