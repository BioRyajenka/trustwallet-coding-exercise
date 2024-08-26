package com.sushencev.client

sealed interface ConsoleCommand {
    val needsConfirmation: Boolean get() = false

    data class SET(val key: String, val value: String): ConsoleCommand
    data class GET(val key: String): ConsoleCommand
    data class DELETE(val key: String, override val needsConfirmation: Boolean = true): ConsoleCommand
    data class COUNT(val value: String): ConsoleCommand

    data object BEGIN_TRANSACTION: ConsoleCommand
    data object COMMIT_TRANSACTION: ConsoleCommand {
        override val needsConfirmation: Boolean = true
    }
    data object ROLLBACK_TRANSACTION: ConsoleCommand {
        override val needsConfirmation: Boolean = true
    }
    data object QUIT: ConsoleCommand {
        override val needsConfirmation: Boolean = true
    }
}
