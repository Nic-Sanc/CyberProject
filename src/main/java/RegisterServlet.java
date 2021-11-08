import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JOptionPane;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private ResourceBundle resource;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	public RegisterServlet() {
        super(); 
        ResourceBundle.clearCache();
    }
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ResourceBundle.clearCache();
		resource = ResourceBundle.getBundle("resources");
		String page = "Register.jsp";		
		//Connessione al database
		Connection connectDB = Service.connectToJDBC(resource.getString("schema_name"));
	    if(connectDB != null) {
	    	if(Service.sanitizeString(request.getParameter("email").getBytes(), "email")) {
		    	if(Service.pullInfoUtente(connectDB, request.getParameter("email"), response, resource) == null) {
			    	//Controllo Password match
					if(Arrays.equals(request.getParameter("password").getBytes(), 
									 request.getParameter("confirm_pasword").getBytes())) {
					
							String idUser = Service.generateAlphaNumericString(Integer.parseInt(resource.getString("idLenght")));
							PasswordManager pwMng = new PasswordManager(request.getParameter("password").getBytes(), idUser, response);
							byte[] passwordCrypt = pwMng.cryptPassword();
							
							if(passwordCrypt != null) {
								try {
									if(Service.isImg(request.getParameter("profileImg"))) {
										//Conversione hashcode da byte a string
									    StringBuffer hexString = new StringBuffer();
									      
									    for (int i = 0;i<passwordCrypt.length;i++) {
									       hexString.append(Integer.toHexString(0xFF & passwordCrypt[i]));
									    }
										
									    if(registrazioneUtente(connectDB, idUser, request, hexString)) {
									    	Service.cleanBytes(passwordCrypt);
											hexString.delete(0, hexString.length());
											
											String msg = "Registrazione effettuata con successo!";
											page = "Login.jsp";
											Service.showOnlyAlert(msg, "INFO", JOptionPane.INFORMATION_MESSAGE);
									    }else {
									    	//Messaggio errore nella registrazione dell'utente
									    	String msg = "Registrazione non effettuata! Errore interno, si prega di ricompilare il modulo.";
									    	Service.showOnlyAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE);
									    }
									}else {
										//Messaggio errore nell'inserimento dell'immagine del profilo
										String msg = "Errore nella cifratura della password!";
										Service.showOnlyAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE);
									}
								} catch (SAXException | TikaException e) {
									//Messaggio errore nell'inserimento dell'immagine del profilo
									String msg = "Errore inserimento immagine profilo!";
									Service.showOnlyAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE);
								}
							}else {
								//Messaggio errore nella creazione della password cifrata
								String msg = "Errore nella cifratura della password!";
								Service.showOnlyAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE);
							}
						
						}else {
							//Visualizza messaggio di errore per la password non corrispondente
							String msg = "Le password inserite non corrispondono!";
							Service.showOnlyAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE);
						}
					}else {
				    	//Visualizza messaggio di errore per la password non corrispondente
						String msg = "Email inserita registrata! Si prega di controllare il modulo di registrazione o di effettuare il login.";
						Service.showOnlyAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE);
				    }
	    	}else {
	    		String msg = "Formattazione email errata!";
				Service.showOnlyAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE);
	    	}
	    }else {
	    	//Connection sbagliata non ha avuto successo
	    	String msg = "Connessione con il database fallita!";
	    	Service.showOnlyAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE);
	    }
	    
	    RequestDispatcher dd=request.getRequestDispatcher(page);
		dd.forward(request, response);
		
	}

	private synchronized boolean registrazioneUtente(Connection connectDB, String idUser, HttpServletRequest request, StringBuffer password) throws IOException, ServletException {
		try {
			String msg="";
			if(!Service.sanitizeString(request.getParameter("surname").getBytes(), "alpha")) {
				msg = "Il cognome inserito contiene caratteri non consentiti";
			}else if(!Service.sanitizeString(request.getParameter("name").getBytes(), "alpha")){
				msg = "Il nome inserito contiene caratteri non consentiti";
			}else if(!Service.sanitizeString(request.getParameter("email").getBytes(), "email")) {
				msg = "L'email inserita non rispetta la formattazione di base o contiene caratteri non consentiti";
			}else {
				//Salvo l'utente sul db con il preparedStatement
				PreparedStatement queryStatement;
				queryStatement = connectDB.prepareStatement(resource.getString("registerQuery"));
				
				//Estrazione dei valori del form
				queryStatement.setString(1, idUser);
				queryStatement.setString(2, request.getParameter("surname"));
				queryStatement.setString(3, request.getParameter("name"));
				queryStatement.setString(4, request.getParameter("email"));
				queryStatement.setString(5, password.toString());
				queryStatement.setString(6, request.getParameter("profileImg"));
				queryStatement.execute();
				
				connectDB.close();
				
				return true;
			}
			Service.showOnlyAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE);
			return false;
				
		} catch (SQLException e) {
			//Visualizza l'errore exception
	    	Service.showOnlyAlert(e.getMessage(), "ERRORE", JOptionPane.ERROR_MESSAGE);
			return false;
		}		
	}

}
