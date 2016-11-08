<%@page import="interfaces.MFMusic"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<style>
.container {
	margin-top: 100px;
}
.panel-group {
	margin-top: 20px;
	line-height:30px;
}

.badge{
	float: right;
}

@media (min-width: 750px) {
	.panel-group {
		width: 60%;
	}
}

@media (max-width: 750px) {
	.panel-group {
		width: 80%;
	}
}
</style>

<head>
	<meta charset="utf-8">
	<link rel="icon" href="images/favicon.png?2">
	<link href="css/bootstrap.min.css" rel="stylesheet">
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
					List<MFMusic> musics = (List<MFMusic>) request.getAttribute("results");
					if(musics == null || musics.isEmpty()){ %>
						<div id="errorMessageSearch" class="alert alert-danger fade in">
							<strong id="strongErrorMessageSearch">Aucun résultat...</strong>
						</div>
			<%		} else {  		%>
						<h4>Résultats de votre recherche : </h4>
						<div class="panel-group" id="panel-results">
						
			<%			for(MFMusic music : musics){
							String id = music.getTrackId();
							if(id==null) id="";
							String artistName = music.getArtistName();
							if(artistName==null) artistName="";
							String trackName = music.getTrackName();
							if(trackName==null) trackName="";
							String genre = music.getMusicGenre();
							if(genre==null) genre="";
							
							String lyricsToDisplay;
							String regex = (String) request.getAttribute("tagsRegex");
							if(music.getLyrics()==null || music.getLyrics().getLyricsBody().isEmpty()){
								lyricsToDisplay = "Pas de lyrics disponibles...";
							} else if(regex==null || regex.isEmpty()){
								lyricsToDisplay = music.getLyrics().getLyricsBody();
							} else {
								lyricsToDisplay = music.getLyrics().getLyricsBody().replaceAll(regex, "<font color=\"red\">$1</font>");
							}
			%>
							<div class="panel panel-default">
								<div class="panel-heading">
									<a class="panel-title" data-toggle="collapse" data-parent="#panel-results" 
										href="#panel-element-<%=id%>"><%=artistName+" - "+trackName %></a>
									<span class="badge"><i>Pop</i></span>
								</div>
								<div id="panel-element-<%=id%>" class="panel-collapse collapse">
									<div class="panel-body"><pre><%=lyricsToDisplay%></pre></div>
								</div>
							</div>
			<%			} 	%>
						</div>
			<%		}
				}
			%>
				
		</div>
	</div>
		
	<div id="footer"></div>
</body>

<script src="js/jquery.min.js"></script>
<% if(request.getSession().getAttribute("acc")==null){ %>
<script> (function() { $("#header").load("htmls/header/headerNotConnected.html"); })(); </script>
<% } else { %>
<script> (function() { $("#header").load("htmls/header/headerConnected.html"); })(); </script>
<% } %>

<script> 
(function() { $("#footer").load("htmls/footer.html"); })(); 

$(window).load(function() {
    var str = $("#panel-body").innerHTML
});
</script>
</body>
</html>