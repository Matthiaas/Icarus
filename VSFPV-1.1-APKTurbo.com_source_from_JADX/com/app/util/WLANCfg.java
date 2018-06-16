package com.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build.VERSION;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class WLANCfg {
    private static WifiManager meWifiManager;
    private List<WifiConfiguration> meWifiConfigurations;
    private WifiInfo meWifiInfo = meWifiManager.getConnectionInfo();
    private List<ScanResult> meWifiList;
    WifiLock meWifiLock;

    public WLANCfg(Context context) {
        meWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    public void openWifi() {
        if (!meWifiManager.isWifiEnabled()) {
            meWifiManager.setWifiEnabled(true);
        }
    }

    public void closeWifi() {
        if (!meWifiManager.isWifiEnabled()) {
            meWifiManager.setWifiEnabled(false);
        }
    }

    public int checkState() {
        return meWifiManager.getWifiState();
    }

    public void acquireWifiLock() {
        this.meWifiLock.acquire();
    }

    public void releaseWifiLock() {
        if (this.meWifiLock.isHeld()) {
            this.meWifiLock.acquire();
        }
    }

    public void createWifiLock() {
        this.meWifiLock = meWifiManager.createWifiLock("test");
    }

    public List<WifiConfiguration> getConfiguration() {
        return this.meWifiConfigurations;
    }

    public void connetionConfiguration(int index) {
        if (index <= this.meWifiConfigurations.size()) {
            meWifiManager.enableNetwork(((WifiConfiguration) this.meWifiConfigurations.get(index)).networkId, true);
        }
    }

    public boolean isWifiConnected(Context context) {
        if (((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1).isConnected()) {
            return true;
        }
        return false;
    }

    public void startScan() {
        meWifiManager.startScan();
        this.meWifiList = meWifiManager.getScanResults();
        this.meWifiConfigurations = meWifiManager.getConfiguredNetworks();
    }

    public List<ScanResult> getWifiList() {
        return this.meWifiList;
    }

    public StringBuffer lookUpScan() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.meWifiList.size(); i++) {
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
            sb.append(((ScanResult) this.meWifiList.get(i)).toString()).append("\n~");
        }
        return sb;
    }

    public String getMacAddress() {
        return this.meWifiInfo == null ? "NULL" : this.meWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return this.meWifiInfo == null ? "NULL" : this.meWifiInfo.getBSSID();
    }

    public int getLinkSpeed() {
        return this.meWifiInfo == null ? 0 : this.meWifiInfo.getLinkSpeed();
    }

    public String whetherToRemoveTheDoubleQuotationMarks(String ssid) {
        if (VERSION.SDK_INT >= 17 && ssid.startsWith("\"") && ssid.endsWith("\"")) {
            return ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    public String getSSID() {
        return this.meWifiInfo == null ? "NULL" : whetherToRemoveTheDoubleQuotationMarks(this.meWifiInfo.getSSID());
    }

    public int getIpAddress() {
        return this.meWifiInfo == null ? 0 : this.meWifiInfo.getIpAddress();
    }

    public int getNetWordId() {
        return this.meWifiInfo == null ? 0 : this.meWifiInfo.getNetworkId();
    }

    public int getRssi() {
        return this.meWifiInfo == null ? 0 : this.meWifiInfo.getRssi();
    }

    public String getWifiInfo() {
        return this.meWifiInfo == null ? "NULL" : this.meWifiInfo.toString();
    }

    public boolean addNetWork(WifiConfiguration configuration) {
        return meWifiManager.enableNetwork(meWifiManager.addNetwork(configuration), true);
    }

    public void removeNetWork(int netId) {
        meWifiManager.removeNetwork(netId);
    }

    public void disConnectionWifi(int netId) {
        meWifiManager.disableNetwork(netId);
        meWifiManager.disconnect();
    }

    public void reconnect() {
        meWifiManager.reconnect();
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == 1) {
            config.allowedKeyManagement.set(0);
        }
        if (Type == 2) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(1);
            config.allowedGroupCiphers.set(3);
            config.allowedGroupCiphers.set(2);
            config.allowedGroupCiphers.set(0);
            config.allowedGroupCiphers.set(1);
            config.allowedKeyManagement.set(0);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(0);
            config.allowedGroupCiphers.set(2);
            config.allowedKeyManagement.set(1);
            config.allowedPairwiseCiphers.set(1);
            config.allowedGroupCiphers.set(3);
            config.allowedPairwiseCiphers.set(2);
            config.status = 2;
        }
        return config;
    }

    public static WifiConfiguration IsExsits(String SSID) {
        for (WifiConfiguration existingConfig : meWifiManager.getConfiguredNetworks()) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    public static void setIpAssignment(String assign, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        setEnumField(wifiConf, assign, "ipAssignment");
    }

    public static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties != null) {
            Object linkAddress = Class.forName("android.net.LinkAddress").getConstructor(new Class[]{InetAddress.class, Integer.TYPE}).newInstance(new Object[]{addr, Integer.valueOf(prefixLength)});
            ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties, "mLinkAddresses");
            mLinkAddresses.clear();
            mLinkAddresses.add(linkAddress);
        }
    }

    public static void setGateway(InetAddress gateway, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties != null) {
            Object routeInfo = Class.forName("android.net.RouteInfo").getConstructor(new Class[]{InetAddress.class}).newInstance(new Object[]{gateway});
            ArrayList mRoutes = (ArrayList) getDeclaredField(linkProperties, "mRoutes");
            mRoutes.clear();
            mRoutes.add(routeInfo);
        }
    }

    public static void setDNS(InetAddress dns, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties != null) {
            ArrayList<InetAddress> mDnses = (ArrayList) getDeclaredField(linkProperties, "mDnses");
            mDnses.clear();
            mDnses.add(dns);
        }
    }

    public static Object getField(Object obj, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        return obj.getClass().getField(name).get(obj);
    }

    public static Object getDeclaredField(Object obj, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(obj);
    }

    public static void setEnumField(Object obj, String value, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf(f.getType(), value));
    }

    public static void set_static(String SSID, String static_ip, String static_gateway, String static_dns) {
        WifiConfiguration tempConfig = IsExsits(SSID);
        if (tempConfig != null) {
            try {
                setIpAssignment("STATIC", tempConfig);
                setIpAddress(InetAddress.getByName(static_ip), 24, tempConfig);
                setGateway(InetAddress.getByName(static_gateway), tempConfig);
                setDNS(InetAddress.getByName(static_dns), tempConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
            meWifiManager.updateNetwork(tempConfig);
        }
    }
}
