 package jvn;

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
	private static final long serialVersionUID = 1L;

	private final int id;
	private LOCKSTATE lock;
	Serializable shared;

	JvnObjectImpl(int id, Serializable shared){
		this.id = id;
		this.shared = shared;
	}
	
	@Override
	public void jvnLockRead() throws JvnException {
		if(lock == LOCKSTATE.NL || lock == LOCKSTATE.RC){
			lock = LOCKSTATE.R;
		}

		JvnLocalServer jvnserver = jvnGetServer();
		if(jvnserver!= null)
			jvnserver.jvnLockRead(id);

	}

	@Override
	public void jvnLockWrite() throws JvnException {
		if(lock == LOCKSTATE.NL || lock == LOCKSTATE.WC){
			lock = LOCKSTATE.W;
		}

		JvnLocalServer jvnserver = jvnGetServer();
		if(jvnserver!= null)
			shared = jvnserver.jvnLockWrite(id);
	}

	@Override
	public void jvnUnLock() throws JvnException {
		lock = LOCKSTATE.NL;
	}

	@Override
	public int jvnGetObjectId() {
		return this.id;
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
