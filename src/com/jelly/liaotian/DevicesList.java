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
	// 本地蓝牙适配器
	private BluetoothAdapter mBtAdapter;
	// 已配对的蓝牙设备
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	// 未配对的蓝牙设备
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

		// 注册当设备被发现时的广播
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);
	}

	/*
	 * 内部类 广播事件接收器，监听发现的设备
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
