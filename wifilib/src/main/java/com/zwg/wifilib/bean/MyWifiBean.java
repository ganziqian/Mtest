package com.zwg.wifilib.bean;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */
public class MyWifiBean {
    private boolean isContent;
    private ScanResult scanResult;
    private boolean isSave;
    private String state;
    private boolean isChange;
    private int stattag;
    private int leve;

    public int getLeve() {
        return leve;
    }

    public void setLeve(int leve) {
        this.leve = leve;
    }

    public int getStattag() {
        return stattag;
    }

    public void setStattag(int stattag) {
        this.stattag = stattag;
    }

    public boolean isChange() {
        return isChange;
    }

    public void setChange(boolean change) {
        isChange = change;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    private WifiConfiguration wifiConfiguration;

    public WifiConfiguration getWifiConfiguration() {
        return wifiConfiguration;
    }

    public void setWifiConfiguration(WifiConfiguration wifiConfiguration) {
        this.wifiConfiguration = wifiConfiguration;
    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        isSave = save;
    }

    public boolean isContent() {
        return isContent;
    }

    public void setContent(boolean content) {
        isContent = content;
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }
}
