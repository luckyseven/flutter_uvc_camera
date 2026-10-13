package com.chenyeju

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UvcRawFrameTapTest {

    private data class Delivered(val data: ByteArray, val width: Int, val height: Int, val format: String)

    @AfterEach
    fun tearDown() {
        UvcRawFrameTap.sink = null
    }

    @Test
    fun `forwards the frame to the installed sink`() {
        var delivered: Delivered? = null
        UvcRawFrameTap.sink = UvcRawFrameTap.Sink { data, width, height, format ->
            delivered = Delivered(data, width, height, format)
        }
        val frame = byteArrayOf(1, 2, 3)

        val accepted = UvcRawFrameTap.deliver(frame, 1280, 720, "NV21")

        assertTrue(accepted)
        assertEquals(1280, delivered!!.width)
        assertEquals(720, delivered!!.height)
        assertEquals("NV21", delivered!!.format)
        assertTrue(frame === delivered!!.data)
    }

    @Test
    fun `is a no-op without a sink`() {
        assertNull(UvcRawFrameTap.sink)

        val accepted = UvcRawFrameTap.deliver(byteArrayOf(1), 2, 2, "NV21")

        assertFalse(accepted)
    }

    @Test
    fun `stops forwarding once the sink is cleared`() {
        var frames = 0
        UvcRawFrameTap.sink = UvcRawFrameTap.Sink { _, _, _, _ -> frames++ }
        UvcRawFrameTap.deliver(byteArrayOf(1), 2, 2, "NV21")

        UvcRawFrameTap.sink = null
        UvcRawFrameTap.deliver(byteArrayOf(1), 2, 2, "NV21")

        assertEquals(1, frames)
    }
}
