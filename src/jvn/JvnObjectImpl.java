package jvn;

import java.io.Serializable;

import jvn.JvnCoordImpl.LOCKSTATE;

public class JvnObjectImpl implements JvnObject {

	private static final long serialVersionUID = 1L;

	private int id;
	private LOCKSTATE lock;
	JvnRemoteServer lock_owner;
	
	JvnObjectImpl(int id){
		this.id = id;
	}
	
	@Override
	public void jvnLockRead() throws JvnException {
		if(lock == LOCKSTATE.NL || lock ==  LOCKSTATE.R) {
			lock= LOCKSTATE.R ;
		}
		else {
			
		}

	}

	@Override
	public void jvnLockWrite() throws JvnException {
		 if(lock == LOCKSTATE.NL) {
			 lock= LOCKSTATE.W ;
		}
	}

	@Override
	public void jvnUnLock() throws JvnException {
		lock = LOCKSTATE.NL;
	}

	@Override
	public int jvnGetObjectId() throws JvnException {
		return this.id;
	}
	

	@Override
	public Serializable jvnGetSharedObject() throws JvnException {
		// TODO Auto-generated method stub
		return null;
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
