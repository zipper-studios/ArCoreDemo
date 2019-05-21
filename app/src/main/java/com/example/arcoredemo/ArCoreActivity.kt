package com.example.arcoredemo

import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.Toast
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode


class ArCoreActivity : AppCompatActivity() {

    private var modelRenderable: ModelRenderable? = null
    private val TAG: String = ArCoreActivity::class.java.simpleName

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_arcore)

        if (checkDeviceCompatibility()) {
            addARContent()
        }

    }

    private fun checkDeviceCompatibility(): Boolean {
        // Check SDK version
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Toast.makeText(this, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show()
            finish()
            return false
        }

        // Get OpenGl version
        val openGlVersion = (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion

        // Check if OpenGl version is minimum 3.0
        if (java.lang.Double.parseDouble(openGlVersion) < 3.0) {
            Toast.makeText(this, getString(R.string.opengl_version_required), Toast.LENGTH_LONG)
                .show()
            finish()
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun addARContent() {
//        Build the model using ModelRenderable class in an asynchronously way.
//              -  setSource() method will load the model from .sfb file
//              -  thenAccept() method will receive the model once it is build and modelRenedable variable will contain that object
//              -  exceptionally() method will be called in case the model will not be created

        ModelRenderable.builder()
            .setSource(
                this,
                Uri.parse("Wolf_One_obj.sfb")
            )
            .build()
            .thenAccept { renderable -> modelRenderable = renderable }
            .exceptionally {
                Toast.makeText(this, "Unable to load object!", Toast.LENGTH_LONG).show()
                null
            }
        val mARFragment = supportFragmentManager.findFragmentById(R.id.sceneform) as ArFragment?

//        Now, it's time to add the model to scene.
//        Set onTapArPlaneListener on arFragment
        mARFragment?.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            if (modelRenderable != null) {
                // Create the Anchor HitResult
                val anchor = hitResult.createAnchor()
//                Create a node out of this anchor called AnchorNode . This node will be attached to scene by calling the
//                setParent method on it and passing the scene from the fragment
                val mAnchorNode = AnchorNode(anchor)
                mAnchorNode.setParent(mARFragment.arSceneView.scene)

                // Create the transformable object and add it to the anchor.
                val mARObject = TransformableNode(mARFragment.transformationSystem)
                mARObject.setParent(mAnchorNode)
//                set the renderable object
                mARObject.renderable = modelRenderable
                mARObject.select()
            }
        }

    }
}