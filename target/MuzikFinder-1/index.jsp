<%@page import="java.util.Enumeration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<style>
.demo-long {
	margin-top: 20px;
	margin-bottom: 20px;
}

@media {
	.container {
		max-width: 80%;
	}
	
	.container .row {
		margin-top: 45px;
	}
}

.album-art {
    width: 50%;
    height: 50%;
    position: relative;
    float: left;
}

.album-art img {
	display: block;
	max-width: 100%;
	height: auto;
	vertical-align: middle;
	border: 0;
}

.col-md-6 h2{
	color: #823982;
}

.albums{
	padding-left: 50px;
}

.classement{
	text-align: center;
	vertical-align: middle;
}

.classement img {
	margin: 0 auto;
	width: 70%;
    height: 70%;
}

.stats img {
	width: 90%;
    height: 90%;
    position: center;
}

.emoji{
	width: 8%;
    height: 8%;
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
	<div id="carousel"></div>

	<div class="container">
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-6">
					<h2>Quick & easy music search</h2>
					<h4><p> Texte un peu long à ajouter Texte un peu long à ajouter Texte un peu long à ajouter Texte un peu long à ajouter Texte un peu long à ajouter Texte un peu long à ajouter Texte un peu long à ajouter</p></h4>
				</div>
				<div class="col-md-6">
					<div class="albums">
						<div id="album-slot1">
							<div class="album-art">
								<img  src="images/ex1.jpg" class="lazy lazy-no-small" style="display: block;">
							</div>
						</div>
						<div id="album-slot2">
							<div class="album-art has-preview">
								<img  src="images/ex2.jpg" class="lazy lazy-no-small" style="display: block;">
							</div>
						</div>
						<div id="album-slot3">
							<div class="album-art has-preview">
								<img  src="images/ex3.jpg" class="lazy lazy-no-small" style="display: block;">
							</div>
						</div>
						<div id="album-slot4">
							<div class="album-art has-preview">
								<img  src="images/ex4.jpg" class="lazy lazy-no-small" style="display: block;">
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6">
					<div class="classement">
						<img class="center-block" src="images/classement.png" style="display: block;">
					</div>
				</div>
				<div class="col-md-6">
					<h2> What's hot ?  <img class="emoji" src="images/1f525.png"/></h2>
					<h4>Texte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouter</h4>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6">
					<h2>Stats about you !</h2>
					<h4>Texte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouterTexte un peu long à ajouter</h4>
				</div>
				<div class="col-md-6">
					<div class="stats">
						<img class="center-block" src="images/stats.png" style="display: block;">
					</div>
				</div>
			</div>
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

<script> (function() { $("#carousel").load("htmls/carousel.html"); })(); </script>
<script> (function() { $("#footer").load("htmls/footer.html"); })(); </script>
</html>