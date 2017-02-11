package com.example.cnavo.teco_envble.ui;

import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cnavo.teco_envble.R;
import com.example.cnavo.teco_envble.data.CardData;
import com.example.cnavo.teco_envble.service.DBHelper;
import com.example.cnavo.teco_envble.service.DataChangeListener;
import com.example.cnavo.teco_envble.service.DataHelper;
import com.example.cnavo.teco_envble.service.Descriptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cnavo on 08.02.2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> implements DataChangeListener {

    List<CardData> items;

    private CardAdapter() {
        if (items == null) {
            items = new ArrayList<CardData>();
        }

        items.add(new CardData(Descriptions.CO.toString(), ""));
        items.add(new CardData(Descriptions.NO2.toString(), ""));
        items.add(new CardData(Descriptions.NH3.toString(), ""));
        items.add(new CardData(Descriptions.TEMPERATURE.toString(), ""));
        items.add(new CardData(Descriptions.HUMIDITY.toString(), ""));
        items.add(new CardData(Descriptions.PRESSURE.toString(), ""));
        items.add(new CardData(Descriptions.DUST.toString(), ""));

        DataHelper.getDataHelper().setLocalDataChangeListener(this);
    }

    public static CardAdapter create() {
        return new CardAdapter();
    }

    @Override
    public CardAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.graph_card, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        CardData cardData = items.get(position);

        holder.caption.setText(cardData.getDescription());
        holder.status.setText(cardData.getStatus());
        holder.cardData = cardData;
        holder.graph.removeAllSeries();
        holder.graph.addSeries(cardData.getSeries());
        holder.adjustGraph();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onValueAdded(String description, DataPoint dataPoint) {

    }

    @Override
    public void onItemChanged(CardData cardData) {
        int itemPosition = getPosition(cardData.getDescription());
        if (itemPosition > 0) {
            items.remove(itemPosition);
            items.add(itemPosition, cardData);
            notifyItemChanged(itemPosition);
        }
    }

    private int getPosition(String description) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getDescription().equals(description)) {
                return i;
            }
        }
        return -1;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        private TextView caption;
        private GraphView graph;
        private TextView status;
        private CardData cardData;

        public CardViewHolder(View view) {
            super(view);

            this.caption = (TextView) view.findViewById(R.id.graph_card_caption);
            this.graph = (GraphView) view.findViewById(R.id.graph_card_graph);
            this.status = (TextView) view.findViewById(R.id.graph_card_status);

            Viewport viewport = this.graph.getViewport();
            viewport.setXAxisBoundsManual(true);
            viewport.setYAxisBoundsManual(true);
            viewport.setMinX(0);
            viewport.setMaxX(50);
            viewport.setMinY(0);
            viewport.setMaxY(50);
        }

        public void adjustGraph() {
            Viewport viewport = this.graph.getViewport();

            double minY = cardData.getMinY();
            double maxY = cardData.getMaxY();
            int maxX = cardData.getMaxX();

            if (viewport.getMaxY(false) < maxY) {
                viewport.setMaxY(maxY);
            }
            if (viewport.getMinY(false) > minY) {
                viewport.setMinY(minY);
            }
            if (viewport.getMaxX(true) < maxX) {
                viewport.setMinX(maxX + 5);
                viewport.setMinX(viewport.getMaxX(true) - 50);
            }


        }
    }
}
