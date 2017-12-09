package slaynash.tcplib.server;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
	private static Map<Integer, User> users = new HashMap<Integer, User>();
	private static Map<Integer, Client> clients = new HashMap<Integer, Client>();

	public static void updateUser(Client client, String type, int permissionLevel, int userId, int state, String username, int[] friends) {
		User user;
		if((user = client.getUser()) != null) {
			user.remove(client);
			if(user.isEmpty()) {
				removeUser(user.getId());
			}
		}
		if((user = users.get(userId)) == null) {
			user = new User(client, type, permissionLevel, userId, state, username, friends);
			addUser(user);
		}
		else user.add(client);
		client.setUser(user);
		
	}
	
	public static void updateUser(Client client, String type, int permissionLevel, int userId) {
		updateUser(client, type, permissionLevel, userId, 0, "", new int[0]);
	}

	public static User getUser(int friendId) {
		return users.get(friendId);
	}

	public static void add(Client client) {
		clients.put(client.getSocketId(), client);
	}

	public static void remove(Client client) {
		clients.remove(client.getSocketId());
		User user;
		if((user = client.getUser()) != null) {
			user.remove(client);
			if(user.isEmpty()) {
				removeUser(user.getId());
			}
		}
	}

	public static int getConnectedClientsNumber() {
		return clients.size();
	}
	
	
	
	
	
	private static void addUser(User user) {
		/*
		User friend = null;
		for(int userId:user.getFriends()) {
			if((friend = users.get(userId)) != null) {
				for(Client client:friend.getInstances()) ((ClientStateChanged)CommandManager.createInstance("CLIENTSTATECHANGED", client)).clientStateChanged(user.getId(), user.getState());
			}
		}
		*/
		users.put(user.getId(), user);
	}
	
	private static void removeUser(int userId) {
		User user = users.remove(userId);
		user.setState(0);
		/*
		User friend = null;
		for(int userIds:user.getFriends()) {
			if((friend = users.get(userIds)) != null) {
				for(Client client:friend.getInstances()) ((ClientStateChanged)CommandManager.createInstance("CLIENTSTATECHANGED", client)).clientStateChanged(user.getId(), user.getState());
			}
		}
		*/
	}

}
