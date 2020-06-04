package arquivosheader.out;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPClient {
	
	private static final int BUFFER_SIZE = 1024;
	
	private static final int HEADER_FILE_NAME = 100;
	
	private static final int HEADER_FILE_SIZE = 10;

	public static void main(String args[]) {
		try {
			Socket socket = new Socket("localhost", 6000);
			//Socket socket = new Socket("187.255.116.242", 6000);

			System.out.println("O cliente conectou ao servidor");
			
			File file = new File("C:/folder/teste" + System.currentTimeMillis() + ".txt");
			byte[] bytes = new byte[BUFFER_SIZE];
			InputStream in = socket.getInputStream();
			OutputStream out = new FileOutputStream(file);
			
			// READING FILE NAME
			byte[] fileNameBytes = new byte[HEADER_FILE_NAME];
			in.read(fileNameBytes);
			String fileName = new String(fileNameBytes).trim();
			System.out.println("File Name: " + fileName);
			
			// READING FILE SIZE
			byte[] fileSizeBytes = new byte[HEADER_FILE_SIZE];
			in.read(fileSizeBytes);
			String fileSize = new String(fileSizeBytes).trim();
			System.out.println("File Size: " + fileSize + " bytes");
			
			// READING FILE CONTENT
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
	}

}
