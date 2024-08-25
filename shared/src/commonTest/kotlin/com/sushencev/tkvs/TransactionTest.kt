package com.sushencev.tkvs

import com.sushencev.tkvs.storage.BaseStorageTest
import com.sushencev.tkvs.storage.IImmutableStorage
import com.sushencev.tkvs.storage.InMemoryStorage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TransactionTest : BaseStorageTest<Transaction>(Transaction(InMemoryStorage()) {}) {
    @Test
    fun `set fails on aborted transaction`() {
        // given
        sut.abort()

        // when, then
        assertFailsWith(IllegalStateException::class) {
            sut["any key"] = "any value"
        }
    }

    @Test
    fun `get fails on aborted transaction`() {
        // given
        sut.abort()

        // when, then
        assertFailsWith(IllegalStateException::class) {
            sut["any key"]
        }
    }

    @Test
    fun `delete fails on aborted transaction`() {
        // given
        sut.abort()

        // when, then
        assertFailsWith(IllegalStateException::class) {
            sut.delete("any key")
        }
    }

    @Test
    fun `count fails on aborted transaction`() {
        // given
        sut.abort()

        // when, then
        assertFailsWith(IllegalStateException::class) {
            sut.count("any key")
        }
    }

    @Test
    fun `commit summarizes changes correctly`() {
        // given
        lateinit var dataModifications: List<DataModification>
        val sourceStorage = object : IImmutableStorage {
            override fun get(key: String) = when (key) {
                "key1" -> "prev value 1"
                "key5" -> "prev value 5"
                "key3" -> "prev value 3"
                else -> null
            }

            override fun count(value: String) = TODO("Not yet implemented")
        }
        val sut = Transaction(sourceStorage) {
            dataModifications = it
        }
        sut["key1"] = "old value1"
        sut["key2"] = "value2"
        sut["key1"] = "value1"
        sut["key3"] = "value"
        sut.delete("key3")
        sut["key3"] = "value3"
        sut["key4"] = "value"
        sut.delete("key4")

        // when
        sut.commit()

        // then
        assertEquals(
            expected = listOf(
                DataModification("key1", "value1", "prev value 1"),
                DataModification("key2", newValue = "value2", previousValue = null),
                DataModification("key3", newValue = "value3", previousValue = "prev value 3"),
                DataModification("key4", newValue = null, previousValue = null),
            ),
            actual = dataModifications,
        )
    }

    @Test
    fun `count works correctly if no data was modified`() {
        // given
        val valueToCount = "value"
        val sourceStorage = object : IImmutableStorage {
            override fun get(key: String) = TODO()

            override fun count(value: String): Int {
                return if (value == valueToCount) 3 else 0
            }
        }

        val sut = Transaction(sourceStorage) {}

        // when, then
        assertEquals(3, sut.count(valueToCount))
    }

    @Test
    fun `count works correctly if data was modified`() {
        // given
        val valueToCount = "value"
        val sourceStorage = object : IImmutableStorage {
            override fun get(key: String) = when (key) {
                "key1" -> valueToCount
                "key2" -> valueToCount
                "key3" -> valueToCount
                "key4" -> valueToCount
                else -> null
            }

            override fun count(value: String): Int {
                return if (value == valueToCount) 4 else 0
            }
        }

        val sut = Transaction(sourceStorage) {}
        sut["key1"] = "new value"
        sut.delete("key2")
        sut.delete("key3")
        sut["key3"] = valueToCount
        sut["key5"] = valueToCount

        // when, then
        assertEquals(3, sut.count(valueToCount))
    }
}