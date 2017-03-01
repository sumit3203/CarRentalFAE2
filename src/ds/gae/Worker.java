package ds.gae;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.taskqueue.DeferredTask;

import ds.gae.entities.Quote;
import ds.gae.view.JSPSite;

public class Worker extends HttpServlet 
implements DeferredTask{

	private static final long serialVersionUID = -7058685883212377590L;

	private byte[] payload;
	private String id;

	public Worker(byte[] payload, String id) {
		// TODO Auto-generated constructor stub
		this.payload = payload;
		this.setId(id);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

			HttpSession session = req.getSession();
			session.setAttribute("quotes", new HashMap<String, ArrayList<Quote>>());

			// TODO
			// If you wish confirmQuotesReply.jsp to be shown to the client as
			// a response of calling this servlet, please replace the following line 
			// with resp.sendRedirect(JSPSite.CONFIRM_QUOTES_RESPONSE.url());
			resp.sendRedirect(JSPSite.CREATE_QUOTES.url());
	//	} catch (ReservationException e) {
		//	session.setAttribute("errorMsg", ViewTools.encodeHTML(e.getMessage()));
			//resp.sendRedirect(JSPSite.RESERVATION_ERROR.url());
	//	} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
	//	}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean success=false;
		try {
			HashMap<String, ArrayList<Quote>> allQuotes = (HashMap<String, ArrayList<Quote>>) deserialize(payload);
			ArrayList<Quote> qs = new ArrayList<Quote>();
            
			System.out.println(allQuotes.toString());

			for (String crcName : allQuotes.keySet()) {
				qs.addAll(allQuotes.get(crcName));
			}
			CarRentalModel.get().confirmQuotes(qs);
			System.out.println("SUCCESS");
			success=true;
			//TODO inform client
		} catch (ClassNotFoundException e1) {
			//e1.printStackTrace();
		} catch (ReservationException e) {
			System.out.println("FAILURE");
			//TODO inform client
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 String msgBody="RESERVATION FAILURE";
		if(success)
		{
		msgBody = "RESERVATION SUCCESSFUL";
		}
		// WE CAN INFORM ABOUT SUCCESS AND FAILURE BY MAIL TO CLIENT
	/*	Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

       

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("abcd@gmail.com", "AutoReply Mail"));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress("user@gmail.com", " Mr/MS"));
            msg.setSubject("Quote Status");
            msg.setText(msgBody+"Please don't repl");
            Transport.send(msg);

        } catch (AddressException e) {
      
        } catch (MessagingException e) {
    
        }*/
	}
	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
