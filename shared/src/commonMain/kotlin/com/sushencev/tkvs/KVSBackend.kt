package com.sushencev.tkvs

import com.sushencev.tkvs.storage.IMutableStorage

class OptimisticLockingException: Exception(
    "Some data modified in this transaction was also modified in other transaction. Retry."
)

class KVSBackend(private val storage: IMutableStorage) {
    fun beginTransaction() = Transaction(storage) { modifications ->
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
