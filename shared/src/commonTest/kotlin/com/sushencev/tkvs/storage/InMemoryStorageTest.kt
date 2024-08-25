package com.sushencev.tkvs.storage

import kotlin.test.Test
import kotlin.test.assertEquals

class InMemoryStorageTest: BaseStorageTest<InMemoryStorage>(InMemoryStorage()) {
    @Test
    fun `CRUD operations don't work on aborted transaction`() {
        // given
        val expected = listOf(
            "key1" to "value1",
            "key2" to "value2",
            "key3" to "value3",
        )
        expected.forEach { (k, v) ->
            sut[k] = v
        }

        // when
        val actual = sut.entries

        // then
        assertEquals(expected, actual)
    }
}
