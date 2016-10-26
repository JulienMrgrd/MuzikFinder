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
	
	.container .container-fluid {
		margin-top: 20px;
	}
}

.album-art {
    width: 40%;
    height: 40%;
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

.col-md-6 h3{
	color: #823982;
}

</style>

<head>
<meta charset="utf-8">
<link rel="icon" href="icon/favicon.ico">
<link href="css/bootstrap.min.css" rel="stylesheet">
<title>Welcome</title>
</head>

<body>
	<div id="header"></div>
	<div id="carousel"></div>

	<div class="container">
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-6">
					<h3> Quick & easy music search </h3>
					<p> Texte un peu long à ajouter </p>
				</div>
				<div class="col-md-6">
					<div class="albums">
						<div id="album-slot1">
							<div class="album-art">
								<img src="images/ex1.jpg"
									class="lazy lazy-no-small" style="display: block;">
							</div>
						</div>
						<div id="album-slot2">
							<div class="album-art has-preview">
								<img src="images/ex2.jpg"
									class="lazy lazy-no-small" style="display: block;">
							</div>
						</div>
						<div id="album-slot3">
							<div class="album-art has-preview">
								<img
									src="images/ex3.jpg"
									class="lazy lazy-no-small" style="display: block;">
							</div>
						</div>
						<div id="album-slot4">
							<div class="album-art has-preview">
								<img
									src="images/ex4.jpg"
									class="lazy lazy-no-small" style="display: block;">
							</div>
						</div>
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