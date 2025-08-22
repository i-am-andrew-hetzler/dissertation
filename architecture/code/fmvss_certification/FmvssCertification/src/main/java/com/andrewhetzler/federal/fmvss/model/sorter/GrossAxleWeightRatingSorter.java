package com.andrewhetzler.federal.fmvss.model.sorter;

import com.andrewhetzler.federal.fmvss.model.GrossAxleWeightRating;

import java.util.Comparator;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
public class GrossAxleWeightRatingSorter implements Comparator<GrossAxleWeightRating> {
    @Override
    public int compare(
            GrossAxleWeightRating first,
            GrossAxleWeightRating second
    ) {
        return Integer.compare(
                Integer.valueOf(first.getOrder()),
                Integer.valueOf(second.getOrder())
        );
    }
}
