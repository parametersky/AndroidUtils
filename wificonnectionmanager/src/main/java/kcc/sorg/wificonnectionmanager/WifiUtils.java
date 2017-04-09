package kcc.sorg.wificonnectionmanager;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by ford-pro2 on 15/11/10.
 */
public class WifiUtils {
    private Context mContext = null;
    private WifiManager mWifiManager = null;
    public WifiUtils(Context context){
        mContext = context;
        mWifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean isWifiEnabled(){
        return mWifiManager.isWifiEnabled();
    }

    public boolean enableWifi(int networkid){
        if(isWifiEnabled()){
            mWifiManager.enableNetwork(networkid,true);
            return true;
        } else {
            mWifiManager.setWifiEnabled(true);
            mWifiManager.startScan();

            mWifiManager.enableNetwork(networkid,true);
            return true;
        }
    }


}
