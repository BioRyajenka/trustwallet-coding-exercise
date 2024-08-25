package com.sushencev.tkvs.storage

class InMemoryStorage: IStorage {
    private val data = mutableMapOf<String, String>()
    private val countByValue = mutableMapOf<String, Int>()

    override fun set(key: String, value: String) {
        data[key]?.let { prevValue ->
            decreaseCount(prevValue)
        }
        data[key] = value
        increaseCount(value)
    }

    override fun get(key: String): String? = data[key]
    override fun delete(key: String): String? {
        return data.remove(key)?.also(::decreaseCount)
    }

    override fun count(value: String) = countByValue[value] ?: 0

    private fun increaseCount(value: String) {
        countByValue[value] = (countByValue[value] ?: 0) + 1
    }

    private fun decreaseCount(value: String) {
        val prevCount = checkNotNull(countByValue[value])

        if (prevCount == 1) {
            countByValue -= value
        } else {
            countByValue[value] = prevCount - 1
        }
    }
}
