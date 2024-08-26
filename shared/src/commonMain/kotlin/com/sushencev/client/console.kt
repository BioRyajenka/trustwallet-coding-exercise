package com.sushencev.client

interface IConsole {
    fun println(message: String)
    fun errorPrintln(message: String)
    fun readln(): String
}

expect class Console : IConsole {
    override fun println(message: String)
    override fun errorPrintln(message: String)
    override fun readln(): String
}
