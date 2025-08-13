package com.andrewhetzler.federal.fmvss.model.sorter;

import com.andrewhetzler.federal.fmvss.model.IntermediateAxleWeightRating;

import java.util.Comparator;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
public class IntermediateAxleWeightRatingSorter implements Comparator<IntermediateAxleWeightRating> {
    @Override
    public int compare(
            IntermediateAxleWeightRating first,
            IntermediateAxleWeightRating second
    ) {
        return Integer.compare(
                Integer.valueOf(first.getOrder()),
                Integer.valueOf(second.getOrder())
        );
    }
}
