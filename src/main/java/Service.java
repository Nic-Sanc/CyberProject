import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public final class Service {
	
	static final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	
	static synchronized boolean cleanBytes(byte[] arrayBytes) {
		boolean flag = true;
		for(int i = 0; i < arrayBytes.length;i++) {
			arrayBytes[i] = 0;
		}	
		
		for(int i = 0; i < arrayBytes.length;i++) {
			if(arrayBytes[i] != 0) {
				flag = false;
			}
		}
		return flag;
	}
	
	static Connection connectToJDBC(String db) {
		
		try {
			ResourceBundle resource = ResourceBundle.getBundle("resources");
		    
			Class.forName(resource.getString("driver"));
			
			String src = resource.getString("source");
		    String timezone = resource.getString("connection_parameters");
		    String jdbc = (new StringBuilder("jdbc:mysql://")).append(src).append("/").append(db).append(timezone).toString();
		    
			return DriverManager.getConnection(jdbc,resource.getString("user"), resource.getString("password"));
			
		} catch (ClassNotFoundException | SQLException e) {
			//Visualizza l'errore exception
			return null;
		}
		
		
	}
	
	static int getRandomInt(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
	
	static String generateAlphaNumericString(int lenghtByte) {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";         
		String aux = "";
		
		 for(int i = 0; i < lenghtByte; i++) {
			 int index = Service.getRandomInt(0,AlphaNumericString.length()-1);
			 aux += AlphaNumericString.charAt(index);
		 }
		 return aux;
	}
	
	static byte[] appendBytes(byte[] firstByte, byte[] nextByte) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(firstByte);//
		baos.write(nextByte);//
		
		return baos.toByteArray();
	}
	
	static void showAlert(String msg, String header, int typeAlert, String pageRedirect, HttpServletResponse response) throws IOException {
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);
		JOptionPane.showMessageDialog(dialog, msg, header, typeAlert, null);
    	response.sendRedirect(pageRedirect);
	}
	
	static void showOnlyAlert(String msg, String header, int typeAlert) throws IOException, ServletException {
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);
		JOptionPane.showMessageDialog(dialog, msg, header, typeAlert, null);
	}	
	
	static synchronized Utente pullInfoUtente(Connection connectDB, String email, HttpServletResponse response, ResourceBundle resource) throws IOException {
		Utente getUser = null;
		try {
			//Collegamento al DB e alla query
			PreparedStatement statement = connectDB.prepareStatement(resource.getString("getUserQuery"));    
			statement.setString(1, email);    
			ResultSet risQuery = statement.executeQuery();
			
			//Prelievo risultati dalla query
			if(risQuery.next()) {
				getUser = new Utente(risQuery.getString("IdStudente"), risQuery.getString("Nome"), risQuery.getString("Cognome"), risQuery.getString("Email"), risQuery.getString("Password").getBytes(), risQuery.getString("ImgPath"));
			}
			
		} catch (SQLException e) {
			//Visualizza l'errore exception
	    	Service.showAlert(e.getMessage(), "ERRORE", JOptionPane.ERROR_MESSAGE, "Login.jsp", response);
			return null;
		}
		
		return getUser;
	}
	
	static boolean isImg(String percorso) throws IOException, SAXException, TikaException{
		boolean flag = false;

		try {
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			FileInputStream file = new FileInputStream(percorso);
			Parser parser = new AutoDetectParser();
		
			parser.parse(file, handler, metadata, new ParseContext());
		
			for(String name : metadata.names()) {
				if(metadata.get(name).equals("image/png") || metadata.get(name).equals("image/jpeg") || metadata.get(name).equals("image/jpg")) {
					flag = true;
				}
			}
		} catch(IOException | SAXException | TikaException e) {
			e.getMessage();
		}

		return flag;
	}
	static Utente logInSystem(Connection connectDB, HttpServletRequest request, HttpServletResponse response, ResourceBundle resource) throws IOException, ServletException {
		Utente selectedUser = pullInfoUtente(connectDB, request.getParameter("email").toString(), response, resource);
    	
	    if(selectedUser != null) {
	    	
	    	PasswordManager pwMng = new PasswordManager(request.getParameter("password").getBytes(),selectedUser.getIdUtente(), response);
	    	
	    	if(pwMng.checkPassword(selectedUser.getPassword())) {
	    		return selectedUser;
	    	}else {
	    		String msg = "Password Errata! Controllare i dati inseriti o effettuare la registrazione.";
	    		showOnlyAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE);
	    	}
	    	
	    }else {
	    	String msg = "Utente non trovato! Controllare i dati inseriti o effettuare la registrazione.";
	    	showOnlyAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE);
	    }
	    
	    return null;
		
	}

	static boolean equalBytes(byte[] byteArray1, byte[] byteArray2) {
		if(byteArray1.length == byteArray2.length) {
			for(int i = 0; i < byteArray1.length; i++) {
				if(byteArray1[i] != byteArray2[i]) {
					return false;
				}
			}
		}else {
			return false;
		}
		return true;
	}
	
	static boolean isTxt(String percorso) throws IOException, SAXException, TikaException{

		boolean flag = false;

		BodyContentHandler handler = new BodyContentHandler();
		TXTParser parser = new TXTParser();
		Metadata metadata = new Metadata();
		ParseContext pcontext = new ParseContext();

		try (FileInputStream file = new FileInputStream(percorso);) {
			parser.parse(file, handler, metadata, pcontext);
			for(String name : metadata.names()) {
				System.out.println(getTxtExtension(percorso));
				if(metadata.get(name).contains("text/plain") && getTxtExtension(percorso)) {
					System.out.println(percorso.substring(percorso.lastIndexOf("."), percorso.length()));
					flag = true;
				}
			}
		}catch(Exception e) {System.out.println(e);}

		return flag;
	}
	
	static boolean getTxtExtension(String percorso) throws IOException, SAXException, TikaException{

		if (percorso.contains(".")) {
			String ext = percorso.substring(percorso.lastIndexOf(".")+1);
			System.out.println(ext);
			if(ext.contains("txt")) {
				return true;
			}
		}
			
		return false;
	}
	
	static boolean sanitizeString(byte stringCheck[], String sanitizeType) {
		
		String sanitazeExp = "";
		
		switch(sanitizeType) {
			case "alpha":
				sanitazeExp = "^[a-zA-Z']+[\\s]*$";
			break;
			
			case "alphaNumb":
				sanitazeExp = "^[a-zA-Z']+[\\w\\s]*$";
			break;
			
			case "numb":
				sanitazeExp = "^[0-9]+$";
			break;
			case "email":
				sanitazeExp = "[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+";
			break;
			//Non bisogna controllare poiché si accettano anche caratteri speciali
			default:
				sanitazeExp = "";
		}
		
		if(sanitazeExp != "") {
			if((new String(stringCheck)).matches(sanitazeExp)) {
				return true;  
			}
		}
		return false;
	}

	static boolean checkValidityFile(String filePath, ResourceBundle resource, Connection connectDB) throws IOException, SQLException, ParseException {
		
		Path checkFile = Paths.get(filePath);
		BasicFileAttributes checkFileAttributes = Files.readAttributes(checkFile, BasicFileAttributes.class);
		
		String timeNewCreation = checkFileAttributes.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(Service.formatoData);
		String timeNewModified = checkFileAttributes.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(Service.formatoData);
		
		ProjectFile savedFile = getSavedFile(resource, connectDB, filePath); 
		
		FileInputStream newFile = new FileInputStream(filePath);
		double size = newFile.getChannel().size();
		newFile.close();
		
		if(savedFile.getDateCreazione().equals(timeNewCreation)&& 
		savedFile.getUltimaModifica().equals(timeNewModified) &&
		savedFile.getSizeFile() == size){
			return true;
		}
		return false;
	}

	static ProjectFile getSavedFile(ResourceBundle resource, Connection connectDB, String filePath) throws SQLException, ParseException {
		PreparedStatement fileInfoQuery = connectDB.prepareStatement(resource.getString("getFileInfoQuery")); 
		ResultSet risQueryInfoFile = fileInfoQuery.executeQuery();
		
		while(risQueryInfoFile.next()) {
			if(risQueryInfoFile.getString("FilePath").equals(filePath)) {
				
				ProjectFile savedFile = new ProjectFile(risQueryInfoFile.getString("FilePath"), 
							filePath.substring(filePath.lastIndexOf("\\")+1,filePath.length()),
							risQueryInfoFile.getString("DataCreazione"),
							risQueryInfoFile.getString("UltimaModifica"),
							risQueryInfoFile.getDouble("GrandezzaFile")							
							);
				connectDB.close();
				
				return savedFile;
			}
		}
		
		return null;
	}
	
	static boolean userCookieIsExpired(HttpServletRequest request) {
		for(Cookie cookie : request.getCookies()) {
			if(cookie.getName().equals("loggedUser")) {
				return true;
			}	
		}
		return false;
	}

	static boolean isProjectOwner(String idUtente, String idProgetto, Connection connectDB, ResourceBundle resource) throws SQLException {
		
		PreparedStatement getOwnerProject = connectDB.prepareStatement(resource.getString("getOwnerProjectQuery"));
		getOwnerProject.setString(1, idProgetto);
		getOwnerProject.setString(2, idUtente);
		ResultSet risQueryOwnProj = getOwnerProject.executeQuery();	
        if(risQueryOwnProj.next()) {
        	return true;
        }
		return false;
	}
}
