package arquivos.in;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPClient {

	public static void main(String args[]) {
		//for (int i = 0; i < 5; i++) {
			try {
				Socket socket = new Socket("127.0.0.1", 6000);

				System.out.println("O cliente conectou ao servidor");
				
				File file = new File("C:/folder/teste.txt");
				byte[] bytes = new byte[1024];
				InputStream in = new FileInputStream(file);
				OutputStream out = socket.getOutputStream();
				
				int count;
		        while ((count = in.read(bytes)) > 0) {
		            out.write(bytes, 0, count);
		        }

		        out.close();
		        in.close();
				socket.close();
				System.out.println("Fim do cliente!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		//}
	}

}
