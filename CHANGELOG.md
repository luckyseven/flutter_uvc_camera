## 0.0.5

* Android: preview frames (`onPreviewData`) are delivered as raw NV21 at the
  capture resolution instead of an RGBA read-back of the OpenGL surface. Payload
  per frame drops from 7-10 MB to 1.4 MB and the buffer is no longer shared
  with the renderer. Consumers must honour the `format` field.
* Android: frames are dropped when two are already queued on the main thread
  (`PreviewFrameGate`). Previously an unbounded queue could exhaust the Java
  heap (`OutOfMemoryError` in `StandardMessageCodec`).
* Android: `captureStreamStart()` is idempotent; repeated calls no longer leak
  a native callback and deliver every frame twice. `closeCamera()` and view
  disposal stop the stream.


## 0.0.4

- feat take video
- feat captureStream callback

## 0.0.3

- feat getAllPreviewSizes updateResolution 
- feat set Camera Preview Parameters
- fix some issues

## 0.0.2

- update documentation

## 0.0.1

- Initial release
