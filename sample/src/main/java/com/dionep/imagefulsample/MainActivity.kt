package com.dionep.imagefulsample

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dionep.imageful.ImageSaver
import com.dionep.imageful.Imageful
import com.dionep.imageful.listeners.ResultCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ResultCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_save_to_gallery.setOnClickListener {
            ImageSaver.create(
                imageUrl = "SOME URL",
                permissionsFailureCallback = {
                    Toast.makeText(applicationContext, "Permissions failure", Toast.LENGTH_LONG).show()
                },
                saveSuccess = {
                    Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
                },
                saveFailure = {
                    Toast.makeText(applicationContext, "Fail", Toast.LENGTH_LONG).show()
                }
            ).show(supportFragmentManager, null)
        }
        btn_from_camera.setOnClickListener {
            Imageful.create(
                inputType = Imageful.InputType.CAMERA
            ).show(supportFragmentManager, null)
        }
        btn_from_gallery_single.setOnClickListener {
            Imageful.create(
                inputType = Imageful.InputType.GALLERY_SINGLE
            ).show(supportFragmentManager, null)
        }
        btn_from_gallery_multi.setOnClickListener {
            Imageful.create(
                inputType = Imageful.InputType.GALLERY_MULTIPLE
            ).show(supportFragmentManager, null)
        }

    }

    private fun onImageGot(image: Uri) {
        iv_image.setImageURI(image)
        Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
    }

    override fun success(uri: Uri) {
        Log.d("rere", uri.toString())
        onImageGot(uri)
    }
}