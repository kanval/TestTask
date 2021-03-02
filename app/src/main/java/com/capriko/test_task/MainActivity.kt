package com.capriko.test_task

import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), RequestListener<Drawable> {

    //Views
    private lateinit var imageView: ImageView
    private lateinit var btnClickMe: Button
    private val URL: String = "https://picsum.photos/1920/1080"
    private var isConnected = false

    //ViewModel
    lateinit var viewModel: ConectionViewModel
    private var cm: ConnectivityManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        btnClickMe = findViewById(R.id.btnClickMe)

        viewModel = ViewModelProvider(this).get(ConectionViewModel::class.java)

        checkNetworkState()

        //Observer for listening network state
        viewModel.listenNetWorkState.observe(this, Observer {
            isConnected = it
            Log.d("NetworkState", "" + it)
        })


        //Force Glide to load image from cache
        imageView.loadImage(URL, true, this)

        btnClickMe.setOnClickListener {
            if (isConnected) {
                lifecycleScope.launch(Dispatchers.IO) {
                    //Clearing glide cache before loading new image. This method needs to be executed in background thread
                    Glide.get(this@MainActivity)
                        .clearDiskCache()
                    lifecycleScope.launch(Dispatchers.Main) {
                        Glide.get(it.context.applicationContext)
                            .clearMemory()
                        imageView.loadImage(URL, false, this@MainActivity)

                    }
                }

            } else {
                val message = getString(R.string.message)
                val parentLayout = findViewById<View>(android.R.id.content)
                Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG)
                    .setAction("CLOSE") { }
                    .setActionTextColor(getColor(android.R.color.holo_red_light))
                    .show()
            }
        }

    }


    private fun checkNetworkState() {
        if (cm == null) {
            cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        val builder: NetworkRequest.Builder = NetworkRequest.Builder()
        cm!!.registerNetworkCallback(builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    viewModel.setNetworkState(true)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    viewModel.setNetworkState(false)
                }

            })

    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            cm?.unregisterNetworkCallback(ConnectivityManager.NetworkCallback())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        return false
    }
}