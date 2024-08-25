package com.sushencev.tkvs

actual typealias Console = JvmConsole

class JvmConsole : IConsole {
    override fun println(message: String) {
        System.out.println(message)
    }

    override fun errorPrintln(message: String) {
        System.err.println(message)
    }
}
