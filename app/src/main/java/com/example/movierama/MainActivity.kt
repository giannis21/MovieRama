package com.example.movierama

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.contains
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.movierama.databinding.ActivityMainBinding
import com.example.movierama.network.NetworkConnectionIncterceptor
import com.example.movierama.ui.DetailsFragment
import com.example.movierama.ui.PopularFragment
import com.example.movierama.viewmodels.SharedViewModel
import com.google.android.material.card.MaterialCardView


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: SharedViewModel
    private lateinit var viewModelFactory: ViewModelFactory
    private var searchcontainerOpened = false

    companion object {
        var internetExceptionListener: (() -> Unit)? = null
    }

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        setStatusBarColor()


        val networkConnectionIncterceptor = this.applicationContext?.let { NetworkConnectionIncterceptor(it) }
        val webService = ApiClient(networkConnectionIncterceptor!!)
        val repository = RemoteRepository(webService)
        viewModelFactory = ViewModelFactory(repository, this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SharedViewModel::class.java)

        binding.searchHereEdittext.doOnTextChanged { text, _, _, _ ->
            val currentFragment = getCurrentFragment()
            if (currentFragment is PopularFragment)
                currentFragment.updateSearch(text.toString())
        }

        binding.searchImg.setOnClickListener {
            if (getCurrentFragment() is PopularFragment) {
                if (searchcontainerOpened)
                    moveMainContainer("up")
                else
                    moveMainContainer("down")
            }

        }

        binding.backBtn.setOnClickListener {
            val currentFragment = getCurrentFragment()
            if (currentFragment is DetailsFragment)
                currentFragment.goBack()
        }


        internetExceptionListener = {
            Toast.makeText(this, "no internet", Toast.LENGTH_SHORT).show()
            showBanner("No Internet connection!")
        }
        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?

        if (navHostFragment != null) {
            val navController: NavController = navHostFragment.navController
            navController.addOnDestinationChangedListener { _, destination, _ ->

                when (destination.id) {
                    R.id.popularFragment -> {
                        binding.backBtn.visibility = View.GONE
                        binding.appBarTxt.text = getString(R.string.most_popular)
                    }
                    R.id.detailsFragment -> {
                        binding.appBarTxt.text = getString(R.string.movie_detail)
                        binding.backBtn.visibility = View.VISIBLE
                        binding.searchHereEdittext.setText("")
                        moveMainContainer("up")
                    }
                    else -> binding.appBarTxt.text = getString(R.string.most_popular)
                }

            }
        }


        viewModel.showLoader.observe(this, Observer {
            it?.let {
                if (it)
                    showGenericLoader()
                else
                    hideGenericLoader()
            }
        })
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.fragments?.last()
    }

    fun showGenericLoader() {
        val view: View = LayoutInflater.from(this).inflate(R.layout.generic_loader_layout, null)
        runOnUiThread {

            binding.loaderFrameLayout.let { cLayout ->
                cLayout.addView(view, 0)
                cLayout.bringToFront()
            }
        }
    }

    fun hideGenericLoader() {
        runOnUiThread {
            binding.loaderFrameLayout.removeAllViews()
        }
    }


fun showBanner(value: String, success: Boolean = false) {
    val view: View = LayoutInflater.from(this).inflate(R.layout.banner_layout, null)


    runOnUiThread {

        binding.frameLayout.let { cLayout ->
            cLayout.addView(view, 0)
            cLayout.bringToFront()


            val BannerTxtV = cLayout.findViewById<TextView>(R.id.BannerTxtV)
            val cardView = cLayout.findViewById<MaterialCardView>(R.id.cardView)
            val imageView = cLayout.findViewById<ImageView>(R.id.imageView)

            BannerTxtV.text = value

            if (!success) {
                cardView.backgroundTintList = ContextCompat.getColorStateList(
                    this,
                    android.R.color.holo_red_light
                )
                imageView.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_close_24
                )

            } else {
                cardView.backgroundTintList = ContextCompat.getColorStateList(
                    this,
                    R.color.teal_200
                )
                imageView.background = ContextCompat.getDrawable(this, R.drawable.tick_icon)
            }

            Handler(Looper.getMainLooper()).postDelayed({
                cLayout.removeView(view)
            }, 3000)
        }
    }
}

private fun moveMainContainer(s: String) {
    if (s == "down") {

        Handler(Looper.getMainLooper()).postDelayed({
            binding.textInputLayout.animate().alpha(1.0f)
        }, 500)


        ObjectAnimator.ofFloat(
            binding.navHostFragment,
            "translationY",
            binding.textInputLayout.y + 8f
        ).apply {
            duration = 600
            addStateListener()
            start()
            searchcontainerOpened = true
        }
        binding.searchHereEdittext.isEnabled = true

    } else {
        binding.textInputLayout.animate().alpha(0.0f)

        ObjectAnimator.ofFloat(binding.navHostFragment, "translationY", 0f).apply {
            duration = 600
            addStateListener()
            start()
            searchcontainerOpened = false
        }
        binding.searchHereEdittext.isEnabled = false
    }

}

private fun ObjectAnimator.addStateListener() {
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            binding.searchImg.isEnabled = false
        }

        override fun onAnimationEnd(animation: Animator?) {
            binding.searchImg.isEnabled = true
        }
    })
}

private fun setStatusBarColor() {
    val window: Window = this.window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = ContextCompat.getColor(this, R.color.navdrawer_color)
}


}