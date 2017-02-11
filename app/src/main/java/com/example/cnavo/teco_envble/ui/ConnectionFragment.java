package com.example.cnavo.teco_envble.ui;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.cnavo.teco_envble.R;
import com.example.cnavo.teco_envble.service.BluetoothBroadcastReceiver;
import com.example.cnavo.teco_envble.service.BluetoothService;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
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
public class ConnectionFragment extends Fragment implements ListButtonClickListener {

    public static final String BROADCAST_CLEAR_LIST = "clear_list";
    private static final int BLUETOOTH_REQUEST = 1;
    private static final int PERMISSION_REQUEST = 2;
    private static final int RESPONSE_ENABLE_BT_SUCCESS = -1;

    @ViewById(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    @ViewById(R.id.connection_recycler_view)
    RecyclerView recyclerView;

    private BluetoothAdapter bluetoothAdapter;
    private ConnectionListAdapter connectionListAdapter;

    public static ConnectionFragment create() {
        return ConnectionFragment_.builder().build();
    }

    @AfterInject
    void init() {
        if (connectionListAdapter == null) {
            BluetoothManager manager = (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);

            this.bluetoothAdapter = manager.getAdapter();
            this.connectionListAdapter = ConnectionListAdapter.create(this);
            BluetoothBroadcastReceiver.addConnectionListAdapter(this.connectionListAdapter);
        }

        initPermissions();
    }

    @AfterViews
    void initView() {
        this.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        this.recyclerView.setLayoutManager(linearLayoutManager);
        this.recyclerView.setAdapter(connectionListAdapter);
    }

    @Click(R.id.floatingActionButton)
    void floatingActionButtonClicked() {
        startScanService();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BLUETOOTH_REQUEST && resultCode == RESPONSE_ENABLE_BT_SUCCESS) {
            startScanService();
        }
    }

    @Override
    public void onConnectButtonClicked(BluetoothDevice device) {
        Intent intent = new Intent(getActivity(), BluetoothService.class);
        intent.setAction(BluetoothService.CONNECT_WITH_DEVICE);
        intent.putExtra(BluetoothService.DEVICE, device);
        getActivity().startService(intent);
    }


    @Override
    public void onDisconnectButtonClicked(BluetoothDevice device) {
        Intent intent = new Intent(getActivity(), BluetoothService.class);
        intent.setAction(BluetoothService.DISCONNECT_FROM_DEVICE);
        intent.putExtra(BluetoothService.DEVICE, device);
        getActivity().startService(intent);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean success = true;
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                success = false;
            }
        }

        if (success) {
            initServices();
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
            startScanService();
        }
    }

    private int getPermission(String permission) {
        return ContextCompat.checkSelfPermission(getActivity(), permission);
    }

    private void startScanService() {
        Log.d(ConnectionFragment.class.getName(), "Start scan service");

        Intent intent = new Intent(getActivity(), BluetoothService.class);
        intent.setAction(BluetoothService.SCAN_FOR_DEVICES);
        getActivity().startService(intent);
    }

    public ConnectionListAdapter getAdapter() {
        return this.connectionListAdapter;
    }
}
