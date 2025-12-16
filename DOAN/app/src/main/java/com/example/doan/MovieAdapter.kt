package com.example.doan

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class MovieAdapter(
    private val movieList: List<Movie>,
    private val onBookTicketClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(
        val moviePoster: ImageView,
        val backgroundBlurImage: ImageView,
        val movieTitle: TextView,
        val movieInfo: TextView,
        val bookTicketButton: Button,
        itemView: View
    ) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val density = parent.context.resources.displayMetrics.density

        // CardView chính - Đồng bộ với HomeActivity
        val cardView = CardView(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(
                    (10 * density).toInt(),
                    (10 * density).toInt(),
                    (10 * density).toInt(),
                    (10 * density).toInt()
                )
            }
            radius = 20 * density
            cardElevation = 12 * density
            setCardBackgroundColor(Color.parseColor("#1C1C1C")) // Đồng bộ với menu
        }

        // FrameLayout container
        val frameLayout = FrameLayout(parent.context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Background blur image
        val backgroundBlurImage = ImageView(parent.context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            alpha = 0.25f
        }

        // Overlay gradient - Đồng bộ với HomeActivity gradient
        val overlayView = View(parent.context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    Color.parseColor("#80000000"),
                    Color.parseColor("#CC000000")
                )
            )
        }

        // Border gradient đỏ - Giống banner HomeActivity
        val borderView = View(parent.context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            background = GradientDrawable().apply {
                setStroke((2 * density).toInt(), Color.parseColor("#E50914"))
                cornerRadius = 20 * density
            }
        }

        // LinearLayout chứa nội dung
        val linearLayout = LinearLayout(parent.context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(
                (20 * density).toInt(),
                (20 * density).toInt(),
                (20 * density).toInt(),
                (20 * density).toInt()
            )
        }

        // Movie Poster với shadow và border
        val posterContainer = FrameLayout(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                (210 * density).toInt(),
                (300 * density).toInt()
            ).apply {
                bottomMargin = (16 * density).toInt()
            }
        }

        val moviePoster = ImageView(parent.context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            elevation = 12 * density

            // Border cho poster
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#2A2A2A"))
                cornerRadius = 16 * density
                setStroke((3 * density).toInt(), Color.parseColor("#E50914"))
            }
            setPadding(
                (3 * density).toInt(),
                (3 * density).toInt(),
                (3 * density).toInt(),
                (3 * density).toInt()
            )
        }
        posterContainer.addView(moviePoster)

        // Movie Title - Đồng bộ với HomeActivity
        val movieTitle = TextView(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (12 * density).toInt()
            }
            textSize = 22f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            maxLines = 2
            letterSpacing = 0.05f
            setPadding((12 * density).toInt(), 0, (12 * density).toInt(), 0)
        }

        // Movie Info - Đồng bộ màu text
        val movieInfo = TextView(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (8 * density).toInt()
            }
            textSize = 15f
            setTextColor(Color.parseColor("#E8E8E8"))
            gravity = Gravity.CENTER
            maxLines = 2
        }

        // Book Ticket Button - Đồng bộ 100% với HomeActivity bottom nav
        val bookTicketButton = Button(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                (280 * density).toInt(),
                (52 * density).toInt()
            ).apply {
                topMargin = (20 * density).toInt()
            }
            text = "ĐẶT VÉ NGAY"
            textSize = 16f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            isAllCaps = true
            letterSpacing = 0.15f

            // Gradient giống bottom nav HomeActivity
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#B00710"),
                    Color.parseColor("#E50914"),
                    Color.parseColor("#FF1F2A")
                )
            ).apply {
                cornerRadius = 26 * density
            }

            elevation = 8 * density
            stateListAnimator = null
        }

        // Thêm các view vào layout
        linearLayout.addView(posterContainer)
        linearLayout.addView(movieTitle)
        linearLayout.addView(movieInfo)
        linearLayout.addView(bookTicketButton)

        frameLayout.addView(backgroundBlurImage)
        frameLayout.addView(overlayView)
        frameLayout.addView(borderView)
        frameLayout.addView(linearLayout)

        cardView.addView(frameLayout)

        return MovieViewHolder(
            moviePoster,
            backgroundBlurImage,
            movieTitle,
            movieInfo,
            bookTicketButton,
            cardView
        )
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]

        // Load ảnh background
        Glide.with(holder.itemView.context)
            .load(movie.imageResId)
            .into(holder.backgroundBlurImage)

        // Load ảnh poster với góc bo tròn
        Glide.with(holder.itemView.context)
            .load(movie.imageResId)
            .apply(RequestOptions().transform(RoundedCorners(48)))
            .into(holder.moviePoster)

        holder.movieTitle.text = movie.title
        holder.movieInfo.text = movie.info

        holder.bookTicketButton.setOnClickListener {
            onBookTicketClick(movie)
        }
    }

    override fun getItemCount() = movieList.size

    fun getImageResource(position: Int): String {
        return movieList[position].imageResId
    }
}