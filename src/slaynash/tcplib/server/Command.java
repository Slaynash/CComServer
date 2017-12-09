package slaynash.tcplib.server;

public abstract class Command {
	
	private Client client = null;
	private String outId = "";
	
	protected abstract boolean checkPermissions();
	protected abstract void handle(String parts);
	
	
	protected void println(String string) {
		client.println(outId+" "+string);
	}
	
	void setClient(Client client) {
		this.client = client;
	}
	
	protected Client getClient() {
		return client;
	}
	
	protected void destroy() {
		CommandManager.remove(this);
	}

	void setOutId(String outId) {
		this.outId = outId;
	}
	
	public String getOutId() {
		return outId;
	}
	
	public void remoteError(String string) {
		destroy();
	}
}
