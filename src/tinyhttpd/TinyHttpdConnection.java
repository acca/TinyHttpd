/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tinyhttpd;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.StringTokenizer;

class TinyHttpdConnection extends Thread {

	Socket sock;

	TinyHttpdConnection(Socket s) {
		sock = s;
		setPriority(NORM_PRIORITY - 1);
		start();
	}

	public void run() {
		System.out.println("=========");
		OutputStream out = null;
		try {
			out = sock.getOutputStream();
			DataInputStream d = new DataInputStream(
					sock.getInputStream());
			String req = d.readLine();
			System.out.println("Request: " + req);
			StringTokenizer st = new StringTokenizer(req);

			if ((st.countTokens() >= 2) && st.nextToken().equals("GET")) {
				if ((req = st.nextToken()).startsWith("/")) {
					req = req.substring(1);
				}
				// Request to start a process
				if (req.startsWith("process")) {                	
					int tokenIndex[] = new int[4];
					int startQueryString = req.indexOf('?');
					// Check parameters of the query string
					if (startQueryString > 0) {
						int j=0;
						for (int i = startQueryString; i < req.length(); i++) {
							//System.out.println(req.charAt(i));
							if ( (req.charAt(i) == '=') || (req.charAt(i) == '&') ) {
								tokenIndex[j]=i;
								j++;
							}
						}
						try {
							Integer op1 = null;
							Integer op2 = null;
							if ( (req.charAt(tokenIndex[0]) == '=') && (req.charAt(tokenIndex[1]) == '&') ){
								op1 = Integer.parseInt(new String(req.substring(tokenIndex[0]+1, tokenIndex[1])));
							}
							if ( (req.charAt(tokenIndex[2]) == '=') ){
								op2 = Integer.parseInt(new String(req.substring(tokenIndex[2]+1, req.length())));	
							}
							if ( (op1 != null) && (op2 != null) ) {
								// Start process with parameters
								String output = startProcess(String.valueOf(op1),String.valueOf(op2));
								final PrintStream printStream = new PrintStream(out);
								printStream.print(output);
								printStream.close();
								sock.close();
							}
							else {
								throw new IllegalArgumentException("Bad request");
							}
						} catch (Exception e) {
							startProcess();
							new PrintStream(out).println("400 Bad Request");
							System.out.println("400 Bad Request: " + req);
							sock.close();
						}                		
					}
					// Query string does not contains parameters, just start a process without parameters
					else {
						startProcess();
						new PrintStream(out).println("");
						sock.close();
					}
				}
				else {
					if (req.endsWith("/") || req.equals("")) {
						req = req + "index.html";
					}
					try {
						FileInputStream fis = new FileInputStream(req);
						byte[] data = new byte[fis.available()];
						fis.read(data);
						out.write(data);
						fis.close();
					} catch (FileNotFoundException e) {
						new PrintStream(out).println("404 Not Found");
						System.out.println("404 Not Found: " + req);
					}	
				}				
			} else {
				new PrintStream(out).println("400 Bad Request");
				System.out.println("400 Bad Request: " + req);
				sock.close();
			}
		} catch (IOException e) {
			System.out.println("Generic I/O error " + e);
		} finally {
			try {
				out.close();
			} catch (IOException ex) {
				System.out.println("I/O error on close" + ex);
			}
		}
	}
	private String startProcess(String op1, String op2) {
		String javaexecutable = System.getProperty("java.home") + "/bin/java";                		
		String line="";
		String result="";
		try {    		
			Process p = Runtime.getRuntime().exec(javaexecutable + " " + "add " + op1 + " " + op2);
			BufferedReader bri = new BufferedReader
					(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader
					(new InputStreamReader(p.getErrorStream()));
			while ((line = bri.readLine()) != null) {
				if (line.startsWith("result:")) {
					result = line.substring(7);
				}
				System.out.println(line);
			}
			bri.close();
			while ((line = bre.readLine()) != null) {				
				System.out.println(line);
			}
			bre.close();
			p.waitFor();
			System.out.println("Process Done.");    		
		}
		catch (Exception err) {
			err.printStackTrace();
		}
		return result;
	}
	private String startProcess() {
		return startProcess("","");
	}
}
