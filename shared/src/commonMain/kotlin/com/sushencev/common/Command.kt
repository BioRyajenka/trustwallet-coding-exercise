package com.sushencev.common

sealed interface Command {
    class SET(val key: String, val value: String): Command
    class GET(val key: String): Command
    class DELETE(val key: String): Command
    class COUNT(val value: String): Command

    class BEGIN_TRANSACTION: Command
    class COMMIT_TRANSACTION: Command
    class ROLLBACK_TRANSACTION: Command
}
