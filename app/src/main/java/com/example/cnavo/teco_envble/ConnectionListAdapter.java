package com.example.cnavo.teco_envble;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Christian on 09/02/17.
 */
public class ConnectionListAdapter extends RecyclerView.Adapter<ConnectionListAdapter.ConnectionViewHolder>{

    List<BluetoothDevice> items;

    private ConnectionListAdapter() {
        if (items == null) {
            items = new ArrayList<BluetoothDevice>();
        }
    }

    public static ConnectionListAdapter create() {
        return new ConnectionListAdapter();
    }

    @Override
    public ConnectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.connection_list_item, parent, false);

        return new ConnectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConnectionViewHolder holder, int position) {
        BluetoothDevice bluetoothDevice = items.get(position);

        holder.deviceName.setText(bluetoothDevice.getName());
        holder.bluetoothDevice = bluetoothDevice;
        holder.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
        holder.disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ConnectionViewHolder extends RecyclerView.ViewHolder {

        private TextView deviceName;
        private Button connectButton;
        private Button disconnectButton;
        private BluetoothDevice bluetoothDevice;

        public ConnectionViewHolder(View view) {
            super(view);

            this.deviceName = (TextView) view.findViewById(R.id.connection_list_item_text_view);
            this.connectButton = (Button) view.findViewById(R.id.connection_list_item_connect_button);
            this.disconnectButton = (Button) view.findViewById(R.id.connection_list_item_disconnect_button);
        }
    }
}
