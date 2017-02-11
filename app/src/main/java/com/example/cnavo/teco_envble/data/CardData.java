package com.example.cnavo.teco_envble.data;

import android.content.Context;

import com.example.cnavo.teco_envble.service.DataChangeListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
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
    private double currentValue;

    public LineGraphSeries<DataPoint> getSeries() {
        return series;
    }

    private LineGraphSeries<DataPoint> series;
    private GraphView graph;
    private int maxX;
    private double minY = Integer.MAX_VALUE;
    private double maxY = Integer.MIN_VALUE;

    public CardData(String description) {
        this.dataChangeListeners = new ArrayList<>();
        this.series = new LineGraphSeries<DataPoint>();
        this.series.setDrawDataPoints(true);
        this.series.setDataPointsRadius(10);
        this.description = description;

    }

    public void setGraph(GraphView graph) {
        this.graph = graph;
        this.graph.getViewport().setXAxisBoundsManual(true);
        this.graph.getViewport().setYAxisBoundsManual(true);
        this.graph.getViewport().setScrollable(true);
        this.graph.setPadding(16, 16, 16, 16);

        this.graph.addSeries(series);
    }

    public int getMaxX() {
        return maxX;
    }

    public String getDescription() {
        return this.description;
    }

    public GraphView getGraph() {
        GraphView graphView = this.graph;
        graphView.removeAllSeries();
        graphView.addSeries(series);
        return this.graph;
    }

    public void addDataPoint(DataPoint dataPoint, boolean save) {
        this.currentValue = dataPoint.getY();
        this.series.appendData(dataPoint, true, 110);
        if (dataPoint.getY() > maxY) {
            this.maxY = dataPoint.getY();
        } else if (dataPoint.getY() < minY) {
            this.minY = dataPoint.getY();
        }

        this.maxX++;

        if (this.graph != null) {
            Viewport viewport = this.graph.getViewport();
            viewport.setMinX(Math.max(0, maxX - 100));
            viewport.setMinY(minY - minY / 10);
            viewport.setMaxX(maxX);
            viewport.setMaxY(maxY + maxY / 10);
        }

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
