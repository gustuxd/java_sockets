package trocararquivos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

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

		System.out.println("Aguardando conex�o do cliente...");

		while (true) {
			Socket cliente = server.accept();
			TCPServer tratamento = new TCPServer(cliente);
			Thread t = new Thread(tratamento);
			t.start();
		}
	}
	
	@Override
	public void run() {
		System.out.println("Conex�o de: " + socket.getInetAddress().getHostAddress());

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = socket.getOutputStream();

			boolean shouldBreak = false;
			while (!shouldBreak) {
				String line = in.readLine();
				System.out.println("Received command: " + line);
				if(line.startsWith("F")) {
					receiveFile(in, line);
				} else {
					line = line.substring(1);
					switch(line.split(" ")[0]) {
					case "ls":
						listDirectory();
						break;
					case "exit":
						shouldBreak = true;
						break;
					case "download":
						sendFile(line.split(" ")[1]);
						break;
					default:
						break;
					}
				}
			}
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Finalizando Conex�o");
	}
	
	public void listDirectory() {
		File directory = new File(FILES_DIRECTORY);
		PrintWriter pw = new PrintWriter(out);
		for(File file : directory.listFiles()) {
			pw.append("T" + file.getName() + System.lineSeparator());
		}
		pw.flush();
	}
	
	public void receiveFile(BufferedReader in, String message) throws IOException {
		System.out.println(message);
		String fileName = message.substring(1, 1 + HEADER_FILE_NAME).trim();
	    System.out.println(fileName);
	    Integer fileSize = Integer.parseInt(message.substring(1 + HEADER_FILE_NAME).trim());
	    
	    System.out.println("Receiving file " + fileName + " with " + fileSize + " bytes");
	    
	    FileOutputStream out = new FileOutputStream(FILES_DIRECTORY + fileName);
	    int count;
	    char[] buffer = new char[BUFFER_SIZE];
	    while ((count = in.read(buffer)) > 0) {
	    	out.write(new String(buffer).getBytes(), 0, count);
	    	if(count >= fileSize)
	    		break;
	    }
	    out.close();
	}
	
	public void sendFile(String fileName) throws IOException {
		File file = new File(FILES_DIRECTORY + fileName);
		
		if(!file.exists() || !file.isFile()) {
			PrintWriter pw = new PrintWriter(out);
			pw.print("The specified file does not exist");
			pw.flush();
			return;
		}
		
	    FileInputStream in = new FileInputStream(file);
	    
	    //SEND FLAG = F
	    byte[] bufferFlag = Arrays.copyOf("F".getBytes(), 1);
	    out.write(bufferFlag, 0, 1);
	    
	    //SEND FILE NAME
	    byte[] bufferName = Arrays.copyOf(file.getName().getBytes(), HEADER_FILE_NAME);
	    out.write(bufferName, 0, HEADER_FILE_NAME);
	    
	    //SEND FILE SIZE
	    byte[] bufferFileSize = Arrays.copyOf(("" + file.length()).getBytes(), HEADER_FILE_SIZE);
	    out.write(bufferFileSize, 0, HEADER_FILE_SIZE);
	    out.write("\n".getBytes());
	    
	    //SEND FILE BYTE CONTENT
	    int count;
	    byte[] buffer = new byte[BUFFER_SIZE];
	    while ((count = in.read(buffer)) > 0) {
	    	out.write(buffer, 0, count);
	    }
	    System.out.println("deixei de usr o out");
	    in.close();
	}
	
}
