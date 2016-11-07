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
	<%
		String message = ((String)request.getAttribute("message"));
		if(message != null){
			out.print("<b><font color=\"red\">"+message+"</font></b><br><br>");
		}
	%>
	
	<div id="header"></div>
	<div id="carousel"></div>

	<div class="container">
		<div class="container-fluid">
			<div id="errorMessageSearch" class="alert alert-danger fade in">
				<strong id="strongErrorMessageSearch"><%=request.getAttribute("success")+" = "+request.getAttribute("message") %></strong>
			</div>
	
			<h4>Résultats de votre recherche : <%=request.getAttribute("results") %></h4>
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