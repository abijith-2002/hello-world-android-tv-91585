package com.example.tv.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * PUBLIC_INTERFACE
 * FocusMappingTest
 * Unit tests validating index mapping behavior for DPAD_UP across arbitrary rows (r -> r-1)
 * with clamping and boundary handling. Adds a simple boundary check representing
 * first row mapping to menu sentinel.
 */
class FocusMappingTest {

    // PUBLIC_INTERFACE
    /**
     * computeTargetIndex
     * Mirrors clamping logic used for mapping from row r to r-1.
     * When upperRowCount == -1 (sentinel), we interpret this as "map to menu".
     * @param currentIndex index of focused child in current row
     * @param upperRowCount number of children in the immediate upper row; -1 denotes menu sentinel
     * @return target index, clamped; or -1 for menu sentinel
     */
    private fun computeTargetIndex(currentIndex: Int, upperRowCount: Int): Int {
        if (upperRowCount < 0) return -1 // menu sentinel
        if (upperRowCount == 0) return -2 // represents no target possible
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
    fun `upper row empty returns no target`() {
        assertEquals(-2, computeTargetIndex(0, 0))
        assertEquals(-2, computeTargetIndex(3, 0))
    }

    @Test
    fun `negative current index coerces to zero`() {
        assertEquals(0, computeTargetIndex(-1, 3))
        assertEquals(0, computeTargetIndex(-10, 2))
    }

    @Test
    fun `first row maps to menu sentinel`() {
        // When on first row, the implementation maps to menu; we encode menu as -1
        assertEquals(-1, computeTargetIndex(0, -1))
        assertEquals(-1, computeTargetIndex(3, -1))
    }
}
