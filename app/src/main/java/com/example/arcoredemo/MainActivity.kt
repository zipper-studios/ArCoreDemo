package com.example.arcoredemo

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        place_object.setOnClickListener { onButtonClicked() }
    }

    private fun onButtonClicked() {
        val perms = arrayOf(Manifest.permission.CAMERA)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.camera_permission_rationale),
                CAMERA_REQUEST,
                *perms
            )
            return
        }
        startSimpleArActivity()
    }

    @AfterPermissionGranted(CAMERA_REQUEST)
    private fun startSimpleArActivity() {
        startActivity(Intent(this, ArCoreActivity::class.java))    }

    companion object {
        const val CAMERA_REQUEST = 201
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}
