package com.example.movierama.ui

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.NestedScrollView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.example.movierama.R
import com.example.movierama.data.movie.MovieResult
import com.example.movierama.data.movieDetail.BelongsToCollection
import com.example.movierama.data.movieDetail.Genre
import com.example.movierama.data.reviews.Result
import com.example.movierama.viewmodels.SharedViewModel
import com.google.android.material.card.MaterialCardView
import java.util.*


object bindingAdapters {


    @BindingAdapter(value = ["updateSrc", "movieId"])
    @JvmStatic
    fun ImageView.updateSrc(viewModel: ViewModel, movieId: String) {
        try {
            (viewModel as? SharedViewModel).let {
                it?.favorites?.value?.firstOrNull { it.id == movieId }?.let {
                    this.setImageResource(R.drawable.ic_baseline_favorite_24)
                } ?: kotlin.run {
                    this.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                }

            }

        } catch (e: Exception) {
            println("exception ${e.localizedMessage}")
        }

    }

    @BindingAdapter(value = ["spannableInSearch", "currentSearchName"])
    @JvmStatic
    fun TextView.spannableInSearch(movie: String, query: String) {
        val tvshowName = movie.toLowerCase(Locale.ROOT)
        val tvshowNameSpan = SpannableString(tvshowName)
        val length_of_word = query.length

        if (query != "") {

            try {
                val indexOf = tvshowName.indexOf(query.toLowerCase(Locale.ROOT))
                if (indexOf >= 0) {
                    tvshowNameSpan.setSpan(
                        BackgroundColorSpan(Color.WHITE),
                        indexOf,
                        length_of_word + indexOf,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    tvshowNameSpan.setSpan(
                        ForegroundColorSpan(Color.BLACK),
                        indexOf,
                        length_of_word + indexOf,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    tvshowNameSpan.setSpan(
                        TextAppearanceSpan(context, R.style.caption_bold),
                        0,
                        tvshowName.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    this.text = tvshowNameSpan
                } else
                    this.text = movie

            } catch (e: java.lang.Exception) {
                this.text = movie
            }
        } else {
            this.text = movie
        }
    }


    @BindingAdapter("imageUrl", "isDetails")
    @JvmStatic
    fun loadImage(view: ImageView, url: String?, isDetails: Boolean) {
        val progressBar: ProgressBar = if (isDetails)
            (view.parent as MaterialCardView).findViewById<ProgressBar>(R.id.progressBar)
        else
            (view.parent as RelativeLayout).findViewById<ProgressBar>(R.id.progressBar)

        Glide.with(view.context).load("https://image.tmdb.org/t/p/w500/$url")
            .error(Glide.with(view.context).load(R.drawable.no_results))
            .apply(RequestOptions().transform(RoundedCorners(40)))
            .skipMemoryCache(true)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }

            }).into(view)

    }

    @BindingAdapter("genres")
    @JvmStatic
    fun loadgenres(view: TextView, genres: List<Genre>?) {
        var genr = "-"
        if (!genres.isNullOrEmpty()) {
            genr = ""
            genres.indices.forEach { i ->
                genr += genres[i].name
                if (i < genres.size - 1)
                    genr += " | "
            }

        }

        view.text = genr
    }

    @BindingAdapter("collection")
    @JvmStatic
    fun loaddirector(view: TextView, collection: BelongsToCollection?) {
        collection?.let {
            view.text = collection.name
        } ?: kotlin.run {
            view.text = "-"
        }

    }

    @BindingAdapter("similarMovies")
    @JvmStatic
    fun similarMovies(view: LinearLayout, movies: List<MovieResult> ?=null) {
      //  view.removeAllViews()
        try {
            movies?.let {
                it.forEach { movie ->
                    val similarView: View = LayoutInflater.from(view.context).inflate(R.layout.similar_layout, null)
                    val image = similarView.findViewById<ImageView>(R.id.image)

//                    Glide.with(view.context).load("https://image.tmdb.org/t/p/w500/${movie.poster_path}")
//                        .error(Glide.with(view.context).load(R.drawable.no_results)).into(image)
//
               //  val backgroundImage = Glide.with(view.context).asBitmap().load("https://image.tmdb.org/t/p/w500/${movie.poster_path}").skipMemoryCache(true).submit().get()
                    Glide.with(view.context).asBitmap().load("https://image.tmdb.org/t/p/w500/${movie.poster_path}").into(
                        BitmapImageViewTarget(image)
                    )
                    //image.setImageBitmap(backgroundImage)
                    view.addView(similarView)
                //   image.setImageBitmap(backgroundImage)

//                    Glide.with(view.context).load("https://image.tmdb.org/t/p/w500/${movie.poster_path}")
//                        .error(Glide.with(view.context).load(R.drawable.no_results))
//                        .listener(object : RequestListener<Drawable> {
//                            override fun onLoadFailed(
//                                e: GlideException?,
//                                model: Any?,
//                                target: com.bumptech.glide.request.target.Target<Drawable>?,
//                                isFirstResource: Boolean
//                            ): Boolean {
//
//                                view.addView(similarView)
//                                return false
//                            }
//
//                            override fun onResourceReady(
//                                resource: Drawable?,
//                                model: Any?,
//                                target: com.bumptech.glide.request.target.Target<Drawable>?,
//                                dataSource: DataSource?,
//                                isFirstResource: Boolean
//                            ): Boolean {
//
//
//                                return false
//                            }
//
//                        }).into(image)






                }
            }









        }catch (e:Exception){
            println("exception ${e.localizedMessage}")
        }



    }



    @BindingAdapter("reviews")
    @JvmStatic
    fun reviews(view: LinearLayout, reviews: List<Result> ?=null ) {

        try {
            reviews?.take(2)?.let {
                if(reviews.isNotEmpty()){
                    val nestedScrollView= (view.parent as NestedScrollView)
                    val reviewsConstraint= (nestedScrollView.parent as ConstraintLayout)
                    val mainContainer= (reviewsConstraint.parent as ConstraintLayout)
                    mainContainer.findViewById<Group>(R.id.reviewsGroup).visibility=View.VISIBLE
                }

                it.forEach { review ->
                    val reviwsView: View = LayoutInflater.from(view.context).inflate(R.layout.reviews_layout, null)
                    val author = reviwsView.findViewById<TextView>(R.id.author)
                    val content = reviwsView.findViewById<TextView>(R.id.content)
                    author.text = review.author
                    content.text= review.content

                    view.addView(reviwsView)

                }
            }

        }catch (e:Exception){
            println("exception ${e.localizedMessage}")
        }



    }
}