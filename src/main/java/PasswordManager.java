import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.annotation.concurrent.Immutable;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JOptionPane;
@Immutable
public final class PasswordManager {
	
	private byte[] pw;
	private ResourceBundle resource;
	private String idUtente;

	private HttpServletResponse response;
	
	public PasswordManager(byte[] pw, String idUtente, HttpServletResponse response) {
		ResourceBundle.clearCache();
		resource = ResourceBundle.getBundle("resources");
		this.pw = pw;
		this.idUtente = idUtente;
		this.response = response;
	}
	public PasswordManager(byte[] pw, HttpServletResponse response) {
		ResourceBundle.clearCache();
		resource = ResourceBundle.getBundle("resources");
		this.pw = pw;
		this.response = response;
	}

	byte[] cryptPassword() throws IOException {
		
		MessageDigest msgDg;
		try {
			msgDg = MessageDigest.getInstance("SHA-256");
			byte[] auxSalt = saveSalt();
			byte[] pwCrypt = Service.appendBytes(this.pw,auxSalt);
			byte[] hashCode = msgDg.digest(pwCrypt);
				
			Service.cleanBytes(pwCrypt);
			Service.cleanBytes(auxSalt);
			Service.cleanBytes(this.pw);
			
			return hashCode;
		} catch (NoSuchAlgorithmException e) {
			//Visualizza l'errore exception
			Service.showAlert(e.getMessage(), "ERRORE", JOptionPane.ERROR_MESSAGE, "Register.jsp", response);
		}
		
		return null;
	}
	
	
	private byte[] saveSalt() throws IOException {
		byte[] aux = generateSalt();	//Genera randomicamente il Salt
		
		try {    
		    Connection connectDB = Service.connectToJDBC(resource.getString("schema_name_salt"));	//Connessione al DB
		    
		    //Registrazione del Salt nel DB
		    if(!registrazioneSalt(connectDB, aux)) {
		    	String msg = "Si e' verificato un errore nella cifratura della password!";
		    	Service.showAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE, "Register.jsp", response);
		    }
		    
			connectDB.close();	//Chiusura DB
			
		} catch (SQLException e) {
			//Visualizza l'errore exception
			Service.showAlert(e.getMessage(), "ERRORE", JOptionPane.ERROR_MESSAGE, "Register.jsp", response);
		}
		
		return aux;
	}
	
	private synchronized boolean registrazioneSalt(Connection connectDB, byte[] salt) throws IOException {
		try {
			PreparedStatement queryStatement = connectDB.prepareStatement(this.resource.getString("registerQuerySalt"));
			
			queryStatement.setString(1, this.idUtente);
			queryStatement.setString(2, new String(salt));
			queryStatement.execute();
			
			ResourceBundle.clearCache();
			return true;
		}catch (SQLException e) {
			//Visualizza l'errore exception
			Service.showAlert(e.getMessage(), "ERRORE", JOptionPane.ERROR_MESSAGE, "Register.jsp", response);
			return false;
		}
	}

	private byte[] generateSalt() {
		// Stringa dizionario
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";     
		byte [] salt = new byte[Integer.parseInt(resource.getString("saltLenght"))];
		
		for(int i = 0; i < Integer.parseInt(resource.getString("saltLenght")); i++) {
			int index = Service.getRandomInt(0,AlphaNumericString.length()-1);
			salt[i] = String.valueOf(AlphaNumericString.charAt(index)).getBytes()[0]; 
		}
		return salt;
    }
	
	public boolean checkPassword(byte[] dbPassword) throws IOException {
		try {
			byte [] insertPassword = Service.appendBytes(this.pw, getSalt());
			MessageDigest msgDg = MessageDigest.getInstance("SHA-256");
						
			byte [] hashCode = msgDg.digest(insertPassword);
			
			//Conversione hashcode da byte a string
		    StringBuffer hexString = new StringBuffer();
		      
		    for (int i = 0;i<hashCode.length;i++) {
		       hexString.append(Integer.toHexString(0xFF & hashCode[i]));
		    }			
			
			Service.cleanBytes(this.pw);
			
			boolean flag = Service.equalBytes(hexString.toString().getBytes(), dbPassword);
			
			Service.cleanBytes(insertPassword);
			Service.cleanBytes(hashCode);
			hexString.delete(0, hexString.length());
			
			return flag;
		} catch (NoSuchAlgorithmException e) {
			//Visualizza l'errore exception
			Service.showAlert(e.getMessage(), "ERRORE", JOptionPane.ERROR_MESSAGE, "Register.jsp", response);
			return false;
		}
		
	}

	private byte[] getSalt() throws IOException {
		byte[] saltSelected = null;
		try {
			Connection connectDB = Service.connectToJDBC(resource.getString("schema_name_salt"));	//Connessione al DB
			PreparedStatement queryStatement = connectDB.prepareStatement(this.resource.getString("getSaltQuery"));
			
			queryStatement.setString(1, this.idUtente);
			ResultSet ris = queryStatement.executeQuery();
			
			if(ris.next()) {
				saltSelected = ris.getString("Salt").getBytes();
			}
			ResourceBundle.clearCache();
			
		}catch (SQLException e) {
			//Visualizza l'errore exception
			Service.showAlert(e.getMessage(), "ERRORE", JOptionPane.ERROR_MESSAGE, "Register.jsp", response);
			return null;
		}
		return saltSelected;
	}

}
