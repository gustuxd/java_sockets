package arquivos.out;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPClient {
	
	private static final int BUFFER_SIZE = 1024;
	
	public static void main(String args[]) {
		//for (int i = 0; i < 5; i++) {
			try {
				//Socket socket = new Socket("localhost", 6000);
				Socket socket = new Socket("187.255.116.242", 6000);

				System.out.println("O cliente conectou ao servidor");
				
				File file = new File("C:/folder/teste" + System.currentTimeMillis() + ".jpg");
				InputStream in = socket.getInputStream();
				OutputStream out = new FileOutputStream(file);
				
				int count;
				byte[] bytes = new byte[BUFFER_SIZE];
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
