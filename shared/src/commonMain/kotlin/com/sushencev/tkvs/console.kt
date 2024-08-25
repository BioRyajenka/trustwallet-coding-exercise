package com.sushencev.tkvs

interface IConsole {
    fun println(message: String)
    fun errorPrintln(message: String)
}

expect class Console : IConsole {

    override fun println(message: String)

    override fun errorPrintln(message: String)
}
