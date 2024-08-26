package com.sushencev.client

interface IConsole {
    suspend fun println(message: String)
    suspend fun errorPrintln(message: String)
    suspend fun readln(): String
}

expect class Console : IConsole {
    override suspend fun println(message: String)
    override suspend fun errorPrintln(message: String)
    override suspend fun readln(): String
}
