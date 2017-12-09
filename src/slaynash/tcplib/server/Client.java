package slaynash.tcplib.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Client extends Thread implements LoggableClass {
	
	private String connectionName;
	private int socketId = -1;
	private Socket socket = null;
	private boolean listen = true;
	//private int permissionLevel = LumPermissionLevels.BASIC;
	
	private BufferedReader inputStream;
	private PrintWriter outputStream;
	private boolean valid = false;
	//private String type = LumClientType.BASIC;
	//private int clientId;
	private User user = null;
	
	public Client(Socket clientSocket, int id) {
		try {
			this.socketId = id;
			connectionName = "["+id+"/"+clientSocket.getInetAddress().getHostAddress()+"/"+clientSocket.getPort()+">"+clientSocket.getLocalPort()+"]";
			this.socket = clientSocket;
			this.setName(connectionName);
			this.setDaemon(true);
			LogSystem.logsystem.out_println("Connected - "+connectionName, this);
			this.start();
			valid = true;
		}
		catch(Exception e) {
			LogSystem.logsystem.err_println("Unable to create client", this);
			LogSystem.logsystem.printStackTrace(e);
		}
	}
	
	@Override
	public void run() {
		try {
			inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputStream = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
			socket.setSoTimeout(10000);
			println("READY");
			if(!readln().equals("TCPLIB_"+ConnectionHandler.getServerVersion())) {
				disconnect("Bad version");
			}
			println("OK");
			socket.setSoTimeout(0);
			listen();
		}
		catch(SocketTimeoutException e) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			onDisconnected("Connection lost");
		}
		catch(SocketException e) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			onDisconnected(e.getMessage());
		}
		catch(Exception e) {
			//LogSystem.err_println("An error occured during auth.", this);
			LogSystem.logsystem.printStackTrace(e);
			disconnect("Critical error: "+e.getMessage());
		}
	}
	
	public String readln() throws IOException {
		return inputStream.readLine();
	}

	public void println(String string) {
		outputStream.println(string);
		outputStream.flush();
	}

	private void listen() throws IOException {
		String input = "";
		while(listen && (input = readln()) != null) {
			CommandManager.runCommand(input, this);
		}
	}

	public void onDisconnected(String reason) {
		LogSystem.logsystem.out_println("Disconnected ("+reason+") - "+connectionName, this);
	}
	
	public void disconnect(String reason) {
		try {
			println("DISCONNECT "+reason);
			socket.close();
		} catch (IOException e) {LogSystem.logsystem.printStackTrace(e);}
		onDisconnected(reason);
	}

	@Override
	public String getLogName() {
		return "Connection "+socketId;
	}

	public boolean isValid() {
		return valid;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public int getSocketId() {
		return socketId;
	}
	
	/*
	
	

	public int getPermissionLevel() {
		return permissionLevel;
	}
	
	public void setPermissionLevel(int level) {
		this.permissionLevel = level;
	}
	
	
	
	public void setUser(int userId) {
		this.type = LumClientType.CLIENT;
		this.clientId = userId;
	}

	public String getType() {
		return type;
	}

	public int getClientId() {
		return clientId;
	}
	
	*/
}
