package com.sushencev.client

import com.sushencev.client.IConsole
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class Console : IConsole {
    private val STDERR = platform.posix.fdopen(2, "w")

    actual override fun println(message: String) {
        kotlin.io.println(message)
    }

    actual override fun errorPrintln(message: String) {
        platform.posix.fprintf(STDERR, "%s\n", message)
        platform.posix.fflush(STDERR)
    }

    actual override fun readln(): String {
        print("> ")
        return checkNotNull(readlnOrNull())
    }
}
