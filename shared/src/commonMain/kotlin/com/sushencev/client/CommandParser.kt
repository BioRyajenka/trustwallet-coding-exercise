package com.sushencev.client

class UnknownCommandException(val command: String): Exception("Unknown command \"$command\"")
class WrongAmountOfArguments(val command: String, val expected: Int): Exception("Wrong amount of arguments for command \"$command\". $expected expected")

class CommandParser {
    private val COMMANDS_AND_ARGUMENTS = mapOf(
        "SET" to 2,
        "GET" to 1,
        "DELETE" to 1,
        "COUNT" to 1,
        "BEGIN" to 0,
        "COMMIT" to 0,
        "ROLLBACK" to 0,
    )

    fun parse(line: String): Command {
        val tokens = line.trim().split(" ")
        val command = tokens[0].uppercase()
        val argsNum = COMMANDS_AND_ARGUMENTS[command]
            ?: throw UnknownCommandException(command)
        val args = tokens.subList(1, tokens.size)
        if (args.size != argsNum) {
            throw WrongAmountOfArguments(command, argsNum)
        }

        return when (command) {
            "SET" -> Command.SET(args[0], args[1])
            "GET" -> Command.GET(args[0])
            "DELETE" -> Command.DELETE(args[0])
            "COUNT" -> Command.COUNT(args[0])
            "BEGIN" -> Command.BEGIN_TRANSACTION
            "COMMIT" -> Command.COMMIT_TRANSACTION
            "ROLLBACK" -> Command.ROLLBACK_TRANSACTION
            else -> throw AssertionError()
        }
    }
}
