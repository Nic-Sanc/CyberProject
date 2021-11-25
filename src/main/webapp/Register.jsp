<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Registration Page</title>
</head>
<body>
	<form action="RegisterServlet" method="POST">
		<div>
			<h1>Registrati subito</h1>

			<p>Inserisci i campi richiesti per creare l'account.</p>
			<hr>
			<table>
				<tr>
					<td colspan="100"><label for="name"><b>Nome </b></label> <br>
					<br> <input id="name" type="text" placeholder="Mario"
						name="name" required> <br>
					<br></td>
					<td><label for="surname"><b>Cognome </b></label> <br>
					<br> <input id="surname" type="text" placeholder="Rossi"
						name="surname" required> <br>
					<br></td>
				</tr>
			</table>
			<label for="name,surname"><i style="color: red;">In caso
					di nomi o cognomi accentati utilitizzare l'apostrofo (') in
					corrispondenza della lettera da accentare </i></label><br>
			<br> <label for="emailLab"><b>Email </b></label> <br>
			<br> <input id="email" type="text" placeholder="prova@mail.com"
				name="email" required> <br>
			<br> <label for="passwordLab"><b>Password </b></label> <br>
			<br> <input id="password" type="password"
				placeholder="Inserire la Password" name="password" required>
			<br>
			<br> <label for="confirmLab"><b>Conferma Password </b></label> <br>
			<br> <input id="confirm_pasword" type="password"
				placeholder="Conferma la Password" name="confirm_pasword" required>
			<br>
			<br> <label for="imgProfile"><b>Inserire l'immagine
					del profilo</b></label><br> <label for="imgProfile"><i
				style="color: red;"> Tipi di file ammessi (*.JPEG, *.JPG, *.PNG)</i></label>
			<img id="myImage" style="border: 1px" /> <br>
			<br> <input type="file" id="profileImg" name="profileImg"
				required> <br>
			<br>

			<hr>

			<button id="submit" type="submit">
				<strong>Registrati</strong>
			</button>
		</div>
		<div>
			<p>
				Hai gia' un account? <a href="./Login.jsp">Accedi subito</a>.
			</p>
		</div>
	</form>
</body>
</html>