package util

import com.sushencev.tkvs.storage.IImmutableStorage

class MockStorage(vararg entries: Pair<String, String>): IImmutableStorage {
    private var entries: MutableMap<String, String> = mutableMapOf(*entries)

    override fun get(key: String) = entries[key]

    override fun count(value: String) = entries.values.count { it == value }

    fun setEntries(vararg entries: Pair<String, String>) {
        this.entries = mutableMapOf(*entries)
    }
}
