package org.example.list

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * PUBLIC_INTERFACE
 * Tests for LinkedList basic operations.
 */
class LinkedListTest {
    @Test
    fun addAndGetWorks() {
        val list = LinkedList<Int>()
        list.add(1)
        list.add(2)
        list.add(3)
        assertEquals(3, list.size())
        assertEquals(1, list.get(0))
        assertEquals(3, list.get(2))
    }
}
