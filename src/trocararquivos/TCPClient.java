package trocararquivos;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

public class TCPClient {
	
	Socket socket;

	private static final int BUFFER_SIZE = 1024;

	private static final int HEADER_FILE_NAME = 100;

	private static final int HEADER_FILE_SIZE = 10;

	private static final String FILES_DIRECTORY = "C:/folder/client/";

	public static void main(String args[]) {
		new TCPClient();
	}
	
	public TCPClient() {
		try {
			socket = new Socket("localhost", 6000);
			// Socket socket = new Socket("187.255.116.242", 6000);

			System.out.println("O cliente conectou ao servidor");

			Scanner s = new Scanner(System.in);

			// Thread to receive server's messages
			new Thread(new ReceiveMessages(socket)).start();;
			
			PrintStream out = new PrintStream(socket.getOutputStream()); 

			while (s.hasNextLine()) {
				String line = s.nextLine();
				sendMessageToServer(out, line);
			}

			s.close();
			out.close();
			socket.close();
			System.out.println("Fim do cliente!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessageToServer(PrintStream out, String message) throws IOException {
		if (message.startsWith("upload")) {
			uploadFile();
		} else {
			out.println("T" + message);
		}
		out.flush();
	}
	
	private void uploadFile() throws IOException {
		FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
	    dialog.setMode(FileDialog.LOAD);
	    dialog.setVisible(true);
	    File file = new File(dialog.getDirectory() + dialog.getFile());
	    
	    InputStream in = new FileInputStream(file);
	    OutputStream out = socket.getOutputStream();
	    //SET FLAG
	    byte[] bufferFlag = Arrays.copyOf("F".getBytes(), 1);
	    out.write(bufferFlag, 0, 1);
	    
	    //SET FILE NAME
	    byte[] bufferName = Arrays.copyOf(file.getName().getBytes(), HEADER_FILE_NAME);
	    out.write(bufferName, 0, HEADER_FILE_NAME);
	    
	    //SET FILE SIZE
	    byte[] bufferSize = Arrays.copyOf(("" + file.length()).getBytes(), HEADER_FILE_SIZE);
	    out.write(bufferSize, 0, HEADER_FILE_SIZE);
	    out.write("\n".getBytes());
	    
	    byte[] bytes = new byte[1024];
		int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }
	    
        in.close();
	}
	
	class ReceiveMessages implements Runnable {
		
		Socket socket;
		
		public ReceiveMessages(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				Stream<String> serverMessages = in.lines();
				serverMessages.forEach(message -> {
					if(message.startsWith("F")) {
						try {
						    String fileName = message.substring(1, 1 + HEADER_FILE_NAME).trim();
						    
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
					    } catch (IOException e) {
					    	e.printStackTrace();
					    }
					} else {
						message = message.substring(1);
						System.out.println(message);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
