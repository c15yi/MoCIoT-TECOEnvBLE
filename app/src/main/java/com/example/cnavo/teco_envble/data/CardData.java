package com.example.cnavo.teco_envble.data;

import com.example.cnavo.teco_envble.service.DataChangeListener;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cnavo on 08.02.2017.
 */

public class CardData {

    private List<DataChangeListener> dataChangeListeners;

    private final String description;
    private String status;
    private LineGraphSeries<DataPoint> series;
    private int minX = 0;
    private int maxX;

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    private double minY;
    private double maxY;

    public CardData(String description, String status) {
        if (series == null) {
            this.series = new LineGraphSeries<DataPoint>();
        }

        this.dataChangeListeners = new ArrayList<>();
        this.description = description;
        this.status = status;
    }

    public String getDescription() {
        return this.description;
    }

    public String getStatus() {
        return this.status;
    }

    public LineGraphSeries<DataPoint> getSeries() {
        return this.series;
    }

    public void setStatus(String status) {
        this.status = status;
        onItemChanged();
    }

    public void addDataPoint(DataPoint dataPoint, boolean save) {
        this.series.appendData(dataPoint, true, 100);
        if (dataPoint.getY() > maxY) {
            this.maxY = dataPoint.getY();
        } else if (dataPoint.getY() < minY) {
            this.minY = dataPoint.getY();
        }

        this.maxX++;

        onItemChanged();
        if (save) {
            onValueAdded(dataPoint);
        }
    }

    public void setDataChangeListener(DataChangeListener dataChangeListener) {
        this.dataChangeListeners.add(dataChangeListener);
    }

    private void onItemChanged() {
        if (this.dataChangeListeners != null) {
            for (DataChangeListener dataChangeListener : this.dataChangeListeners) {
                dataChangeListener.onItemChanged(this);
            }
        }
    }

    private void onValueAdded(DataPoint dataPoint) {
        if (this.dataChangeListeners != null) {
            for (DataChangeListener dataChangeListener : this.dataChangeListeners) {
                dataChangeListener.onValueAdded(description, dataPoint);
            }
        }
    }


}
