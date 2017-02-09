package com.example.cnavo.teco_envble;


import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by cnavo on 07.02.2017.
 */

@EFragment(R.layout.graph_fragment)
public class GraphFragment extends Fragment {

    @ViewById(R.id.graph_recycler_view)
    RecyclerView recyclerView;

    private CardAdapter cardAdapter;

    public static GraphFragment create() {
        return GraphFragment_.builder().build();
    }

    @AfterViews
    void init() {
        if (cardAdapter == null) {
            this.cardAdapter = CardAdapter.create();
        }

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        this.recyclerView.setAdapter(cardAdapter);
    }

}
