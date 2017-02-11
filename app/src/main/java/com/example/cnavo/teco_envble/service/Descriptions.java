package com.example.cnavo.teco_envble.service;

/**
 * Created by cnavo on 11.02.2017.
 */

public enum Descriptions {
    CO("co"),
    NO2("no2"),
    NH3("nh3"),
    TEMPERATURE("temperature"),
    HUMIDITY("humidity"),
    PRESSURE("pressure"),
    DUST("dust");

    private String description;

    Descriptions(String description) {
        this.description = description;
    }

    public static boolean contains(String descriptionString) {
        for (Descriptions description : Descriptions.values()) {
            if (description.toString().equals(descriptionString)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return this.description;
    }
}