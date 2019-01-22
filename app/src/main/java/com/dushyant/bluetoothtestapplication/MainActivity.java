package com.dushyant.bluetoothtestapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AvailableDevices.AvailableDevicesCallback, DevicesAdapter.OnDeviceAdapterListener,
        BluetoothConnection {

//    Button b1, b2, b3, b4;
//    ListView lv;
//    private BluetoothAdapter BA;
//    private Set<BluetoothDevice> pairedDevices;

    Button onBtn, listBtn, offBtn, connectBluetooth, receiveData;
    ListView listView;
    DevicesAdapter devicesAdapter;
    BtConnection btConnection;
    BluetoothSPP bt;
    //    private String currentMacAddress = "";
    DeviceModel currentDeviceModel = null;
    boolean isSocketConnected;
    ConnectThread connectThread;
    private BluetoothAdapter mBluetoothAdapter;
    private AvailableDevices availableDevicesReceivers;
    private ArrayList<DeviceModel> availableDevices;

    private TextView receiveDataTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onBtn = findViewById(R.id.on_bluetooth);
        listBtn = findViewById(R.id.list_bluetooth);
        offBtn = findViewById(R.id.off_bluetooth);
        connectBluetooth = findViewById(R.id.connect_bluetooth);
        receiveData = findViewById(R.id.receive_data);
        listView = findViewById(R.id.list_view);
        receiveDataTv = findViewById(R.id.receive_data_tv);

        onBtn.setOnClickListener(this);
        listBtn.setOnClickListener(this);
        offBtn.setOnClickListener(this);
        connectBluetooth.setOnClickListener(this);
        receiveData.setOnClickListener(this);

        btConnection = BtConnection.getInstance(this);
        btConnection.setBluetoothConnection(this);

        bt = new BluetoothSPP(this);
        devicesAdapter = new DevicesAdapter(this, null, this);
        listView.setAdapter(devicesAdapter);
        availableDevices = new ArrayList<>();

//
//        b1 = (Button) findViewById(R.id.button);
//        b2 = (Button) findViewById(R.id.button2);
//        b3 = (Button) findViewById(R.id.button3);
//        b4 = (Button) findViewById(R.id.button4);
//
//        BA = BluetoothAdapter.getDefaultAdapter();
//        lv = (ListView) findViewById(R.id.listView);

        pairedDevices();

    }

    /*Get Paired Devies and Show in Recycler View*/
    private void pairedDevices() {
        btConnection = BtConnection.getInstance(this);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        ArrayList<DeviceModel> deviceList = new ArrayList<>();
        Log.d("Paired Devices: ", pairedDevices.size() + "");
        for (BluetoothDevice bt : pairedDevices) {
            DeviceModel model = new DeviceModel();
            model.setName(bt.getName());
            model.setAddress(bt.getAddress());
            deviceList.add(model);
        }
        devicesAdapter.setDeviceList(deviceList);
        devicesAdapter.setPairedDevice();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.on_bluetooth:
                onBluetooth();
                break;
            case R.id.list_bluetooth:
                listBluetooth();
                break;
            case R.id.off_bluetooth:
                offBluetooth();
                break;
            case R.id.connect_bluetooth:
                connectBluetooth();
                break;
            case R.id.receive_data:
                System.out.println("Send Data Called");
                btConnection.send("{\n" +
                        "\t\"name\": \"fs-extra\",\n" +
                        "\t\"repo\": \"https://github.com/jprichardson/node-fs-extra\",\n" +
                        "\t\"dependents\": 3279,\n" +
                        "\t\"disable\": true\n" +
                        "}");
                break;
        }
    }

    private void connectBluetooth() {
        if (currentDeviceModel == null)
            return;

        System.out.println("Mac Address: " + currentDeviceModel.getAddress());
        btConnection.connect(currentDeviceModel.getAddress());
//        connectThread = new ConnectThread();
//        UUID uuid = new UUID(1000, 100);
//        System.out.println("UUID " + uuid.toString());
//        isSocketConnected = connectThread.connect(currentDeviceModel.getDevice(), uuid);
//        btConnection.connect(currentMacAddress);
    }

    private void onBluetooth() {
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            }
        }
    }

    private void listBluetooth() {
        unregisterReceiver();

//        showAvailableDeviceDialog();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        availableDevicesReceivers = new AvailableDevices(this);
        registerReceiver(availableDevicesReceivers, filter);

        if (mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.startDiscovery();
    }

    private void offBluetooth() {
        if (bt == null || !bt.isBluetoothEnabled())
            return;

        if (bt.isServiceAvailable())
            bt.stopService();
        if (bt.isDiscovery())
            bt.cancelDiscovery();
        bt.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void newDeviceFound(DeviceModel model) {
        Toast.makeText(this, "New Device Found", Toast.LENGTH_SHORT).show();
        availableDevices.add(model);
        devicesAdapter.setDeviceList(availableDevices);
    }

    @Override
    public void discoveryFinished() {
        Toast.makeText(this, "Discovery Finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void discoveryStarted() {
        availableDevices.clear();
    }

    @Override
    public void bluethoothStateChanged(boolean state) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    private void unregisterReceiver() {
        if (availableDevicesReceivers != null) {
            try {
                unregisterReceiver(availableDevicesReceivers);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemClick(DeviceModel model) {
        if (devicesAdapter.isPairedDevice()) {
            currentDeviceModel = model;
            System.out.println("Model Selected: " + currentDeviceModel.getName());
            return;
        }

        try {
            System.out.println("Start Pairing... ");
            Method m = model.getDevice().getClass().getMethod("createBond", (Class[]) null);
            m.invoke(model.getDevice(), (Object[]) null);
            System.out.println("Stop Pairing");
            Log.d("pairDevice()", "Pairing finished.");

            currentDeviceModel = model;
        } catch (Exception e) {
            Log.e("pairDevice()", e.getMessage());
        }
    }

    @Override
    public void onSuccessfulConnection(String name, String address) {
        Toast.makeText(this, "Connection Successful with " + name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed() {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnect() {
        Toast.makeText(this, "Connection Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceiveData(byte[] data, String message) {
        receiveDataTv.setVisibility(View.VISIBLE);
        receiveDataTv.setText(message);
    }

    //    public void on(View v) {
//        if (!BA.isEnabled()) {
//            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(turnOn, 0);
//            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    public void off(View v) {
//        BA.disable();
//        Toast.makeText(getApplicationContext(), "Turned off", Toast.LENGTH_LONG).show();
//    }
//
//
//    public void visible(View v) {
//        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        startActivityForResult(getVisible, 0);
//    }
//
//
//    public void list(View v) {
//        pairedDevices = BA.getBondedDevices();
//
//        ArrayList list = new ArrayList();
//
//        for (BluetoothDevice bt : pairedDevices) list.add(bt.getName());
//        Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();
//
//        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
//
//        lv.setAdapter(adapter);
//    }
}
