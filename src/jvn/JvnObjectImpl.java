 package jvn;

import irc.Sentence;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static jvn.JvnServerImpl.jvnGetServer;

 public class JvnObjectImpl implements JvnObject {
	enum LOCKSTATE{
		NL,
		RC,
		WC,
		R,
		W,
		RWC
	}
	private static final long serialVersionUID = 1L;

	private final int id;
	private LOCKSTATE lockstate;
	Serializable shared;

	JvnObjectImpl(int id, Serializable shared){
		this.id = id;
		this.shared = shared;
	}
	
	@Override
	public void jvnLockRead() throws JvnException {
		if(lockstate == LOCKSTATE.NL || lockstate == LOCKSTATE.RC){
			lockstate = LOCKSTATE.R;
		}else{
				jvnGetServer().jvnLockRead(id);
		}
	}

	@Override
	public void jvnLockWrite() throws JvnException {
		if(lockstate == LOCKSTATE.NL || lockstate == LOCKSTATE.WC){
			lockstate = LOCKSTATE.W;
		}else{
				jvnGetServer().jvnLockWrite(id);
		}
	}

	@Override
	public void jvnUnLock() throws JvnException {
		lockstate = LOCKSTATE.NL;
	}

	@Override
	public int jvnGetObjectId() throws JvnException {
		return this.id;
	}
	

	@Override
	public Serializable jvnGetSharedObject() throws JvnException {
		System.out.println("Shared Object : "+ shared);
		return shared;
	}

	@Override
	public void jvnInvalidateReader() throws JvnException {
		// TODO Auto-generated method stub

	}

	@Override
	public Serializable jvnInvalidateWriter() throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

}
