package com.jelly.liaotian;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

		//得到本地蓝牙适配器
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		// 得到已配对的设备集合
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_item);
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_item);
		// 已配对设备的列表
		ListView pairedList = (ListView) findViewById(R.id.list_devices);
		pairedList.setAdapter(mPairedDevicesArrayAdapter);
		// 未配对的新设备的列表
		ListView newList = (ListView) findViewById(R.id.new_devices);
		newList.setAdapter(mNewDevicesArrayAdapter);

		// 注册广播 当设备被发现时
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		// 注册广播 当发现过程完成以后
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);
		
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
	}

	/*
	 * 内部类 广播事件接收器，监听发现的设备
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 每个action都与onCreate里面的filter一一对应
			// 当发现一个设备的时候
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// 如果已将配对，就略过。BOND_BONDED代表已匹配
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					mNewDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				}
				// 当发现过程完成以后
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				setProgressBarIndeterminateVisibility(false);
				setTitle(R.string.select_device);
				if (mNewDevicesArrayAdapter.getCount() == 0) {
					String noDevices = getResources().getText(
							R.string.none_found).toString();
					mNewDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
