package com.omni.ble.library.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.omni.ble.library.R;
import com.omni.ble.library.model.BLEDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描到的蓝牙设备列表 <br />
 * created by CxiaoX at 2017/1/10 15:19.
 */

public class ScanDeviceAdapter extends BaseAdapter{
    private static final String TAG=ScanDeviceAdapter.class.getSimpleName();

    private List<BLEDevice> list=new ArrayList<>();
    private LayoutInflater inflater;
    private ViewHolder holder;
    public ScanDeviceAdapter(Context context){
        inflater =LayoutInflater.from(context);

    }

    public void addBLEDevice(BLEDevice bleDevice){
        if(!list.contains(bleDevice)) {
            list.add(bleDevice);
            notifyDataSetChanged();

        }
    }
    public void clearDevice(){
        list.clear();
        notifyDataSetChanged();
    }

    public void removeBLEDevice(String deviceAddress){
        for(BLEDevice bleDevice:list){
            if(bleDevice.getDevice().getAddress().equals(deviceAddress)){
                list.remove(bleDevice);
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view==null){
            view = inflater.inflate(R.layout.adapter_scan_device,parent,false);

            holder = new ViewHolder();
            holder.txMac = (TextView) view.findViewById(R.id.tx_ble_mac);
            holder.txName = (TextView) view.findViewById(R.id.tx_ble_name);
            holder.txPower = (TextView) view.findViewById(R.id.tx_power);
            holder.txStatus = view.findViewById(R.id.tx_status);

            holder.txRssiValue = (TextView) view.findViewById(R.id.tx_ble_rssi);


            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }


        BLEDevice bleDevice= list.get(position);


        holder.txName.setText(bleDevice.getDevice().getName());
        holder.txMac.setText(bleDevice.getDevice().getAddress());
        holder.txRssiValue.setText(String.valueOf(bleDevice.getRssi()));
        holder.txPower.setText(String.format("power：%smV",bleDevice.getPowerByScanRecord()));
        holder.txStatus.setText(String.format("open status：%s",bleDevice.getLockStatus()));



        return view;
    }

    private class ViewHolder {
         TextView txName;
         TextView txPower;
         TextView txMac;
         TextView txStatus;
         TextView txRssiValue;


    }


}
