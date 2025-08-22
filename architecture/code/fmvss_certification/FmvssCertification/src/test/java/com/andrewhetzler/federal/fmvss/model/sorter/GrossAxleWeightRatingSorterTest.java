package com.andrewhetzler.federal.fmvss.model.sorter;

import com.andrewhetzler.federal.fmvss.model.GrossAxleWeightRating;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
class GrossAxleWeightRatingSorterTest {
    private final GrossAxleWeightRatingSorter subject = new GrossAxleWeightRatingSorter();

    @Test
    void shouldDisplayInCorrectOrder() {
        final List<GrossAxleWeightRating> result = new ArrayList<>();

        result.add(new GrossAxleWeightRating(
                "FRONT-2",
                List.of(),
                "2",
                "REAR-2"
        ));
        result.add(
                new GrossAxleWeightRating(
                        "FRONT-1",
                        List.of(),
                        "1",
                        "REAR-1"
                )
        );

        result.sort(subject);

        assertEquals(
                List.of(
                        new GrossAxleWeightRating(
                                "FRONT-1",
                                List.of(),
                                "1",
                                "REAR-1"
                        ),
                        new GrossAxleWeightRating(
                                "FRONT-2",
                                List.of(),
                                "2",
                                "REAR-2"
                        )
                ),
                result
        );
    }
}
