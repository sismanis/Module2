package com.jmel.fragmentpagersupport;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import android.app.Application;

public class MyApplication extends Application {
	Socket sock = null;
	    
	//  Called when the user wants to send a message
	
	public void sendMessage(int id) {
		
		// Get the message from the box
		
		//EditText et = (EditText) findViewById(R.id.MessageText);
		//String msg = et.getText().toString();

		// Create an array of bytes.  First byte will be the
		// message length, and the next ones will be the message
//		Integer i = Integer.valueOf(id);
//		String s = i.toString();
		byte buf[] = new byte[1];
		buf[0] = (byte) id;
//		System.arraycopy(s.getBytes(), 0, buf, 1, 1);

		// Now send through the output stream of the socket
		
		OutputStream out;
		try {
			out = sock.getOutputStream();
			try {
				out.write(buf, 0, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
