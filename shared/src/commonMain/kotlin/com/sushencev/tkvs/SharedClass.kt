package com.sushencev.tkvs

class SharedClass(private val console: IConsole) {

    fun printMe() {
        console.println("Hello, Kotlin!")
    }
}
