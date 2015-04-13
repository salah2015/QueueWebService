package com.webservice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jms.*;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class GcdCalculator {

	private final String JMS_CONNECTION_FACTORY = "ConnectionFactory";
	private final String JMS_QUEUE = "queue/test";
	// proposed queue to store computed GCDs
	// private final String JMS_QUEUE_GCD = "queue/gcd";

	private QueueConnection conn;
	private QueueSession session;
	private Queue queue;

	private List<Integer> gcdList = new ArrayList<Integer>();

	@WebMethod
	@WebResult(partName = "gcd")
	public int gcd() throws JMSException {
		try {
			createConnection(QueueSession.AUTO_ACKNOWLEDGE, JMS_QUEUE);

			QueueReceiver queueReceiver = session.createReceiver(queue);

			TextMessage textMessage = (TextMessage) queueReceiver.receive(1);
			int num1 = Integer.parseInt(textMessage.getText());
			textMessage = (TextMessage) queueReceiver.receive();
			int num2 = Integer.parseInt(textMessage.getText());

			//queueReceiver.close();
			closeConnections();
			
			System.out.println("Find GCD of: " + num1 + ", " + num2);
			int gcd = findGCD(num1, num2);
			
			// future improvement: add calculated GCD to another JMS queue instead of list
			gcdList.add(gcd);
			return gcd;
		} catch (Exception e) {
			closeConnections();
			return 0;
		}
	}

	@WebMethod
	@WebResult(partName = "gcd")
	public List<Integer> gcdList() {
		// future improvement: read all computed GCDs from JMS queue
		return gcdList;
	}

	@WebMethod
	@WebResult(partName = "gcdsum")
	public int gcdSum() {
		// future improvement: read all computed GCDs from JMS queue and return SUM
		int sum = 0;
		Iterator<Integer> i = gcdList.iterator();
		while (i.hasNext()) {
			sum += i.next();
		}
		return sum;
	}

	private int findGCD(int number1, int number2) {
		// base case
		if (number2 == 0) {
			return number1;
		}
		return findGCD(number2, number1 % number2);
	}

	private void createConnection(int type, String queueName) throws JMSException, NamingException {
		InitialContext iniCtx = new InitialContext();
		QueueConnectionFactory qcf = (QueueConnectionFactory) iniCtx.lookup(JMS_CONNECTION_FACTORY);
		conn = qcf.createQueueConnection();
		queue = (Queue) iniCtx.lookup(queueName);
		session = conn.createQueueSession(false, type);
		conn.start();
	}

	private void closeConnections() {
		try {
			session.close();
			conn.close();
		} catch (JMSException jmse) {
			// log error
		}
	}

}
