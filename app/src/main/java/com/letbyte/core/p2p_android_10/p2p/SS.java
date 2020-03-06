package com.letbyte.core.p2p_android_10.p2p;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.letbyte.core.p2p_android_10.MainActivity;

import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;

/**
 * ============================================================================
 * Copyright (C) 2020 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2020-03-06 at 4:11 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: P2P_Android_10.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2020-03-06 at 4:11 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2020-03-06 at 4:11 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class SS implements WifiP2pManager.ChannelListener {

    private PeerReceiver mPeerReceiver;
    private Context mContext;
    private P2PStateListener mP2PStateListener;
    private final String mServiceType = MainActivity.SERVICE_TYPE;

    public SS(Context context) {
        mContext = context;

        mWifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);

        mChannel = mWifiP2pManager.initialize(mContext, mContext.getMainLooper(), this);

        mPeerReceiver = new PeerReceiver(mP2PStateListener);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        filter.addAction(WIFI_P2P_PEERS_CHANGED_ACTION);
        mContext.registerReceiver(mPeerReceiver, filter);
    }

    private final String TAG = getClass().getSimpleName();
    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager.DnsSdServiceResponseListener mDnsSdServiceResponseListener =
            new WifiP2pManager.DnsSdServiceResponseListener() {

                public void onDnsSdServiceAvailable(String instanceName, String serviceType, WifiP2pDevice device) {

                    if (serviceType.startsWith(mServiceType)) {

                        Log.d(TAG, "Service found:"+serviceType+":"+instanceName);

                    }
                    startPeerDiscovery();
                }
            };



    public boolean start() {
        if (mWifiP2pManager == null) {
            return false;
        }

        mWifiP2pManager.setDnsSdResponseListeners(mChannel, mDnsSdServiceResponseListener, null);

        mWifiP2pManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "ss stop success");
                startPeerDiscovery();
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "ss stop failed:"+reason);
            }
        });

        return true;
    }
    private synchronized void startPeerDiscovery() {
        mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                Log.i(TAG, " Started peer discovery");
            }

            public void onFailure(int reason) {
                Log.e(TAG, "Starting peer discovery failed, error code " + reason);
            }
        });
    }

    @Override
    public void onChannelDisconnected() {

    }
}
