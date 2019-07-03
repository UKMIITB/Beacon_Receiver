package org.altbeacon.beaconreference;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.altbeacon.beacon.startup.BootstrapNotifier;

public class BeaconReferenceApplication extends Application implements BootstrapNotifier {
    private static final String TAG = "TAG";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;

    public void onCreate() {
        Log.e("Beacon","Oncreate");
        super.onCreate();

        BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().clear();
        //adding layout for IBEACON
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        // adding layout for ALTBEACON
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        //adding layout for EDDYSTONE  TLM
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
        //adding layout for EDDYSTONE UID
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        //adding layout for EDDYSTONE URL
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));

        beaconManager.setDebug(true);

        Log.e(TAG, "setting up background monitoring for beacons and power saving");
        // wake up the app when a beacon is seen
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
        backgroundPowerSaver = new BackgroundPowerSaver(this);

        Intent intent = new Intent(this, RangingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    @Override
    public void didEnterRegion(Region arg0) {
        Log.e(TAG, "did enter region.");
    }

    @Override
    public void didExitRegion(Region region) {
        Log.e("TAG", "I no longer see a beacon.");
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        Log.e("TAG", "Current region state is: " + (state == 1 ? "INSIDE" : "OUTSIDE ("+state+")"));
    }
}
