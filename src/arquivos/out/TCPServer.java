package arquivos.out;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {

	public Socket socket;
	
	private static final int PORT = 6000;
	
	private static final int BUFFER_SIZE = 1024;

	public TCPServer(Socket socket) {
		this.socket = socket;
	}

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		ServerSocket server = new ServerSocket(PORT);
		System.out.println("Porta " + PORT + " aberta!");

		System.out.println("Aguardando conexão do cliente...");

		while (true) {
			Socket cliente = server.accept();
			TCPServer tratamento = new TCPServer(cliente);
			Thread t = new Thread(tratamento);
			t.start();
		}
	}

	public void run() {
		System.out.println("Conexão de: " + this.socket.getInetAddress().getHostAddress());

		try {
			OutputStream out = socket.getOutputStream();
		    FileInputStream in = new FileInputStream(new File("C:/folder/teste.txt"));
		    
		    int count;
		    byte[] buffer = new byte[BUFFER_SIZE];
		    while ((count = in.read(buffer)) > 0) {
		    	out.write(buffer, 0, count);
		    }
		    
		    out.close();
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
