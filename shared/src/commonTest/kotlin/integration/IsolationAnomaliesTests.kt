package integration

import com.sushencev.tkvs.KVSBackend
import com.sushencev.tkvs.OptimisticLockingException
import com.sushencev.tkvs.Transaction
import com.sushencev.tkvs.storage.InMemoryStorage
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class IsolationAnomaliesTests {
    @Test
    fun `lost update is not happening`() = runTest {
        lateinit var txn1: Transaction

        // given
        val storage = InMemoryStorage()
        val key = "key"
        storage[key] = "old value"
        val backend = KVSBackend(storage)

        txn1 = backend.beginTransaction()
        txn1[key] = "value1"

        val txn2 = backend.beginTransaction()
        txn2[key] = "value2"
        txn2.commit()

        // when, then
        assertFailsWith(OptimisticLockingException::class) {
            txn1.commit()
        }
    }
}
