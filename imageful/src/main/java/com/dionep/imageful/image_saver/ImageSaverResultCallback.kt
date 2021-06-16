package com.dionep.imageful.image_saver

interface ImageSaverResultCallback {
    fun savedSuccess() {}
    fun savedFailure() {}
    fun onPermissionFailure(throwable: Throwable?) {}
}