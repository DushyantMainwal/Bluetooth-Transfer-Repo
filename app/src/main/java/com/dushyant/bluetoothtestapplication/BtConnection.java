package com.dushyant.bluetoothtestapplication;

import android.content.Context;
import android.util.Log;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

/*Bluethooth Connection Class*/
public class BtConnection implements BluetoothSPP.OnDataReceivedListener, BluetoothSPP.BluetoothConnectionListener {

    private static BluetoothSPP btInstance = null;
    private static BtConnection instance = null;
    private BluetoothConnection bluetoothConnection;

    private BtConnection() {
        // Exists only to defeat instantiation.
    }

    public static BtConnection getInstance(Context context) {
        if (btInstance == null || instance == null) {
            btInstance = new BluetoothSPP(context);
            instance = new BtConnection();
        }
        return instance;
    }

    public void setBluetoothConnection(BluetoothConnection bluetoothConnection) {
        this.bluetoothConnection = bluetoothConnection;
    }

    public void connect(String macAdd) {
        if (btInstance == null) {
            return;
        }

        if (!btInstance.isBluetoothEnabled()) {
            btInstance.enable();
        } else {
            if (!btInstance.isServiceAvailable()) {
                btInstance.setupService();
                btInstance.startService(BluetoothState.DEVICE_OTHER);
            }
        }

        if (btInstance.getServiceState() == BluetoothState.STATE_CONNECTED) {
            btInstance.disconnect();
        } else {
            btInstance.connect(macAdd);
            btInstance.setBluetoothConnectionListener(this);
            btInstance.setOnDataReceivedListener(this);

        }
    }

    public void disconnect() {
        if (btInstance != null) {
            btInstance.disconnect();
        }
    }

    public void send(String text) {
        if (btInstance == null) {
            return;
        }
        btInstance.send(text, true);
    }

    @Override
    public void onDataReceived(byte[] data, String message) {
        System.out.println("Receive Data Called");
        System.out.println("Message: " + message);
        Log.w("message", message);
        Log.e("data", data.toString());
        bluetoothConnection.onReceiveData(data, message);
    }

    @Override
    public void onDeviceConnected(String name, String address) {
        if (bluetoothConnection != null)
            bluetoothConnection.onSuccessfulConnection(name, address);
        Log.w("onDeviceConnected", "onDeviceConnected");
    }

    @Override
    public void onDeviceDisconnected() {
        if (bluetoothConnection != null)
            bluetoothConnection.onDisconnect();
        Log.w("onDeviceDisconnected", "onDeviceDisconnected");
    }

    @Override
    public void onDeviceConnectionFailed() {
        if (bluetoothConnection != null)
            bluetoothConnection.onConnectionFailed();
        Log.w("Failed", "onDeviceConnectionFailed");
    }
}
