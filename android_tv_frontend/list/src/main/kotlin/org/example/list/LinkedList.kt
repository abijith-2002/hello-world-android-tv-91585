package org.example.list

/**
 * PUBLIC_INTERFACE
 * A minimal generic singly linked list used as a placeholder for the list module.
 */
class LinkedList<T> {
    private data class Node<T>(val value: T, var next: Node<T>? = null)

    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var _size: Int = 0

    /**
     * PUBLIC_INTERFACE
     * Adds an element to the end of the list.
     */
    fun add(value: T) {
        val node = Node(value)
        if (head == null) {
            head = node
            tail = node
        } else {
            tail?.next = node
            tail = node
        }
        _size++
    }

    /**
     * PUBLIC_INTERFACE
     * Returns the element at the specified index or throws IndexOutOfBoundsException.
     */
    fun get(index: Int): T {
        if (index < 0 || index >= _size) throw IndexOutOfBoundsException("index=$index size=$_size")
        var cur = head
        var i = 0
        while (i < index) {
            cur = cur?.next
            i++
        }
        return cur!!.value
    }

    /**
     * PUBLIC_INTERFACE
     * Returns the number of elements in the list.
     */
    fun size(): Int = _size

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("[")
        var cur = head
        while (cur != null) {
            sb.append(cur.value)
            cur = cur.next
            if (cur != null) sb.append(", ")
        }
        sb.append("]")
        return sb.toString()
    }
}
