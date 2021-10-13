/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class JvnServerImpl
              extends UnicastRemoteObject 
							implements JvnLocalServer, JvnRemoteServer{ 
	
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;
	JvnRemoteCoord jrc;
	private final ArrayList<JvnObject> locks = new ArrayList<>();

	/**
  * Default constructor
  * @throws JvnException
  **/
	private JvnServerImpl() throws Exception {
		super();
	    Registry registry= LocateRegistry.getRegistry();
	    jrc = (JvnRemoteCoord) registry.lookup("Coordinateur");
	}
	
  /**
    * Static method allowing an application to get a reference to 
    * a JVN server instance
    * @throws JvnException
    **/
	public static JvnServerImpl jvnGetServer() {
		if (js == null){
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				return null;
			}
		}
		return js;
	}
	
	/**
	* The JVN service is not used anymore
	* @throws JvnException
	**/
	public  void jvnTerminate()
	throws jvn.JvnException {
		try {
			jrc.jvnTerminate(js);
		} catch (RemoteException e) {
			System.out.println("jvnTerminate error : " + e.detail);
			throw new JvnException();
		}
	} 
	
	/**
	* creation of a JVN object
	* @param o : the JVN object state
	* @throws JvnException
	**/
	public  JvnObject jvnCreateObject(Serializable o)
	throws jvn.JvnException {
		int id ;
		try {
			id = jrc.jvnGetObjectId();
		} catch (RemoteException e) {
			System.out.println("jvnGetObjectId error in jvnCreateObject : " + e.detail);
			throw new JvnException();
		}
		return new JvnObjectImpl(id,o);

	}
	
	/**
	*  Associate a symbolic name with a JVN object
	* @param jon : the JVN object name
	* @param jo : the JVN object 
	* @throws JvnException
	**/
	public  void jvnRegisterObject(String jon, JvnObject jo)
	throws jvn.JvnException {
		try {
			jrc.jvnRegisterObject(jon, jo, this);
		} catch (RemoteException e) {
			System.out.println("jvnRegisterObject error : " + e.detail);
			throw new JvnException();
		}
	}
	
	/**
	* Provide the reference of a JVN object beeing given its symbolic name
	* @param jon : the JVN object name
	* @return the JVN object 
	* @throws JvnException
	**/
	public  JvnObject jvnLookupObject(String jon)
	throws jvn.JvnException {
		 try {
			 return jrc.jvnLookupObject(jon, js);
		} catch (RemoteException e) {
			System.out.println("jvnLookupObject error : " + e.detail);
			throw new JvnException();
		}
	}	
	
	/**
	* Get a Read lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
   public Serializable jvnLockRead(int joi)
	 throws JvnException {
	   try {
		   return jrc.jvnLockRead(joi,this);
	   } catch (RemoteException e) {
		   e.printStackTrace();
		   return null;
	   }

   }
	/**
	* Get a Write lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
   public Serializable jvnLockWrite(int joi)
	 throws JvnException {
	   try {
		   return jrc.jvnLockWrite(joi,this);
	   } catch (RemoteException e) {
		   e.printStackTrace();
		   return null;
	   }
   }

	
  /**
	* Invalidate the Read lock of the JVN object identified by id 
	* called by the JvnCoord
	* @param joi : the JVN object id
	* @return void
	* @throws java.rmi.RemoteException,JvnException
	**/
  public void jvnInvalidateReader(int joi)
	throws java.rmi.RemoteException,jvn.JvnException {
	  JvnObject jo = locks.stream().filter(o -> o.jvnGetObjectId() == joi).findAny().get();
	  locks.remove(jo);
	  jo.jvnInvalidateReader();
  }

	/**
	* Invalidate the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object states
	* @throws java.rmi.RemoteException,JvnException
	**/
  public Serializable jvnInvalidateWriter(int joi)
	throws java.rmi.RemoteException,jvn.JvnException {
	  JvnObject jo = locks.stream().filter(o -> o.jvnGetObjectId() == joi).findAny().get();
	  locks.remove(jo);
	  jo.jvnInvalidateWriter();
	  return jo;
	}
	
	/**
	* Reduce the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
   public Serializable jvnInvalidateWriterForReader(int joi)
	 throws java.rmi.RemoteException,jvn.JvnException {
	   JvnObject jo = locks.stream().filter(o -> o.jvnGetObjectId() == joi).findAny().get();
	   locks.remove(jo);
	   jo.jvnInvalidateWriterForReader();
	   return jo;
	 }

}

 
