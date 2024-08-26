package util

import com.sushencev.client.IConsole
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlin.test.assertEquals

class MockConsole: IConsole {
    private val outputChannel = Channel<String>(1000)
    private val inputChannel = Channel<String>(1000)

    override fun println(message: String) = runBlocking {
        outputChannel.send(message)
    }

    override fun errorPrintln(message: String) {
        println(message)
    }

    override fun readln(): String = runBlocking {
        inputChannel.receive()
    }

    suspend fun send(s: String) {
        inputChannel.send(s)
    }

    suspend fun sendAndCheckResponse(s: String, expectedResponse: String) {
        inputChannel.send(s)
        val actualResponse = outputChannel.receive()
        assertEquals(expectedResponse, actualResponse)
    }
}
