import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.swing.JOptionPane;

/**
 * Application Lifecycle Listener implementation class HttpSessionListener
 *
 */
@WebListener
public class HttpSessionListener implements javax.servlet.http.HttpSessionListener, HttpSessionActivationListener {

    /**
     * Default constructor. 
     */
    public HttpSessionListener() {
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent se)  { 
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent se)  {
    	String msg;
    	
		msg = "Sessione distrutta! Logout effettuato o tempo di inattività superato.";
		try {
			Service.showOnlyAlert(msg,"ERRORE",JOptionPane.ERROR_MESSAGE);
		} catch (IOException | ServletException e1) {
			e1.printStackTrace();
		}
    }

	@Override
	public void sessionDidActivate(HttpSessionEvent se) {
		
	}

	@Override
	public void sessionWillPassivate(HttpSessionEvent se) {
		
	}
	
}
