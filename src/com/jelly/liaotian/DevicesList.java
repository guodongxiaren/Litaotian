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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

		// �õ���������������
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		// �õ�����Ե��豸����
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_item);
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_item);
		// ������豸���б�
		ListView pairedList = (ListView) findViewById(R.id.list_devices);
		pairedList.setAdapter(mPairedDevicesArrayAdapter);
		pairedList.setOnItemClickListener(mDeviceClickListener);
		// δ��Ե����豸���б�
		ListView newList = (ListView) findViewById(R.id.new_devices);
		newList.setAdapter(mNewDevicesArrayAdapter);
		newList.setOnItemClickListener(mDeviceClickListener);
		// ע��㲥 ���豸������ʱ
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		// ע��㲥 �����ֹ�������Ժ�
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);

		// �����������豸�������Ƕ���ӵ�adapter��
		if (pairedDevices.size() > 0) {
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				mPairedDevicesArrayAdapter.add(device.getName() + "\n"
						+ device.getAddress());
			}
		} else {
			String noDevices = getResources().getText(R.string.none_paired)
					.toString();
			mPairedDevicesArrayAdapter.add(noDevices);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ȷ�����ǲ���ȥ�����豸
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}

		// ����㲥��������ע��
		this.unregisterReceiver(mReceiver);
	}

	/*
	 * ListView��Ŀ�����¼�������
	 */
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long id) {
			// ��������Ҫ����ĳ���豸ʱ��ֹͣ����
			mBtAdapter.cancelDiscovery();

			// �õ�MAC��ַ��������17λ
			String info = ((TextView) v).getText().toString();
			String address = info.substring(info.length() - 17);

			// ����һ��Intent���󣬰���Mac��ַ
			Intent intent = new Intent();
			intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

			// ����activity�ķ��ؽ�������ر�Activity
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	};
	/*
	 * �ڲ��� �㲥�¼����������������ֵ��豸
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// ÿ��action����onCreate�����filterһһ��Ӧ
			// ������һ���豸��ʱ��
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// ����ѽ���ԣ����Թ���BOND_BONDED������ƥ��
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					mNewDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				}
				// �����ֹ�������Ժ�
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
