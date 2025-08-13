package com.andrewhetzler.federal.fmvss.model.sorter;

import com.andrewhetzler.federal.fmvss.model.GrossVehicleWeightRating;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
class GrossVehicleWeightRatingSorterTest {
    private final GrossVehicleWeightRatingSorter subject = new GrossVehicleWeightRatingSorter();

    @Test
    void shouldDisplayInCorrectOrder() {
        final List<GrossVehicleWeightRating> result = new ArrayList<>();

        result.add(
                new GrossVehicleWeightRating(
                        "2",
                        "200"
                )
        );
        result.add(
                new GrossVehicleWeightRating(
                        "1",
                        "100"
                )
        );

        result.sort(subject);

        assertEquals(
                List.of(
                        new GrossVehicleWeightRating(
                                "1",
                                "100"
                        ),
                        new GrossVehicleWeightRating(
                                "2",
                                "200"
                        )
                ),
                result
        );
    }

}
