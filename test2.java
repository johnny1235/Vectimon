
import com.sun.jmx.defaults.ServiceName;
import com.sun.jmx.snmp.Timestamp;

import java.awt. *;
import java.io.PrintWriter;
import java.util. *; 
import java.text. *; 
import javax.telephony.Address;
import javax.telephony.AddressEvent;
import javax.telephony.AddressListener;
import javax.telephony.AddressObserver;
import javax.telephony.Call;
import javax.telephony.CallEvent;
import javax.telephony.CallListener;
import javax.telephony.CallObserver;
import javax.telephony.Connection;
import javax.telephony.ConnectionEvent;
import javax.telephony.JtapiPeer; 
import javax.telephony.JtapiPeerFactory; 
import javax.telephony.JtapiPeerUnavailableException; 
import javax.telephony.Provider; 
import javax.telephony.ProviderObserver; 
import javax.telephony.Terminal;

import com.avaya.jtapi.tsapi.LucentTerminal;
import com.avaya.jtapi.tsapi.TsapiPeer;
import com.avaya.jtapi.tsapi.adapters.ACDAddressListenerAdapter;

import javax.telephony.Address;
import javax.telephony.Call;
import javax.telephony.CallEvent;
import javax.telephony.CallListener;
import javax.telephony.Connection;
import javax.telephony.ConnectionEvent;
import javax.telephony.JtapiPeer;
import javax.telephony.JtapiPeerFactory;
import javax.telephony.JtapiPeerUnavailableException;
import javax.telephony.MetaEvent;
import javax.telephony.PlatformException;
import javax.telephony.Provider;
import javax.telephony.ProviderEvent;
import javax.telephony.ProviderListener;
import javax.telephony.Terminal;
import javax.telephony.TerminalConnection;
import javax.telephony.TerminalConnectionEvent;
import javax.telephony.callcenter.ACDAddress;
import javax.telephony.callcenter.Agent;
import javax.telephony.callcenter.CallCenterProvider;
import javax.telephony.callcontrol.CallControlConnectionEvent;
import javax.telephony.callcontrol.CallControlTerminalConnection;
import javax.telephony.callcontrol.CallControlTerminalConnectionEvent;
import javax.telephony.callcontrol.CallControlTerminalConnectionListener;
import javax.telephony.media.MediaTerminalConnection;
import javax.telephony.events.*;
import javax.telephony.callcontrol.*;

import javax.telephony.callcenter.ACDAddress;
import javax.telephony.callcenter.ACDAddressEvent;
import javax.telephony.callcenter.Agent;
import javax.telephony.callcenter.AgentTerminal;
import javax.telephony.callcenter.CallCenterProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@SuppressWarnings("unused")
public class test2{
	
	@SuppressWarnings("null")
	public static void main(String[] args) {
		String[] services;
		Address[] addresses;
		
		/* Config file
		 * Skill extension; db name; IP to DB;DB user; DB password;log location; log level; AES user; AES password;
		 * 0 = skill extension
		 * 1 = db name
		 * 2 = IP
		 * 3 = DB user
		 * 4 = Db password
		 * 5 = log level -- none, fine or finest
		 * 6 = AES user
		 * 7 = AES password
		 */
	    	
		try
		{
		    Date d = new Date();
		    
		    Scanner read = new Scanner (new File("config.txt"));
		    read.useDelimiter(";");
		   
			String[] config = new String[20];
			int num = 0;
			while(read.hasNext())
			{
				config[num] = read.next();
				num++;
			}
			System.out.println("Log Level: " + config[5]);
			
			writer ww = new writer("events");
		    //Vectimon connection

			 JtapiPeer peer = JtapiPeerFactory.getJtapiPeer("com.avaya.jtapi.tsapi.TsapiPeer");
			 services = peer.getServices(); // get tlinks
			 System.out.println(services[0]);
		     System.out.println(services[1]);
			 if(services == null)
				 {
				 	if("finest".equals(config[5]))
				 	{
					    Date d1 = new Date();
				 		 ww.writeData(d1 + ":  AES Connection not working");
				 	}
		             ww.close();
					 System.exit(0);
				 }
		
		    Provider myprovider = peer.getProvider(services[0] + ";login=" + config[6] +";passwd=" + config[7]); 
		    
			Address hunt = myprovider.getAddress(config[0]); // Hunt Group
		
		    ACDAddress test2 = (ACDAddress) hunt;
		    Agent[] agent = test2.getLoggedOnAgents();
		    
		    // Add Listener to hunt address to log agent Events
	      ACDAddressListener boss5 = new ACDAddressListener(agent, ww);
     	  test2.addAddressListener(boss5);
           	   
		    //wait
		    int a = 0;
			Scanner sc = new Scanner(System.in);
		    while(a!=5)
			{
				 System.out.println("Enter 5 to exit");  
				 a = sc.nextInt();

			}		
		
		    // Exit
		    if(a == 5) // Close program and remove Listener
		    {
		        
		     	test2.removeAddressListener(boss5);
	            ww.close();
		    	System.out.println("Done!");
		    	 System.exit(0);
		    }
		    
		    // Waiting time
		
		}
		catch (Exception excp)
		{
		 System.out.println("Exception during getting JtapiPeer: " + excp);
		} 
	}



}
