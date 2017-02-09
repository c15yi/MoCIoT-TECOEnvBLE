package com.example.cnavo.teco_envble.data;

import android.support.annotation.Nullable;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by cnavo on 08.02.2017.
 */

public class CardData {

    private String caption;
    private String status;
    private DataPoint[] dataPoints;

    public CardData(String caption, String status, @Nullable DataPoint[] dataPoints) {
        if (dataPoints == null) {
            this.dataPoints = new DataPoint[0];
        } else {
            this.dataPoints = dataPoints;
        }

        this.caption = caption;
        this.status = status;
    }

    public String getCaption() {
        return this.caption;
    }

    public String getStatus() {
        return this.status;
    }

    public LineGraphSeries<DataPoint> getSeries() {
        return new LineGraphSeries<>(this.dataPoints);
    }

}
