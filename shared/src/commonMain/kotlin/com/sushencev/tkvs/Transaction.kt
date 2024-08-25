package com.sushencev.tkvs

import com.sushencev.tkvs.storage.IImmutableStorage
import com.sushencev.tkvs.storage.IMutableStorage
import com.sushencev.tkvs.storage.InMemoryStorage

internal data class DataModification(val key: String, val newValue: String?, val previousValue: String?)

class Transaction internal constructor(
    private val sourceStorage: IImmutableStorage,
    private val onCommit: (modifications: List<DataModification>) -> Unit,
) : IMutableStorage {
    private val txnStorage = InMemoryStorage()
    private val cachedValuesStorage = InMemoryStorage()

    private val prevValueByKey = mutableMapOf<String, String?>()
    private val deletedKeys = mutableSetOf<String>()

    private var aborted = false
    private var committed = false

    override fun set(key: String, value: String) {
        ensureTxnState()
        ensureLoaded(key)
        txnStorage[key] = value
        deletedKeys -= key
    }

    override fun get(key: String): String? {
        ensureTxnState()
        ensureLoaded(key)
        return txnStorage[key]
    }

    override fun delete(key: String): String? {
        ensureTxnState()
        ensureLoaded(key)
        deletedKeys += key
        return txnStorage.delete(key)
    }

    override fun count(value: String): Int {
        ensureTxnState()
        // allow phantom reads
        return sourceStorage.count(value) + txnStorage.count(value) - cachedValuesStorage.count(value)
    }

    fun commit() {
        ensureTxnState()

        val modifications = txnStorage.entries.map { (key, value) ->
            DataModification(key, newValue = value, previousValue = prevValueByKey[key])
        } + deletedKeys.map { key ->
            DataModification(key, newValue = null, previousValue = prevValueByKey[key])
        }
        onCommit(modifications)

        committed = true
    }

    fun abort() {
        aborted = true
    }

    private fun ensureTxnState() {
        check(!aborted) { "Transaction is aborted" }
        check(!committed) { "Transaction is already committed" }
    }

    private fun ensureLoaded(key: String) {
        if (txnStorage[key] != null) {
            return
        }
        val prevValue = sourceStorage[key]
        prevValueByKey[key] = prevValue
        if (prevValue != null) {
            txnStorage[key] = prevValue
            cachedValuesStorage[key] = prevValue
        }
    }
}
