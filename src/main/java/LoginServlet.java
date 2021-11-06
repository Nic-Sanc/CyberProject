import java.io.IOException;
import java.sql.Connection;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JOptionPane;
import javax.servlet.RequestDispatcher;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ResourceBundle resource;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ResourceBundle.clearCache();
		resource = ResourceBundle.getBundle("resources");
		String page = "Login.jsp";
		
		//Connessione al database
		Connection connectDB = Service.connectToJDBC(resource.getString("schema_name"));
	    if(connectDB != null) {
	    	if(Service.sanitizeString(request.getParameter("email").getBytes(), "email")) {
	    		Utente loggedUser = Service.pullInfoUtente(connectDB, request.getParameter("email").toString(), response, resource);
	        	
				if(Service.logInSystem(connectDB, request, response, resource) != null){
		    		String msg = "Login effettuato con successo!";
		    		page =  "HomePage.jsp";
			    	Service.showOnlyAlert(msg, "INFO", JOptionPane.INFORMATION_MESSAGE);
			    	
			    	//HTTP Session
			    	HttpSession session = request.getSession();
			    	if(session.getAttribute("user") != null) {
			    		session.removeAttribute("user");
			    		session.invalidate();
			    		session = request.getSession(true);
			    	}
			    	//Tempo del session timeout pari a 15min
			    	session.setMaxInactiveInterval(60*Integer.parseInt(resource.getString("timeoutTime")));
			    	session.setAttribute("user", loggedUser.getIdUtente());
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

}
