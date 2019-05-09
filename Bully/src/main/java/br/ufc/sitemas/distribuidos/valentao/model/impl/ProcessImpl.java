package br.ufc.sitemas.distribuidos.valentao.model.impl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import br.ufc.sitemas.distribuidos.valentao.model.Process;

public class ProcessImpl implements Process, Runnable {

	private static Long PID;

	private static Long COORDINATOR;

	public static int PORT = 3030;

	public ProcessImpl() {

		super();

		this.PID = ProcessHandle.current().pid();

	}

	public static Registry getRegistry() {

		Registry reg = null;

		try {

			reg = LocateRegistry.createRegistry(PORT);

		} catch (Exception ex) {
			try {
				reg = LocateRegistry.getRegistry(PORT);
			} catch (RemoteException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		return reg;
	}

	public static int getRandomPort() {

		double rand = Math.random();

		return (int) (rand * ((3999 - 3000) + 1)) + 3000;
	}

	public static int getRandomTime() {

		double rand = Math.random();

		return (int) (rand * ((60000 - 30000) + 1)) + 3000;
	}

	@Override
	public void run() {

		while (true) {
			try {
				int randomTime = getRandomTime();

				System.out.println(+randomTime / 1000 + " seconds to start election. ");
				Thread.sleep(randomTime);

				this.startElection();

				if (!contactCoordinator()) {
					getRegistry().unbind(COORDINATOR.toString());
					this.COORDINATOR = null;
					this.startElection();
				}

			} catch (InterruptedException | RemoteException | NotBoundException e) {
				e.printStackTrace();
			}
		}

	}

	public List<Long> listMajorProcesses() {

		List<Long> processosMaiores = new ArrayList();

		try {

			for (String p : getRegistry().list()) {

				if (Long.parseLong(p) > PID) {

					processosMaiores.add(Long.parseLong(p));

				}
			}

		} catch (RemoteException e) {
			System.out.println("Error fetching list of registry");
		}

		return processosMaiores;
	}

	private boolean contactCoordinator() {

		if (COORDINATOR == null) {

			return false;

		}

		try {

			Process coordinatorProcess = (Process) getRegistry().lookup(COORDINATOR.toString());

			Long coordinatorPID = coordinatorProcess.getPID();

			return true;

		} catch (RemoteException | NotBoundException e) {
			System.out.println("Error.");
			return false;
		}

	}

	@Override
	public void startElection() throws RemoteException {

		List<Long> processes = this.listMajorProcesses();

		if (processes.size() == 0) {

			System.out.println("Starts the election");

			if (COORDINATOR == null) {

				this.setCoordinator(PID);

				this.notifyProcess();
			}
		} else {
			if (PID > COORDINATOR) {

				this.setCoordinator(PID);

				this.notifyProcess();
			}
		}

	}

	public void notifyProcess() throws RemoteException {

		String[] registries = getRegistry().list();

		for (String registry : registries) {
			if (!(Long.parseLong(registry) == COORDINATOR)) {
				try {
					Process process = (Process) getRegistry().lookup(registry);
					process.setCoordinator(COORDINATOR);
				} catch (NotBoundException e) {
					System.out.println("Could not notify: " + registry);
				}
			}
		}

	}

	public void setCoordinator(Long coordinator) {

		this.COORDINATOR = coordinator;

		System.out.println("Coordinator: " + this.COORDINATOR);
	}

	public Long getPID() throws RemoteException {

		return this.PID;

	}

	public static void main(String[] args) {

		System.out.println("Create Process");

		try {

			ProcessImpl processImp = new ProcessImpl();

			Process process = (Process) UnicastRemoteObject.exportObject(processImp, getRandomPort());
			getRegistry().rebind(PID.toString(), process);

			System.out.println("PID: " + processImp.getPID());

			new Thread(processImp).run();

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

}
