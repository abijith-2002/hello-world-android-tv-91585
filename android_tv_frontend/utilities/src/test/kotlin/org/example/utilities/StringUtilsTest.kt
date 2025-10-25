package org.example.utilities

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * PUBLIC_INTERFACE
 * Tests for StringUtils.joinNonBlank.
 */
class StringUtilsTest {
    @Test
    fun joinNonBlank_filtersAndJoins() {
        val result = StringUtils.joinNonBlank(listOf("Hello", null, " ", "TV"), " ")
        assertEquals("Hello TV", result)
    }
}
