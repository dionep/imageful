package com.dionep.imageful.imageful

import android.Manifest
import android.app.AlertDialog
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
import com.dionep.imageful.openAppSettings
import kotlinx.android.parcel.Parcelize
import java.util.*

class Imageful: DialogFragment() {

    private val resultCallback: ImagefulResultCallback?
        get() = (parentFragment as? ImagefulResultCallback) ?: (activity as? ImagefulResultCallback)

    private val inputType: InputType? by lazy { arguments?.getParcelable(ARG_INPUT_TYPE) }
    private val explainingMessageToUser: String by lazy { arguments?.getString(ARG_EXPLAINING_MESSAGE) ?: "Allow access to the device memory to get the image" }
    private val forbidBtnText: String by lazy { arguments?.getString(ARG_FORBID_BTN_TEXT) ?: "Forbid" }
    private val allowBtnText: String by lazy { arguments?.getString(ARG_ALLOW_BTN_TEXT) ?: "Allow" }

    private lateinit var cameraPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var galleryPermissionsLauncher: ActivityResultLauncher<String>
    private lateinit var cameraContractLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryContractLauncher: ActivityResultLauncher<String>

    private val galleryImageUri by lazy { createGalleryImageUri() }

    private val cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val galleryPermission = Manifest.permission.READ_EXTERNAL_STORAGE
    private var isSettingsOpened = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerActivityResults()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        launchPermissionsLauncher()
        return super.onCreateDialog(savedInstanceState)
    }

    private fun launchPermissionsLauncher(isShowRequestPermissionNotRationale: Boolean? = null) {
        when(isShowRequestPermissionNotRationale) {
            true -> {
                openAppSettings {
                    resultCallback?.onPermissionFailure(it)
                }
            }
            else -> {
                when (inputType) {
                    InputType.CAMERA -> cameraPermissionsLauncher.launch(cameraPermissions)
                    else -> galleryPermissionsLauncher.launch(galleryPermission)
                }
            }
        }
    }

    private fun registerActivityResults() {
        when (inputType) {
            InputType.CAMERA -> {
                // permissions
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    if (permissions.all { it.value == true })
                        cameraContractLauncher.launch(galleryImageUri)
                    else
                        showPermissionsExplainDialog(permissions.all { !shouldShowRequestPermissionRationale(it.key) })
                }.apply { cameraPermissionsLauncher = this }
                // contract
                registerForActivityResult(ActivityResultContracts.TakePicture()) {
                    if (it && galleryImageUri != null)
                        resultCallback?.onImageReceived(uri = galleryImageUri!!)
                    dismiss()
                }.apply { cameraContractLauncher = this }
            }
            else -> {
                // permissions
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionAllowed ->
                    if (permissionAllowed)
                        galleryContractLauncher.launch(GALLERY_INPUT_TYPE)
                    else
                        showPermissionsExplainDialog(!shouldShowRequestPermissionRationale(galleryPermission))
                }.apply { galleryPermissionsLauncher = this }
                // contracts
                when (inputType) {
                    InputType.GALLERY_MULTIPLE -> {
                        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri?> ->
                            uris.filterNotNull()
                                .apply {
                                    if (!isNullOrEmpty()) {
                                        resultCallback?.onImagesReceived(uris = this)
                                    }
                                    dismiss()
                                }
                        }.apply { galleryContractLauncher = this }
                    }
                    InputType.GALLERY_SINGLE -> {
                        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                            if (uri != null)
                                resultCallback?.onImageReceived(uri)
                            dismiss()
                        }.apply { galleryContractLauncher = this }
                    }
                }
            }
        }
    }

    private fun showPermissionsExplainDialog(
        isShowRequestPermissionNotRationale: Boolean
    ) {
        with(AlertDialog.Builder(requireContext())) {
            setMessage(explainingMessageToUser)
            setPositiveButton(allowBtnText) { _, _ ->
                launchPermissionsLauncher(isShowRequestPermissionNotRationale)
            }
            setNegativeButton(forbidBtnText) { _, _ ->
                this@Imageful.dismiss()
            }
        }.show()
    }

    private fun createGalleryImageUri() =
        requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, "${UUID.randomUUID()}.jpg")
            }
        )

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isSettingsOpened = true
    }

    override fun onResume() {
        super.onResume()
        if (isSettingsOpened) {
            launchPermissionsLauncher()
            isSettingsOpened = false
        }
    }

    companion object {
        fun create(
            inputType: InputType,
            explainingMessageToUser: String? = null,
            allowBtnText: String? = null,
            forbidBtnText: String? = null
        ) = Imageful().apply {
            arguments = bundleOf(
                ARG_INPUT_TYPE to inputType,
                ARG_EXPLAINING_MESSAGE to explainingMessageToUser,
                ARG_FORBID_BTN_TEXT to forbidBtnText,
                ARG_ALLOW_BTN_TEXT to allowBtnText
            )
        }

        private const val GALLERY_INPUT_TYPE = "image/*"
        private const val ARG_INPUT_TYPE = "arg_input_type"
        private const val ARG_EXPLAINING_MESSAGE = "arg_explaining_message"
        private const val ARG_ALLOW_BTN_TEXT = "arg_allow_btn_text"
        private const val ARG_FORBID_BTN_TEXT = "arg_forbid_btn_text"
    }

    @Parcelize
    enum class InputType : Parcelable {
        CAMERA, GALLERY_SINGLE, GALLERY_MULTIPLE
    }

}