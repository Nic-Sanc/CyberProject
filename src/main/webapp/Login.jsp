<%@page import="javax.swing.JOptionPane"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Login Page</title>
</head>
<body>
<%	if(request.getCookies() != null) {
	
		boolean cookieFlag = false;
		for(Cookie cookie : request.getCookies()) {
			if(cookie.getName().equals("loggedUser")) {
				cookieFlag = true;
			}	
		}
		
		if(request.getSession().getAttribute("user") != null && cookieFlag) {
			JOptionPane.showMessageDialog(null, "Login effettuato precedentemente ", "INFO", 1, null);
			response.sendRedirect("HomePage.jsp");
		}
		request.getSession().removeAttribute("user");
		for(Cookie cookie : request.getCookies()) {
			cookie.setMaxAge(0);
		}
	}
%>
	<form action="LoginServlet" method="POST">
		<div>
			<h1>Accedi subito</h1>
			<p>Inserisci le credenziali per accedere al tuo account.</p>
			<hr>
			<label for="email"><b>Email </b></label> <br>
			<br> <input id="email" type="text" placeholder="prova@mail.com"
				name="email" required> <br>
			<br> <label for="pwd"><b>Password </b></label> <br>
			<br> <input id="password" type="password"
				placeholder="Inserire la Password" name="password" required>
			<br>
			<br>

			<hr>

			<button id="submit" type="submit">
				<strong>Accedi</strong>
			</button>
		</div>
		<div>
			<p>
				Non hai un account? <a href="./Register.jsp">Registrati subito</a>.
			</p>
		</div>
	</form>
</body>
</html>