package com.dionep.imageful.listeners

import android.net.Uri

interface ResultCallback {
    fun success(uris: List<Uri>) {}
    fun success(uri: Uri) {}
    fun failure() {}
}
