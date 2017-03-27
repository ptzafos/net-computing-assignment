import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

public class Client extends java.rmi.server.UnicastRemoteObject implements ServerRemote{

    private BufferedReader in;
    private PrintWriter out;
    
    public Client() throws RemoteException{
    	try {
    		Registry registry = LocateRegistry.getRegistry(2002);
            ServerRemote server = (ServerRemote) registry.lookup("Date");
            System.out.println( server.getDate() );
        }
    	catch (java.io.IOException e) {
    		// I/O Error or bad URL
        }
    	catch (NotBoundException e) {
    		//NiftyServer isn't registered
    	}
    }

    public static void main(String[] args) throws Exception {
		Connection connection = null;
			// Producer
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					"tcp://localhost:61616");
			connection = connectionFactory.createConnection();
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue("customerQueue");
    	Client client = new Client();
        while(true){
        	
        	String payload = "Important Task";
        	Message msg = session.createTextMessage(payload);
        	MessageProducer producer = session.createProducer(queue);
        	System.out.println("Sending text '" + payload + "'");
        	producer.send(msg);
        	Thread.sleep(1*60*1000);
        }
    }
	@Override
	public Date getDate() throws RemoteException {
		return null;
	}
}