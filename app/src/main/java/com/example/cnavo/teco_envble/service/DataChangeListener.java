package com.example.cnavo.teco_envble.service;

import com.example.cnavo.teco_envble.data.CardData;
import com.jjoe64.graphview.series.DataPoint;

/**
 * Created by cnavo on 11.02.2017.
 */

public interface DataChangeListener {
    void onValueAdded(String description, DataPoint dataPoint);
    void onItemChanged(CardData cardData);
}
