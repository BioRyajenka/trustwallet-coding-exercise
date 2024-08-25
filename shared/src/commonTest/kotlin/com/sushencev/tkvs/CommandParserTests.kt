package com.sushencev.tkvs

import com.sushencev.client.Command
import com.sushencev.client.CommandParser
import com.sushencev.client.UnknownCommandException
import com.sushencev.client.WrongAmountOfArguments
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CommandParserTests {
    private val parser = CommandParser()

    @Test
    fun `parse should parse SET correctly`() {
        assertEquals(Command.SET("1", "2"), parser.parse("SET 1 2"))
    }

    @Test
    fun `parse should parse GET correctly`() {
        assertEquals(Command.GET("1"), parser.parse("GET 1"))
    }

    @Test
    fun `parse should parse DELETE correctly`() {
        assertEquals(Command.DELETE("1"), parser.parse("DELETE 1"))
    }

    @Test
    fun `parse should parse COUNT correctly`() {
        assertEquals(Command.COUNT("1"), parser.parse("COUNT 1"))
    }

    @Test
    fun `parse should parse BEGIN correctly`() {
        assertEquals(Command.BEGIN_TRANSACTION, parser.parse("BEGIN"))
    }

    @Test
    fun `parse should parse COMMIT correctly`() {
        assertEquals(Command.COMMIT_TRANSACTION, parser.parse("COMMIT"))
    }

    @Test
    fun `parse should parse ROLLBACK correctly`() {
        assertEquals(Command.ROLLBACK_TRANSACTION, parser.parse("ROLLBACK"))
    }

    @Test
    fun `parse should throw UnknownCommandException if command is unknown`() {
        assertFailsWith(UnknownCommandException::class) {
            parser.parse("command")
        }
    }

    @Test
    fun `parse should throw WrongNumberOfArguments if the number of arguments is wrong`() {
        assertFailsWith(WrongAmountOfArguments::class) {
            parser.parse("SET 1")
        }
    }
}
