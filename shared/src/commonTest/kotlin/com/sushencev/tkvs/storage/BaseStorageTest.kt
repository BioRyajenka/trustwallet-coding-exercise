package com.sushencev.tkvs.storage

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Ignore
abstract class BaseStorageTest<T : IMutableStorage>(protected val sut: T) {
    @Test
    fun `set should set value when value didn't exist`() {
        // given
        val key = "key"
        val value = "value"

        // when
        sut[key] = value

        // then
        assertEquals(sut[key], value)
    }

    @Test
    fun `set should update value when value existed`() {
        // given
        val key = "key"
        val oldValue = "oldValue"
        val value = "value"
        sut[key] = oldValue

        // when
        sut[key] = value

        // then
        assertEquals(sut[key], value)
    }

    @Test
    fun `get should return value when value exist`() {
        // given
        val key = "key"
        val expected = "value"
        sut[key] = expected

        // when
        val actual = sut[key]

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `get should return null when value doesn't exist`() {
        // given
        val key = "key"

        // when
        val actual = sut[key]

        // then
        assertNull(actual)
    }

    @Test
    fun `delete should delete value when value existed`() {
        // given
        val key = "key"
        val value = "value"
        sut[key] = value

        // when
        sut.delete(key)

        // then
        assertNull(sut[key])
    }

    @Test
    fun `delete should return previous value when value existed`() {
        // given
        val key = "key"
        val value = "value"

        // when
        sut[key] = value

        // then
        assertEquals(sut[key], value)
    }

    @Test
    fun `delete should return null when value didn't exist`() {
        // when
        val actual = sut.delete("key")

        // then
        assertNull(actual)
    }

    @Test
    fun `count should return 0 when value not exist`() {
        // given
        val value = "value"
        sut["key1"] = "another value"
        sut["key2"] = value
        sut.delete("key2")

        // when
        val actual = sut.count(value)

        // then
        assertEquals(0, actual)
    }

    @Test
    fun `count should return count when value exist`() {
        // given
        val value = "value"
        sut["key1"] = value
        sut["key2"] = value
        sut["key2"] = "another value"
        sut["key3"] = value
        sut.delete("key3")
        sut["key4"] = value

        // when
        val actual = sut.count(value)

        // then
        assertEquals(2, actual)
    }

}

