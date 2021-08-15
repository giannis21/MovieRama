package com.example.movierama.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=(activity as MainActivity).viewModel

        viewModel.getMovieDetails(args.id)

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