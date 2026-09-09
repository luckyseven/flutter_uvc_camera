package com.chenyeju

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Bounds how many preview frames may sit in the main-thread queue waiting to
 * be handed to Flutter.
 *
 * Each frame is serialised on the main thread by the platform channel (two
 * full copies of the pixel buffer). If the producer outpaces that thread the
 * looper queue grows without limit and the Java heap runs into its ceiling,
 * which surfaces as an OutOfMemoryError inside StandardMessageCodec. Frames
 * that arrive while [maxPending] are already in flight are dropped instead.
 *
 * Thread-safe: [tryAcquire] runs on the camera thread, [release] on main.
 */
class PreviewFrameGate(private val maxPending: Int) {
    private val pending = AtomicInteger(0)
    private val dropped = AtomicLong(0)

    val droppedCount: Long
        get() = dropped.get()

    /** Reserves a slot for one frame; false means the frame must be dropped. */
    fun tryAcquire(): Boolean {
        while (true) {
            val current = pending.get()
            if (current >= maxPending) {
                dropped.incrementAndGet()
                return false
            }
            if (pending.compareAndSet(current, current + 1)) {
                return true
            }
        }
    }

    /** Frees the slot once the frame has been handed to Flutter. */
    fun release() {
        pending.decrementAndGet()
    }

    /** Forgets in-flight and dropped frames, e.g. when the camera is closed. */
    fun reset() {
        pending.set(0)
        dropped.set(0)
    }
}
