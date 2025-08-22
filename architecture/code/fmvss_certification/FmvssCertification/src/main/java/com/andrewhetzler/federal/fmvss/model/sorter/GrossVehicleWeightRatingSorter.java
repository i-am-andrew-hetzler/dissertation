package com.andrewhetzler.federal.fmvss.model.sorter;

import com.andrewhetzler.federal.fmvss.model.GrossVehicleWeightRating;

import java.util.Comparator;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
public class GrossVehicleWeightRatingSorter implements Comparator<GrossVehicleWeightRating> {
    @Override
    public int compare(
            GrossVehicleWeightRating first,
            GrossVehicleWeightRating second
    ) {
        return Integer.compare(
                Integer.valueOf(first.getOrder()),
                Integer.valueOf(second.getOrder())
        );
    }
}
