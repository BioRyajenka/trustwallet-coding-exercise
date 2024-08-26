package com.sushencev.client

sealed interface ConsoleCommand {
    data class SET(val key: String, val value: String): ConsoleCommand
    data class GET(val key: String): ConsoleCommand
    data class DELETE(val key: String): ConsoleCommand
    data class COUNT(val value: String): ConsoleCommand

    data object BEGIN_TRANSACTION: ConsoleCommand
    data object COMMIT_TRANSACTION: ConsoleCommand
    data object ROLLBACK_TRANSACTION: ConsoleCommand
    data object QUIT: ConsoleCommand
}
