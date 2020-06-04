package trocararquivos.teste;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class TCPServer implements Runnable {

	private Socket socket;
	
	private OutputStream out;
	
	private static final int PORT = 6000;
	
	private static final int BUFFER_SIZE = 1024;
	
	private static final int HEADER_FILE_NAME = 100;
	
	private static final int HEADER_FILE_SIZE = 10;
	
	private static final String FILES_DIRECTORY = "C:/folder/server/";

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
	
	@Override
	public void run() {
		System.out.println("Conexão de: " + socket.getInetAddress().getHostAddress());

		try {
			
			Scanner s = new Scanner(socket.getInputStream());

			while (s.hasNextLine()) {
				out = socket.getOutputStream();
				String line = s.nextLine();
				System.out.println(line);
				
				switch(line.split(" ")[0]) {
				case "ls":
					listDirectory();
					break;
				case "send":
					receiveFile();
					break;
				case "download":
					sendFile(line.split(" ")[1]);
					break;
				default:
					break;
				}
				out.close();
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void listDirectory() {
		File directory = new File(FILES_DIRECTORY);
		PrintWriter pw = new PrintWriter(out);
		for(File file : directory.listFiles())
			pw.append(file.getName() + System.lineSeparator());
		pw.flush();
		pw.close();
	}
	
	public void receiveFile() {
		try {
		    DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		    
		    //GET FILE NAME
		    byte[] bufferName = new byte[HEADER_FILE_NAME];
		    in.read(bufferName, 0, HEADER_FILE_NAME);
		    String fileName = String.valueOf(bufferName).trim();
		    
		    //GET FILE SIZE
		    byte[] bufferSize = new byte[HEADER_FILE_SIZE];
		    in.read(bufferSize, 0, HEADER_FILE_SIZE);
		    String fileSize = String.valueOf(bufferSize).trim();
		    
		    System.out.println("Receiving file " + fileName + " with " + fileSize + " bytes");
		    
		    FileOutputStream out = new FileOutputStream(FILES_DIRECTORY + fileName);
		    int count;
		    byte[] buffer = new byte[BUFFER_SIZE];
		    while ((count = in.read(buffer)) > 0) {
		    	out.write(buffer, 0, count);
		    }
		    
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendFile(String fileName) throws IOException {
		File file = new File(FILES_DIRECTORY + fileName);
		
		if(!file.exists() || !file.isFile()) {
			PrintWriter pw = new PrintWriter(out);
			pw.print("The specified file does not exist");
			pw.flush();
			pw.close();
			return;
		}
		
	    FileInputStream in = new FileInputStream(file);
	    
	    //SEND FILE NAME
	    byte[] bufferName = Arrays.copyOf(file.getName().getBytes(), HEADER_FILE_NAME);
	    out.write(bufferName, 0, HEADER_FILE_NAME);
	    
	    //SEND FILE SIZE
	    byte[] bufferFileSize = Arrays.copyOf(("" + file.length()).getBytes(), HEADER_FILE_SIZE);
	    out.write(bufferFileSize, 0, HEADER_FILE_SIZE);
	    
	    //SEND FILE BYTE CONTENT
	    int count;
	    byte[] buffer = new byte[BUFFER_SIZE];
	    while ((count = in.read(buffer)) > 0) {
	    	out.write(buffer, 0, count);
	    }
	    
	    in.close();
	}
	
}
