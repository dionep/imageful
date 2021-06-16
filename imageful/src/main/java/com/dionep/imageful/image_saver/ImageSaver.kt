package com.dionep.imageful.image_saver

import android.Manifest
import android.app.AlertDialog
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
import com.dionep.imageful.openAppSettings
import java.util.*

class ImageSaver : DialogFragment() {

    private val imageUrl by lazy { arguments?.getString(ARG_IMAGE_URL, "") }
    private val explainingMessageToUser: String by lazy { arguments?.getString(ARG_EXPLAINING_MESSAGE) ?: "Allow access to the device memory to save the image" }
    private val forbidBtnText: String by lazy { arguments?.getString(ARG_FORBID_BTN_TEXT) ?: "Forbid" }
    private val allowBtnText: String by lazy { arguments?.getString(ARG_ALLOW_BTN_TEXT) ?: "Allow" }

    private val resultCallback: ImageSaverResultCallback?
        get() = (parentFragment as? ImageSaverResultCallback) ?: (activity as? ImageSaverResultCallback)

    private lateinit var galleryPermissionLauncher: ActivityResultLauncher<String>
    private val galleryPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private var isSettingsOpened = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerActivityResults()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        launchPermissionLauncher()
        return super.onCreateDialog(savedInstanceState)
    }

    private fun launchPermissionLauncher(isShowRequestPermissionNotRationale: Boolean? = null) {
        when(isShowRequestPermissionNotRationale) {
            true -> openAppSettings {
                resultCallback?.onPermissionFailure(it)
            }
            else -> galleryPermissionLauncher.launch(galleryPermission)
        }
    }

    private fun registerActivityResults() {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            if (permission)
                uploadAndSaveImage()
            else
                showPermissionsExplainDialog(
                    !shouldShowRequestPermissionRationale(galleryPermission)
                )
        }.apply { galleryPermissionLauncher = this }
    }

    private fun uploadAndSaveImage() {
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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                            else
                                put(MediaStore.MediaColumns.DATA, "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName")
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
                        resultCallback?.savedSuccess()
                        dismiss()
                    }
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        resultCallback?.savedFailure()
                        dismiss()
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {}
                }
            )
    }

    private fun showPermissionsExplainDialog(
        isShowRequestPermissionNotRationale: Boolean
    ) {
        with(AlertDialog.Builder(requireContext())) {
            setMessage(explainingMessageToUser)
            setPositiveButton(allowBtnText) { _, _ ->
                launchPermissionLauncher(isShowRequestPermissionNotRationale)
            }
            setNegativeButton(forbidBtnText) { _, _ ->
                this@ImageSaver.dismiss()
            }
        }.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isSettingsOpened = true
    }

    override fun onResume() {
        super.onResume()
        if (isSettingsOpened) {
            launchPermissionLauncher()
            isSettingsOpened = false
        }
    }

    companion object {

        fun create(
            imageUrl: String,
            explainingMessageToUser: String? = null,
            allowBtnText: String? = null,
            forbidBtnText: String? = null
        ) = ImageSaver().apply {
            arguments = bundleOf(
                ARG_IMAGE_URL to imageUrl,
                ARG_EXPLAINING_MESSAGE to explainingMessageToUser,
                ARG_ALLOW_BTN_TEXT to allowBtnText,
                ARG_FORBID_BTN_TEXT to forbidBtnText
            )
        }

        private const val ARG_IMAGE_URL = "arg_image_url"
        private const val ARG_EXPLAINING_MESSAGE = "arg_explaining_message"
        private const val ARG_ALLOW_BTN_TEXT = "arg_allow_btn_text"
        private const val ARG_FORBID_BTN_TEXT = "arg_forbid_btn_text"
    }

}