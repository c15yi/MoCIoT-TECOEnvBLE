package com.example.cnavo.teco_envble.data;

/**
 * Created by privat on 09.02.17.
 */

public class BLESensorData {

    private int COValue;
    private int NO2Value;
    private int NH3Value;

    private int temperature;
    private int humidity;
    private int pressure;

    private int dustRaw;

    public BLESensorData(int COValue, int NO2Value, int NH3Value, int temperature, int humidity, int pressure, int dustRaw) {
        this.COValue = COValue;
        this.NO2Value = NO2Value;
        this.NH3Value = NH3Value;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.dustRaw = dustRaw;
    }

    public int getCOValue() {
        return COValue;
    }

    public int getNO2Value() {
        return NO2Value;
    }

    public int getNH3Value() {
        return NH3Value;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public int getDustRaw() {
        return dustRaw;
    }

}
