package com.sushencev.tkvs

import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class Console : IConsole {
    private val STDERR = platform.posix.fdopen(2, "w")

    actual override fun println(message: String) {
        platform.posix.printf("$message\n")
    }

    actual override fun errorPrintln(message: String) {
        platform.posix.fprintf(STDERR, "%s\n", message)
        platform.posix.fflush(STDERR)
    }
}
