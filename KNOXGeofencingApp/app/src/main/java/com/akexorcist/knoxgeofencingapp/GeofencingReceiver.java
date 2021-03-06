package com.akexorcist.knoxgeofencingapp;

import android.app.enterprise.geofencing.Geofence;
import android.app.enterprise.geofencing.Geofencing;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.akexorcist.knoxgeofencingapp.bus.BusProvider;
import com.akexorcist.knoxgeofencingapp.bus.event.InsideGeofenceEvent;
import com.akexorcist.knoxgeofencingapp.bus.event.LocationUnavailableEvent;
import com.akexorcist.knoxgeofencingapp.bus.event.OutsideGeofenceEvent;

/**
 * Created by Akexorcist on 5/10/16 AD.
 */

public class GeofencingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equalsIgnoreCase(Geofencing.ACTION_DEVICE_INSIDE_GEOFENCE)) {
            int[] geofenceIdList = intent.getIntArrayExtra(Geofencing.INTENT_EXTRA_ID);
            BusProvider.getProvider().post(new InsideGeofenceEvent(geofenceIdList));
        } else if (action.equalsIgnoreCase(Geofencing.ACTION_DEVICE_OUTSIDE_GEOFENCE)) {
            int[] geofenceIdList = intent.getIntArrayExtra(Geofencing.INTENT_EXTRA_ID);
            BusProvider.getProvider().post(new OutsideGeofenceEvent(geofenceIdList));
        } else if (action.equalsIgnoreCase(Geofencing.ACTION_DEVICE_LOCATION_UNAVAILABLE)) {
            BusProvider.getProvider().post(new LocationUnavailableEvent());
        }
    }

    private Geofence getGeofenceById(Context context, int id) {
        Geofencing geofencingService = Geofencing.getInstance(context);
        if (geofencingService.getGeofences() != null) {
            for (Geofence geofence : geofencingService.getGeofences()) {
                if (geofence.id == id) {
                    return geofence;
                }
            }
        }
        return null;
    }
}



