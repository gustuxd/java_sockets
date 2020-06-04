package mensageria;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

	public static void main(String args[]) {
		for (int i = 0; i < 5; i++) {
			try {
				Socket socket = new Socket("127.0.0.1", 6000);

				PrintStream saida;
				System.out.println("O cliente conectou ao servidor");

				Scanner s = new Scanner(System.in);

				saida = new PrintStream(socket.getOutputStream());

				while (s.hasNextLine()) {
					saida.println(s.nextLine());
				}
				
				saida.close();
				s.close();
				socket.close();
				System.out.println("Fim do cliente!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
