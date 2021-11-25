<%@page import="javax.swing.JOptionPane"
		import="java.sql.*"
		import = "java.util.*"
		import = "java.io.*" 
%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
  
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Home Page</title>
	</head>
	<body>
		<%
			if(request.getSession().getAttribute("user") == null) {
				JOptionPane.showMessageDialog(null, "Devi effettuare il login o la sessione è scaduta per accedere a questa pagina!!!", "ERRORE", 0, null);
				response.sendRedirect("Login.jsp");
			}else {
		
			ResourceBundle resource = ResourceBundle.getBundle("resources");
			Class.forName(resource.getString("driver"));
			
			//Connessione al database
			String src = resource.getString("source");
		    String timezone = resource.getString("connection_parameters");
		    String jdbc = (new StringBuilder("jdbc:mysql://")).append(src).append("/").append(resource.getString("schema_name")).append(timezone).toString();						    
			Connection connectDB = DriverManager.getConnection(jdbc,resource.getString("user"), resource.getString("password"));
		
			//Creazione e avvio della query per avere tutte le tuple degli utenti correlati alle proposte
	    	PreparedStatement statement0 = connectDB.prepareStatement(resource.getString("getUserIdQuery")); 
	    	statement0.setString(1, request.getSession().getAttribute("user").toString());
		 	ResultSet risQueryUser = statement0.executeQuery();	
             
        	if(risQueryUser.next()) {
            %>
                 	 
		<table style="text-align:center">
			<tr style="text-align:center">
				<td colspan="100" style="text-align:center">
					<img width="40" height="40" alt="Profilo immagine non disponibile" src="<%=risQueryUser.getString("ImgPath") %>">
					<br>
						<%=risQueryUser.getString("Nome") %>
						<%=risQueryUser.getString("Cognome") %>
				</td>
			</tr>
		</table>
		<%}%>
		<form action = "HomePageServlet" method = "POST">
			<table>
				<tr>
					<td colspan="100"></td>
					
					<td colspan="100" align="center">
						<h1 align="center">Tabella proposte progettuali</h1>
						<table border="1">
						    <% 
						    	
						    	
								//Creazione e avvio della query per avere tutte le tuple delle proposte progettuali
						    	PreparedStatement statement = connectDB.prepareStatement(resource.getString("getProjectQuery"));    						
								ResultSet risQueryProject = statement.executeQuery();					        					     
						        
						      	//Creazione e avvio della query per avere tutte le tuple degli utenti correlati alle proposte
						    	PreparedStatement statement2 = connectDB.prepareStatement(resource.getString("getUserIdQuery"));  	
						    
						        %>
						        
						        <tr>
						        	<th>ID Progetto</th>
						        	<th>Nome</th>
						        	<th>Cognome</th>
						        	<th>Proposta progettuale</th>
						        	<th>Azione</th>
						        </tr>
						        <%
						        while(risQueryProject.next())
						        {
					            %>
					                <tr>
					                     <td>
					                     	<%= risQueryProject.getString("IdProposta")%>
					                     </td>				                   
					                     <%
					                    statement2.setString(1, risQueryProject.getString("CodStudente"));
									 	risQueryUser = statement2.executeQuery();	
					                     
				                    	if(risQueryUser.next()) {
					                    %>
						                     <td>
						                     	<%=risQueryUser.getString("Nome") %>
						                     </td>
						                     <td>
						                     	<%=risQueryUser.getString("Cognome") %>
						                     </td>
					                     <%}%>
						                     <td>
						                     	<%= risQueryProject.getString("NomeProposta")%>
						                     </td>
					                		
					                		<td>
					                			<% String filePath;
					                			filePath = risQueryProject.getString("FilePath");%>
					                								                
					                			<button name="openFile" type="submit" formtarget="_blank" value="<%= filePath %>">Apri</button>
					                			<button name="deleteFile" type="submit" value="<%= risQueryProject.getString("IdProposta")%>" style="color: red">Cancella</button>
					                			
					                		</td>                   
					                </tr>
					            <%}%>					          
						</table>
						
						<br><br>
						
						<table>
							<tr>
								<td colspan="100">
									<input type=button onClick="parent.location='AddProjectIdea.jsp'" value='Aggiungi una proposta'>
								</td>
								
								<td colspan="100">
									<button id="Logout" name="Logout" type="submit">Logout</button>
								</td>
							</tr>
						</table>
						
					</td>
					
					<td colspan="100"></td>
				</tr>
			</table>
		</form>
		<%} %>
	</body>
</html>