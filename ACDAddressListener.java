
import javax.telephony.*;
import javax.telephony.callcenter.Agent;
import javax.telephony.callcenter.AgentTerminal;
import javax.telephony.callcenter.events.RouteEvent;
import javax.telephony.callcenter.events.RouteSessionEvent;
import javax.telephony.events.*;

import com.avaya.jtapi.tsapi.LucentRouteSession;
import com.avaya.jtapi.tsapi.LucentTerminal;
import com.avaya.jtapi.tsapi.LucentV5CallInfo;
import com.avaya.jtapi.tsapi.adapters.ACDAddressListenerAdapter;

import javax.telephony.callcontrol.*;
import javax.telephony.callcenter.ACDAddressEvent; 

import com.sun.jmx.defaults.ServiceName;

import java.awt. *;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util. *; 
import java.text. *;

import com.avaya.jtapi.tsapi.LucentTerminal;
import com.avaya.jtapi.tsapi.TsapiPeer;
import com.avaya.jtapi.tsapi.adapters.ACDAddressListenerAdapter;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.Date;
import java.util.Scanner;

// New ACD Class for Listener
class ACDAddressListener extends ACDAddressListenerAdapter{

	PrintWriter writer;
	String[] config = new String[10];
	Agent[] currentLoggedOn;
	Vector<Terminal> lias = new Vector<Terminal>();
	Vector<TerminalListener> liaListener = new Vector<TerminalListener>();
	writer ww2;
	
    public ACDAddressListener(Agent[] agent, writer ww) throws FileNotFoundException, UnsupportedEncodingException 
    {
        Date dd = new Date();
    	ww2 = ww;
    	Scanner read = null;
		try {
			read = new Scanner (new File("config.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
		}
		read.useDelimiter(";");
		int num = 0;
		while(read.hasNext())
		{
			config[num] = read.next();
			num++;
		}
		
		currentLoggedOn = agent;
    	if(agent != null)
    	{
	    	for(int i=0; i<agent.length;i++)
	    	{
	    		lias.add(agent[i].getAgentTerminal());
	    		TerminalListener addListener = new TerminalListener(agent[i].getAgentAddress().getProvider(), agent[i].getAgentID(), ww2);
	        	liaListener.add(addListener);
	        	try {
					lias.get(i).addCallListener(liaListener.lastElement());
					if(!"none".equals(config[5]))
					{
						ww2.writeData(dd + ":  Agent logged in: " + agent[i].getAgentID());
					}
	
				} catch (ResourceUnavailableException e) {
					if("finest".equals(config[5]))
					{
						ww2.writeData(dd + ":  Error adding agent already Logged on : " + agent[i].getAgentID());
					}
					e.printStackTrace();
				} catch (MethodNotSupportedException e) {
					if("finest".equals(config[5]))
					{
						ww2.writeData(dd + ":  Error adding agent already Logged on : " + agent[i].getAgentID());
					}
					e.printStackTrace();
				}
	    	}
    	}
    }
	
	

	public void acdAddressBusy(ACDAddressEvent event)
    {
    }
	
	 
    public void acdAddressLoggedOff(ACDAddressEvent event)
    {
    	try{
    	    Date dd = new Date();
	    	if("finest".equals(config[5]))
			{
	    		ww2.writeData(dd + ":  Received ACD Addr Logged Off Ev - Agent: " + event.getAgent().getAgentTerminal().getName() + "\n");
			}
	        
	        if(lias.contains(event.getAgent().getAgentTerminal()))
	    	{
	      	
	      	 	int num = lias.indexOf(event.getAgent().getAgentTerminal());
	        		
	        	for(int i = 0; i <liaListener.size(); i++)
	        	{
	        		if(liaListener.get(i).termAgentID == event.getAgent().getAgentID())
	        		{
	        			if(!"none".equals(config[5]))
						{
	        				ww2.writeData(dd + ":  Agent removed: "+ event.getAgent().getAgentID());

						}
	                	lias.get(num).removeCallListener(liaListener.get(i));
	        			liaListener.remove(i);
	        		}
	        	}
	       
	        	lias.remove(event.getAgent().getAgentTerminal());
	        	        	
	    	}
	    	
	    	else
	    	{
	    		if("finest".equals(config[5]))
				{
	    			ww2.writeData(dd + ":  Agent not found when logged off: " + event.getAgent().getAgentID());
	    			
				}
	
	
	    	}
    	} catch(UnsupportedEncodingException | FileNotFoundException e) 
    	{
    		  throw new AssertionError("UTF-8 not supported");
    	}
    	
    }
	

    public void acdAddressLoggedOn(ACDAddressEvent event)
    {
    	try 
    	{
    	    Date dd = new Date();
	    	if("finest".equals(config[5]))
			{
	    		ww2.writeData(dd + ":  Received ACD Addr Logged On Ev - Agent: " + event.getAgent().getAgentTerminal().getName() + "\n");
			}
	    	
	    	if(lias.contains(event.getAgent().getAgentTerminal()))
	    	{
	    		if(!"none".equals(config[5]))
				{
	    			ww2.writeData(dd + ":  Agent already added: " + event.getAgent().getAgentID());
				}	
	    	}
	    	else
	    	{
	    		if(!"none".equals(config[5]))
				{
	    			ww2.writeData(dd + ":  Agent Logged In: " + event.getAgent().getAgentID());
	    			
				}
	        	lias.add(event.getAgent().getAgentTerminal());
	        	TerminalListener addListener = new TerminalListener(event.getAddress().getProvider(), event.getAgent().getAgentID(), ww2);
	        	liaListener.add(addListener);
	        	
	        	try {
					lias.lastElement().addCallListener(liaListener.lastElement());
				} catch (ResourceUnavailableException e) {
					if(!"none".equals(config[5]))
					{
						ww2.writeData(dd + ":  Error adding agent when logging in : " + event.getAgent().getAgentID());
					
					}
		    		e.printStackTrace();
				} catch (MethodNotSupportedException e) {
					if(!"none".equals(config[5]))
					{
						ww2.writeData(dd + ":  Error adding agent when logging in : " + event.getAgent().getAgentID());
					}
		    		e.printStackTrace();
				}
	        	
	    	}
    	} catch(UnsupportedEncodingException | FileNotFoundException e) 
    	{
  		  throw new AssertionError("UTF-8 not supported");
  	}
    	
    }

    public void acdAddressNotReady(ACDAddressEvent event) 
    {
    }


    public void acdAddressReady(ACDAddressEvent event)
    {
 
    }

    public void acdAddressUnknown(ACDAddressEvent event) 
    {
    }

 
    public void acdAddressWorkNotReady(ACDAddressEvent event)
    {
    }

  
    public void acdAddressWorkReady(ACDAddressEvent event) 
    {
    }
}


