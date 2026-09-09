package com.chenyeju

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PreviewFrameGateTest {

    @Test
    fun `admits frames while fewer than maxPending are in flight`() {
        val gate = PreviewFrameGate(maxPending = 2)

        assertTrue(gate.tryAcquire())
        assertTrue(gate.tryAcquire())
        assertFalse(gate.tryAcquire())
    }

    @Test
    fun `release frees a slot for the next frame`() {
        val gate = PreviewFrameGate(maxPending = 1)

        assertTrue(gate.tryAcquire())
        assertFalse(gate.tryAcquire())
        gate.release()
        assertTrue(gate.tryAcquire())
    }

    @Test
    fun `counts rejected frames`() {
        val gate = PreviewFrameGate(maxPending = 1)

        gate.tryAcquire()
        gate.tryAcquire()
        gate.tryAcquire()

        assertEquals(2L, gate.droppedCount)
    }

    @Test
    fun `reset clears in-flight and dropped counters`() {
        val gate = PreviewFrameGate(maxPending = 1)
        gate.tryAcquire()
        gate.tryAcquire()

        gate.reset()

        assertTrue(gate.tryAcquire())
        assertEquals(0L, gate.droppedCount)
    }
}
