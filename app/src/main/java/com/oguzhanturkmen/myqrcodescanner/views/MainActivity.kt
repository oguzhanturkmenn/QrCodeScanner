package com.oguzhanturkmen.myqrcodescanner.views

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.oguzhanturkmen.myqrcodescanner.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_scanner.*


class MainActivity : AppCompatActivity() {

    private val REQUEST_CAMERA_PERMISSION = 101


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkForPermission()


    }
    private fun checkForPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            goToScan()
        } else {
            requestPermission()
        }
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA_PERMISSION
        )
    }

    private fun goToScan() {
        startActivity(Intent(this, ScannerActivity::class.java))

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goToScan()
            } else if (isPermanentlyDenied()) {
                showGoToAppSettingsDialog()
            } else
                requestPermission()
        }
    }

    private fun isPermanentlyDenied(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA).not()
        } else {
            return false
        }
    }


    private fun showGoToAppSettingsDialog() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle(getString(R.string.grant_permissions))
            .setMessage(getString(R.string.we_need_permission))
            .setPositiveButton(getString(R.string.grant)) { _, _ ->
                goToAppSettings()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                run {
                    finish()
                }
            }.show()
    }

    private fun goToAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onRestart() {
        super.onRestart()
        checkForPermission()
    }


}