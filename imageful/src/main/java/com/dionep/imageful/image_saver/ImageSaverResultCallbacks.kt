package com.dionep.imageful.image_saver

interface ImageSaverResultCallbacks {
    fun savedSuccess() {}
    fun savedFailure() {}
    fun onPermissionFailure(throwable: Throwable?) {}
}