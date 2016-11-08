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
.list-group {
	margin-top: 20px;
	line-height:30px;
}

@media (min-width: 750px) {
	.list-group {
		width: 60%;
	}
	}

@media (max-width: 750px) {
	.list-group {
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
							<strong id="strongErrorMessageSearch"><%=request.getAttribute("message")%></strong>
						</div>
			<%		} else {  %>
						<h4>Résultats de votre recherche : </h4>
						<form method="post" action="DisplayOneMusicServlet">	
							<div class="list-group">
			<%				for(MFMusic music : musics){	%>
								<input type="hidden" name="idMusic" value="<%=music.getTrackId()%>"/>
								<button type="submit" class="list-group-item clearfix">
									<%=music.getArtistName()+" - "+music.getTrackName()%>
									<span class="badge"><i>Pop</i></span>
								</button>
			<%				} 	%>
							</div>
						</form>
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

<script> (function() { $("#footer").load("htmls/footer.html"); })(); </script>
</body>
</html>