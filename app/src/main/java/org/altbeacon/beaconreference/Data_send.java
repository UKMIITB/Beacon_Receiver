package org.altbeacon.beaconreference;

import java.util.Calendar;

public class Data_send {
    int minor;
    int major;
    String uuid1, firebaseId;
    String veh_num;
    String date;
    String status;
    long time;


    public Data_send()
    {
        date =java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        status="";
        time= System.currentTimeMillis();
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getDate() {
        return date;
    }

    public Data_send(int minor, int major, String uuid1, String veh_num, String date) {
        this.minor = minor;
        this.major = major;
        this.uuid1 = uuid1;
        this.veh_num = veh_num;
        this.date = date;
    }

    public int getMinor() {
        return minor;
    }

    public int getMajor() {
        return major;
    }

    public String getUuid1() {
        return uuid1;
    }

    public String getVeh_num() {
        return veh_num;
    }

    public String getStatus() {
        return  status;
    }


    public void setMinor(int minor) {
        this.minor = minor;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public void setUuid1(String uuid1) {
        this.uuid1 = uuid1;
    }

    public void setVeh_num(String veh_num) {
        this.veh_num = veh_num;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTime() {
        return time;
    }
}
