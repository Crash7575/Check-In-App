/*
Socket Server class
Handles client connections and distributes student IDs to all connected clients
*/

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Server {
	ServerSocket sc;
	List<ServerSocketThread> socketList;
	HashSet<Integer> ids;

	public Server() {

		// initializes serversocket
		try {
			sc = new ServerSocket(8080);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// initializes list of ServerSocketThreads to an arraylist
		// initializes list of ids to a hashset of integers
		socketList = new ArrayList<ServerSocketThread>();
		ids = new HashSet<Integer>();

		/*
		 * accepts connection from client creates new ServerSocketThread from acception
		 * of client adds ServerSocketThread to list of all ServerSocketThreads starts
		 * new ServerSocketThread sends all current ids
		 */
		Runnable task = () -> {
			try {
				while (true) {
					ServerSocketThread sst = new ServerSocketThread(sc.accept(), this);
					addSST(sst);
					sst.start();
					sendAllIds(sst);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		Thread t = new Thread(task);
		t.start();
	}

	/**
	 * adds ServerSocketThread to list of ServerSocketThreads
	 * 
	 * @param sst ServerSocketThread to add to the list
	 */
	public void addSST(ServerSocketThread sst) {
		synchronized (socketList) {
			socketList.add(sst);
		}
	}

	/**
	 * removes a ServerSocketThread from the list
	 * 
	 * @param sst ServerSocketThread to remove from list
	 */
	public void removeSST(ServerSocketThread sst) {
		synchronized (socketList) {
			socketList.remove(sst);
		}
	}

	/**
	 * iterates through all ServerSocketThreads in socketList, excluding the sender
	 * sends information about the Update object by using a negative value of the id
	 * to remove and positive to add
	 * 
	 * @param u      update object to remove or add id
	 * @param sender the ServerSocketThread that is sending the information
	 */
	public void sendNewIdToAll(Update u, ServerSocketThread sender) {
		synchronized (socketList) {
			Iterator<ServerSocketThread> i = socketList.iterator();
			while (i.hasNext()) {
				ServerSocketThread tmp = i.next();
				if (tmp != sender) {
					tmp.write((u.getStatus() ? 1 : -1) * u.getID());
				}
			}
		}
	}

	public void updateID(Update u, ServerSocketThread sender) {
		synchronized (ids) {
			if (u.getStatus()) {
				ids.add(u.getID());
			} else {
				ids.remove(u.getID());
			}
		}
		sendNewIdToAll(u, sender);
	}

	public synchronized void sendAllIds(ServerSocketThread sst) {
		synchronized (ids) {
			Iterator<Integer> i = ids.iterator();
			while (i.hasNext()) {
				sst.write(i.next());
			}
		}
	}

	public ArrayList<Integer> getAllIds() {
		synchronized (ids) {
			ArrayList<Integer> arr = new ArrayList<Integer>();
			Iterator<Integer> i = ids.iterator();
			while (i.hasNext()) {
				arr.add(i.next());
			}
			return arr;
		}
	}
}
