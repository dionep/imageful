package com.dionep.imagefulsample

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dionep.imageful.image_saver.ImageSaver
import com.dionep.imageful.image_saver.ImageSaverResultCallbacks
import com.dionep.imageful.imageful.Imageful
import com.dionep.imageful.imageful.ImagefulResultCallbacks
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ImagefulResultCallbacks, ImageSaverResultCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_save_to_gallery.setOnClickListener {
            ImageSaver.create(
                imageUrl = "https://picsum.photos/200/300",
                explainingMessageToUser = "Allow access to device memory to save image",
                allowBtnText = "Allow",
                forbidBtnText = "Forbid"
            ).show(supportFragmentManager, null)
        }
        btn_from_camera.setOnClickListener {
            Imageful.create(
                Imageful.InputType.CAMERA
            ).show(supportFragmentManager, null)
        }
        btn_from_gallery_single.setOnClickListener {
            Imageful.create(
                inputType = Imageful.InputType.GALLERY_SINGLE,
                permissionsRequiredExplainingMessageToUser = "Allow access to device memory to upload photos",
                allowBtnText = "Allow",
                forbidBtnText = "Forbid"
            ).show(supportFragmentManager, null)
        }
        btn_from_gallery_multi.setOnClickListener {
            Imageful.create(
                Imageful.InputType.GALLERY_MULTIPLE
            ).show(supportFragmentManager, null)
        }

    }

    private fun onImageGot(image: Uri?) {
        image?.let {
            iv_image.setImageURI(it)
            Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
        }
    }

    override fun onImageReceived(uri: Uri) {
        onImageGot(uri)
    }

    override fun onImagesReceived(uris: List<Uri>) {
        onImageGot(uris.firstOrNull())
    }

    override fun onPermissionFailure(throwable: Throwable?) {
        Toast.makeText(applicationContext, "Failure", Toast.LENGTH_SHORT).show()
    }

    override fun savedSuccess() {
        Toast.makeText(applicationContext, "Saved successfully", Toast.LENGTH_SHORT).show()
    }

    override fun savedFailure() {
        Toast.makeText(applicationContext, "Saved with failure", Toast.LENGTH_SHORT).show()
    }

}