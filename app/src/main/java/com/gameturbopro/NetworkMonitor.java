package com.gameturbopro;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Handler;
import android.os.Looper;

import java.net.InetAddress;

public class NetworkMonitor {

    private final Context context;

    public NetworkMonitor(Context context) {
        this.context = context.getApplicationContext();
    }

    public String type() {

        ConnectivityManager manager =
                (ConnectivityManager)
                        context.getSystemService(
                                Context.CONNECTIVITY_SERVICE
                        );

        if (manager == null) {
            return "Not Available";
        }

        Network network =
                manager.getActiveNetwork();

        if (network == null) {
            return "Offline";
        }

        NetworkCapabilities capabilities =
                manager.getNetworkCapabilities(network);

        if (capabilities == null) {
            return "Offline";
        }

        if (capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI)) {

            return "Wi-Fi";
        }

        if (capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR)) {

            return "Mobile Data";
        }

        if (capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_ETHERNET)) {

            return "Ethernet";
        }

        return "Connected";
    }

    public boolean isConnected() {

        return !type().equals("Offline")
                && !type().equals("Not Available");
    }

    public void ping(Callback callback) {

        new Thread(() -> {

            long start =
                    System.nanoTime();

            String result;

            try {

                InetAddress address =
                        InetAddress.getByName(
                                "1.1.1.1"
                        );

                boolean reachable =
                        address.isReachable(2500);

                if (!reachable) {
                    result = "Unavailable";
                } else {

                    long elapsed =
                            (System.nanoTime()
                                    - start)
                                    / 1_000_000L;

                    result = elapsed + " ms";
                }

            } catch (Exception e) {

                result = "Unavailable";
            }

            String finalResult = result;

            new Handler(
                    Looper.getMainLooper()
            ).post(() -> {

                if (callback != null) {
                    callback.done(finalResult);
                }

            });

        }).start();
    }

    public interface Callback {
        void done(String value);
    }
}
