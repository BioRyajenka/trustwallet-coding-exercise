package com.sushencev.tkvs

import com.sushencev.tkvs.storage.BaseStorageTest
import common.MockStorage
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TransactionTest : BaseStorageTest<Transaction>(Transaction(MockStorage()) {}) {
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
    fun `commit summarizes changes correctly`() = runBlocking {
        // given
        lateinit var dataModifications: List<DataModification>
        val sourceStorage = MockStorage(
            "key1" to "prev value 1",
            "key5" to "prev value 5",
            "key3" to "prev value 3",
        )
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
        val sourceStorage = MockStorage(
            "key1" to valueToCount,
            "key2" to valueToCount,
            "key3" to valueToCount,
        )

        val sut = Transaction(sourceStorage) {}

        // when, then
        assertEquals(3, sut.count(valueToCount))
    }

    @Test
    fun `count works correctly if data was modified`() {
        // given
        val valueToCount = "value"
        val sourceStorage = MockStorage(
            "key1" to valueToCount,
            "key2" to valueToCount,
            "key3" to valueToCount,
            "key4" to valueToCount,
        )
        val sut = Transaction(sourceStorage) {}

        sut["key1"] = "new value"
        sut.delete("key2")
        sut.delete("key3")
        sut["key3"] = valueToCount
        sut["key5"] = valueToCount

        // when, then
        assertEquals(3, sut.count(valueToCount))
    }

    @Test
    fun `CRUD fails if nested transaction is in progress`() {
        // given
        sut.beginTransaction()

        // when, then
        assertFailsWith(IllegalStateException::class) {
            sut["key"]
        }
    }

    @Test
    fun `beginning new transaction fails if nested transaction is already in progress`() {
        // given
        sut.beginTransaction()

        // when, then
        assertFailsWith(IllegalStateException::class) {
            sut.beginTransaction()
        }
    }

    @Test
    fun `beginning new transaction works if previous nested transaction was committed`() = runBlocking {
        // given
        val nested = sut.beginTransaction()
        nested["key"] = "value"
        nested.commit()

        // when, then
        sut.beginTransaction() // no exception
    }

    @Test
    fun `nested transaction commit modifies data in parent transaction`() = runBlocking {
        // given
        val nested = sut.beginTransaction()
        val key = "key"
        val value = "value"
        nested[key] = value

        // when
        nested.commit()

        // then
        assertEquals(value, sut[key])
    }
}
