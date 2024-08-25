package com.sushencev.tkvs

import com.sushencev.tkvs.storage.IMutableStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class OptimisticLockingException: Exception(
    "Some data modified in this transaction was also modified in other transaction. Retry."
)

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class KVSBackend(private val storage: IMutableStorage) {
    private val modificationsChannel = Channel<List<DataModification>>()

    private val singleThreadContext = newSingleThreadContext("CommitThread")
    private val scope = CoroutineScope(singleThreadContext)

    private var stopped = false

    init {
        scope.launch {
            for (modifications in modificationsChannel) {
                applyModifications(modifications)
            }
        }
    }

    fun stop() {
        stopped = true
        modificationsChannel.close()
        scope.coroutineContext.cancelChildren()
        singleThreadContext.close()
    }

    suspend fun beginTransaction(): Transaction {
        check(!stopped) { "KVSBackend is stopped!" }

        return Transaction(storage) { modifications ->
            modificationsChannel.send(modifications)
        }
    }

    private fun applyModifications(modifications: List<DataModification>) {
        modifications.forEach { (key, _, prevValue) ->
            if (storage[key] != prevValue) {
                throw OptimisticLockingException()
            }
        }

        modifications.forEach { (key, newValue) ->
            if (newValue == null) {
                storage.delete(key)
            } else {
                storage[key] = newValue
            }
        }
    }
}
