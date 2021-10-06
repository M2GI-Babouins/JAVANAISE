package jvn;

import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;

enum LOCKSTATE{
	NL,
	RC,
	WC,
	R,
	W,
	RWC
}
public class JvnObjectImpl implements JvnObject {

	private static final long serialVersionUID = 1L;

	private final int id;
	private LOCKSTATE lockstate;
	ReadWriteLock lock;

	JvnObjectImpl(int id){
		this.id = id;
	}
	
	@Override
	public void jvnLockRead() throws JvnException {
		lock.readLock().lock();
		lockstate = LOCKSTATE.R;
	}

	@Override
	public void jvnLockWrite() throws JvnException {
		lock.writeLock().lock();
		lockstate = LOCKSTATE.W;
	}

	@Override
	public void jvnUnLock() throws JvnException {
		if(lockstate == LOCKSTATE.R)
			lock.readLock().unlock();
		if(lockstate == LOCKSTATE.W)
			lock.writeLock().unlock();
		lockstate = LOCKSTATE.NL;
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
