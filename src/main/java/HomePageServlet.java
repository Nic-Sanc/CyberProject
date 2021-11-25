import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JOptionPane;

/**
 * Servlet implementation class HomePageServlet
 */
@WebServlet("/HomePageServlet")
public class HomePageServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
       
	private ResourceBundle resource;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HomePageServlet() {
        super();
        ResourceBundle.clearCache();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ResourceBundle.clearCache();
		resource = ResourceBundle.getBundle("resources");
		
		if(request.getParameter("Logout")!=null)
		{
			//Rimozione dei cookie e della sessione http
			HttpSession session = request.getSession();
			session.removeAttribute("user");
			session.invalidate();
			
			for(Cookie cookie : request.getCookies()) {
				System.out.println(cookie.getName());
				cookie.setValue(null);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
			
			RequestDispatcher dd=request.getRequestDispatcher("Login.jsp");
			dd.forward(request, response);
		}else if(request.getRequestedSessionId() != null &&
				request.isRequestedSessionIdValid() &&
				Service.userCookieIsExpired(request)) {
			Connection connectDB = Service.connectToJDBC(resource.getString("schema_name"));
			
			if(request.getParameter("openFile")!=null) {
				try {	
					if(Service.checkValidityFile(request.getParameter("openFile"), resource, connectDB)) {
						Service.showAlert("File valido! Apertura in corso...", "INFO", JOptionPane.INFORMATION_MESSAGE, request.getParameter("openFile"), response);
					}else {
						String msg = "Il file di progetto è stato corrotto! Eliminare ed in seguito ricaricare il progetto.";
						Service.showAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE, "HomePage.jsp", response);
					}
				} catch (IOException | SQLException | ParseException e) {
			    	Service.showOnlyAlert(e.getMessage(), "ERRORE", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}else if (request.getParameter("deleteFile")!=null) {
				try {
					if(Service.isProjectOwner(request.getSession().getAttribute("user").toString(), request.getParameter("deleteFile"), connectDB, resource)) {
						PreparedStatement deleteStatement = connectDB.prepareStatement(resource.getString("deleteProjectQuery"));
						deleteStatement.setString(1, request.getParameter("deleteFile"));
						deleteStatement.executeUpdate();
						Service.showAlert("Rimozione progetto effettuata!", "INFO", JOptionPane.INFORMATION_MESSAGE, "HomePage.jsp", response);
					}else {
						String msg = "AUTORIZZAZIONE NEGATA! Non si possiedono gli adeguati privilegi.";
						Service.showAlert(msg, "ERRORE", JOptionPane.ERROR_MESSAGE, "HomePage.jsp", response);
					}

				} catch (SQLException e) {
					Service.showOnlyAlert(e.getMessage(), "ERRORE", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}  	
			}
		}else {
			Service.showAlert("Accesso negato!", "ERRORE", JOptionPane.ERROR_MESSAGE, "HomePage.jsp", response);
		}
	}
}
