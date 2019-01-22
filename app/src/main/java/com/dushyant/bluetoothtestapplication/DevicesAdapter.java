package com.dushyant.bluetoothtestapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DevicesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<DeviceModel> deviceList;
    private OnDeviceAdapterListener listener;
    private boolean isPairedDevice = false;

    public DevicesAdapter(Context context, ArrayList<DeviceModel> deviceList, OnDeviceAdapterListener listener) {
        this.context = context;
        this.deviceList = deviceList;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return deviceList == null ? 0 : deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setDeviceList(ArrayList<DeviceModel> deviceList) {
        this.deviceList = deviceList;
        this.isPairedDevice = false;
        notifyDataSetChanged();
    }

    public void setPairedDevice() {
        this.isPairedDevice = true;
    }

    public boolean isPairedDevice() {
        return isPairedDevice;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_devices, null);
        }

        final DeviceModel model = deviceList.get(position);

        TextView name = convertView.findViewById(R.id.device_name);
        TextView address = convertView.findViewById(R.id.device_address);
        LinearLayout linearLayout = convertView.findViewById(R.id.linear_layout);

        name.setText(model.getName());
        address.setText(model.getAddress());
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(model);
            }
        });

        return convertView;
    }

    public interface OnDeviceAdapterListener {
        void onItemClick(DeviceModel model);
    }
}
