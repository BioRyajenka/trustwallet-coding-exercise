# In-memory transactional key-value storage

## Functional requirements
1. Support commands (both *k* and _v_ are Strings):
   * SET k, v
   * GET k
   * DELETE k
   * COUNT v
   * BEGIN, COMMIT, ROLLBACK
2. Only in-memory for now but make it extendable.
3. Show alerts to confirm COMMIT, ROLLBACK or DELETE.
4. Support concurrent transactions (some "sane" level of isolation guarantee - no lost updates, no dirty reads, no non-repeatable reads. Closer to Repeatable Read).

### Out of scope
   * non-string keys/values
   * session timeouts
   * different isolation levels - as it's not clear how the structure is intended to be used
   * cache eviction policies
   * multi-node setup (master-slave; master-master)
   * partitioning, replication
   * consistency guarantees (weak, eventual, strong)
    
## Backlog
 - [x] storage engine which can run "raw" commands and not thread-safe
 - [x] KVSBackend - main backend (utilises storage engine, responsible for txns, single-threaded for simplicity but thread-safe)
 - [ ] Nested transactions
 - [ ] Console client - support all base commands
 - [ ] Console client - show alerts to confirm COMMIT, ROLLBACK or DELETE.
