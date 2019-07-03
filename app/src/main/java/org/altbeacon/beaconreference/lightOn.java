package org.altbeacon.beaconreference;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

public class lightOn {
    private final BluetoothSocket mmSocket;
    private OutputStream mmOutStream=null;
    public lightOn(BluetoothSocket socket)

    {

        mmSocket=socket;

        try{

            mmOutStream = socket.getOutputStream();
            mmOutStream.flush();
        }catch (IOException e)
        {
            Log.e("[THREAD-CT]","Error:"+ e.getMessage());
            return;
        }

        Log.i("[THREAD-CT]","IO's obtained");
    }



    public void write(byte[] bytes){
        try{
            Log.i("[THREAD-CT]", "Writting bytes");
            Log.i("{THREAD-CT}",Thread.currentThread().getName());
            mmOutStream.write(bytes);
        }catch(IOException e){}
    }

}

