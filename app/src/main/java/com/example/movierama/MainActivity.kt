package com.example.movierama

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.movierama.databinding.ActivityMainBinding
import com.example.movierama.ui.PopularFragment
import com.example.movierama.viewmodels.SharedViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.banner_layout.view.*


class MainActivity : AppCompatActivity(),View.OnClickListener {

    lateinit var viewModel: SharedViewModel
    private lateinit var viewModelFactory: ViewModelFactory
    var searchcontainerOpened = false
    private lateinit var controller: NavController
    lateinit var listener:NavController.OnDestinationChangedListener
    companion object {
        var internetExceptionListener: (() -> Unit)? = null
    }

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        setStatusBarColor()
        setGlobalActionListeners()

        val networkConnectionIncterceptor = this.applicationContext?.let { NetworkConnectionIncterceptor(it) }
        val webService = ApiClient(networkConnectionIncterceptor!!)
        val repository = RemoteRepository(webService)
        viewModelFactory = ViewModelFactory(repository,this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SharedViewModel::class.java)

        searchImg.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_searchFragment)
        }

        searchHereEdittext.doOnTextChanged { text, start, before, count ->
            viewModel.searchedText.postValue(text.toString())
        }
        searchImg.setOnClickListener {
            if (searchcontainerOpened)
                moveMainContainer("up")
            else
                moveMainContainer("down")
        }

        internetExceptionListener ={
            Toast.makeText(this,"no internet",Toast.LENGTH_SHORT).show()
            showBanner("No Internet connection!")
        }
//        controller = Navigation.findNavController(this, R.id.nav_host_fragment)

//        listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
//            when (destination.id) {
//                R.id.popularFragment -> appBarTxt.text="Most Popular"
//                R.id.detailsFragment -> appBarTxt.text="Favorite Movies"
//                else -> appBarTxt.text="Search Movie"
//            }
//        }


    }

    fun showBanner(value: String, success: Boolean = false) {
        val view: View = LayoutInflater.from(this).inflate(R.layout.banner_layout, null)

        runOnUiThread {
            frameLayout?.let { cLayout ->
                cLayout.addView(view, 0)
                cLayout.bringToFront()
                cLayout.BannerTxtV.text = value

                if(!success){
                    cLayout.cardView.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_red_light)
                    cLayout.imageView.background = ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24)

                }else{
                    cLayout.cardView.backgroundTintList = ContextCompat.getColorStateList(this, R.color.teal_200)
                    cLayout.imageView.background = ContextCompat.getDrawable(this, R.drawable.tick_icon)
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
                text_input_layout.visibility=View.VISIBLE
                text_input_layout.animate().alpha(1.0f)
            }, 500)


            ObjectAnimator.ofFloat(nav_host_fragment, "translationY",  text_input_layout.y+8f).apply {
                duration = 600
                addStateListener()
                start()
                searchcontainerOpened = true
            }
            searchHereEdittext.isEnabled = true

        } else {
            text_input_layout.animate().alpha(0.0f)
            text_input_layout.visibility=View.GONE
            ObjectAnimator.ofFloat(nav_host_fragment, "translationY",  0f).apply {
                duration = 600
                addStateListener()
                start()
                searchcontainerOpened = false
            }
            searchHereEdittext.isEnabled = false
        }

    }
    private fun ObjectAnimator.addStateListener() {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                searchImg.isEnabled = false

            }

            override fun onAnimationEnd(animation: Animator?) {
                searchImg.isEnabled = true

            }
        })
    }
    private fun setGlobalActionListeners() {
        searchImg.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_searchFragment)
        }
        motionLayoutId.findViewById<TextView>(R.id.popularTxt).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_popularFragment)
        }
        motionLayoutId.findViewById<ImageView>(R.id.popularIcon).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_popularFragment)
        }
        motionLayoutId.findViewById<ImageView>(R.id.favIcon).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_favoritesFragment)
        }
        motionLayoutId.findViewById<TextView>(R.id.favTxt).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_favoritesFragment)
        }
//        motionLayoutId.findViewById<ImageView>(R.id.TopRatedicon).setOnClickListener {
//            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_Top_Rated)
//        }
//        motionLayoutId.findViewById<TextView>(R.id.TopRatedTxt).setOnClickListener {
//            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_Top_Rated)
//        }
//        motionLayoutId.findViewById<ImageView>(R.id.watchlistIicon).setOnClickListener {
//            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_watchlist)
//        }
//        motionLayoutId.findViewById<TextView>(R.id.watchlisttxt).setOnClickListener {
//            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_watchlist)
//        }
//        motionLayoutId.findViewById<ImageView>(R.id.favoritesIcon).setOnClickListener {
//            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_favorites)
//        }
//        motionLayoutId.findViewById<TextView>(R.id.favoritestxt).setOnClickListener {
//            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_favorites)
//        }
//        motionLayoutId.findViewById<ImageView>(R.id.seenIcon).setOnClickListener {
//            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_seen)
//        }
//        motionLayoutId.findViewById<TextView>(R.id.seentxt).setOnClickListener {
//            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_seen)
//        }
//        motionLayoutId.findViewById<ImageView>(R.id.settingsIcon).setOnClickListener {
//            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_settings)
//        }
//        motionLayoutId.findViewById<TextView>(R.id.settingstxt).setOnClickListener {
//            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_settings)
//        }
//        motionLayoutId.findViewById<ImageView>(R.id.closeIcon).setOnClickListener {
//            if(isFirstTime(this)){
//                createBaloon()
//                set_FirstTime(false,this)
//            }
//
//            topIconVisible = true
//            motionLayoutId.visibility = View.GONE
//            invalidateOptionsMenu() //ξανα καλειται η onPrepareOptionsMenu
//
//            val id= motionLayoutId.findViewById<MotionLayout>(R.id.motionLayoutId)
//            id?.transitionToStart()
//
//        }
    }

//    override fun onResume() {
//        super.onResume()
//        controller.addOnDestinationChangedListener(listener)
//    }
//
//    override fun onPause() {
//        controller.removeOnDestinationChangedListener(listener)
//        super.onPause()
//    }
    private fun setStatusBarColor() {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}