package com.example.movierama.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.movierama.MainActivity
import com.example.movierama.R
import com.example.movierama.data.movie.Movies
import com.example.movierama.databinding.FragmentDetailsBinding
import com.example.movierama.viewmodels.SharedViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment : Fragment() {

    lateinit var viewModel:SharedViewModel
    lateinit var binding: FragmentDetailsBinding
    val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel=(activity as MainActivity).viewModel

        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        binding.viewModel=viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.getMovieDetails(args.id)
        observeViewmodel()

        binding.favorite.setOnClickListener {
            viewModel.addFavorite(args.id)
        }
        val layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 700)
        binding.NestedScrollView.layoutParams = layoutParams
        binding.reviewsTitle.setOnClickListener {
            if(binding.NestedScrollView.isGone){
                binding.NestedScrollView.visibility=View.VISIBLE
                binding.reviewsTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_bottom_details, 0)
                binding.ScrollView.post(Runnable { binding.ScrollView.fullScroll(ScrollView.FOCUS_DOWN) })
                binding.reviewsTitle.text= "hide reviews"
            }else{
                binding.NestedScrollView.visibility=View.GONE
                binding.reviewsTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_details, 0)
                binding.reviewsTitle.text= "show reviews"
            }
        }



    }



    fun observeViewmodel() {
        viewModel.currentDetailObj.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.movieDetails=it
            }
        })

        viewModel.currentReviewsObj.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.reviews=it
            }
        })

        viewModel.currentSimilarObj.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.movies=it
            }
        })
    }
    fun goBack(){
        findNavController().navigateUp()
    }

//    fun <T> nonNull(var movies:Movies?,callback:((T) ->Unit)? = null){
//        movies?.let {
//           callback(binding.movies)=it
//        }
//    }
}