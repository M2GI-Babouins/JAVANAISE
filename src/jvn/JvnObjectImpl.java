 package jvn;

import java.io.Serial;
import java.io.Serializable;

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
	@Serial
	private static final long serialVersionUID = 1L;

	private final int id;
	private LOCKSTATE lock;
	Serializable shared;

	JvnObjectImpl(int id, Serializable shared){
		this.id = id;
		this.shared = shared;
		this.lock = LOCKSTATE.W;
	}
	 @Override
	 public int jvnGetObjectId() {
		 return this.id;
	 }

	@Override
	public void jvnLockRead() throws JvnException {
		if(lock != LOCKSTATE.R){
			shared = ((JvnObject)jvnGetServer().jvnLockRead(id)).jvnGetSharedObject();
			lock = LOCKSTATE.R;
		}
	}

	@Override
	public void jvnLockWrite() throws JvnException {
		if(lock != LOCKSTATE.W){
			shared = ((JvnObject)jvnGetServer().jvnLockWrite(id)).jvnGetSharedObject();
			lock = LOCKSTATE.W;
		}
	}

	@Override
	public void jvnUnLock() throws JvnException {
		switch (lock) {
			case R -> lock = LOCKSTATE.RC;
			case W -> lock = LOCKSTATE.WC;
		}
	}

	

	@Override
	public Serializable jvnGetSharedObject() throws JvnException {
		System.out.println("Shared Object : "+ shared);
		return shared;
	}

	@Override
	public void jvnInvalidateReader() throws JvnException {
		lock = LOCKSTATE.NL;
	}

	@Override
	public Serializable jvnInvalidateWriter() throws JvnException {
		lock = LOCKSTATE.NL;
		return this;
	}

	@Override
	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		lock = LOCKSTATE.NL;
		return this;
	}

}
