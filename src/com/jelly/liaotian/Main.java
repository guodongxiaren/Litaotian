package com.jelly.liaotian;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Main extends Activity {

	private EditText chatext;
	private TextView chatbox;
	private Button chatok;

	private static final String IP = "192.168.56.1";
	private static final int PORT = 5432;
	private Thread mThread = null;

	private Socket socket = null;
	private InputStream in;
	private OutputStream out;
	private DataOutputStream dout;
	private DataInputStream din;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// handler = new MyHandler();
		chatext = (EditText) findViewById(R.id.chattxt);
		chatbox = (TextView) findViewById(R.id.chatbox);
		chatok = (Button) findViewById(R.id.chatok);
		
		chatbox.setCursorVisible(false);
		chatbox.setFocusable(false);
		chatbox.setFocusableInTouchMode(false);
		chatbox.setGravity(2);// ??

		chatok.setOnClickListener(new BtnOnClickListener());

		mThread = new Thread(new SocketThread());
		mThread.start();
		
		PrintThread pt = new PrintThread();
		new Thread(pt).start();
	}

/**
 * 每次发送消息后，清空编辑框。不能再按钮事件中清空。否则发送的消息都变成空
 */
	@SuppressLint("HandlerLeak")
	Handler textHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			boolean flag = b.getBoolean("chatext");
			if(flag)
				chatext.setText("");
		}
		
	};
	
	@SuppressLint("HandlerLeak")
	Handler mainHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			String text = b.getString("text")+"\n";
			chatbox.append(text);
		}
		
	};
/**
 * socket连接的线程
 * @author Acer
 *
 */
	class SocketThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				socket = new Socket(IP, PORT);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
/**
 * 发送消息的线程
 * @author Acer
 *
 */
	class SendThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				out = socket.getOutputStream();
				dout = new DataOutputStream(out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String msg = chatext.getText().toString();
			try {
				dout.writeUTF(msg);
				Bundle b = new Bundle();
				b.putBoolean("chatext",true);
				Message m = new Message();
				m.setData(b);
				textHandler.sendMessage(m);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
/**
 * 接收并显示的线程
 * @author Acer
 *
 */
	class PrintThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				in = socket.getInputStream();
				din = new DataInputStream(in);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			while (true) {
			
				try {
					String line = din.readUTF();
					Bundle b = new Bundle();
					b.putString("text", line);
					Message msg = new Message();
					msg.setData(b);
					mainHandler.sendMessage(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

	}

	class BtnOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SendThread st = new SendThread();
			new Thread(st).start();
			
		}

	}
}
