package com.example.cnavo.teco_envble.data;

import com.example.cnavo.teco_envble.service.BluetoothBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by privat on 09.02.17.
 */

public class BLESensorDataBuilder {

    private Integer CORaw;
    private Integer COCalibration;
    private Integer NO2Raw;
    private Integer NO2Calibration;
    private Integer NH3Raw;
    private Integer NH3Calibration;

    public boolean CO_READY = false;
    public boolean NO2_READY = false;
    public boolean NH3_READY = false;
    public boolean DUST_READY = false;

    private Double temperature;

    private List<Integer> dustValues = new ArrayList<>();

    public BLESensorDataBuilder setCORaw(int CORaw) {
        if (this.CORaw == null) {
            this.CORaw = CORaw;
        } else if (temperature != null) {
            this.CO_READY = true;
        }
        return this;
    }

    public BLESensorDataBuilder setCOCalibration(int COCalibration) {
        if (this.COCalibration == null) {
            this.COCalibration = COCalibration;
        } else if (temperature != null) {
            this.CO_READY = true;
        }
        return this;
    }

    public BLESensorDataBuilder setNO2Raw(int NO2Raw) {
        if (this.NO2Raw == null) {
            this.NO2Raw = NO2Raw;
        } else if (temperature != null) {
            this.NO2_READY = true;
        }
        return this;
    }

    public BLESensorDataBuilder setNO2Calibration(int NO2Calibration) {
        if (this.NO2Calibration == null) {
            this.NO2Calibration = NO2Calibration;
        } else if (temperature != null) {
            this.NO2_READY = true;
        }
        return this;
    }

    public BLESensorDataBuilder setNH3Raw(int NH3Raw) {
        if (this.NH3Raw == null) {
            this.NH3Raw = NH3Raw;
        } else if (temperature != null) {
            this.NH3_READY = true;
        }
        return this;
    }

    public BLESensorDataBuilder setNH3Calibration(int NH3Calibration) {
        if (this.NH3Calibration == null) {
            this.NH3Calibration = NH3Calibration;
        } else if (temperature != null) {
            this.NH3_READY = true;
        }
        return this;
    }

    public BLESensorDataBuilder setTemperature(double temperature) {
        this.temperature = temperature;

        return this;
    }

    public BLESensorDataBuilder setDustRaw(int dustRaw) {
        if (this.dustValues.size() < 16) {
            this.dustValues.add(dustRaw);
        } else {
            this.DUST_READY = true;
        }

        return this;
    }

    public double buildDustValue() {
        double result = BluetoothBroadcastReceiver.INVALID_VALUE;

        if (this.dustValues.size() >= 15) {
            int sum = 0;
            for (Integer dustValue : this.dustValues) {
                sum += dustValue;
            }
            result = 2.7 * (sum / dustValues.size()) - 220;

            dustValues = new ArrayList<>();

            this.DUST_READY = false;
        }

        return result;
    }

    public double buildCoValue() {
        int result = BluetoothBroadcastReceiver.INVALID_VALUE;

        if (CORaw != null && COCalibration != null && temperature != null) {
            result = calculateRealValue(CORaw, COCalibration, temperature, 350, -1.179, 4.385);
            CORaw = null;
            COCalibration = null;
            CO_READY = false;
        }

        return result;
    }

    public double buildNo2Value() {
        int result = BluetoothBroadcastReceiver.INVALID_VALUE;

        if (NO2Raw != null && NO2Calibration != null && temperature != null) {
            result = calculateRealValue(NO2Raw, NO2Calibration, temperature, 1, 1.007, 0.145);
            NO2Raw = null;
            NO2Calibration = null;
            NO2_READY = false;
        }

        return result;
    }

    public double buildNh3Value() {
        int result = BluetoothBroadcastReceiver.INVALID_VALUE;

        if (NH3Raw != null && NH3Calibration != null && temperature != null) {
            result = calculateRealValue(NH3Raw, NH3Calibration, temperature, 40, -1.67, 0.68);
            NH3Raw = null;
            NH3Calibration = null;
            NH3_READY = false;
        }

        return result;
    }

    private int calculateRealValue(Integer raw, Integer calibration, Double temperature, int t, double a, double b) {
        double tmp = raw + (temperature - 25) * t;
        return (int) (Math.pow(tmp / calibration, a) * b);
    }
}
