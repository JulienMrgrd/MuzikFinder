<%@page import="sql.metier.Search"%>
<%@page import="java.util.List"%>
<%@page import="java.sql.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<style>
.container {
	margin-top: 100px;
	margin-bottom: 150px;
}

button {
	vertical-align: middle;
}

label.form-control-label {
	display: block;
	text-align: center;
}
</style>
<head>
<meta charset="utf-8">
<link rel="icon" href="images/favicon.png?2">
<link href="css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="css/ladda-themeless.min.css">
<link href="css/index.css" rel="stylesheet">
<title>MuzikFinder</title>
</head>

<body>
	<div id="header"></div>
	<div class="container">
		<% if( !((boolean)request.getAttribute("success")) ){ %>
		<div id="errorMessageSearch" class="alert alert-danger fade in">
			<strong id="strongErrorMessageSearch"><%=request.getAttribute("message") %></strong>
		</div>
		<% } else { %>

		<div class="col-md-1"></div>
		<div class="col-md-10">
			<div class="tabbable" id="tabs-362910">
				<ul class="nav nav-tabs">
					<li class="active"><a href="#panel-Modification"
						data-toggle="tab">Modification coordonnées</a></li>
					<li><a href="#panel-Historique" data-toggle="tab">Historique
							de recherche</a></li>
					<li><a href="#panel-Suppression" data-toggle="tab">Suppresion</a>
					</li>
				</ul>

				<div class="tab-content">
					<div class="tab-pane active" id="panel-Modification">
						<form action="AccountServlet" method="post">
							<div class="form-group">
								<label class="form-control-label">Password :</label> 
								<input id="newPassword" type="password" name="newPassword"
									class="form-control"></input>
							</div>
							<div class="form-group">
								<label class="form-control-label">Repeat Password :</label> 
								<input id="newPasswordBis" type="password" name="newPasswordBis"
									class="form-control"></input>
							</div>
							<div class="form-group">
								<label class="form-control-label">Email :</label> 
								<input id="NewEmail" type="email" name="NewEmail" class="form-control"
									autocomplete="on" />
							</div>
							<button id="ButtonSetAccount" class="btn btn-default btn-block"
								type="submit">Modifier mes informations</button>

						</form>
					</div>


					<div class="tab-pane" id="panel-Historique">
						<p>
						<h4>Historique de vos recherches :</h4>
						<ul>
							<%	@SuppressWarnings("unchecked")
									List<Search> listSearch = (List<Search>) request.getAttribute("results");
									Date date;
									String recherche;
									for(Search search : listSearch){
										recherche = search.getRecherche();
										date = search.getDateSearch();
										recherche=recherche.replaceAll(" ", "+"); %>

							<li
								onclick="location.href='http://muzikfinder.herokuapp.com/SearchServlet?userSearch=<%=recherche%>';"
								class="list-group-item"><i><%=date%> : </i> <b><%=recherche%></b>
							</li>
							<%			} 	%>
						</ul>
					</div>


					<div class="tab-pane" id="panel-Suppression">
						<form action="AccountServlet" method="post">
							<br>
							<%-- //TODO: voir comment on fait pour la date --%>
							<br>
							<button id="ButtonDeleteSearch" class="btn btn-warning btn-block"
								type="submit">Supprimer Recherche</button>
							<br>
							<button id="ButtonDeleteAccount" class="btn btn-danger btn-block"
								type="submit">Supprimer mon compte</button>
						</form>
					</div>
				</div>
			</div>
		</div>
		<div class="col-md-1"></div>
		<%			} 	%>
	</div>
	<div id="footer"></div>

</body>

<script src="js/jquery.min.js"></script>
<script src="js/js.cookie.min.js"></script>
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