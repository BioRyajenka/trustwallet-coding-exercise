package com.sushencev.tkvs

import com.sushencev.client.CommandParser
import com.sushencev.client.Console
import com.sushencev.client.ConsoleClient
import com.sushencev.tkvs.storage.InMemoryStorage
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val console = Console()
    val parser = CommandParser()
    val backend = KVSBackend(InMemoryStorage())

    ConsoleClient(console, parser, backend, alertConfirmationsEnabled = true).run()
}
