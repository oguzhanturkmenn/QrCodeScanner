package com.oguzhanturkmen.myqrcodescanner.views

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.oguzhanturkmen.myqrcodescanner.R
import com.oguzhanturkmen.myqrcodescanner.util.ImageAnalyzer

import kotlinx.android.synthetic.main.activity_scanner.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerActivity : AppCompatActivity()  {

    private lateinit var cameraProviderFuture:ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor:ExecutorService
    private lateinit var analyzer:ImageAnalyzer

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)


       init()

    }




    fun init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else{
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        //set analyzer
        analyzer = ImageAnalyzer(supportFragmentManager)
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
         val cameraProvider  = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        },ContextCompat.getMainExecutor(this))

    }


    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {

        val preview :Preview = Preview.Builder()
            .build()

        val cameraSelector:CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280,720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(cameraExecutor,analyzer)
        cameraProvider?.bindToLifecycle(this as LifecycleOwner,
            cameraSelector,imageAnalysis,preview)

    }

}