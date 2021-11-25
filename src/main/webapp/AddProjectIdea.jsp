<%@ page import="javax.swing.JOptionPane"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>New Project Idea</title>
</head>
<body>
	<%
		boolean cookieFlag = false;
		for(Cookie cookie : request.getCookies()) {
			if(cookie.getName().equals("loggedUser")) {
				cookieFlag = true;
			}	
		}
		if(!cookieFlag) {
			JOptionPane.showMessageDialog(null, "Cookie scaduto! Effettuare nuovamene il login.", "ERRORE", 0, null);
			response.sendRedirect("Login.jsp");
		}else if(request.getSession().getAttribute("user") == null) {
			JOptionPane.showMessageDialog(null, "Devi effettuare il login o la sessione è scaduta per accedere a questa pagina!!!", "ERRORE", 0, null);
			response.sendRedirect("Login.jsp");
		}
	%>
	<form action="AddProjectIdeaServlet" method="POST">
		<div>
			<h1>Aggiunta di un nuovo progetto</h1>

			<p>Inserisci i campi richiesti per creare l'idea di progetto.</p>
			<hr>
			<label for="ideaLab"><b>Nome idea</b></label> <br>
			<br> <input id="idea" type="text"
				placeholder="Es. Progetto Missile NSA" name="idea" required>
			<br>
			<br> <label for="projectTxtLbl"><b>Inserire il file
					di progetto</b></label><br> <label for="imgProfile"><i
				style="color: red;"> Tipi di file ammessi (.txt)</i></label> <br>
			<br> <input type="file" id="projectTxt" name="projectTxt"
				required> <br>
			<br>

			<hr>

			<button id="submit" type="submit">
				<strong>Crea nuova idea</strong>
			</button>
			<input type=button onClick="parent.location='HomePage.jsp'"
				value='Torna alla home'>
		</div>
	</form>
</body>
</html>