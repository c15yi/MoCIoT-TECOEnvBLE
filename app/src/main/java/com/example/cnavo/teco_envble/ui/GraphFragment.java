package com.example.cnavo.teco_envble.ui;


import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.example.cnavo.teco_envble.R;
import com.example.cnavo.teco_envble.data.CardData;
import com.example.cnavo.teco_envble.service.DataChangeListener;
import com.example.cnavo.teco_envble.service.DataHelper;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

/**
 * Created by cnavo on 07.02.2017.
 */

@EFragment(R.layout.graph_fragment)
public class GraphFragment extends Fragment implements DataChangeListener {

    @ViewById(R.id.graph_graph)
    GraphView graphView;

    @FragmentArg
    String description;

    public static GraphFragment create(String description) {
        return GraphFragment_.builder().description(description).build();
    }

    @AfterViews
    void init() {
        DataHelper.getCardDates().get(description).setGraph(graphView);
    }

    void loadGraph() {
        if (graphView != null) {
            graphView.refreshDrawableState();
        }
    }

    @Override
    public void onValueAdded(String description, DataPoint dataPoint) {
        if (description.equals(this.description)) {

        }
    }

    @Override
    public void onItemChanged(CardData cardData) {

    }
}
