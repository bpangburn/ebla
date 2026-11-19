package com.greatmindsworking.ebla;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * Small, headless regression checks for legacy utility classes. These do not
 * replace the research pipeline's manual video/database integration tests.
 */
class LegacySmokeTest {

    @Test
    void arrayStatisticsForKnownValues() {
        ArrayAnalysis result = new ArrayAnalysis(
                new ArrayList<Double>(Arrays.asList(26.0, 33.0, 16.0, 22.0, 18.0)));

        assertEquals(26.0, result.getInitialValue(), 0.001);
        assertEquals(16.0, result.getMinValue(), 0.001);
        assertEquals(33.0, result.getMaxValue(), 0.001);
        assertEquals(23.0, result.getAvgValue(), 0.001);
        assertEquals(6.78, result.getStdDeviation(), 0.001);
    }

    @Test
    void matchScoresSortAscending() {
        MatchScore lower = new MatchScore();
        lower.score = 0.2;
        MatchScore higher = new MatchScore();
        higher.score = 0.8;
        MatchScore equal = new MatchScore();
        equal.score = 0.2;

        assertTrue(lower.compareTo(higher) < 0);
        assertTrue(higher.compareTo(lower) > 0);
        assertEquals(0, lower.compareTo(equal));
    }
}
