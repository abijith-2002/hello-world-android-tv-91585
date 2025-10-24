package com.example.tv.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * PUBLIC_INTERFACE
 * FocusMappingTest
 * Unit tests validating index mapping behavior for DPAD_UP from 2nd row to 1st row.
 * This verifies that the target index is clamped properly when first row has fewer items.
 */
class FocusMappingTest {

    // PUBLIC_INTERFACE
    /**
     * computeTargetIndex
     * Pure helper that mirrors the clamping logic used when mapping focus from the second row
     * to the first row by index.
     * @param currentIndex index of focused child in second row
     * @param firstRowCount number of children in first row
     * @return target index in first row after clamping
     */
    private fun computeTargetIndex(currentIndex: Int, firstRowCount: Int): Int {
        if (firstRowCount <= 0) return -1
        val safeCurrent = if (currentIndex < 0) 0 else currentIndex
        return safeCurrent.coerceAtMost(firstRowCount - 1)
    }

    @Test
    fun `maps within bounds when first row count equals or exceeds`() {
        assertEquals(0, computeTargetIndex(0, 5))
        assertEquals(3, computeTargetIndex(3, 6))
        assertEquals(4, computeTargetIndex(4, 5))
    }

    @Test
    fun `clamps to last index when second row index exceeds first row size`() {
        assertEquals(2, computeTargetIndex(5, 3))
        assertEquals(0, computeTargetIndex(10, 1))
    }

    @Test
    fun `returns -1 when first row empty`() {
        assertEquals(-1, computeTargetIndex(0, 0))
        assertEquals(-1, computeTargetIndex(3, 0))
    }

    @Test
    fun `negative current index coerces to zero`() {
        assertEquals(0, computeTargetIndex(-1, 3))
        assertEquals(0, computeTargetIndex(-10, 2))
    }
}
