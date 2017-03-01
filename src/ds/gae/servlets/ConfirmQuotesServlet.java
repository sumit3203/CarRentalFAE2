package ds.gae.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import ds.gae.Worker;
import ds.gae.entities.Quote;
import ds.gae.view.ViewTools;
import ds.gae.view.JSPSite;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.labs.repackaged.com.google.common.collect.Multiset.Entry;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

@SuppressWarnings("serial")
public class ConfirmQuotesServlet extends HttpServlet 
					implements java.io.Serializable {
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		//HashMap<String, ArrayList<Quote>> allQuotes = (HashMap<String, ArrayList<Quote>>) session.getAttribute("quotes");
	
		try {
			
			byte[] payload = serialize(session.getAttribute("quotes"));
			// TODO
	        // Add the task to the default queue.
			
			//TaskOptions taskOptions = TaskOptions.Builder.withUrl("/worker").payload(payload);
			//taskOptions.payload(payload);
			
			Worker task = new Worker(payload,session.getId());
			// Set instance variables etc as you wish
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(withPayload(task));
	        
	       // session.setAttribute("quotes", new HashMap<String, ArrayList<Quote>>());
	        
			// If you wish confirmQuotesReply.jsp to be shown to the client as
			// a response of calling this servlet, please replace the following line 
			// with resp.sendRedirect(JSPSite.CONFIRM_QUOTES_RESPONSE.url());
			resp.sendRedirect(JSPSite.CREATE_QUOTES.url());
		} catch (IOException e) {
			session.setAttribute("errorMsg", ViewTools.encodeHTML(e.getMessage()));
			resp.sendRedirect(JSPSite.RESERVATION_ERROR.url());				
		}
	}
	
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }
}
