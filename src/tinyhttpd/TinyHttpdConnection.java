/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tinyhttpd;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                if (req.endsWith("/") || req.equals("")) {
                    req = req + "index.html";
                } 
                
                    try {
                        FileInputStream fis = new FileInputStream(req);
                        byte[] data = new byte[fis.available()];
                        fis.read(data);
                        out.write(data);
                    } catch (FileNotFoundException e) {
                        new PrintStream(out).println("404 Not Found");
                        System.out.println("404 Not Found: " + req);

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
}
