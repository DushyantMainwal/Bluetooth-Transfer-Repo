package com.dushyant.bluetoothtestapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import java.util.ArrayList;

/*Broadcast Receiver for finding Available Bluethooth Devices*/

public class AvailableDevices extends BroadcastReceiver {

    private AvailableDevicesCallback callback;

    private static ArrayList<String> deviceList = new ArrayList<>();

    public AvailableDevices(AvailableDevicesCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("action is", action);
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            if (state == BluetoothAdapter.STATE_ON) {
                callback.bluethoothStateChanged(true);
            }else {
                callback.bluethoothStateChanged(false);
            }
        }else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            deviceList.clear();
            callback.discoveryStarted();
        }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            callback.discoveryFinished();
        }else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device != null && device.getName() != null) {
                if (!deviceList.contains(device.getAddress())) {
                    deviceList.add(device.getAddress());
                    DeviceModel model = new DeviceModel();
                    model.setName(device.getName());
                    model.setAddress(device.getAddress());
                    model.setDevice(device);
                    Log.d("MAC Address:", device.getAddress());
                    callback.newDeviceFound(model);
                }
            }
        }
    }
    public interface AvailableDevicesCallback{
        void newDeviceFound(DeviceModel model);
        void discoveryFinished();
        void discoveryStarted();
        void bluethoothStateChanged(boolean state);
    }
}
