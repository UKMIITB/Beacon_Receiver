package org.altbeacon.beaconreference;

import android.bluetooth.BluetoothSocket;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private ArrayList<Data_send> listdata;

    public Button allow;
    public Button deny;
    RangingActivity rangingActivity;
    public lightOn btt = null;
    public  BluetoothSocket mmSocket = null;

    public MyListAdapter(ArrayList<Data_send> listdata, RangingActivity rangingActivity,BluetoothSocket socket,lightOn btt) {
        this.listdata = listdata;
        this.rangingActivity = rangingActivity;
       mmSocket =socket;
       this.btt = btt;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
        allow = (Button) listItem.findViewById(R.id.allow);
        deny = (Button) listItem.findViewById(R.id.deny);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.e("ListAdapter", "Inside onbindviewholder");
        Data_send data_send = listdata.get(position);
        holder.veh_num.setText(data_send.getVeh_num());
        holder.date_time.setText(data_send.getDate());

        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listdata.get(position).status = "allow";
                listdata.remove(listdata.get(position));
                stopRingtone();
                notifyDataSetChanged();


                Log.i("[BLUETOOTH]", "Attempting to send data");
                if (mmSocket.isConnected() && btt != null) {

                    String sendtxt = "RY";
                    btt.write(sendtxt.getBytes());

                } else {
                    Toast.makeText(rangingActivity, "bluetooth not connected to module", Toast.LENGTH_LONG).show();
                }
            }
        });

        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listdata.get(position).status = "deny";
                listdata.remove(listdata.get(position));
                stopRingtone();
                notifyDataSetChanged();

                Log.i("[BLUETOOTH]", "Attempting to send data");
                if (mmSocket.isConnected() && btt != null) {

                    String sendtxt = "GY";
                    btt.write(sendtxt.getBytes());

                } else {
                    Toast.makeText(rangingActivity, "bluetooth not connected to module", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView veh_num;
        public TextView date_time;
        public Button allow;
        public Button deny;

        public ViewHolder(View itemView) {
            super(itemView);
            this.veh_num = (TextView) itemView.findViewById(R.id.veh_num);
            this.date_time = (TextView) itemView.findViewById(R.id.date_time);
            this.allow = (Button) itemView.findViewById(R.id.allow);
            this.deny = (Button) itemView.findViewById(R.id.deny);
        }
    }

    private void stopRingtone() {
        if (rangingActivity != null && rangingActivity.getMediaPlayer() != null && rangingActivity.getMediaPlayer().isPlaying()) {
            rangingActivity.getMediaPlayer().stop();
            try {
                rangingActivity.getMediaPlayer().prepare();
            } catch (IOException E) {
            }

        }
    }
}