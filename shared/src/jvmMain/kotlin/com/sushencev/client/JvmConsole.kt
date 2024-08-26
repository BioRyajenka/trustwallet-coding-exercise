package com.sushencev.client

actual typealias Console = JvmConsole

class JvmConsole : IConsole {
    override suspend fun println(message: String) {
        System.out.println(message)
    }

    override suspend fun errorPrintln(message: String) {
        System.err.println(message)
    }

    override suspend fun readln(): String {
        print("> ")
        return checkNotNull(readlnOrNull())
    }
}
