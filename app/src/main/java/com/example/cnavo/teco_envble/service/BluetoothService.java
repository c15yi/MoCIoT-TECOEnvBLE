package com.example.cnavo.teco_envble.service;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import com.example.cnavo.teco_envble.data.BLESensorDataBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by cnavo on 10.02.2017.
 */

public class BluetoothService extends IntentService {

    public static final String BLUETOOTH_SERVICE_NAME = BluetoothService.class.getName();
    public static final String SCAN_FOR_DEVICES = "scan_for_devices";
    public static final String CONNECT_WITH_DEVICE = "connect_with_device";
    public static final String DEVICE = "device";
    public static final String DISCONNECT_FROM_DEVICE = "disconnect_from_device";

    private static final String LOG_TAG = "Bluetooth TAG";

    private static final int SCAN_PERIOD = 10000;

    private static final UUID GAS_SERVICE_UUID = new UUID(0x4b822f9039414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID ENVIRONMENTAL_SERVICE_UUID = new UUID(0x0000181a00001000L, 0x800000805F9B34FBL);
    private static final UUID DUST_SERVICE_UUID = new UUID(0x4b822fe039414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID CO_RAW_UUID = new UUID(0x4b822fa139414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID NO2_RAW_UUID = new UUID(0x4b822f9139414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID NH3_RAW_UUID = new UUID(0x4b822fb139414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID CO_CALIBRATION_UUID = new UUID(0x4b822fa239414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID NO2_CALIBRATION_UUID = new UUID(0x4b822f9239414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID NH3_CALIBRATION_UUID = new UUID(0x4b822fb239414a4bL, 0xa3ccb2602ffe0d00L);
    private static final UUID TEMPERATURE_UUID = new UUID(0x00002a6e00001000L, 0x800000805f9b34fbL);
    private static final UUID HUMIDITY_UUID = new UUID(0x00002a6f00001000L, 0x800000805f9b34fbL);
    private static final UUID PRESSURE_UUID = new UUID(0x00002a6d00001000L, 0x800000805f9b34fbL);
    private static final UUID DUST_RAW_UUID = new UUID(0x4b822fe139414a4bL, 0xa3ccb2602ffe0d00L);

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt gatt;
    private BluetoothGattCallback gattCallback;
    private ScanCallback leScanCallback;

    private boolean scanning;
    private BLESensorDataBuilder bleSensorDataBuilder;
    private Handler handler;
    private BluetoothDevice lastDevice;

    private List<ScanFilter> serviceScanFilters;
    private List<UUID> serviceUUIDs;
    private List<UUID> gasServiceCharacteristicUUIDs;
    private List<UUID> environmentCharacteristicUUIDs;
    private List<UUID> dustCharacteristicUUIDs;

    public BluetoothService() {
        super(BLUETOOTH_SERVICE_NAME);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        init();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case SCAN_FOR_DEVICES:
                Log.d("BluetoothService", "Start scanning");
                scanLeDevice(true);
                break;
            case CONNECT_WITH_DEVICE:
                if (gatt != null) {
                    gatt.disconnect();
                }
                lastDevice = intent.getExtras().getParcelable(BluetoothService.DEVICE);
                connectWithDevice(lastDevice);
                break;
            case DISCONNECT_FROM_DEVICE:
                lastDevice = intent.getExtras().getParcelable(BluetoothService.DEVICE);
                if (gatt.getDevice() == lastDevice) {
                    gatt.disconnect();
                    lastDevice = null;
                }
                break;
            default:
                break;
        }
    }

    private void connectWithDevice(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice != null) {
            gatt = bluetoothDevice.connectGatt(this, false, gattCallback);
        }
    }

    private void init() {
        BluetoothManager manager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);

        this.bluetoothAdapter = manager.getAdapter();
        this.handler = new Handler();
        this.bleSensorDataBuilder = new BLESensorDataBuilder();
        this.leScanCallback = initScanCallback();
        this.gattCallback = initGattCallback();
        this.serviceUUIDs = makeListOfServiceUUIDs();
        this.gasServiceCharacteristicUUIDs = makeListOfGasUUIDs();
        this.environmentCharacteristicUUIDs = makeListOfEnvironmentUUIDs();
        this.dustCharacteristicUUIDs = makeListOfDustUUIDs();
        this.serviceScanFilters = createFilterList(serviceUUIDs);
    }

    private ScanCallback initScanCallback() {
        return new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                Log.d(BLUETOOTH_SERVICE_NAME, "Found device " + result.getDevice().getName());

                Intent intent = new Intent();
                intent.setAction(BluetoothBroadcastReceiver.BROADCAST_ADD_DEVICE);
                intent.putExtra(BluetoothBroadcastReceiver.BROADCAST_DEVICE, result.getDevice());
                sendBroadcast(intent);
            }
        };
    }

    private BluetoothGattCallback initGattCallback() {
        return new BluetoothGattCallback() {

            private List<BluetoothGattCharacteristic> characteristics;
            private List<BluetoothGattCharacteristic> valueCharacteristics;
            private List<BluetoothGattDescriptor> descriptors;

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i(LOG_TAG, "Connected with device");
                    characteristics = new ArrayList<>();
                    valueCharacteristics = new ArrayList<>();
                    descriptors = new ArrayList<>();
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    if (lastDevice != null) {
                        BluetoothService.this.connectWithDevice(lastDevice);
                    }
                }
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                boolean success = gatt.setCharacteristicNotification(descriptor.getCharacteristic(), true);
                if (success) {
                    descriptors.add(descriptor);
                }

                writeDescriptor(gatt);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                valueCharacteristics.add(characteristic);
                consumeCharacteristic(gatt);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    for (BluetoothGattService gattService : gatt.getServices()) {
                        if (serviceUUIDs.contains(gattService.getUuid())) {
                            for (BluetoothGattCharacteristic characteristic : gattService.getCharacteristics()) {
                                if (gasServiceCharacteristicUUIDs.contains(characteristic.getUuid())
                                        || environmentCharacteristicUUIDs.contains(characteristic.getUuid())
                                        || dustCharacteristicUUIDs.contains(characteristic.getUuid())) {
                                    characteristics.add(characteristic);
                                }
                            }
                        }
                    }

                    setUpNextCharacteristic(gatt);
                }
            }

            private void setUpNextCharacteristic(BluetoothGatt gatt) {
                UUID readUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

                if (characteristics.size() > 0) {
                    BluetoothGattCharacteristic characteristic = characteristics.remove(0);
                    gatt.readDescriptor(characteristic.getDescriptor(readUUID));
                }
            }

            private void writeDescriptor(BluetoothGatt gatt) {
                if (characteristics.size() != 0) {
                    setUpNextCharacteristic(gatt);
                } else if (descriptors.size() > 0) {
                    BluetoothGattDescriptor descriptor = descriptors.remove(0);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);
                }
            }

            private void consumeCharacteristic(BluetoothGatt gatt) {
                if (characteristics.size() != 0) {
                    setUpNextCharacteristic(gatt);
                } else if (descriptors.size() != 0) {
                    writeDescriptor(gatt);
                } else if (valueCharacteristics.size() > 0) {
                    BluetoothGattCharacteristic characteristic = valueCharacteristics.remove(0);
                    handleCharacteristics(characteristic);
                    consumeCharacteristic(gatt);
                }
            }
        };
    }

    private void handleCharacteristics(BluetoothGattCharacteristic characteristic) {
        UUID currentUuid = characteristic.getUuid();
        Intent intent = new Intent();
        intent.setAction(BluetoothBroadcastReceiver.BROADCAST_ADD_VALUE);
        int value;

        if (currentUuid.equals(CO_RAW_UUID)) {
            value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
            bleSensorDataBuilder.setCORaw(value);
            if (bleSensorDataBuilder.CO_READY) {
                intent.putExtra(BluetoothBroadcastReceiver.CO_VALUE, bleSensorDataBuilder.buildCoValue());
                sendBroadcast(intent);
            }
        } else if (currentUuid.equals(CO_CALIBRATION_UUID)) {
            value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
            bleSensorDataBuilder.setCOCalibration(value);
            if (bleSensorDataBuilder.CO_READY) {
                intent.putExtra(BluetoothBroadcastReceiver.CO_VALUE, bleSensorDataBuilder.buildCoValue());
                sendBroadcast(intent);
            }
        } else if (currentUuid.equals(NO2_RAW_UUID)) {
            value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
            bleSensorDataBuilder.setNO2Raw(value);
            if (bleSensorDataBuilder.NO2_READY) {
                intent.putExtra(BluetoothBroadcastReceiver.NO2_VALUE, bleSensorDataBuilder.buildNo2Value());
                sendBroadcast(intent);
            }
        } else if (currentUuid.equals(NO2_CALIBRATION_UUID)) {
            value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
            bleSensorDataBuilder.setNO2Calibration(value);
            if (bleSensorDataBuilder.NO2_READY) {
                intent.putExtra(BluetoothBroadcastReceiver.NO2_VALUE, bleSensorDataBuilder.buildNo2Value());
                sendBroadcast(intent);
            }
        } else if (currentUuid.equals(NH3_RAW_UUID)) {
            value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
            bleSensorDataBuilder.setNH3Raw(value);
            if (bleSensorDataBuilder.NH3_READY) {
                intent.putExtra(BluetoothBroadcastReceiver.NH3_VALUE, bleSensorDataBuilder.buildNh3Value());
                sendBroadcast(intent);
            }
        } else if (currentUuid.equals(NH3_CALIBRATION_UUID)) {
            value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
            bleSensorDataBuilder.setNH3Calibration(value);
            if (bleSensorDataBuilder.NH3_READY) {
                intent.putExtra(BluetoothBroadcastReceiver.NH3_VALUE, bleSensorDataBuilder.buildNh3Value());
                sendBroadcast(intent);
            }
        } else if (currentUuid.equals(TEMPERATURE_UUID)) {
            double temperature = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0) / 100d;
            bleSensorDataBuilder.setTemperature(temperature);
            intent.putExtra(BluetoothBroadcastReceiver.TEMPERATURE_VALUE, temperature);
            sendBroadcast(intent);
        } else if (currentUuid.equals(HUMIDITY_UUID)) {
            intent.putExtra(BluetoothBroadcastReceiver.HUMIDITY_VALUE, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0) / 100d);
            sendBroadcast(intent);
        } else if (currentUuid.equals(PRESSURE_UUID)) {
            intent.putExtra(BluetoothBroadcastReceiver.PRESSURE_VALUE, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0) / 10d);
            sendBroadcast(intent);
        } else if (currentUuid.equals(DUST_RAW_UUID)) {
            value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
            bleSensorDataBuilder.setDustRaw(value);
            if (bleSensorDataBuilder.DUST_READY) {
                intent.putExtra(BluetoothBroadcastReceiver.DUST_VALUE, bleSensorDataBuilder.buildDustValue());
                sendBroadcast(intent);
            }
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

    private void scanLeDevice(final boolean enable) {
        Intent intent = new Intent();
        intent.setAction(BluetoothBroadcastReceiver.BROADCAST_CLEAR_LIST);
        sendBroadcast(intent);

        if (enable) {
            handler.postDelayed(() -> {
                scanning = false;
                bluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
            }, SCAN_PERIOD);

            ScanSettings settings = new ScanSettings.Builder().build();

            scanning = true;
            bluetoothAdapter.getBluetoothLeScanner().startScan(serviceScanFilters, settings, leScanCallback);
        } else {
            scanning = false;
            bluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
        }

    }

}