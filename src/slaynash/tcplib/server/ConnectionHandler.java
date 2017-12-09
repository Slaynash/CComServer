package slaynash.tcplib.server;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class ConnectionHandler extends Thread {
	
	private static ConnectionHandler instance = null;
	private static int nextUserID = 0;
	
	private static final String passphrase = "serverpw";
	
	
	private int port;
	private String serverVersion;
	
	private ConnectionHandler(int port, String serverVersion) {
		this.port = port;
		this.serverVersion = serverVersion;
	}

	public static void startServer(int port, String serverVersion) {
		instance = new ConnectionHandler(port, serverVersion);
		instance.setName("ConnectionHandler");
		//instance.setDaemon(true);
		instance.start();
	}

	private SSLServerSocket serverSocket = null;
	private boolean running = true;
	
	private SSLContext sslContext;
	
	@Override
	public void run() {
		System.out.println("Starting server...");
		try {
			setupSSL();
			SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
			serverSocket = (SSLServerSocket)sf.createServerSocket(port);
			serverSocket.setNeedClientAuth( true );
			System.out.println("Server started");
			
			while(running) {
				Socket clientSocket = serverSocket.accept();
				Client client = new Client(clientSocket, nextUserID++);
				if(client.isValid()) UserManager.add(client);
			}
			
		} catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException | UnrecoverableKeyException | KeyManagementException e) {
			LogSystem.logsystem.err_println("Unable to start LUM Server: ", null);
			LogSystem.logsystem.printStackTrace(e);
		}
	}
	/*
	
	public static void addClientToList(Client client) {
		synchronized (clients) {
			clients.add(client);
		}
	}
	
	public static void removeClientFromList(Client client) {
		synchronized (clients) {
			clients.remove(client);
		}
	}

	public static int getConnectedClientsNumber() {
		return clients.size();
	}

	public static Client getClient(int index) {
		synchronized (clients) {
			return clients.get(index);
		}
	}
	
	*/

	private void setupSSL() throws NoSuchAlgorithmException,  CertificateException, IOException,  KeyStoreException,  UnrecoverableKeyException,  KeyManagementException {
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextInt();
		KeyStore clientKeyStore = KeyStore.getInstance( "JKS" );
		clientKeyStore.load( ConnectionHandler.class.getClassLoader().getResourceAsStream("client.public"), "public".toCharArray() );
		
		KeyStore serverKeyStore = KeyStore.getInstance( "JKS" );
		serverKeyStore.load( ConnectionHandler.class.getClassLoader().getResourceAsStream( "server.private" ), passphrase.toCharArray() );
		
		TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
		tmf.init( clientKeyStore );
		
		KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
		kmf.init( serverKeyStore, passphrase.toCharArray() );
		
		sslContext = SSLContext.getInstance( "TLS" );
		sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom );
	}

	public static String getServerVersion() {
		return instance.serverVersion;
	}
	
}
