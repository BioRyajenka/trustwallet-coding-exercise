package com.sushencev.tkvs

import com.sushencev.tkvs.storage.IImmutableStorage
import com.sushencev.tkvs.storage.IMutableStorage
import com.sushencev.tkvs.storage.InMemoryStorage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred

internal data class DataModification(val key: String, val newValue: String?, val previousValue: String?)

class Transaction internal constructor(
    private val sourceStorage: IImmutableStorage,
    private val onAbort: () -> Unit = {},
    private val onCommit: suspend (modifications: List<DataModification>) -> Unit,
) : IMutableStorage {
    private val txnStorage = InMemoryStorage()
    private val cachedValuesStorage = InMemoryStorage()

    private val prevValueByKey = mutableMapOf<String, String?>()
    private val deletedKeys = mutableSetOf<String>()

    private var aborted = false
    private var committed = false
    private var awaitingNestedTransaction = false

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
        return countWithoutDuplicates(value)
    }

    suspend fun commit() {
        ensureTxnState()

        val modifications = txnStorage.entries.map { (key, value) ->
            DataModification(key, newValue = value, previousValue = prevValueByKey[key])
        } + deletedKeys.map { key ->
            DataModification(key, newValue = null, previousValue = prevValueByKey[key])
        }
        try {
            onCommit(modifications)
        } catch (exception: Exception) {
            aborted = true
            throw exception
        }

        committed = true
    }

    fun abort() {
        onAbort()
        aborted = true
    }

    fun beginTransaction(): Transaction {
        ensureNoNestedTransaction()
        awaitingNestedTransaction = true

        val sourceStorageForNested = object : IImmutableStorage {
            override fun get(key: String) = txnStorage[key]
            override fun count(value: String) = countWithoutDuplicates(value)
        }

        return Transaction(sourceStorageForNested,
            onCommit = { modifications ->
                awaitingNestedTransaction = false

                modifications.forEach { (key, newValue, prevValue) ->
                    if (this[key] != prevValue) {
                        throw AssertionError("It should not be possible to run nested transactions concurrently")
                    }

                    if (newValue == null) {
                        this.delete(key)
                    } else {
                        this[key] = newValue
                    }
                }
            },
            onAbort = { awaitingNestedTransaction = false }
        )
    }

    private fun ensureTxnState() {
        check(!aborted) { "Transaction is aborted" }
        check(!committed) { "Transaction is already committed" }
        ensureNoNestedTransaction()
    }

    private fun ensureNoNestedTransaction() {
        check(!awaitingNestedTransaction) { "Nested transaction is neither committed nor aborted" }
    }

    private fun ensureLoaded(key: String) {
        if (cachedValuesStorage[key] != null) {
            return
        }
        val prevValue = sourceStorage[key]
        prevValueByKey[key] = prevValue
        if (prevValue != null) {
            txnStorage[key] = prevValue
            cachedValuesStorage[key] = prevValue
        }
    }

    private fun countWithoutDuplicates(value: String) =
        sourceStorage.count(value) + txnStorage.count(value) - cachedValuesStorage.count(value)
}
