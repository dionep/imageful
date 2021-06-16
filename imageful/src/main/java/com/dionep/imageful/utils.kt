package com.dionep.imageful

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.DialogFragment

fun DialogFragment.openAppSettings(onFailure: (Throwable) -> Unit) {
    kotlin.runCatching {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts(
                    "package",
                    activity?.packageName,
                    null
                )
            )
        )
    }.onFailure {
        onFailure.invoke(it)
        dismiss()
    }
}