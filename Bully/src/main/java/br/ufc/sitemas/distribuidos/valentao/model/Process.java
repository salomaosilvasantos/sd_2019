package br.ufc.sitemas.distribuidos.valentao.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Process extends Remote {

	public void startElection() throws RemoteException;
	
	public void setCoordinator(Long coordinator) throws RemoteException;

	public Long getPID() throws RemoteException;

}
