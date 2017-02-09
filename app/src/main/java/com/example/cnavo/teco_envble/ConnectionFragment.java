package com.example.cnavo.teco_envble;


import android.support.v4.app.Fragment;

import org.androidannotations.annotations.EFragment;

/**
 * Created by cnavo on 07.02.2017.
 */

@EFragment(R.layout.connection_fragment)
public class ConnectionFragment extends Fragment {

    public static ConnectionFragment create() { return ConnectionFragment_.builder().build(); }
}
