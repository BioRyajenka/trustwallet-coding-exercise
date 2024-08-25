package com.sushencev.tkvs.storage

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InMemoryStorageTest {
    private val storage = InMemoryStorage()

    @Test
    fun `set should set value when value didn't exist`() {
        // given
        val key = "key"
        val value = "value"

        // when
        storage.set(key, value)

        // then
        assertEquals(storage.get(key), value)
    }

    @Test
    fun `set should update value when value existed`() {
        // given
        val key = "key"
        val oldValue = "oldValue"
        val value = "value"
        storage.set(key, oldValue)

        // when
        storage.set(key, value)

        // then
        assertEquals(storage.get(key), value)
    }

    @Test
    fun `get should return value when value exist`() {
        // given
        val key = "key"
        val expected = "value"
        storage.set(key, expected)

        // when
        val actual = storage.get(key)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `get should return null when value doesn't exist`() {
        // given
        val key = "key"

        // when
        val actual = storage.get(key)

        // then
        assertNull(actual)
    }

    @Test
    fun `delete should delete value when value existed`() {
        // given
        val key = "key"
        val value = "value"
        storage.set(key, value)

        // when
        storage.delete(key)

        // then
        assertNull(storage.get(key))
    }
    @Test
    fun `delete should return previous value when value existed`() {
        // given
        val key = "key"
        val value = "value"

        // when
        storage.set(key, value)

        // then
        assertEquals(storage.get(key), value)
    }

    @Test
    fun `delete should return null when value didn't exist`() {
        // when
        val actual = storage.delete("key")

        // then
        assertNull(actual)
    }

    @Test
    fun `count should return 0 when value not exist`() {
        // given
        val value = "value"
        storage.set("key1", "another value")
        storage.set("key2", value)
        storage.delete("key2")

        // when
        val actual = storage.count(value)

        // then
        assertEquals(0, actual)
    }

    @Test
    fun `count should return count when value exist`() {
        // given
        val value = "value"
        storage.set("key1", value)
        storage.set("key2", value)
        storage.set("key2", "another value")
        storage.set("key3", value)
        storage.delete("key3")
        storage.set("key4", value)

        // when
        val actual = storage.count(value)

        // then
        assertEquals(2, actual)
    }

}
