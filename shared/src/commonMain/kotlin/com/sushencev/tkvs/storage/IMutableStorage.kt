package com.sushencev.tkvs.storage

interface IImmutableStorage {
    operator fun get(key: String): String?
    fun count(value: String): Int
}

interface IMutableStorage : IImmutableStorage {
    operator fun set(key: String, value: String)
    fun delete(key: String): String?
}
