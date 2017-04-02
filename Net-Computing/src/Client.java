import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public  class Client extends java.rmi.server.UnicastRemoteObject implements ServerRemote{
	private int clientID;
	private static BufferedReader in;
	private JFrame frame = new JFrame("Client");
    private static JTextArea messageArea = new JTextArea(8, 60);
    private Date date;
    
    public Client() throws RemoteException{
    	messageArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
    	try {
    		Registry registry = LocateRegistry.getRegistry(2002);
            ServerRemote server = (ServerRemote) registry.lookup("Date");
            this.date = server.getDate();
        }
    	catch (java.io.IOException e) {
    		// I/O Error or bad URL
        }
    	catch (NotBoundException e) {
    		//NiftyServer isn't registered
    	}
    }

    public static void main(String[] args) throws Exception {
	    Client client = new Client();
	    client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    client.frame.pack();
	    client.frame.setVisible(true);
	    System.out.println("Ftanw5");
	    Socket socket = new Socket("localhost", 9899);
	    System.out.println("Ftanw4");
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
        // Consume the initial welcoming messages from the server
        System.out.println("Ftanw");
        for (int i = 0; i < 1; i++) {
        	String sr = in.readLine();
        	String[] co = sr.split(" ");
            client.setClientID(Integer.parseInt(co[1]));
            System.out.println("Ftanw2");
        	messageArea.append(sr +" "+client.date+"\n");
        }
        System.out.println("Ftanw3");
        while(true){
        	Mem mem = null;
            CpuPerc cpuperc = null;
            Sigar sigar = new Sigar();

            try {
                mem = sigar.getMem();
                cpuperc = sigar.getCpuPerc();      
            } catch (SigarException se) {
                se.printStackTrace();
            }
            float memoryusage = (float) mem.getUsedPercent();
            float cpuusage = (float) cpuperc.getCombined()*100;
            //System.out.println("CPU usage:"+cpuusage+" memory usage:"+memoryusage);
            if(cpuusage>30 ||memoryusage>50){
            	ReportObject ro = new ReportObject(memoryusage,cpuusage,client.clientID);
            	outToServer.writeObject(ro);
            	messageArea.append("CPU usage: "+ro.getCpuusage()+"% and Memory usage: "+ro.getMemoryusage()+"% \n");
            	Thread.sleep(5000);
            }
        }
    }
	@Override
	public Date getDate() throws RemoteException {
		return null;
	}

	@Override
	public ReportObject ReportObjet(float memoryusage, float cpuusage,int clientID) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}
}