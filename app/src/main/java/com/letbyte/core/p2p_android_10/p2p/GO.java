package com.letbyte.core.p2p_android_10.p2p;

import android.content.Context;
import android.content.IntentFilter;
import android.net.MacAddress;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.Build;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static android.net.wifi.p2p.WifiP2pConfig.GROUP_OWNER_BAND_2GHZ;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;

/**
 * ============================================================================
 * Copyright (C) 2020 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2020-03-06 at 12:57 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: P2P_Android_10.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2020-03-06 at 12:57 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2020-03-06 at 12:57 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class GO implements WifiP2pManager.ChannelListener, WifiP2pManager.ConnectionInfoListener {

    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;
    private PeerReceiver mPeerReceiver;

    public GO(Context context) {
        mContext = context;

        mWifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        if (mWifiP2pManager != null) {
            mChannel = mWifiP2pManager.initialize(mContext, mContext.getMainLooper(), this);

            mPeerReceiver = new PeerReceiver(mP2PStateListener);
            IntentFilter filter = new IntentFilter();
            filter.addAction(WIFI_P2P_STATE_CHANGED_ACTION);
            filter.addAction(WIFI_P2P_CONNECTION_CHANGED_ACTION);
            filter.addAction(WIFI_P2P_PEERS_CHANGED_ACTION);
            mContext.registerReceiver(mPeerReceiver, filter);
        }

    }

    private WifiP2pManager.GroupInfoListener mGroupInfoListenerToClearIfExist = new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            {
                if (group != null) {

                    mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {

                            createGO();
                        }

                        @Override
                        public void onFailure(int reason) {
                        }
                    });
                } else {
                    createGO();
                }
            }
        }
    };
    private void createGO() {
        WifiP2pManager.ActionListener actionListener =new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Group creation success");            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Group creation failed:"+reason);
            }
        };
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {

            MacAddress macAddress = MacAddress.fromString("0:a:d:3:9:a");

            WifiP2pConfig.Builder builder = new WifiP2pConfig.Builder();
            builder.setGroupOperatingBand(GROUP_OWNER_BAND_2GHZ);
            builder.setNetworkName("DIRECT-xy");
            builder.setPassphrase("123456789");
            builder.enablePersistentMode(true);
            builder.setDeviceAddress(macAddress);
            WifiP2pConfig wifiP2pConfig = builder.build();

            mWifiP2pManager.createGroup(mChannel, wifiP2pConfig, actionListener);
        } else {
            mWifiP2pManager.createGroup(mChannel, actionListener);
        }
    }

    public void start() {
        mWifiP2pManager.requestGroupInfo(mChannel, mGroupInfoListenerToClearIfExist);
    }


    private P2PStateListener mP2PStateListener = new P2PStateListener() {
        @Override
        public void onP2PStateChange(int state) {
        }

        @Override
        public void onP2PPeersStateChange() {
        }

        @Override
        public void onP2PConnectionChanged() {
            mWifiP2pManager.requestConnectionInfo(mChannel, GO.this);
        }

        @Override
        public void onP2PConnectionChanged(Collection<WifiP2pDevice> wifiP2pDevices) {

        }

        @Override
        public void onP2PDisconnected() {

        }

        @Override
        public void onP2PPeersDiscoveryStarted() {

        }

        @Override
        public void onP2PPeersDiscoveryStopped() {

        }
    };

    @Override
    public void onChannelDisconnected() {

    }

    private WifiP2pManager.GroupInfoListener mGroupInfoListener = new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            Log.d(TAG, "Group info:"+(group == null ? "null" : group.getNetworkName()));
            startServiceBroadcasting();
        }
    };

    private void startServiceBroadcasting() {

        Map<String, String> record = new HashMap<>();
        record.put("available", "visible");

        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance("_test",
                "_presence._tcp", record);

        mWifiP2pManager.addLocalService(mChannel, service, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                Log.d(TAG, "Service added");
            }

            public void onFailure(int reason) {
                Log.d(TAG, "Service add failed:"+reason);
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

        if (info.isGroupOwner) {

            mWifiP2pManager.requestGroupInfo(mChannel, mGroupInfoListener);
        }
    }
}
