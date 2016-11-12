<%@page import="sql.metier.Search"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<style>
.container {
	margin-top: 100px;
	margin-bottom: 40px;
}

.btn-info.btn-outline:hover, .btn-info.btn-outline:active{
    color: #fff;
    background-color: #9348be;
    border-color: #8d32ac;
    font-weight: bold;
}
.btn-info.btn-outline {
    color: #9348be;
    border-color: #8d32ac;
    font-weight: bold;
    background-color: transparent;
}

</style>

<script src="js/jquery.min.js"></script>
<script src="js/js.cookie.min.js"></script>
<head>
<meta charset="utf-8">
<link rel="icon" href="images/favicon.png?2">
<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/ladda-themeless.min.css">
<title>MuzikFinder</title>
</head>

<body>
	<div id="header"></div>
	<div class="container">
		<div class="col-md-7">
			<div class="tabbable" id="tabs-362910">
				<ul class="nav nav-tabs">
					<li class="active"><a href="#panel-Modification"
						data-toggle="tab">Modification coordonnées</a></li>
					<li><a href="#panel-Historique" data-toggle="tab">Historique
							de recherche</a></li>
					<li><a href="#panel-Suppression" data-toggle="tab">Suppresion</a>
					</li>
				</ul>

				<div class="tab-content" style="margin-top: 5%">
				
					<div class="tab-pane active" id="panel-Modification">
					
						<div id="errorMessage" class="alert alert-danger fade in" style="display:none;">
							<strong id="errorMessageStrong"></strong>
						</div>
						<div id="successMessage" class="alert alert-success fade in" style="display:none;">
							<strong id="successMessageStrong">Modifications OK.</strong>
						</div>
						<form action="AccountServlet" method="post">
							<div class="form-group">
								<label class="form-control-label">Password :</label> 
								<input id="newPassword" type="password" name="newPassword"
									class="form-control" placeholder="5 characters minimum..."></input>
							</div>
							<div class="form-group">
								<label class="form-control-label">Repeat the password :</label> 
								<input id="newPasswordBis" type="password" name="newPasswordBis"
									class="form-control" placeholder="The exactly same..."></input>
							</div>
							<div class="form-group">
								<label class="form-control-label">Email :</label> 
								<input id="newEmail" type="email" name="NewEmail" class="form-control"
									autocomplete="on" />
							</div>
							<button id="buttonSetAccount" type="submit" class="btn btn-info btn-outline btn-sm ladda-button" 
								data-style="expand-right">Modifier mes informations</button>

						</form>
					</div>


					<div class="tab-pane" id="panel-Historique">
						<p>
						<h4 style="margin-bottom: 10px;">Historique de vos recherches :</h4>
							<%	@SuppressWarnings("unchecked")
								List<Search> listSearch = (List<Search>) request.getAttribute("results");
								String tags;
								for(Search search : listSearch){
									tags = search.getRecherche();
									tags=tags.replaceAll(" ", "+");
									if(!tags.isEmpty()){
										tags = tags.substring(0, tags.length()-1);
									} %>
									<a href="./SearchServlet?userSearch=<%=tags%>" 
										class="list-group-item"><i><%=search.getDateSearch()%> : </i><b><%=tags%></b></a>
					<%			} 	%>
					</div>
					
					<div class="tab-pane" id="panel-Suppression">
					<form action="AccountServlet" method="post">
						<div>
							<button id="buttonDeleteSearch" class="btn btn-warning" style="margin-right: 10px; float:left;"
								type="submit">Supprimer Recherche</button>
							<button id="buttonDeleteAccount" class="btn btn-danger" style="float:left;"
								type="submit">Supprimer mon compte</button>
						</div>
					</form>
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
		
		//Stops the submit request
		$("#accountAjaxRequestForm").submit(function(e) {
			e.preventDefault();
		});

		//checks for the button click event
		$("#buttonSetAccount").click(function(e) {
			
			var pass = $("#newPassword").val();
			var passBis = $("#newPasswordBis").val();
			var mail = $("#newEmail").val();
			
			if( (pass==null && mail==null) || (pass=="" && mail=="") || pass!=passBis ){
				$("#errorMessageStrong").text("Bad inputs !");
				$("#errorMessage").show();
				$("#successMessage").hide();
				return false;
			}
			
			e.preventDefault();
			var laddaButton = Ladda.create(this);
			laddaButton.start();
			$.ajax({
				type : "POST",
				url : "AccountServlet",
				data : {
					type : "setAccount",
					password : pass,
					passwordBis : passBis,
					mail : mail
				},

				//if received a response from the server
				success : function(data, textStatus, jqXHR) {
					laddaButton.stop();
					if (data.success) {
						$("#successMessage").show();
						$("#errorMessage").hide();
					} else { //display error message
						$("#errorMessageStrong").text(data.message);
						$("#errorMessage").show();
						$("#successMessage").hide();
					}
				},

				error : function(jqXHR, textStatus, errorThrown) {
					$("#errorMessageStrong").text("No response from the server...");
					$("#errorMessage").show();
					$("#successMessage").hide();
					laddaButton.stop();
				},

				always : function() {
					laddaButton.stop();
				}
			});
			return false;
		});
	})(); 
</script>
</html>