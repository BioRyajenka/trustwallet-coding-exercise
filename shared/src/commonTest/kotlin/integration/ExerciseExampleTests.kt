package integration

import com.sushencev.client.CommandParser
import com.sushencev.client.ConsoleClient
import com.sushencev.tkvs.KVSBackend
import com.sushencev.tkvs.storage.InMemoryStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import util.MockConsole
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.seconds

class ExerciseExampleTests {
    private val console = MockConsole()
    private val parser = CommandParser()
    private val backend = KVSBackend(InMemoryStorage())
    private val client = ConsoleClient(console, parser, backend)

    @Test
    fun `Set and get a value`() = runBlocking {
        launch { client.run() }

        with(console) {
            send("SET foo 123")
            sendAndCheckResponse("GET foo", "123")
            send("QUIT")
        }
    }

    @Test
    fun `Count the number of occurrences of a value`() = runBlocking {
        launch { client.run() }

        with(console) {
            send("SET foo 123")
            send("SET bar 456")
            send("SET baz 123")
            sendAndCheckResponse("COUNT 123", "2")
            sendAndCheckResponse("COUNT 456", "1")
            send("QUIT")
        }
    }

    @Test
    fun `Commit a transaction`() = verifyLog(
        """
            > SET bar 123
            > GET bar
            123
            > BEGIN
            > SET foo 456
            > GET bar
            123
            > DELETE bar
            > COMMIT
            > GET bar
            key not set
            > ROLLBACK
            no transaction
            > GET foo
            456
        """.trimIndent()
    )

    @Test
    fun `Rollback a transaction`() = verifyLog(
        """
            > SET foo 123
            > SET bar abc
            > BEGIN
            > SET foo 456
            > GET foo
            456
            > SET bar def
            > GET bar
            def
            > ROLLBACK
            > GET foo
            123
            > GET bar
            abc
            > COMMIT
            no transaction
        """.trimIndent()
    )

    @Test
    fun `Nested transactions`() = verifyLog("""
        > SET foo 123
        > SET bar 456
        > BEGIN
        > SET foo 456
        > BEGIN
        > COUNT 456
        2
        > GET foo
        456
        > SET foo 789
        > GET foo
        789
        > ROLLBACK
        > GET foo
        456
        > DELETE foo
        > GET foo
        key not set
        > ROLLBACK
        > GET foo
        123
    """.trimIndent())

    private fun verifyLog(log: String) = runBlocking {
        launch { client.run() }

        val lines = log.split("\n").map { it.trim() } + listOf("> QUIT", ">")
        for (i in 1 until lines.size) {
            val prevLine = lines[i - 1]
            val curLine = lines[i]
            if (!prevLine.startsWith(">")) {
                continue
            }
            if (curLine.startsWith(">")) {
                console.send(prevLine.removePrefix(">"))
            } else {
                console.sendAndCheckResponse(prevLine.removePrefix(">"), curLine)
            }
        }
    }
}
