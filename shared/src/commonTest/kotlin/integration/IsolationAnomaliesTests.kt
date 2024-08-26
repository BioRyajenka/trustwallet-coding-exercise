package integration

import com.sushencev.tkvs.KVSBackend
import com.sushencev.tkvs.OptimisticLockingException
import com.sushencev.tkvs.storage.InMemoryStorage
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IsolationAnomaliesTests {
    @Test
    fun `lost update is not happening`() = runTest {
        // given
        val storage = InMemoryStorage()
        val key = "key"
        storage[key] = "old value"
        val backend = KVSBackend(storage)

        val txn1 = backend.beginTransaction()
        txn1[key] = "value1"

        val txn2 = backend.beginTransaction()
        txn2[key] = "value2"
        txn2.commit()

        // when, then
        assertFailsWith(OptimisticLockingException::class) {
            txn1.commit()
        }
    }

    @Test
    fun `dirty read is not happening`() = runTest {
        // given
        val storage = InMemoryStorage()
        val key = "key"
        val oldValue = "old value"
        storage[key] = oldValue
        val backend = KVSBackend(storage)

        val txn1 = backend.beginTransaction()
        txn1[key] = "value1"
        val txn2 = backend.beginTransaction()

        // when
        val actual = txn2[key]

        // then
        assertEquals(oldValue, actual)
    }

    @Test
    fun `non-repeatable read is not happening`() = runTest {
        // given
        val storage = InMemoryStorage()
        val key = "key"
        val oldValue = "old value"
        storage[key] = oldValue
        val backend = KVSBackend(storage)

        val txn1 = backend.beginTransaction()

        // when, then
        val txn2 = backend.beginTransaction()
        assertEquals(oldValue, txn2[key])

        txn1[key] = "value1"
        txn1.commit()
        assertEquals(oldValue, txn2[key])
    }

    @Test
    fun `phantom read IS happening`() = runTest {
        // given
        val storage = InMemoryStorage()
        storage["key1"] = "value"
        val backend = KVSBackend(storage)

        // when, then
        val txn1 = backend.beginTransaction()
        txn1["key2"] = "value"
        assertEquals(2, txn1.count("value"))

        val txn2 = backend.beginTransaction()
        txn2["key3"] = "value"
        txn2.commit()
        assertEquals(3, txn1.count("value"))
    }
}
