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
		this.lock = LOCKSTATE.NL;
	}

	public LOCKSTATE getLock(){
		return lock;
	}

	 @Override
	 public int jvnGetObjectId() {
		 return this.id;
	 }

	@Override
	public void jvnLockRead() throws JvnException {
		if (lock == LOCKSTATE.NL){
			shared = ((JvnObject) jvnGetServer().jvnLockRead(id)).jvnGetSharedObject();
			lock = LOCKSTATE.R;
		}

		if(lock == LOCKSTATE.RC){
			lock = LOCKSTATE.R;
		}

		if(lock == LOCKSTATE.WC){
			lock = LOCKSTATE.RWC;
		}

	}

	@Override
	public void jvnLockWrite() throws JvnException {
		if(lock == LOCKSTATE.NL){
			shared = ((JvnObject) jvnGetServer().jvnLockWrite(id)).jvnGetSharedObject();
			lock = LOCKSTATE.W;
		}

		if(lock == LOCKSTATE.RC){
			shared = ((JvnObject) jvnGetServer().jvnLockWrite(id)).jvnGetSharedObject();
			lock = LOCKSTATE.W;
		}

		if(lock == LOCKSTATE.WC) {
			lock = LOCKSTATE.W;
		}
	}

	@Override
	public void jvnUnLock() throws JvnException {
		System.out.print("OldLock = " + lock);
		switch (lock) {
			case R -> {
				lock = LOCKSTATE.RC;
				Notify();		}
			case W, RWC -> {
				lock = LOCKSTATE.WC;
				Notify();			}

		}
		System.out.println(" | NewLock = " + lock);
	}

	

	@Override
	public Serializable jvnGetSharedObject() throws JvnException {
		System.out.println("Shared Object : "+ shared);
		return shared;
	}

	@Override
	public void jvnInvalidateReader() throws JvnException {
		if(lock == LOCKSTATE.R) {
			Wait();
			lock = LOCKSTATE.NL;
		}

		if(lock == LOCKSTATE.RWC) {
			Wait();
			lock = LOCKSTATE.NL;
		}

		if(lock == LOCKSTATE.RC)
			lock = LOCKSTATE.NL;
	}

	@Override
	public Serializable jvnInvalidateWriter() throws JvnException {
		if(lock == LOCKSTATE.W) {
			Wait();
			lock = LOCKSTATE.NL;
		}

		if(lock == LOCKSTATE.WC)
			lock = LOCKSTATE.NL;

		return this;
	}

	@Override
	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		if(lock == LOCKSTATE.W) {
			Wait();
			lock = LOCKSTATE.NL;
		}

		if(lock == LOCKSTATE.WC)
			lock = LOCKSTATE.NL;

		return this;
	}

	private synchronized void Wait(){
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	 private synchronized  void Notify(){
		notify();
	 }
}
