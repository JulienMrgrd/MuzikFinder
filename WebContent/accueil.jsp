<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Accueil</title>
</head>
<body>

	<%
		String message = ((String)request.getAttribute("message"));
		if(message != null){
			out.print("<b><font color=\"red\">"+message+"</font></b><br><br>");
		}
		
	%>

	<ul>
		<li><a href="addContact.jsp">Add contact</a></li>
		<li><a href="deleteContact.jsp">Remove contact</a></li>
		<li><a href="updateContact.jsp">Update contact</a></li>
		<li><a href="searchContact.jsp">Search contact</a></li>
	</ul>
</body>
</html>