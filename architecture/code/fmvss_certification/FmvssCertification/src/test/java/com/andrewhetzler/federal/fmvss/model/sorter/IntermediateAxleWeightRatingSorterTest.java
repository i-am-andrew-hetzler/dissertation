package com.andrewhetzler.federal.fmvss.model.sorter;

import com.andrewhetzler.federal.fmvss.model.IntermediateAxleWeightRating;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Author:       Andrew Hetzler
 * Date Created: 8/11/25
 **/
class IntermediateAxleWeightRatingSorterTest {
    private final IntermediateAxleWeightRatingSorter subject = new IntermediateAxleWeightRatingSorter();

    @Test
    void shouldDisplayInCorrectOrder() {
        final List<IntermediateAxleWeightRating> result = new ArrayList<>();

        result.add(new IntermediateAxleWeightRating(
                "2",
                "200"
        ));
        result.add(new IntermediateAxleWeightRating(
                "1",
                "100"
        ));

        result.sort(subject);

        assertEquals(
                List.of(
                        new IntermediateAxleWeightRating(
                                "1",
                                "100"
                        ),
                        new IntermediateAxleWeightRating(
                                "2",
                                "200"
                        )
                ),
                result
        );
    }

}
