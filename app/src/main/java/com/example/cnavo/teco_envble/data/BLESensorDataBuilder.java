package com.example.cnavo.teco_envble.data;

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

    private int COValue;
    private int NO2Value;
    private int NH3Value;

    private Integer temperature;
    private Integer humidity;
    private Integer pressure;

    private Integer dustRaw;

    public BLESensorDataBuilder setCORaw(int CORaw) {
        this.CORaw = CORaw;
        return this;
    }

    public BLESensorDataBuilder setCOCalibration(int COCalibration) {
        this.COCalibration = COCalibration;
        return this;
    }

    public BLESensorDataBuilder setNO2Raw(int NO2Raw) {
        this.NO2Raw = NO2Raw;
        return this;
    }

    public BLESensorDataBuilder setNO2Calibration(int NO2Calibration) {
        this.NO2Calibration = NO2Calibration;
        return this;
    }

    public BLESensorDataBuilder setNH3Raw(int NH3Raw) {
        this.NH3Raw = NH3Raw;
        return this;
    }

    public BLESensorDataBuilder setNH3Calibration(int NH3Calibration) {
        this.NH3Calibration = NH3Calibration;
        return this;
    }

    public BLESensorDataBuilder setTemperature(int temperature) {
        this.temperature = temperature;
        return this;
    }

    public BLESensorDataBuilder setHumidity(int humidity) {
        this.humidity = humidity;
        return this;
    }

    public BLESensorDataBuilder setPressure(int pressure) {
        this.pressure = pressure;
        return this;
    }

    public BLESensorDataBuilder setDustRaw(int dustRaw) {
        this.dustRaw = dustRaw;
        return this;
    }

    public int getMissingValuesCount() {
        int counter = 0;
        if (CORaw != null) {
            counter++;
        }
        if (COCalibration != null) {
            counter++;
        }
        if (NO2Raw != null) {
            counter++;
        }
        if (NO2Calibration != null) {
            counter++;
        }
        if (NH3Raw != null) {
            counter++;
        }
        if (NH3Calibration != null) {
            counter++;
        }
        if (temperature != null) {
            counter++;
        }
        if (humidity != null) {
            counter++;
        }
        if (pressure != null) {
            counter++;
        }
        if (dustRaw != null) {
            counter++;
        }

        return counter;
    }

    public BLESensorData build() {
        if (CORaw != null && COCalibration != null
                && NO2Raw != null && NO2Calibration != null
                && NH3Raw != null && NH3Calibration != null
                && temperature != null
                && humidity != null
                && pressure != null
                && dustRaw != null) {
            this.COValue = calculateRealValue(CORaw, COCalibration, temperature, 350, -1.179, 4.385);
            this.NO2Value = calculateRealValue(NO2Raw, NO2Calibration, temperature, 1, 1.007, 0.145);
            this.NH3Value = calculateRealValue(NH3Raw, NH3Calibration, temperature, 40, -1.67, 0.68);

            return new BLESensorData(COValue, NO2Value, NH3Value, temperature, humidity, pressure, dustRaw);
        }

        return null;
    }

    private int calculateRealValue(Integer raw, Integer calibration, Integer temperature, int t, double a, double b) {
        double tmp = raw + (temperature - 25) * t;
        return (int) (Math.pow(tmp / calibration, a) * b);
    }
}
