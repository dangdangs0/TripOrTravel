package com.example.trip.models.direction;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;

public interface DirectionFinderListener {
    void onDirectionFinderStart();

    void onDirectionFinderSuccess(List<Route> route);
}