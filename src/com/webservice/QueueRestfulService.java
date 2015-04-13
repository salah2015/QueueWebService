package com.webservice;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/queueservice")
public class QueueRestfulService {

	private final String SUCCESS = "SUCCESS";
	private final String JMS_CONNECTION_FACTORY = "ConnectionFactory";
	private final String JMS_QUEUE = "queue/test";

	private QueueConnection conn;
    private QueueSession session;
    private Queue queue;

	@GET
	@Produces("text/plain")
	@Path("push")
	public String push(@QueryParam("i1") int i1, @QueryParam("i2") int i2) {
		System.out.println("Push "+ i1 + " and " + i2);
		return sendMessage(i1, i2);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("list")
	public List<Integer> list() {
		List<Integer> results =  readAllMessages();
		System.out.println(results.toString());
		return results;				
	}

	private String sendMessage(int num1, int num2) {
		try {
			createConnection(QueueSession.AUTO_ACKNOWLEDGE);

			// add first number to the queue
			QueueSender sender = session.createSender(queue);
			TextMessage message = session.createTextMessage();
			message.setText(String.valueOf(num1));
			sender.send(message);

			// add second number to the queue
			message.setText(String.valueOf(num2));
			sender.send(message);

			//sender.close();
			closeConnections();
		}
		catch(Exception e) {
			return e.getMessage();
		}
		return SUCCESS;
	}
	
	private List<Integer> readAllMessages() {
		List<Integer> numbers = new ArrayList<Integer>();
		try {
			createConnection(QueueSession.CLIENT_ACKNOWLEDGE);

			QueueBrowser browser = session.createBrowser(queue);
			TextMessage textMessage = null;

			// browse the messages
		    Enumeration e = browser.getEnumeration();
		                                                                          
		    // add messages to the result list
		    while (e.hasMoreElements()) {
		       textMessage = (TextMessage) e.nextElement();
		       numbers.add(Integer.parseInt(textMessage.getText()));
		    }
		    
		    System.out.println(queue + " has " + numbers.size() + " messages");		    
		    closeConnections();
		}
		catch(Exception e) {
			closeConnections();
			// log error
		}
		return numbers;
	}
	
	private void createConnection(int type) throws JMSException, NamingException
	{
		InitialContext iniCtx = new InitialContext();
		QueueConnectionFactory qcf = (QueueConnectionFactory) iniCtx.lookup(JMS_CONNECTION_FACTORY);
		conn = qcf.createQueueConnection();
		queue = (Queue) iniCtx.lookup(JMS_QUEUE);
		session = conn.createQueueSession(false, type);
		conn.start();
	}
	
	private void closeConnections() {
		try {
			session.close();
			conn.close();
		}
		catch (JMSException jmse) {
			// log error
		}
	}
}
