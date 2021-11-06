import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ResourceBundle;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JOptionPane;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

/**
 * Servlet implementation class AddProjectIdeaServlet
 */
@WebServlet("/AddProjectIdeaServlet")
public class AddProjectIdeaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private ResourceBundle resource;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddProjectIdeaServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ResourceBundle.clearCache();
		resource = ResourceBundle.getBundle("resources");
		String page = "AddProjectIdea.jsp";
		
		if(Service.sanitizeString(request.getParameter("idea").getBytes(), "alphaNumb")){
			//Connessione al database
			Connection connectDB = Service.connectToJDBC(resource.getString("schema_name"));
		    if(connectDB != null) {
				HttpSession session = request.getSession();
				String idProject = Service.generateAlphaNumericString(Integer.parseInt(resource.getString("idLenght")));
				try {
					if(Service.isTxt(request.getParameter("projectTxt"))) {
						if(aggiuntaNuovoProgetto(connectDB, idProject, request, response, (String)session.getAttribute("user"))) {
							String msg = "Creazione idea di progetto effettuata!";
							page = "HomePage.jsp";
							Service.showOnlyAlert(msg, "INFO", JOptionPane.INFORMATION_MESSAGE);
						}
					}else {
						String msg = "File inserito non corrisponde al tipo richiesto!";
						Service.showOnlyAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE);
						
					}
				} catch (IOException | SAXException | TikaException e) {
					Service.showOnlyAlert(e.getMessage(), "ERRORE", JOptionPane.ERROR_MESSAGE);
				}    
		    }else {
		    	//Connection sbagliata non ha avuto successo
		    	String msg = "Connessione con il database fallita!";
		    	page = "Login.jsp";
		    	Service.showOnlyAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE);
		    }
		}else {
			Service.showOnlyAlert("Il nome del progetto contiene caratteri non consentiti!", "ERRORE", JOptionPane.ERROR_MESSAGE);
		}
	    
	    RequestDispatcher dd=request.getRequestDispatcher(page);
		dd.forward(request, response);
	}

	synchronized boolean aggiuntaNuovoProgetto(Connection connectDB, String idProject, HttpServletRequest request, HttpServletResponse response, String userId) throws IOException, ServletException {
		//Salvo l'utente sul db con il preparedStatement
		PreparedStatement queryStatement;
		
		Path selectedFile = Paths.get(request.getParameter("projectTxt"));
		
		BasicFileAttributes attr = Files.readAttributes(selectedFile, BasicFileAttributes.class);
		FileTime creationTime = attr.creationTime();
		FileTime modifiedTime = attr.lastModifiedTime();
		
		try {
			queryStatement = connectDB.prepareStatement(resource.getString("registerProjectQuery"));
			//Estrazione dei valori del form
			queryStatement.setString(1, idProject);//IdProposta
			queryStatement.setString(2, request.getParameter("idea"));//NomeProposta
			queryStatement.setString(3, request.getParameter("projectTxt"));//FilePath
			queryStatement.setString(4, creationTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(Service.formatoData));//DataCreazione
			queryStatement.setString(5, modifiedTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(Service.formatoData));//UltimaModifica
			queryStatement.setString(6, userId);//CodStudente
			queryStatement.execute();
			
			connectDB.close();
			
			return true;
		} catch (SQLException e) {
			//Errore nella creazione dell'idea di progetto
    		String msg = "Errore creazione idea di progetto! Si prega di ricompilare il modulo di creazione.";
    		System.out.println(e.getMessage());
	    	Service.showOnlyAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE);
		}		
		return false;
	}

}
