package util

import com.sushencev.client.IConsole
import kotlinx.coroutines.channels.Channel
import kotlin.test.assertEquals

class MockConsole: IConsole {
    private val outputChannel = Channel<String>(1000)
    private val inputChannel = Channel<String>(1000)

    override suspend fun println(message: String) {
        outputChannel.send(message)
    }

    override suspend fun errorPrintln(message: String) {
        println(message)
    }

    override suspend fun readln(): String {
        return inputChannel.receive()
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
