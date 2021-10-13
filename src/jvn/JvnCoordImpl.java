/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:  
 *
 * Authors: 
 */

package jvn;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Optional;


public class JvnCoordImpl 	
              extends UnicastRemoteObject 
							implements JvnRemoteCoord{

	public static void main(String[] argv) throws Exception {
		new JvnCoordImpl();
	}
	
	private static final long serialVersionUID = 1L;
	private int lastId = 0;
	private final HashMap<String,JvnObject> registre = new HashMap<>();
	private final HashMap<Integer,String> names = new HashMap<>();
	private final HashMap<Integer,JvnRemoteServer> locks = new HashMap<>();

	/**
  * Default constructor
  * @throws JvnException
  **/
public JvnCoordImpl() throws Exception {
		Registry registry= LocateRegistry.getRegistry();
		registry.bind("Coordinateur",this);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {try{ LocateRegistry.getRegistry().unbind("Coordinateur");}catch (Exception ignored){}}));

	System.out.println("Coordinateur listening ...");
}

  /**
  *  Allocate a NEW JVN object id (usually allocated to a 
  *  newly created JVN object)
  * @throws java.rmi.RemoteException,JvnException
  **/
  public int jvnGetObjectId()
  throws java.rmi.RemoteException,jvn.JvnException {
    return this.lastId++;
  }
  
  /**
  * Associate a symbolic name with a JVN object
  * @param jon : the JVN object name
  * @param jo  : the JVN object 
  * @param js  : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
	 
	  registre.put(jon, jo);
	  names.put(jo.jvnGetObjectId(),jon);
	  System.out.println("Registered Object : " + jon);
  }
  
  /**
  * Get the reference of a JVN object managed by a given JVN server 
  * @param jon : the JVN object name
  * @param js : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{

	  System.out.println("Requested Object : " + jon + " | Exist : " + registre.containsKey(jon));

	  return registre.get(jon);
  }
  
  /**
  * Get a Read lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public Serializable jvnLockRead(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
	   String jon = names.get(joi);
	   JvnObject jo = registre.get(jon);

	   JvnRemoteServer lock_owner = locks.get(joi);
		if(lock_owner != null){
			lock_owner.jvnInvalidateReader(joi);
		}

		jo.jvnLockRead();

	   return jo.jvnGetSharedObject();
   }

  /**
  * Get a Write lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
	   String jon = names.get(joi);
	   JvnObject jo = registre.get(jon) ;

	   JvnRemoteServer lock_owner = locks.get(joi);
	   if(lock_owner != null){
		  jo = (JvnObject) lock_owner.jvnInvalidateWriter(joi);
	   }

	   jo.jvnLockWrite();

	   return jo.jvnGetSharedObject();
   }

	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
    public void jvnTerminate(JvnRemoteServer js)
	 throws java.rmi.RemoteException, JvnException {
		locks.values().removeIf(value -> value == js);

	}
}


