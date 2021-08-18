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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.movierama.databinding.ActivityMainBinding
import com.example.movierama.ui.details.DetailsFragment
import com.example.movierama.ui.popular.PopularFragment
import com.example.movierama.viewmodels.SharedViewModel
import com.google.android.material.card.MaterialCardView
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private var searchcontainerOpened = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewModel: SharedViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(SharedViewModel::class.java)
    }

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        setStatusBarColor()

        setUpViews()
        observeViewmodel()
    }

    private fun observeViewmodel() {
        //this is triggered and shows the loader until the main api call finish. When it does, it hides it
        viewModel.showLoader.observe(this, Observer {
            it?.let {
                if (it)
                    showGenericLoader()
                else
                    hideGenericLoader()
            }
        })
    }

    private fun setUpViews() {
        binding.searchHereEdittext.doOnTextChanged { text, _, _, _ ->
            val currentFragment = getCurrentFragment()
            if (currentFragment is PopularFragment)
                currentFragment.updateSearch(text.toString())
        }

        //when clicking the search icon either i show the search inputfield or i hide it
        binding.searchImg.setOnClickListener {
            if (getCurrentFragment() is PopularFragment) {
                if (searchcontainerOpened)
                    moveMainContainer(ContainerDirection.Up)
                else
                    moveMainContainer(ContainerDirection.Down)
            }

        }

        binding.backBtn.setOnClickListener {
            val currentFragment = getCurrentFragment()
            if (currentFragment is DetailsFragment)  //if i am currently in DetailsFragment then i can use its functions
                currentFragment.goBack()
        }

        val navHostFragment: NavHostFragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?

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
                        moveMainContainer(ContainerDirection.Up)
                    }
                    else -> binding.appBarTxt.text = getString(R.string.most_popular)
                }

            }
        }
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.fragments?.last()
    }

    private fun showGenericLoader() {
        val view: View = LayoutInflater.from(this).inflate(R.layout.generic_loader_layout, null)
        runOnUiThread {
            binding.loaderFrameLayout?.let { cLayout ->
                cLayout.addView(view, 0)
            }
        }
    }

    private fun hideGenericLoader() {
        runOnUiThread {
            binding.loaderFrameLayout.removeAllViews()
        }
    }


    fun showBanner(value: String, success: Boolean = false) {
        val view: View = LayoutInflater.from(this).inflate(R.layout.banner_layout, null)

        runOnUiThread {

            binding.frameLayout.let { cLayout -> // i add in the frameLayout of Mainactivity the bannerLayout and after 3 seconds i remove it
                cLayout.removeAllViews()
                cLayout.addView(view, 0)
                cLayout.bringToFront()


                val BannerTxtV = cLayout.findViewById<TextView>(R.id.BannerTxtV)
                val cardView = cLayout.findViewById<MaterialCardView>(R.id.cardView)
                val imageView = cLayout.findViewById<ImageView>(R.id.imageView)

                BannerTxtV.text = value

                if (!success) {
                    cardView.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_red_light)
                    imageView.background = ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24)

                } else {
                    cardView.backgroundTintList = ContextCompat.getColorStateList(this, R.color.teal_200)
                    imageView.background = ContextCompat.getDrawable(this, R.drawable.tick_icon)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    cLayout.removeView(view)
                }, 3000)
            }
        }
    }

    private fun moveMainContainer(s: ContainerDirection) {
        if (s == ContainerDirection.Down) {

            Handler(Looper.getMainLooper()).postDelayed({
                binding.textInputLayout.animate().alpha(1.0f)
            }, 500)


            ObjectAnimator.ofFloat(binding.navHostFragment, "translationY", binding.textInputLayout.y + 8f).apply {
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

    //this State listener is must because when the animation is happening i don't want to click the search btn, so i disable it and re-enable it
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.toolbar_color)
    }

    enum class ContainerDirection {
        Up,
        Down
    }
}