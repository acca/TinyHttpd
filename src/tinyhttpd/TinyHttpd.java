
package tinyhttpd;

import java.net.*; 
import java.io.*; 

 
public class TinyHttpd { 
   public static void main( String argv[] ) 
    throws IOException {
       int port = 8000;
        if (argv.length>0) port=Integer.parseInt(argv[0]);
	ServerSocket ss = new ServerSocket(port);
        System.out.println("Server is ready");
	while ( true ) 
		new TinyHttpdConnection(ss.accept() ); 
   } 
}

