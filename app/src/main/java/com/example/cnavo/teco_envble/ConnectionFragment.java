package com.example.cnavo.teco_envble;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cnavo on 07.02.2017.
 */

@EFragment(R.layout.connection_fragment)
public class ConnectionFragment extends Fragment {

    private static final int SCAN_PERIOD = 10000;
    private static final int BLUETOOTH_REQUEST = 1;
    private static final int PERMISSION_REQUEST = 2;
    private static final int RESPONSE_ENABLE_BT_SUCCESS = -1;

    @ViewById(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    @ViewById(R.id.connection_recycler_view)
    RecyclerView recyclerView;

    private BluetoothAdapter bluetoothAdapter;
    private ConnectionListAdapter connectionListAdapter;
    private Handler handler;
    private boolean scanning;
    private ScanCallback leScanCallback;

    public static ConnectionFragment create() {
        return ConnectionFragment_.builder().build();
    }

    @AfterInject
    void init() {
        BluetoothManager manager = (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = manager.getAdapter();

        handler = new Handler();

        leScanCallback = initCallback();

        initPermissions();
        initServices();
    }

    @Click(R.id.floatingActionButton)
    void floatingActionButtonClicked() {
        scanLeDevice(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BLUETOOTH_REQUEST && resultCode == RESPONSE_ENABLE_BT_SUCCESS) {
            scanLeDevice(true);
        }
    }


    private ScanCallback initCallback() {
        return new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                System.out.println("Result Name: " + result.getDevice().getName() + " callbackType: " + callbackType);
            }
        };
    }

    private void initPermissions() {
        List<String> tempPermissions = new ArrayList<>();

        if (getPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            tempPermissions.add(Manifest.permission.INTERNET);
        }
        if (getPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            tempPermissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        }
        if (getPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            tempPermissions.add(Manifest.permission.BLUETOOTH);
        }
        if (getPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            tempPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (tempPermissions.size() > 0) {
            String[] permissions = Arrays.copyOf(tempPermissions.toArray(), tempPermissions.size(), String[].class);

            requestPermissions(permissions, PERMISSION_REQUEST);
        }
    }

    private void initServices() {
        if (getPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
                && getPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initBluetooth();
        }
    }

    private void initBluetooth() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_REQUEST);
        } else {
            scanLeDevice(true);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            bluetoothAdapter.getBluetoothLeScanner().startScan(leScanCallback);
        } else {
            scanning = false;
            bluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
        }

    }

    private int getPermission(String permission) {
        return ContextCompat.checkSelfPermission(getActivity(), permission);
    }


}
