package com.jelly.liaotian;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Main extends Activity {

	private EditText chatext;
	private Button chatok;

	private static final String IP = "192.168.56.1";//56.1
	private static final int PORT = 5432;
	
	private Thread mThread = null;

	private Socket socket = null;
	private InputStream in;
	private OutputStream out;
	private DataOutputStream dout;
	private DataInputStream din;
	private listViewAdapter adapter = new listViewAdapter();
	List<String> chatList;
	ListView listView;
	LayoutInflater inflater;
	LinearLayout loadingLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
		chatList = new LinkedList<String>();
		chatext = (EditText) findViewById(R.id.chattxt);
		listView = (ListView) findViewById(R.id.listView);
		chatok = (Button) findViewById(R.id.chatok);

		loadingLayout = new LinearLayout(this);
		listView.addFooterView(loadingLayout);
		listView.setAdapter(adapter);
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
	Handler clearHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			boolean flag = b.getBoolean("text");
			if(flag)
				chatext.setText("");
		}
		
	};
	
	@SuppressLint("HandlerLeak")
	Handler mainHandler = new Handler(){

		@Override
		public void handleMessage(Message m) {
			// TODO Auto-generated method stub
			super.handleMessage(m);
			Bundle b = m.getData();
			String msg = b.getString("info")+"\n";
			Log.i("hand----", msg);
			if(msg.startsWith("-")){
				Log.i("send----", msg);
				msg = msg.substring(1);
				View sendView = inflater.inflate(R.layout.chat_item_right, null);
				TextView listText = (TextView)sendView.findViewById(R.id.message);
				listText.setText(msg);
				listView.addFooterView(sendView);
			}
			else if(msg.startsWith("+")){
				Log.i("rece----", msg);
				msg = msg.substring(1);
				View receView = inflater.inflate(R.layout.chat_item_left, null);
				TextView listText = (TextView)receView.findViewById(R.id.message);
				listText.setText(msg);
				listView.addFooterView(receView);
			}
			adapter.notifyDataSetChanged();
		}
		
	};
/**
 * socket连接的线程
 * @author Acer
 *
 */
	private class SocketThread implements Runnable {

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
	private class SendThread implements Runnable {

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
				b.putBoolean("text",true);
				Message m = new Message();
				Message m2 = new Message();
				m.setData(b);
				clearHandler.sendMessage(m);
				Bundle b2 = new Bundle();
				b2.putString("info", "-"+msg);
				m2.setData(b2);
				mainHandler.sendMessage(m2);
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
	private class PrintThread implements Runnable {

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
					String msg = din.readUTF();
					Bundle b = new Bundle();
					b.putString("info", "+"+msg);
					Message m = new Message();
					m.setData(b);
					mainHandler.sendMessage(m);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

	}

	private class BtnOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SendThread st = new SendThread();
			new Thread(st).start();
			
		}
	}
	private class listViewAdapter extends BaseAdapter{

		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

		
	}

}
