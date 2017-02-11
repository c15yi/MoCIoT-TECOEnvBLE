package com.example.cnavo.teco_envble.ui;

import android.bluetooth.BluetoothDevice;
import android.view.View;

/**
 * Created by privat on 09.02.17.
 */

public interface ListButtonClickListener {
    void onConnectButtonClicked(BluetoothDevice device);
}
