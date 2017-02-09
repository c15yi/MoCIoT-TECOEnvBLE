package com.example.cnavo.teco_envble;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.cnavo.teco_envble.data.BLESensorDataBuilder;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by cnavo on 07.02.2017.
 */

@EFragment(R.layout.connection_fragment)
public class ConnectionFragment extends Fragment implements ListButtonClickListener {

    private static final int SCAN_PERIOD                    = 10000;
    private static final int BLUETOOTH_REQUEST              = 1;
    private static final int PERMISSION_REQUEST             = 2;
    private static final int RESPONSE_ENABLE_BT_SUCCESS     = -1;

    private static final UUID GAS_SERVICE_UUID              = new UUID(0x4b822f9039414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID ENVIRONMENTAL_SERVICE_UUID    = new UUID(0x0000181a00001000L, 0x800000805F9B34FBL);
    private static final UUID DUST_SERVICE_UUID             = new UUID(0x4b822fe039414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID CO_RAW_UUID                   = new UUID(0x4b822fa139414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID NO2_RAW_UUID                  = new UUID(0x4b822f9139414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID NH3_RAW_UUID                  = new UUID(0x4b822fb139414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID CO_CALIBRATION_UUID           = new UUID(0x4b822fa239414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID NO2_CALIBRATION_UUID          = new UUID(0x4b822f9239414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID NH3_CALIBRATION_UUID          = new UUID(0x4b822fb239414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID TEMPERATURE_UUID              = new UUID(0x00002a6e00001000L, 0x800000805f9b34fbL);
    private static final UUID HUMIDITY_UUID                 = new UUID(0x00002a6f00001000L, 0x800000805f9b34fbL);
    private static final UUID PRESSURE_UUID                 = new UUID(0x00002a6d00001000L, 0x800000805f9b34fbL);
    private static final UUID DUST_RAW_UUID                 = new UUID(0x4b822fe139414a4bL, 0xa3ccb2602ffe0d00L);

    @ViewById(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    @ViewById(R.id.connection_recycler_view)
    RecyclerView recyclerView;

    private BluetoothAdapter bluetoothAdapter;
    private ConnectionListAdapter connectionListAdapter;

    private ScanCallback leScanCallback;
    private BluetoothGattCallback gattCallback;
    private BluetoothGatt gatt;

    private Handler handler;
    private BLESensorDataBuilder bleSensorDataBuilder;
    private boolean scanning;

    private List<UUID> serviceUUIDs;
    private List<UUID> gasServiceCharacteristicUUIDs;
    private List<UUID> environmentCharacteristicUUIDs;
    private List<UUID> dustCharacteristicUUIDs;
    private List<ScanFilter> serviceScanFilters;

    public static ConnectionFragment create() {
        return ConnectionFragment_.builder().build();
    }

    @AfterInject
    void init() {
        BluetoothManager manager = (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);

        this.bluetoothAdapter = manager.getAdapter();
        this.connectionListAdapter = ConnectionListAdapter.create(this);
        this.handler = new Handler();
        this.bleSensorDataBuilder = new BLESensorDataBuilder();
        this.leScanCallback = initScanCallback();
        this.gattCallback = initGattCallback();
        this.serviceUUIDs = makeListOfServiceUUIDs();
        this.gasServiceCharacteristicUUIDs = makeListOfGasUUIDs();
        this.environmentCharacteristicUUIDs = makeListOfEnvironmentUUIDs();
        this.dustCharacteristicUUIDs = makeListOfDustUUIDs();
        this.serviceScanFilters = createFilterList(serviceUUIDs);

        initPermissions();
        initServices();
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
        scanLeDevice(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BLUETOOTH_REQUEST && resultCode == RESPONSE_ENABLE_BT_SUCCESS) {
            scanLeDevice(true);
        }
    }

    @Override
    public void onConnectButtonClicked(BluetoothDevice device) {
        gatt = device.connectGatt(getContext(), false, this.gattCallback);
    }


    @Override
    public void onDisconnectButtonClicked(BluetoothDevice device) {
        if (gatt.getDevice() == device) {
            gatt.close();
        }
    }

    private ScanCallback initScanCallback() {
        return new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                System.out.println(result.getDevice());

                connectionListAdapter.addDevice(result.getDevice());
            }
        };
    }

    private BluetoothGattCallback initGattCallback() {
        return new BluetoothGattCallback() {
            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                handleCharacteristics(characteristic);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
            }
        };
    }

    private void handleCharacteristics(BluetoothGattCharacteristic characteristic) {
        String characteristicUUID = characteristic.getUuid().toString();
        int formatType = BluetoothGattCharacteristic.FORMAT_UINT16;
        int offset = 0;

        if (CO_RAW_UUID.toString().equals(characteristicUUID)) {
            bleSensorDataBuilder.setCORaw(characteristic.getIntValue(formatType, offset));
        } else if (NO2_RAW_UUID.toString().equals(characteristicUUID)) {
            bleSensorDataBuilder.setNO2Raw(characteristic.getIntValue(formatType, offset));
        } else if (NH3_RAW_UUID.toString().equals(characteristicUUID)) {
            bleSensorDataBuilder.setNH3Raw(characteristic.getIntValue(formatType, offset));
        } else if (CO_CALIBRATION_UUID.toString().equals(characteristicUUID)) {
            bleSensorDataBuilder.setCOCalibration(characteristic.getIntValue(formatType, offset));
        } else if (NO2_CALIBRATION_UUID.toString().equals(characteristicUUID)) {
            bleSensorDataBuilder.setNO2Calibration(characteristic.getIntValue(formatType, offset));
        } else if (NH3_CALIBRATION_UUID.toString().equals(characteristicUUID)) {
            bleSensorDataBuilder.setNH3Calibration(characteristic.getIntValue(formatType, offset));
        } else if (TEMPERATURE_UUID.toString().equals(characteristicUUID)) {
            bleSensorDataBuilder.setTemperature(characteristic.getIntValue(formatType, offset));
        } else if (HUMIDITY_UUID.toString().equals(characteristicUUID)) {
            bleSensorDataBuilder.setHumidity(characteristic.getIntValue(formatType, offset));
        } else if (PRESSURE_UUID.toString().equals(characteristicUUID)) {
            bleSensorDataBuilder.setPressure(characteristic.getIntValue(formatType, offset));
        } else if (DUST_RAW_UUID.toString().equals(characteristicUUID)) {
            bleSensorDataBuilder.setDustRaw(characteristic.getIntValue(formatType, offset));
        }

        if (bleSensorDataBuilder.getMissingValuesCount() == 0) {

        }
    }

    private List<UUID> makeListOfServiceUUIDs() {
        List<UUID> result = new ArrayList<>();
        result.add(GAS_SERVICE_UUID);
        result.add(ENVIRONMENTAL_SERVICE_UUID);
        result.add(DUST_SERVICE_UUID);

        return result;
    }

    private List<UUID> makeListOfGasUUIDs() {
        List<UUID> result = new ArrayList<>();
        result.add(CO_RAW_UUID);
        result.add(NO2_RAW_UUID);
        result.add(NH3_RAW_UUID);
        result.add(CO_CALIBRATION_UUID);
        result.add(NO2_CALIBRATION_UUID);
        result.add(NH3_CALIBRATION_UUID);

        return result;
    }

    private List<UUID> makeListOfEnvironmentUUIDs() {
        List<UUID> result = new ArrayList<>();
        result.add(TEMPERATURE_UUID);
        result.add(HUMIDITY_UUID);
        result.add(PRESSURE_UUID);

        return result;
    }

    private List<UUID> makeListOfDustUUIDs() {
        List<UUID> result = new ArrayList<>();
        result.add(DUST_RAW_UUID);

        return result;
    }

    private List<ScanFilter> createFilterList(List<UUID> uuids) {
        List<ScanFilter> result = new ArrayList<>();

        for (UUID uuid : uuids) {
            ScanFilter filter = new ScanFilter
                    .Builder()
                    .setServiceUuid(new ParcelUuid(uuid))
                    .build();

            result.add(filter);
        }

        return result;
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
        connectionListAdapter.clearDevices();

        if (enable) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            ScanSettings settings = new ScanSettings.Builder().build();

            scanning = true;
            bluetoothAdapter.getBluetoothLeScanner().startScan(/*serviceScanFilters, settings, */leScanCallback);
        } else {
            scanning = false;
            bluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
        }

    }


    private int getPermission(String permission) {
        return ContextCompat.checkSelfPermission(getActivity(), permission);
    }
}
