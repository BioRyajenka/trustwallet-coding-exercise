package integration

import com.sushencev.tkvs.KVSBackend
import com.sushencev.tkvs.storage.InMemoryStorage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class KVSBackendParallelismTests {
    @Test
    fun `beginTransaction works in parallel context`() = runBlocking {
        val storage = InMemoryStorage()
        val backend = KVSBackend(storage)

        val txn1Ready = CompletableDeferred<Unit>()
        val txn2Ready = CompletableDeferred<Unit>()

        val job1 = launch {
            val transaction1 = backend.beginTransaction()
            txn1Ready.complete(Unit)
            txn2Ready.await()
            transaction1["key1"] = "value1"
            transaction1.commit()
        }

        val job2 = launch {
            val transaction2 = backend.beginTransaction()
            txn2Ready.complete(Unit)
            txn1Ready.await()
            transaction2["key2"] = "value2"
            transaction2.commit()
        }

        // Wait for both coroutines to finish
        joinAll(job1, job2)

        // Verify that the modifications were applied sequentially
        assertEquals("value1", storage["key1"])
        assertEquals("value2", storage["key2"])

        // Stop the backend
        backend.stop()
    }
}
