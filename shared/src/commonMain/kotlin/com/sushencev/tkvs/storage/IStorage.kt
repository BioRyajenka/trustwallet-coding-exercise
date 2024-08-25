package com.sushencev.tkvs.storage

interface IStorage {
    fun set(key: String, value: String)
    fun get(key: String): String?
    fun delete(key: String): String?
    fun count(value: String): Int

//    TODO fun beginTransaction(): Transaction
}
