package com.example.cnavo.teco_envble;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cnavo.teco_envble.data.CardData;
import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cnavo on 08.02.2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    List<CardData> items;

    private CardAdapter() {
        if (items == null) {
            items = new ArrayList<CardData>();
        }
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

        holder.caption.setText(cardData.getCaption());
        holder.graph.addSeries(cardData.getSeries());
        holder.status.setText(cardData.getStatus());
        holder.cardData = cardData;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(CardData cardData) {
        if (cardData != null) {
            items.add(cardData);
            notifyItemInserted(items.size() - 1);
        }
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
        }

    }
}
