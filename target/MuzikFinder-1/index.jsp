<%@page import="java.util.Enumeration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<script src="js/jquery.min.js"></script>
<script src="js/js.cookie.min.js"></script>
<head>
	<meta charset="utf-8">
	<link rel="icon" href="images/favicon.png?2">
	<link rel="stylesheet" href="css/bootstrap.min.css">
	<link rel="stylesheet" href="css/ladda-themeless.min.css">
	<link rel="stylesheet" href="css/index.css">
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
					<h4><p>Texte un peu long à ajouter Texte un peu long à ajouter Texte un peu long à ajouter Texte un peu long à ajouter Texte un peu long à ajouter Texte un peu long à ajouter Texte un peu long à ajouter</p></h4>
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

<script> 
(function() { 
	var login = Cookies.get('MUZIKFINDERLOGIN');
	if(login==null || login=="") $("#header").load("htmls/header/headerNotConnected.html");
	else $("#header").load("htmls/header/headerConnected.html");
	
	$("#carousel").load("htmls/carousel.html");
	$("#footer").load("htmls/footer.html");
})(); 
</script>
</html>