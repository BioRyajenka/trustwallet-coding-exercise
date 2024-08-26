package com.sushencev.client

import com.sushencev.tkvs.KVSBackend
import com.sushencev.tkvs.OptimisticLockingException
import com.sushencev.tkvs.Transaction

private const val KEY_NOT_SET = "key not set"
private const val NO_ONGOING_TRANSACTION = "no transaction"

class ConsoleClient(
    private val console: IConsole,
    private val parser: CommandParser,
    private val backend: KVSBackend,
) {
    private val transactionStack = mutableListOf<Transaction>()
    private val curTransaction get() = transactionStack.lastOrNull()

    suspend fun run() {
        while (true) {
            try {
                val line = console.readln()
                when (val cmd = parser.parse(line)) {
                    ConsoleCommand.QUIT -> break
                    is ConsoleCommand.SET -> runInTransaction { set(cmd.key, cmd.value) }
                    is ConsoleCommand.GET -> runInTransaction {
                        console.println(get(cmd.key) ?: KEY_NOT_SET)
                    }
                    is ConsoleCommand.DELETE -> runInTransaction { delete(cmd.key) }
                    is ConsoleCommand.COUNT -> runInTransaction { console.println(count(cmd.value).toString()) }
                    ConsoleCommand.BEGIN_TRANSACTION -> beginTransaction()
                    ConsoleCommand.COMMIT_TRANSACTION -> commitOrRollbackTransaction { commit() }
                    ConsoleCommand.ROLLBACK_TRANSACTION -> commitOrRollbackTransaction { abort() }
                }
            } catch (e: OptimisticLockingException) {
                console.errorPrintln(e.message!!)
            } catch (e: ParsingException) {
                console.errorPrintln(e.message!!)
            }
        }
    }

    private suspend fun beginTransaction() {
        transactionStack += if (curTransaction == null) {
            backend.beginTransaction()
        } else {
            curTransaction!!.beginTransaction()
        }
    }

    private suspend fun commitOrRollbackTransaction(run: suspend Transaction.() -> Unit) {
        if (curTransaction != null) {
            run(curTransaction!!)
            transactionStack.removeLast()
        } else {
            console.errorPrintln(NO_ONGOING_TRANSACTION)
        }
    }

    private suspend fun runInTransaction(run: Transaction.() -> Unit) {
        if (curTransaction != null) {
            run(curTransaction!!)
        } else {
            val txn = backend.beginTransaction()
            run(txn)
            txn.commit()
        }
    }
}
