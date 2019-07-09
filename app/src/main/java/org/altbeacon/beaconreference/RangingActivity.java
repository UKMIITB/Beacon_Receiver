package org.altbeacon.beaconreference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

public class RangingActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    ArrayList<Data_send> datasend;
    MediaPlayer mediaPlayer;
    private MyListAdapter adapter;
    private RecyclerView recyclerView;
   // public final static String MODULE_MAC = "00:18:E4:40:00:06";
    public final static String MODULE_MAC = "00:13:EF:00:B2:1B";
    public final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    BluetoothAdapter bta;                 //bluetooth stuff
    BluetoothSocket mmSocket;             //bluetooth stuff
    BluetoothDevice mmDevice;             //bluetooth stuff
    private lightOn btt = null;
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("Ranging", "Just inside oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.kabir_singh_ringtone);
        datasend = new ArrayList<>();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                Log.e("BroadCast reciever","in recieved method");
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR);

                    Log.e("BroadCast reciever","Adapter state changed");
                    switch (bluetoothState) {
                        case BluetoothAdapter.STATE_OFF: {
                            Log.e("BroadCast reciever","Adapter state off");
                            BluetoothConnect();
                            break;
                        }

                    }
                }
                if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
                {
                    Log.e("BroadCast reciever","bond state changed");
                    final int ConnectionModuleState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,BluetoothDevice.ERROR);
                    final String RemoteDevice = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                    switch (ConnectionModuleState) {
                        case BluetoothDevice.BOND_NONE:
                            Log.e("BroadCast reciever","device not bonded");
                            BluetoothConnect();

                    }
                }
            }
        };
        BluetoothConnect();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.unbind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.bind(this);
        registerReceiver(broadcastReceiver,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(broadcastReceiver,new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.e("Ranging", "Inside OnBeaconServiceConnect");

        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.e("Ranging", "Beacon Size" + beacons.size());
                if (beacons.size() > 0) {
                    Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
                    Beacon firstBeacon = beacons.iterator().next();
                    Data_send data_send = new Data_send();
                    data_send.setVeh_num(firstBeacon.getId1().toString());
                    boolean flag = true;
                    long currenttime = System.currentTimeMillis();
                    for(int i=0;i<datasend.size();i++)
                    {
                        if(datasend.get(i).getVeh_num().equalsIgnoreCase(firstBeacon.getId1().toString())
                        && (currenttime-datasend.get(i).getTime()<=30000))
                            flag = false;
                    }


                    if (data_send.getStatus().equals("") && flag)
                    {
                        Log.e("Ranging", "Inside data_send.getstatus==null");
                       /* String address = firstBeacon.getBluetoothAddress();
                        Log.e("Ranging", "Address"+ address);*/
                        datasend.add(0, data_send);
                        adapter.notifyDataSetChanged();

                        new Handler().postDelayed(new Runnable() {
                            @Override

                            public void run() {
                                recyclerView.scrollToPosition(0);
                                String sendtxt = "RY";
                                btt.write(sendtxt.getBytes());

                            }
                        }, 200);
                        if (!mediaPlayer.isPlaying())
                            mediaPlayer.start();
                    }

                }
            }

        };
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {
            Log.e("Ranging", "Remote exception error");
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    private void  BluetoothConnect()
    {
        Log.e("Ranging", "Inside bluetoothconnect");
        bta = BluetoothAdapter.getDefaultAdapter();
        if (!bta.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        } else {
            Log.e("Ranging", "calling initiate bluetooth process");
            initiateBluetoothProcess();
        }
    }
    private void initiateBluetoothProcess() {

        Log.e("Ranging" ,"Inside initiatebluetoothprocess");
        if (bta.isEnabled()) {
            //attempt to connect to bluetooth module;

            Log.i("bluetooth enabled","entered into inititate");
            mmDevice = bta.getRemoteDevice(MODULE_MAC);
            //Log.i("device name",mmDevice.getName());
            //creating socket and connecting to it

            try {
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                Log.i("mmDevice ", "got the socket");
                mmSocket.connect();
                Log.i("[BLUETOOTH]", "Connected to: " + mmDevice.getName());




            } catch (IOException e) {
                Log.e("creating socket", "error in creating socket");
                Log.e("creating socket", e.getMessage());
                initiateBluetoothProcess();
            }

            Log.e("ranging","recycler view stuffs");
            btt = new lightOn(mmSocket);
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            adapter = new MyListAdapter(datasend,this, mmSocket,btt);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT)
            initiateBluetoothProcess();
    }
}

