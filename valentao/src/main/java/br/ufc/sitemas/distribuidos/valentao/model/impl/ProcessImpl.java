package br.ufc.sitemas.distribuidos.valentao.model.impl;

import java.rmi.RemoteException;

import br.ufc.sitemas.distribuidos.valentao.model.Process;

public class ProcessImpl implements Process, Runnable {
	
	private Long pid;
	
	private String coordinator;
	
	
    public ProcessImpl() {
    	
    	this.pid = ProcessHandle.current().pid();
    
    }
	
    @Override
    public void run() {
		
		
	}

	public void startElection() throws RemoteException {
		
	}

	public void setLeader(String lider) throws RemoteException {
		
	}
	
	public Long getPID() {
		
		return this.pid;
	
	}

}
