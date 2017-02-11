package com.example.cnavo.teco_envble.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.cnavo.teco_envble.ui.ConnectionFragment;
import com.example.cnavo.teco_envble.ui.ConnectionListAdapter;

/**
 * Created by cnavo on 10.02.2017.
 */

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    public static final int INVALID_VALUE = Integer.MIN_VALUE;

    public static final String BROADCAST_CLEAR_LIST = "BROADCAST_CLEAR_LIST";
    public static final String BROADCAST_ADD_DEVICE = "BROADCAST_ADD_DEVICE";
    public static final String BROADCAST_ADD_VALUE = "BROADCAST_ADD_VALUE";
    public static final String BROADCAST_DEVICE = "BROADCAST_DEVICE";
    public static final String CANCEL_PROGRESS_DIALOG = "cancel_progress_dialog";

    public static final String CO_VALUE = Descriptions.CO.toString();
    public static final String NO2_VALUE = Descriptions.NO2.toString();
    public static final String NH3_VALUE = Descriptions.NH3.toString();
    public static final String TEMPERATURE_VALUE = Descriptions.TEMPERATURE.toString();
    public static final String HUMIDITY_VALUE = Descriptions.HUMIDITY.toString();
    public static final String PRESSURE_VALUE = Descriptions.PRESSURE.toString();
    public static final String DUST_VALUE = Descriptions.DUST.toString();

    private static final String[] VALUES = {CO_VALUE, NO2_VALUE, NH3_VALUE, TEMPERATURE_VALUE, HUMIDITY_VALUE, PRESSURE_VALUE, DUST_VALUE};
    private static ConnectionListAdapter connectionListAdapter;
    private DataHelper dataHelper;

    public BluetoothBroadcastReceiver() {
            this.dataHelper = DataHelper.getDataHelper();
    }

    public static BluetoothBroadcastReceiver create() {
        return new BluetoothBroadcastReceiver();
    }

    public static void addConnectionList(ConnectionListAdapter adapter) {
        connectionListAdapter = adapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case BROADCAST_CLEAR_LIST:
                if (connectionListAdapter != null) {
                    connectionListAdapter.clearDevices();
                }
                break;
            case BROADCAST_ADD_DEVICE:
                if (connectionListAdapter != null) {
                    connectionListAdapter.addDevice(intent.getExtras().getParcelable(BROADCAST_DEVICE));
                }
                break;
            case BROADCAST_ADD_VALUE:
                for (String value : VALUES) {
                    double sensorValue = intent.getDoubleExtra(value, INVALID_VALUE);
                    if (sensorValue != INVALID_VALUE) {
                        dataHelper.addValue(value, sensorValue, true);
                    }
                }
                break;
            default:
                break;
        }
    }
}
