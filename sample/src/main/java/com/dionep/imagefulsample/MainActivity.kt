package com.dionep.imagefulsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dionep.imageful.Image
import com.dionep.imageful.Imageful
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_from_camera.setOnClickListener {
            Imageful.create(
                inputType = Imageful.InputType.CAMERA,
                imagesGotCallback = { onImageGot(it) },
                permissionsFailureCallback = {
                    Toast.makeText(applicationContext, "Permissions failure", Toast.LENGTH_LONG).show()
                }
            ).show(supportFragmentManager, null)
        }
        btn_from_gallery_single.setOnClickListener {
            Imageful.create(
                inputType = Imageful.InputType.GALLERY_SINGLE,
                imagesGotCallback = { onImageGot(it) },
                permissionsFailureCallback = {
                    Toast.makeText(applicationContext, "Permissions failure", Toast.LENGTH_LONG).show()
                }
            ).show(supportFragmentManager, null)
        }
        btn_from_gallery_multi.setOnClickListener {
            Imageful.create(
                inputType = Imageful.InputType.GALLERY_MULTIPLE,
                imagesGotCallback = { onImageGot(it) },
                permissionsFailureCallback = {
                    Toast.makeText(applicationContext, "Permissions failure", Toast.LENGTH_LONG).show()
                }
            ).show(supportFragmentManager, null)
        }

    }

    private fun onImageGot(images: List<Image.Local>) {
        iv_image.setImageURI(images.first().uri)
        Toast.makeText(applicationContext, "Received ${images.size} images", Toast.LENGTH_LONG).show()
    }

}