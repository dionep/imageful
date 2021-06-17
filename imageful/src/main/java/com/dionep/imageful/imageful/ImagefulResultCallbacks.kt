package com.dionep.imageful.imageful

import android.net.Uri

interface ImagefulResultCallbacks {
    fun onImagesReceived(uris: List<Uri>) {}
    fun onImageReceived(uri: Uri) {}
    fun onPermissionFailure(throwable: Throwable?) {}
}