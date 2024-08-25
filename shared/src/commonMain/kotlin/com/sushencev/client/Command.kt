package com.sushencev.client

sealed interface Command {
    data class SET(val key: String, val value: String): Command
    data class GET(val key: String): Command
    data class DELETE(val key: String): Command
    data class COUNT(val value: String): Command

    data object BEGIN_TRANSACTION: Command
    data object COMMIT_TRANSACTION: Command
    data object ROLLBACK_TRANSACTION: Command
}
