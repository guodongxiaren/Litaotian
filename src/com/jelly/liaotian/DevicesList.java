package com.jelly.liaotian;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;

public class DevicesList extends Activity {
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	// ��������������
	private BluetoothAdapter mBtAdapter;
	// ����Ե������豸
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	// δ��Ե������豸
	private ArrayAdapter<String> mNewDevicesArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devices_list);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_item);
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_item);

		// ע�ᵱ�豸������ʱ�Ĺ㲥
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);
	}

	/*
	 * �ڲ��� �㲥�¼����������������ֵ��豸
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			System.out.println(device.getAddress());
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
