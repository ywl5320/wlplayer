package com.ywl5320.player.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ywl on 2016/1/30.
 */
public class NetUtil {

    public static final int NETWORN_NONE = 0;
    public static final int NETWORN_WIFI = 1;
    public static final int NETWORN_MOBILE = 2;

    /**
     * 获取网络状态
     *
     * @param context
     * @return
     */
    public static int getNetworkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        // Wifi
        NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
            return NETWORN_WIFI;
        }

        // 3G
        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState();
        if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
            return NETWORN_MOBILE;
        }
        return NETWORN_NONE;
    }

    /**
     * 获取当前网络状态
     *
     * @param context
     *            上下文
     * @return 网络连接返回true；未连接返回false
     */
    public static boolean getNetworkIsConnected(Context context) {
        // 获取网络连接管理器
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 如果管理器为null，返回false
        if (manager == null) {
            return false;
        }
        // 获取正在活动的网络信息
        NetworkInfo info = manager.getActiveNetworkInfo();
        // 如果网络信息为null，返回false
        if (info == null) {
            return false;
        }
        // 返回网络是否连接
        return info.isConnected();
    }
    /**
     * 网络类型
     *
     * @param context
     * @return true ：是，false ：否
     */
    public static boolean isWiFi(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context

                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info == null) {
            return false;
        } else {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else {
                return false;
            }
        }
    }
}
