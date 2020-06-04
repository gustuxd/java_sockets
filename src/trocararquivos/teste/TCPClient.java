package trocararquivos.teste;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.stream.Stream;

public class TCPClient {
	
	private static final int BUFFER_SIZE = 1024;
	
	private static final int HEADER_FILE_NAME = 100;
	
	private static final int HEADER_FILE_SIZE = 10;
	
	private static final String FILES_DIRECTORY = "C:/folder/client/";

	public static void main(String args[]) {
		try {
			Socket socket = new Socket("localhost", 6000);
			//Socket socket = new Socket("187.255.116.242", 6000);

			System.out.println("O cliente conectou ao servidor");
			
			Scanner s = new Scanner(System.in);

			PrintStream out;
			
			BufferedReader in;

			while (s.hasNextLine()) {
				String line = s.nextLine();
				out = new PrintStream(socket.getOutputStream());
				out.println(line);
				
				
				if(line.startsWith("download")) {
					byte[] bytes = new byte[BUFFER_SIZE];
					InputStream inStream = socket.getInputStream();
					
					// READING FILE NAME
					byte[] fileNameBytes = new byte[HEADER_FILE_NAME];
					inStream.read(fileNameBytes);
					String fileName = new String(fileNameBytes).trim();
					System.out.println("File Name: " + fileName);
					
					// READING FILE SIZE
					byte[] fileSizeBytes = new byte[HEADER_FILE_SIZE];
					inStream.read(fileSizeBytes);
					String fileSize = new String(fileSizeBytes).trim();
					System.out.println("File Size: " + fileSize + " bytes");
					
					File file = new File(FILES_DIRECTORY + fileName);
					OutputStream outStream = new FileOutputStream(file);
					// READING FILE CONTENT
					int count;
					while ((count = inStream.read(bytes)) > 0) {
						outStream.write(bytes, 0, count);
					}
					outStream.close();
				} else {
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					Stream<String> serverMessages = in.lines();
					serverMessages.forEach(message -> {
						System.out.println(message);
					});
				}
			}
			
			//out.close();
			s.close();
			socket.close();
			
			/*
			File file = new File("C:/folder/client/teste" + System.currentTimeMillis() + ".txt");
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
			*/
			System.out.println("Fim do cliente!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
