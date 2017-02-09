package com.example.cnavo.teco_envble;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
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
    ListButtonClickListener listButtonClickListener;

    private ConnectionListAdapter(ListButtonClickListener listButtonClickListener) {
        if (items == null) {
            items = new ArrayList<BluetoothDevice>();
        }

        this.listButtonClickListener = listButtonClickListener;
    }

    public static ConnectionListAdapter create(ListButtonClickListener listButtonClickListener) {
        return new ConnectionListAdapter(listButtonClickListener);
    }

    @Override
    public ConnectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.connection_list_item, parent, false);

        return new ConnectionViewHolder(view, listButtonClickListener);
    }

    @Override
    public void onBindViewHolder(ConnectionViewHolder holder, int position) {
        BluetoothDevice bluetoothDevice = items.get(position);

        holder.deviceName.setText(bluetoothDevice.getName());
        holder.bluetoothDevice = bluetoothDevice;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addDevice(BluetoothDevice device) {
        if (!items.contains(device)) {
            items.add(device);
            notifyItemInserted(items.size() - 1);
        }
    }

    public void clearDevices() {
        items = new ArrayList<BluetoothDevice>();
        notifyDataSetChanged();
    }

    public class ConnectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView deviceName;
        private Button connectButton;
        private Button disconnectButton;
        private BluetoothDevice bluetoothDevice;
        private ListButtonClickListener listButtonClickListener;

        public ConnectionViewHolder(View view, @NonNull ListButtonClickListener listButtonClickListener) {
            super(view);

            this.deviceName = (TextView) view.findViewById(R.id.connection_list_item_text_view);
            this.connectButton = (Button) view.findViewById(R.id.connection_list_item_connect_button);
            this.disconnectButton = (Button) view.findViewById(R.id.connection_list_item_disconnect_button);

            this.listButtonClickListener = listButtonClickListener;

            this.connectButton.setOnClickListener(this);
            this.disconnectButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == this.connectButton) {
                listButtonClickListener.onConnectButtonClicked(bluetoothDevice);
            } else if (view == this.disconnectButton) {
                listButtonClickListener.onDisconnectButtonClicked(bluetoothDevice);
            }
        }
    }
}
