package com.sushencev.client

abstract class ParsingException(message: String): Exception(message)
class UnknownCommandException(val command: String): ParsingException("Unknown command \"$command\"")
class WrongAmountOfArguments(val command: String, val expected: Int): ParsingException("Wrong amount of arguments for command \"$command\". $expected expected")

class CommandParser {
    private val COMMANDS_AND_ARGUMENTS = mapOf(
        "SET" to 2,
        "GET" to 1,
        "DELETE" to 1,
        "COUNT" to 1,
        "BEGIN" to 0,
        "COMMIT" to 0,
        "ROLLBACK" to 0,
        "QUIT" to 0,
    )

    fun parse(line: String): ConsoleCommand {
        val tokens = line.trim().split(" ")
        val command = tokens[0].uppercase()
        val argsNum = COMMANDS_AND_ARGUMENTS[command]
            ?: throw UnknownCommandException(command)
        val args = tokens.subList(1, tokens.size)
        if (args.size != argsNum) {
            throw WrongAmountOfArguments(command, argsNum)
        }

        return when (command) {
            "SET" -> ConsoleCommand.SET(args[0], args[1])
            "GET" -> ConsoleCommand.GET(args[0])
            "DELETE" -> ConsoleCommand.DELETE(args[0])
            "COUNT" -> ConsoleCommand.COUNT(args[0])
            "BEGIN" -> ConsoleCommand.BEGIN_TRANSACTION
            "COMMIT" -> ConsoleCommand.COMMIT_TRANSACTION
            "ROLLBACK" -> ConsoleCommand.ROLLBACK_TRANSACTION
            "QUIT" -> ConsoleCommand.QUIT
            else -> throw AssertionError()
        }
    }
}
