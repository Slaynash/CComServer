package slaynash.tcplib.server;

public class LogSystem {
	
	public static LogSystem logsystem = new LogSystem();

	public void printStackTrace(Throwable throwable) {
		throwable.printStackTrace();
	}

	public void out_println(String string, LoggableClass loggableClass) {
		System.out.println("["+(loggableClass != null ? loggableClass.getLogName() : "null")+"] "+string);
	}

	public void err_println(String string, LoggableClass loggableClass) {
		System.err.println("["+(loggableClass != null ? loggableClass.getLogName() : "null")+"] "+string);
	}

}
