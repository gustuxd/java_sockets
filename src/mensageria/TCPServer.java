package mensageria;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCPServer implements Runnable {

	public Socket cliente;

	public TCPServer(Socket cliente) {
		this.cliente = cliente;
	}

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		ServerSocket server = new ServerSocket(6000);
		System.out.println("Porta 6000 aberta!");

		System.out.println("Aguardando conexão do cliente...");

		while (true) {
			Socket cliente = server.accept();
			TCPServer tratamento = new TCPServer(cliente);
			Thread t = new Thread(tratamento);
			t.start();
		}
	}

	public void run() {
		System.out.println("Nova conexao com o cliente " + this.cliente.getInetAddress().getHostAddress());

		try {
			Scanner s = new Scanner(cliente.getInputStream());

			while (s.hasNextLine()) {
				System.out.println(s.nextLine());
			}

			s.close();
			cliente.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
