import javax.telephony.*;
import javax.telephony.Connection;
import javax.telephony.callcenter.Agent;
import javax.telephony.callcenter.AgentTerminal;
import javax.telephony.callcenter.events.RouteEvent;
import javax.telephony.callcenter.events.RouteSessionEvent;
import javax.telephony.events.*;

import com.avaya.jtapi.tsapi.LucentRouteSession;
import com.avaya.jtapi.tsapi.LucentTerminal;
import com.avaya.jtapi.tsapi.LucentV5CallInfo;

import javax.telephony.callcontrol.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.Date;
import java.util.Scanner;

/*
 * The MyCallCtlInTermConnListener class implements the CallControlTermConnListener and
 * receives all Call-related events.
 */

public class TerminalListener implements CallControlTerminalConnectionListener {
		 
	Provider myProvider = null;
	String termAgentID = "";
	PrintWriter writer;
	
	String DBHost;
	String DBDb;
	String DBUser;
	String DBPasswd;
	String[] config = new String[10];
	writer ww2;

	public TerminalListener(Provider boss, String id, writer write) throws FileNotFoundException, UnsupportedEncodingException
	{
		 termAgentID = id;
		 myProvider = boss;
		 ww2 = write;
		 connectDB();
	}
	public TerminalListener(Provider boss)
	{
		 myProvider = boss;
	}
	
	public void connectDB() throws FileNotFoundException, UnsupportedEncodingException
	{
		Scanner read = null;
		try {
		    Date d = new Date();
			read = new Scanner (new File("config.txt"));
			read.useDelimiter(";");
			int num = 0;
			while(read.hasNext())
			{
				config[num] = read.next();
				num++;
			}
			DBHost = config[2];
			DBDb = config[1];
			DBUser = config[3];
		    DBPasswd = config[4];
			
		} catch (FileNotFoundException e) {
			if("finest".equals(config[5]))
		    {
			ww2.writeData("Config File not found");
		    }
		}
	
	}
	
	private void insert(String ucid, String agentID) throws SQLException, ClassNotFoundException, FileNotFoundException, UnsupportedEncodingException
	{			
	    Date d = new Date();
		Class.forName("org.postgresql.Driver");
	
		
		 // Database connection


		String connectionUrl = "jdbc:postgresql://" + DBHost + "/" + DBDb + "?user=" + DBUser + "&password=" + DBPasswd + "";
		java.sql.Connection con = DriverManager.getConnection(connectionUrl);
		
		Statement statement = con.createStatement();
		Statement statement2 = con.createStatement();
		String Query2 = "select 1 from calls where agentid like '"+agentID+"' AND ucid like '"+ucid+"'";

		// check if entry exists 
		ResultSet result = statement2.executeQuery(Query2);
		if(result.next())
		{       // data exist
				con.close();
        }

       else
        {//data not exist
    	   statement.executeUpdate("INSERT INTO calls (agentid, ucid)" + "VALUES ('"+agentID+"', '"+ucid+"');");
 	       if("finest".equals(config[5]))
			{
   	    	ww2.writeData(d + ":  Inserted: "+ucid+" "+ agentID);
			}
    	   con.close();
        } 
		
	}
    private String getTerminalName(TerminalConnectionEvent event) {
        String name = null;
        try {
          TerminalConnection termConn = event.getTerminalConnection();
          Terminal term = termConn.getTerminal();
          name = term.getName();
        } catch (Exception excp) {
          // Handle Exceptions
		}
		return name;
	}
    
    private String getTerminalName2(ConnectionEvent event) {
        String name = null;
        try {
        	
        	Call testCall = event.getCall();
        	Connection good = event.getConnection();
        	TerminalConnection[] hot = good.getTerminalConnections();
            Terminal term = hot[0].getTerminal();
            name = term.getName();
            

        } catch (Exception excp) {
          // Handle Exceptions
		}
		return name;
	}
   
	public void terminalConnectionActive(TerminalConnectionEvent event) {
	}

	public void terminalConnectionCreated(TerminalConnectionEvent event) {
	}

	public void terminalConnectionDropped(TerminalConnectionEvent event) {
	}

	public void terminalConnectionPassive(TerminalConnectionEvent event) {
	}

	public void terminalConnectionRinging(TerminalConnectionEvent event) {
	}

	public void terminalConnectionUnknown(TerminalConnectionEvent event) {
	}

	public void connectionAlerting(ConnectionEvent event) {
	}

	public void connectionConnected(ConnectionEvent event) {
	}
	
	public void connectionCreated(ConnectionEvent event) {
	}

	public void connectionDisconnected(ConnectionEvent event) {
	}
	
	public void connectionFailed(ConnectionEvent event) {
	}

	public void connectionInProgress(ConnectionEvent event) {
	}
	public void connectionUnknown(ConnectionEvent event) {
	}

	public void callActive(CallEvent event) {
	}

	public void callInvalid(CallEvent event) {
	}

	public void callEventTransmissionEnded(CallEvent event) {
	}

	public void singleCallMetaProgressStarted(MetaEvent event) {
	}

	public void singleCallMetaProgressEnded(MetaEvent event) {
	}

	public void singleCallMetaSnapshotStarted(MetaEvent event) {
	}

	public void singleCallMetaSnapshotEnded(MetaEvent event) {
	}

	public void multiCallMetaMergeStarted(MetaEvent event) {
	}

	public void multiCallMetaMergeEnded(MetaEvent event) {
	}

	public void multiCallMetaTransferStarted(MetaEvent event) {
	}

	public void multiCallMetaTransferEnded(MetaEvent event) {
	}

	public void connectionAlerting(CallControlConnectionEvent event) {
	}
	
	public void connectionDialing(CallControlConnectionEvent event) {
	}
	
	public void connectionDisconnected(CallControlConnectionEvent event) {
	}
	
	public void connectionEstablished(CallControlConnectionEvent event) {
	Call call = event.getCall(); 
	String ucid = ((LucentV5CallInfo)call).getUCID();
    Date d = new Date();

	try {
		insert(ucid, termAgentID);
		if("finest".equals(config[5]))
	    {
			ww2.writeData(d + ":  TerminalConnection to Terminal: " 
			+ getTerminalName2(event)+" Agent ID:" + termAgentID +" UCID: "+ucid + " is established"); //AgentID & UCID Log file
	    }
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
	    if("finest".equals(config[5]))
	    {
	    	try {
				ww2.writeData(d + ":  Cant find Jar file for Db connection");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    }
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		if("finest".equals(config[5]))
	    {
			try {
				ww2.writeData(d + ":  No database connection...");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		e.printStackTrace();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}
	
	public void connectionFailed(CallControlConnectionEvent event) {
	}
	
	public void connectionInitiated(CallControlConnectionEvent event) {
	}
	
	public void connectionNetworkAlerting(CallControlConnectionEvent event) {
	}
	
	public void connectionNetworkReached(CallControlConnectionEvent event) {
	}
	
	public void connectionOffered(CallControlConnectionEvent event) {
	}
	
	public void connectionQueued(CallControlConnectionEvent event) {
	}
	
	public void connectionUnknown(CallControlConnectionEvent event) {
	}

	public void terminalConnectionBridged(CallControlTerminalConnectionEvent event) {
	}

	public void terminalConnectionDropped(CallControlTerminalConnectionEvent event) {
		
	}

	public void terminalConnectionHeld(CallControlTerminalConnectionEvent event) {
	}

	public void terminalConnectionInUse(CallControlTerminalConnectionEvent event) {
	}
	
	public void terminalConnectionRinging(CallControlTerminalConnectionEvent event) {
		
		try {
		    Date d = new Date();
			if("finest".equals(config[5]))
		    {
			ww2.writeData(d + ":  TerminalConnection to Terminal: " 
				+ getTerminalName(event) + " CAll ID: "+event.getID() + " is RINGING");
		    }
			
			/* ---------------------  Auto answer -------------------------
			final TerminalConnection termconn = event.getTerminalConnection();
	     	Runnable runnable = new Runnable() {
				public void run() {
					try {
						termconn.answer();
				  	} 
					catch (Exception excp) {
			    		// Handle answer exceptions
				  	}
				};
			};
			Thread thread = new Thread(runnable);
			thread.start();*/
        } 
		catch (Exception excp) {
            // Handle Exceptions;
        }
	}

	public void terminalConnectionTalking(CallControlTerminalConnectionEvent event) {
		
	}

	public void terminalConnectionUnknown(CallControlTerminalConnectionEvent event) {
	}
}
