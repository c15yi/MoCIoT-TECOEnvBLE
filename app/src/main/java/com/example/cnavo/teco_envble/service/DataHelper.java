package com.example.cnavo.teco_envble.service;

import android.content.Context;
import android.util.Log;

import com.example.cnavo.teco_envble.data.CardData;
import com.jjoe64.graphview.series.DataPoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cnavo on 10.02.2017.
 */

public class DataHelper {

    public static Map<String, CardData> getCardDates() {
        return cardDates;
    }

    private static Map<String, CardData> cardDates;
    private static DataHelper dataHelper;

    private DataHelper() {
        cardDates = new HashMap<String, CardData>();

        for (Descriptions descriptions : Descriptions.values()) {
            CardData cardData = new CardData(descriptions.toString());
            cardDates.put(descriptions.toString(), cardData);
        }
    }

    public static DataHelper getDataHelper() {
        if (dataHelper != null) {
            return dataHelper;
        } else {
            dataHelper = new DataHelper();
            return dataHelper;
        }
    }

    public CardData getCardData(String description) {
        if (Descriptions.contains(description) && cardDates.containsKey(description)) {
            return cardDates.get(description);
        }
        return null;
    }

    public void addValue(String description, double value, boolean save) {
        CardData tmp;

        if (Descriptions.contains(description) && cardDates.containsKey(description)) {
            tmp = cardDates.get(description);
            tmp.addDataPoint(new DataPoint(tmp.getMaxX() + 1, value), save);
            cardDates.put(description, tmp);

            Log.d("Value added", "Value added: " + description + " " + value);
        }
    }

    public void setLocalDataChangeListener(DataChangeListener dataChangeListener) {
        for (Descriptions descriptions : Descriptions.values()) {
            CardData cardData = new CardData(descriptions.toString());
            cardData.setDataChangeListener(dataChangeListener);
            cardDates.put(descriptions.toString(), cardData);
        }
    }
}
