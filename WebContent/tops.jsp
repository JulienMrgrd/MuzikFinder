<%@page import="interfaces.MFMusic"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Enumeration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<style>

@media(min-width: 1010px){
	.col-md-4 {
	    width: 30% !important;
	}
	
	.col-md-5 {
	    width: 40% !important;
	    margin-top: 10%;
	}
}

</style>

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
	
	<%	@SuppressWarnings("unchecked")
		List<MFMusic> general = (List<MFMusic>) request.getAttribute("topMusicWeek");
		
		@SuppressWarnings("unchecked")
		List<MFMusic> topMusicMoins18 = (List<MFMusic>) request.getAttribute("topMusic-18");
		
		@SuppressWarnings("unchecked")
		List<MFMusic> topMusicMoins25 = (List<MFMusic>) request.getAttribute("topMusic-25");
		
		@SuppressWarnings("unchecked")
		List<MFMusic> topMusicMoins50 = (List<MFMusic>) request.getAttribute("topMusic-50");
		
		@SuppressWarnings("unchecked")
		List<MFMusic> topMusicPlus50 = (List<MFMusic>) request.getAttribute("topMusic+50");
	%>
	
	<div class="container">
		<div class="container-fluid">
			<% if( !((boolean)request.getAttribute("success")) ){ %>
				<div id="errorMessageSearch" class="alert alert-danger fade in">
					<strong id="strongErrorMessageSearch"><%=request.getAttribute("message") %></strong>
				</div>
			<% } else { %>
				<div class="col-md-5">
					<div class="thumbnail">
						<img src="images/topgeneral.jpg" />
						<div class="caption">
							<h3>
								Top of this week !
							</h3>
							<%	 if(topMusicMoins18==null || topMusicMoins18.isEmpty()){ %>
									<p>Aucune donnée pour le moment...</p>
							<%	} else { %>
									<ul>
							<%		for(MFMusic music : topMusicMoins18){  %>
										<li><%=music.getArtistName()+" - "+music.getTrackName() %></li>
							<%		} %>
									</ul>
							<%	} %>
						</div>
					</div>
				</div>
				<div class="col-md-4">
					<div class="thumbnail">
						<img src="images/top-18.jpg" />
						<div class="caption">
							<h3>
								Under 18 years
							</h3>
							<%	 if(topMusicMoins18==null || topMusicMoins18.isEmpty()){ %>
									<p>Aucune donnée pour le moment...</p>
							<%	} else { %>
									<ul>
							<%		for(MFMusic music : topMusicMoins18){  %>
										<li><%=music.getArtistName()+" - "+music.getTrackName() %></li>
							<%		} %>
									</ul>
							<%	} %>
						</div>
					</div>
					<div class="thumbnail">
						<img src="images/top-25.jpg" />
						<div class="caption">
							<h3>
								Under 25 years
							</h3>
							<%	 if(topMusicMoins25==null || topMusicMoins25.isEmpty()){ %>
									<p>Aucune donnée pour le moment...</p>
							<%	} else { %>
									<ul>
							<%		for(MFMusic music : topMusicMoins25){  %>
										<li><%=music.getArtistName()+" - "+music.getTrackName() %></li>
							<%		} %>
									</ul>
							<%	} %>
						</div>
					</div>
				</div>
				<div class="col-md-4">
					<div class="thumbnail">
						<img src="images/top-50.jpg" />
						<div class="caption">
							<h3>
								Under 50 years
							</h3>
							<%	 if(topMusicMoins50==null || topMusicMoins50.isEmpty()){ %>
									<p>Aucune donnée pour le moment...</p>
							<%	} else { %>
									<ul>
							<%		for(MFMusic music : topMusicMoins50){  %>
										<li><%=music.getArtistName()+" - "+music.getTrackName() %></li>
							<%		} %>
									</ul>
							<%	} %>
						</div>
					</div>
					<div class="thumbnail">
						<img src="images/top+50.jpg" />
						<div class="caption">
							<h3>
								Over 50
							</h3>
							<%	 if(topMusicPlus50==null || topMusicPlus50.isEmpty()){ %>
									<p>Aucune donnée pour le moment...</p>
							<%	} else { %>
									<ul>
							<%		for(MFMusic music : topMusicPlus50){  %>
										<li><%=music.getArtistName()+" - "+music.getTrackName() %></li>
							<%		} %>
									</ul>
							<%	} %>
						</div>
					</div>
				</div>
			<% } %>
				
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
	})(); 
</script>
</html>