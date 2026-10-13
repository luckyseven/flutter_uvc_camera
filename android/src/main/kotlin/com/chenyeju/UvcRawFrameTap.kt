package com.chenyeju

/**
 * Raw preview frames for a native consumer inside the host app (for example a
 * local recorder), independent of the Flutter preview-data stream.
 *
 * The frames are the same `onPreviewData` buffers libausbc produces (NV21 at
 * the capture resolution when raw preview data is enabled) and are delivered
 * on the decoder thread, with no main-thread hop and no gating: the sink must
 * return quickly and must not keep a reference to `data` beyond the call.
 *
 * The tap is attached by [UVCCameraView] as soon as the camera reports OPENED
 * and detached on close, so it keeps receiving frames whether or not the
 * Flutter side has called `captureStreamStart`.
 */
object UvcRawFrameTap {
    fun interface Sink {
        fun onFrame(data: ByteArray, width: Int, height: Int, format: String)
    }

    @Volatile
    var sink: Sink? = null

    /** Returns true when a sink consumed the frame. */
    fun deliver(data: ByteArray, width: Int, height: Int, format: String): Boolean {
        val target = sink ?: return false
        target.onFrame(data, width, height, format)
        return true
    }
}
