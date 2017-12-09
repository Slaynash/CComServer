package slaynash.tcplib.server;

import java.util.ArrayList;
import java.util.List;

public class User {

	private String type;
	private int id = -1;
	private List<Client> instances = new ArrayList<Client>();
	private int state = 0;
	private String username = "";
	private int[] friends;
	
	public User(Client client, String type, int permissionLevel, int userId, int defaultState, String username, int[] friends) {
		this.id = userId;
		this.type = type;
		this.state = defaultState;
		this.username = username;
		this.friends = friends;
		add(client);
	}

	public void remove(Client client) {
		instances.remove(client);
	}

	public boolean isEmpty() {
		return instances.isEmpty();
	}

	public void add(Client client) {
		instances.add(client);
	}

	public int getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(int state) {
		this.state = state;
	}

	public List<Client> getInstances() {
		return instances;
	}

	public int[] getFriends() {
		return friends;
	}
	
}
