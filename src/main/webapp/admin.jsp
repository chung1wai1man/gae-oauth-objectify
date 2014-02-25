<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Welcome to GAE OAuth Objectify project</title>
</head>
<body>
	<c:forEach var="cookies" items="${cookie}" begin="0" end="0">
		<c:choose>
			<c:when test="${cookies.key == 'user_name'}">
				<a href="/logout">Logout ${cookies.value.value}</a>
			</c:when>
		</c:choose>
	</c:forEach>
	<br></br>
	Welcome to our application. This page is seen only if the user has loggedIn.
</body>
</html>