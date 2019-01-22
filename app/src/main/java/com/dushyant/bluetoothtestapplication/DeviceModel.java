package com.dushyant.bluetoothtestapplication;

import android.bluetooth.BluetoothDevice;

/*Bluethooth Device Model*/

public class DeviceModel {
    private String name;
    private String address;
    private BluetoothDevice device;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
}
