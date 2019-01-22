package com.dushyant.bluetoothtestapplication;

/*Interface for Bluethooth Connection Status Check*/
public interface BluetoothConnection {
    void onSuccessfulConnection(String name, String address);
    void onConnectionFailed();
    void onDisconnect();
    void onReceiveData(byte[] data, String message);
}
