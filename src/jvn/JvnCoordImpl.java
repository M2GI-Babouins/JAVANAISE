/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:  
 *
 * Authors: 
 */

package jvn;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;


public class JvnCoordImpl 	
              extends UnicastRemoteObject 
							implements JvnRemoteCoord{

	public static void main(String[] argv) throws Exception {
		new JvnCoordImpl();
	}
	
	@Serial
	private static final long serialVersionUID = 1L;
	private int lastId = 0;
	private final HashMap<String,JvnObject> registre = new HashMap<>();
	private final HashMap<Integer,String> names = new HashMap<>();
	private final HashMap<Integer,ArrayList<JvnRemoteServer>> locks_r = new HashMap<>();
	private final HashMap<Integer,JvnRemoteServer> locks_w = new HashMap<>();

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

	  System.out.println("Requested Object by "+ js.getName() +" : " + jon + " | Exist : " + registre.containsKey(jon));

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
	   System.out.println("Lock R demandé par " + js.getName() + " sur " + joi);
	   String jon = names.get(joi);
	   JvnObject jo = registre.get(jon);

	   //On verifie les write locks
	   JvnRemoteServer lock_owner = locks_w.get(joi);
		if(lock_owner != null){
			System.out.println("Invalidation demandée a "+ lock_owner.getName() +" sur " + joi);
			jo = (JvnObject) lock_owner.jvnInvalidateWriter(joi);
			locks_w.remove(joi);
		}

	    locks_r.computeIfAbsent(joi, k -> new ArrayList<>());
		locks_r.get(joi).add(js);

	   System.out.println("Lock R accordé a " + js.getName() + " sur " + joi);

	   return jo;
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
	   System.out.println("Lock W demandé par " + js.getName() + " sur " + joi);
	   String jon = names.get(joi);
	   JvnObject jo = registre.get(jon) ;

		//On verifie les write locks
	   JvnRemoteServer lock_owner_w = locks_w.get(joi);
	   if(lock_owner_w != null){
		   System.out.println("Invalidation demandée a "+ lock_owner_w.getName() +" sur " + joi);
		   jo = (JvnObject) lock_owner_w.jvnInvalidateWriter(joi);
		   locks_w.remove(joi);
	   }

	   //On verifie les read locks
	   ArrayList<JvnRemoteServer> lock_owners_r = locks_r.get(joi);
	   if(lock_owners_r != null){
		   while (!lock_owners_r.isEmpty()){
			   System.out.println("Invalidation demandée a "+ lock_owners_r.get(0).getName() +" sur " + joi);
			   lock_owners_r.get(0).jvnInvalidateReader(joi);
			   lock_owners_r.remove(0);
		   }
	   }

	   locks_w.put(joi,js);


	   System.out.println("Lock W accordé a "+js.getName() +" sur " + joi);
	   return jo;
   }

	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
    public void jvnTerminate(JvnRemoteServer js)
	 throws java.rmi.RemoteException, JvnException {
		locks_w.values().removeIf(value -> value == js);
		for (ArrayList<JvnRemoteServer> locks:locks_r.values()) {
			locks.removeIf(value -> value == js);
		}

	}
}


