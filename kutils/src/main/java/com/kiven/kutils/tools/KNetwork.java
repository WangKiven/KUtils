package com.kiven.kutils.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import com.kiven.kutils.logHelper.KLog;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by kiven on 16/2/23.
 */
public class KNetwork {
    public static final int NETTYPE_NONE = 0x00;
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;
    public static final int NETTYPE_OTHER = 0x04;

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络 4:其他网络（如：VPN，蓝牙，WLAN等）
     */
    public static int getNetworkType(Context context) {
        int netType = NETTYPE_NONE;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return netType;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (networkCapabilities == null) {
                return netType;
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                netType = NETTYPE_CMNET;
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                netType = NETTYPE_WIFI;
            } else {
                // 能拿到 networkCapabilities，说明是有网络的
                netType = NETTYPE_OTHER;
            }
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
                return netType;
            }
            int nType = networkInfo.getType();
            if (nType == ConnectivityManager.TYPE_MOBILE) {
                String extraInfo = networkInfo.getExtraInfo();
                if (KString.isBlank(extraInfo)) {
                    if (extraInfo.toLowerCase().equals("cmnet")) {
                        netType = NETTYPE_CMNET;
                    } else {
                        netType = NETTYPE_CMWAP;
                    }
                } else {
                    netType = NETTYPE_CMNET;
                }
            } else if (nType == ConnectivityManager.TYPE_WIFI) {
                netType = NETTYPE_WIFI;
            } else {
                // 能拿到networkInfo，说明是有网络的
                netType = NETTYPE_OTHER;
            }
        }
        return netType;
    }

    /**
     * 获取IP
     */
    public static String getIPAddress() {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    //这里需要注意：这里增加了一个限定条件( inetAddress instanceof Inet4Address ),主要是在Android4.0高版本中可能优先得到的是IPv6的地址
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            KLog.e(ex);
        }
        return null;
    }


    /**
     * 检测网络是否可用.
     */
    public static boolean isHaveNetwork(Context context) {
        boolean flag;
        if (null == context) {
            flag = false;
        } else {
            try {
                ConnectivityManager connectivity = (ConnectivityManager) context
                        .getApplicationContext().getSystemService(
                                Context.CONNECTIVITY_SERVICE);
                if (connectivity == null) {
                    flag = false;
                } else {
                    NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
                    flag = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnectedOrConnecting();
                }
            } catch (Exception e) {
                flag = false;
            }
        }
        return flag;
    }
}
