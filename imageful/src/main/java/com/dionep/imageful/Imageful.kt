package com.dionep.imageful

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by Damir Rakhimulin on 21.09.2020.
 * damirpq1@gmail.com
 * tg: dima2828
 */

class Imageful : DialogFragment() {

    private val inputType: InputType? by lazy { arguments?.getParcelable(ARG_INPUT_TYPE) }
    private var imagesGotCallback: (List<Image.Local>) -> Unit = {}
    private var permissionsFailureCallback: () -> Unit = {}

    private lateinit var cameraPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var galleryPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var cameraContractLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryContractLauncher: ActivityResultLauncher<String>

    private val galleryImageUri by lazy { createGalleryImageUri() }

    private val cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val galleryPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerActivityResults()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        when (inputType) {
            InputType.CAMERA -> cameraPermissionsLauncher.launch(cameraPermissions)
            else -> galleryPermissionsLauncher.launch(galleryPermissions)
        }
        return super.onCreateDialog(savedInstanceState)
    }

    private fun registerActivityResults() {
        when (inputType) {
            InputType.CAMERA -> {
                // permissions
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    if (permissions.all { it.value == true })
                        cameraContractLauncher.launch(galleryImageUri)
                    else {
                        permissionsFailureCallback.invoke()
                        dismiss()
                    }
                }.apply { cameraPermissionsLauncher = this }
                // contract
                registerForActivityResult(ActivityResultContracts.TakePicture()) {
                    if (it && galleryImageUri != null)
                        imagesGotCallback.invoke(
                            listOf(Image.Local(galleryImageUri!!))
                        )
                    dismiss()
                }.apply { cameraContractLauncher = this }
            }
            else -> {
                // permissions
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    if (permissions.all { it.value == true })
                        galleryContractLauncher.launch(GALLERY_INPUT_TYPE)
                    else {
                        permissionsFailureCallback.invoke()
                        dismiss()
                    }
                }.apply { galleryPermissionsLauncher = this }
                // contracts
                when (inputType) {
                    InputType.GALLERY_MULTIPLE -> {
                        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri?> ->
                            uris.filterNotNull().map { uri ->
                                Image.Local(uri)
                            }.apply {
                                if (!isNullOrEmpty())
                                    imagesGotCallback.invoke(this)
                                dismiss()
                            }
                        }.apply { galleryContractLauncher = this }
                    }
                    InputType.GALLERY_SINGLE -> {
                        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                            if (uri != null)
                                imagesGotCallback.invoke(
                                    listOf(Image.Local(uri))
                                )
                            dismiss()
                        }.apply { galleryContractLauncher = this }
                    }
                }
            }
        }
    }

    private fun createGalleryImageUri() =
        requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, "${UUID.randomUUID()}.jpg")
            }
        )

    companion object {
        fun create(
            inputType: InputType,
            imagesGotCallback: (List<Image.Local>) -> Unit = {},
            permissionsFailureCallback: () -> Unit = {}
        ) = Imageful().apply {
            this.imagesGotCallback = imagesGotCallback
            this.permissionsFailureCallback = permissionsFailureCallback
            arguments = bundleOf(ARG_INPUT_TYPE to inputType)
        }

        private const val GALLERY_INPUT_TYPE = "image/*"
        private const val ARG_INPUT_TYPE = "arg_input_type"
    }

    @Parcelize
    enum class InputType : Parcelable {
        CAMERA, GALLERY_SINGLE, GALLERY_MULTIPLE
    }

}