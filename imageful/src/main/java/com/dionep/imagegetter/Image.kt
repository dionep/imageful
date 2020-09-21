package com.dionep.imagegetter

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Damir Rakhimulin on 21.09.2020.
 * damirpq1@gmail.com
 * tg: dima2828
 */
sealed class Image {
    @Parcelize
    data class Local(val uri: Uri) : Image(), Parcelable

    @Parcelize
    data class Server(val url: String) : Image(), Parcelable
}