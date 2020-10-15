package com.dionep.imageful

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.util.*

class ImageSaver : DialogFragment() {

    private var permissionsFailureCallback: () -> Unit = {}
    private var saveSuccess: () -> Unit = {}
    private var saveFailure: () -> Unit = {}
    private val imageUrl by lazy { arguments?.getString(ARG_IMAGE_URL, "") }

    private lateinit var galleryPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private val galleryPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerActivityResults()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        galleryPermissionsLauncher.launch(galleryPermissions)
        return super.onCreateDialog(savedInstanceState)
    }

    private fun registerActivityResults() {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value == true }) {
                Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .into(
                        object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                val resolver = requireContext().contentResolver
                                val displayName = "${UUID.randomUUID()}.jpg"
                                val contentValues = ContentValues().apply {
                                    put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                                    } else {
                                        put(MediaStore.MediaColumns.DATA, "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName")
                                    }
                                }
                                val uri = resolver.insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    contentValues
                                )
                                resolver.openOutputStream(uri!!).use {
                                    resource.compress(Bitmap.CompressFormat.JPEG, 90, it)
                                    it?.flush()
                                    it?.close()
                                }
                                saveSuccess.invoke()
                                dismiss()
                            }
                            override fun onLoadFailed(errorDrawable: Drawable?) {
                                saveFailure.invoke()
                                dismiss()
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {}
                        }
                    )
            } else {
                permissionsFailureCallback.invoke()
                dismiss()
            }
        }.apply { galleryPermissionsLauncher = this }
    }

    companion object {
        fun create(
            imageUrl: String,
            permissionsFailureCallback: () -> Unit = {},
            saveSuccess: () -> Unit = {},
            saveFailure: () -> Unit = {}
        ) = ImageSaver().apply {
            arguments = bundleOf(ARG_IMAGE_URL to imageUrl)
            this.permissionsFailureCallback = permissionsFailureCallback
            this.saveSuccess = saveSuccess
            this.saveFailure = saveFailure
        }

        private const val ARG_IMAGE_URL = "image_url"
    }

}