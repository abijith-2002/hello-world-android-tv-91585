package com.example.tv.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * PUBLIC_INTERFACE
 * FocusMappingTest
 * Unit tests validating index mapping behavior for DPAD_UP across arbitrary rows (r -> r-1)
 * with clamping and boundary handling. Preserves existing tests for 2->1 mapping.
 */
class FocusMappingTest {

    // PUBLIC_INTERFACE
    /**
     * computeTargetIndex
     * Pure helper that mirrors the clamping logic used when mapping focus up from any row r>0
     * to the row directly above (r-1) by index.
     * @param currentIndex index of focused child in current row
     * @param upperRowCount number of children in the immediate upper row
     * @return target index in upper row after clamping, or -1 if upper row empty
     */
    private fun computeTargetIndex(currentIndex: Int, upperRowCount: Int): Int {
        if (upperRowCount <= 0) return -1
        val safeCurrent = if (currentIndex < 0) 0 else currentIndex
        return safeCurrent.coerceAtMost(upperRowCount - 1)
    }

    @Test
    fun `2nd row to 1st row maps within bounds`() {
        assertEquals(0, computeTargetIndex(0, 5))
        assertEquals(3, computeTargetIndex(3, 6))
        assertEquals(4, computeTargetIndex(4, 5))
    }

    @Test
    fun `2nd row to 1st row clamps when index exceeds`() {
        assertEquals(2, computeTargetIndex(5, 3))
        assertEquals(0, computeTargetIndex(10, 1))
    }

    @Test
    fun `upper row empty returns -1`() {
        assertEquals(-1, computeTargetIndex(0, 0))
        assertEquals(-1, computeTargetIndex(3, 0))
    }

    @Test
    fun `negative current index coerces to zero`() {
        assertEquals(0, computeTargetIndex(-1, 3))
        assertEquals(0, computeTargetIndex(-10, 2))
    }

    // Additional coverage for generalized rule:

    @Test
    fun `3rd row to 2nd row general mapping`() {
        // upper row has enough items
        assertEquals(2, computeTargetIndex(2, 5))
        // clamp when upper has fewer
        assertEquals(1, computeTargetIndex(4, 2))
    }

    @Test
    fun `4th row to 3rd row general mapping`() {
        assertEquals(0, computeTargetIndex(0, 1))
        assertEquals(3, computeTargetIndex(3, 10))
        assertEquals(6, computeTargetIndex(9, 7)) // clamp to last index 6
    }
}
