package com.dionep.imagefulsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dionep.getimagefragment.Image
import com.dionep.imageful.ImagesGetter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_from_camera.setOnClickListener {
            ImagesGetter.create(
                inputType = ImagesGetter.InputType.CAMERA,
                imagesGotCallback = { onImageGot(it) }
            ).show(supportFragmentManager, null)
        }
        btn_from_gallery_single.setOnClickListener {
            ImagesGetter.create(
                inputType = ImagesGetter.InputType.GALLERY_SINGLE,
                imagesGotCallback = { onImageGot(it) }
            ).show(supportFragmentManager, null)
        }
        btn_from_gallery_multi.setOnClickListener {
            ImagesGetter.create(
                inputType = ImagesGetter.InputType.GALLERY_MULTIPLE,
                imagesGotCallback = { onImageGot(it) }
            ).show(supportFragmentManager, null)
        }

    }

    private fun onImageGot(images: List<Image.Local>) {
        iv_image.setImageURI(images.first().uri)
        Toast.makeText(applicationContext, "Received ${images.size} images", Toast.LENGTH_LONG).show()
    }

}