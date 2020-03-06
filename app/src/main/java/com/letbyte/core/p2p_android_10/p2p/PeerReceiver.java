package com.letbyte.core.p2p_android_10.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION;

//For GO side it trigger while any connection state or device connection/disconnection happens.
//Trigger upon available device list change. Provide device list. Client result.


/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-03-18 at 3:03 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-03-18 at 3:03 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-03-18 at 3:03 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class PeerReceiver extends BroadcastReceiver {

    private P2PStateListener mP2PStateListener;
    /**
     * Represent last connectivity state
     */
    private volatile boolean mIsConnected;

    public PeerReceiver(P2PStateListener p2PStateListener) {

        this.mP2PStateListener = p2PStateListener;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
//        Timber.d("Received intent: %s", action);

        if (mP2PStateListener == null)
            return;


        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            mP2PStateListener.onP2PStateChange(state);

        } else if (WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            WifiP2pDeviceList wifiP2pDeviceList = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
//            Timber.d(wifiP2pDeviceList.toString());

            mP2PStateListener.onP2PPeersStateChange();

        } else if (WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            //WifiP2pDevice device = intent.getParcelableExtra(EXTRA_WIFI_P2P_DEVICE);
            //addText("Local device: " + MyP2PHelper.deviceToString(device));
        } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED);
            String persTatu = "Discovery state changed to ";

            if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED) {
                persTatu = persTatu + "Stopped.";
                mP2PStateListener.onP2PPeersDiscoveryStopped();

            } else if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {

                mP2PStateListener.onP2PPeersDiscoveryStarted();

            } else {
                persTatu = persTatu + "unknown  " + state;
            }

        } else if (WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            WifiP2pInfo wifiP2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
            WifiP2pGroup wifiP2pGroup = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);

            mP2PStateListener.onP2PConnectionChanged(wifiP2pGroup == null ? null :
                    wifiP2pGroup.getClientList());//As from Android 10 the Broadcast is not sticky

            if (networkInfo != null && networkInfo.isConnected()) {

                if(!mIsConnected) {
                    mP2PStateListener.onP2PConnectionChanged();
                }
                mIsConnected = true;

            } else {

                mIsConnected = false;
                mP2PStateListener.onP2PDisconnected();

            }
        }
    }
}
