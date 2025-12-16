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

class FilmAdminAdapter(
    private var filmList: MutableList<FilmAdminActivity.Film>,
    private val onEdit: (FilmAdminActivity.Film) -> Unit,
    private val onDelete: (FilmAdminActivity.Film) -> Unit
) : RecyclerView.Adapter<FilmAdminAdapter.FilmViewHolder>() {

    inner class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFilm: ImageView
        val tvName: TextView
        val tvDirector: TextView
        val tvDuration: TextView
        val tvGenre: TextView
        val tvRelease: TextView
        val btnEdit: Button
        val btnDelete: Button

        init {
            val cardView = itemView as CardView
            val backgroundLayer = cardView.getChildAt(0) as LinearLayout
            val mainLayout = backgroundLayer.getChildAt(0) as LinearLayout
            val posterContainer = mainLayout.getChildAt(0) as FrameLayout
            val infoLayout = mainLayout.getChildAt(1) as LinearLayout
            val buttonLayout = infoLayout.getChildAt(6) as LinearLayout

            imgFilm = posterContainer.getChildAt(0) as ImageView
            tvName = infoLayout.getChildAt(0) as TextView
            tvDirector = infoLayout.getChildAt(2) as TextView
            tvDuration = infoLayout.getChildAt(3) as TextView
            tvGenre = infoLayout.getChildAt(4) as TextView
            tvRelease = infoLayout.getChildAt(5) as TextView
            btnEdit = buttonLayout.getChildAt(0) as Button
            btnDelete = buttonLayout.getChildAt(1) as Button
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = createItemView(parent)
        return FilmViewHolder(view)
    }

    private fun createItemView(parent: ViewGroup): CardView {
        val context = parent.context
        val density = context.resources.displayMetrics.density

        fun dp(value: Int): Int = (value * density).toInt()

        // CardView chính với elevation cao
        val cardView = CardView(context).apply {
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(dp(16), dp(10), dp(16), dp(10))
            }
            radius = dp(18).toFloat()
            cardElevation = dp(10).toFloat()
            setCardBackgroundColor(Color.TRANSPARENT)
        }

        // Background gradient layer
        val backgroundLayer = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.parseColor("#1F1F1F"),
                    Color.parseColor("#2A2A2A")
                )
            ).apply {
                cornerRadius = dp(18).toFloat()
            }
        }

        // Main Layout (Horizontal)
        val mainLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Poster Container với border gradient
        val posterContainer = FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(dp(120), dp(170)).apply {
                marginEnd = dp(16)
            }
            background = GradientDrawable().apply {
                cornerRadius = dp(14).toFloat()
                setStroke(dp(3), Color.parseColor("#E50914"))
                setColor(Color.parseColor("#0D0D0D"))
            }
            elevation = dp(4).toFloat()
        }

        // Poster ImageView
        val imgFilm = ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(dp(4), dp(4), dp(4), dp(4))
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = GradientDrawable().apply {
                cornerRadius = dp(11).toFloat()
                setColor(Color.parseColor("#1A1A1A"))
            }
            clipToOutline = true
        }
        posterContainer.addView(imgFilm)
        mainLayout.addView(posterContainer)

        // Info Layout (Vertical)
        val infoLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            gravity = Gravity.CENTER_VERTICAL
        }

        // Film Name với style đặc biệt
        val tvName = TextView(context).apply {
            text = "Tên phim"
            textSize = 19f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            maxLines = 2
            letterSpacing = 0.02f
            setShadowLayer(3f, 0f, 1f, Color.parseColor("#80000000"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        infoLayout.addView(tvName)

        // Divider line
        val divider = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                dp(50),
                dp(3)
            ).apply {
                topMargin = dp(10)
                bottomMargin = dp(10)
            }
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#E50914"),
                    Color.parseColor("#FF6B6B"),
                    Color.TRANSPARENT
                )
            ).apply {
                cornerRadius = dp(2).toFloat()
            }
        }
        infoLayout.addView(divider)

        // Director với icon
        val tvDirector = createInfoRow(context, "🎬", "Đạo diễn: ", Color.parseColor("#DDDDDD"), dp(0))
        infoLayout.addView(tvDirector)

        // Duration với icon
        val tvDuration = createInfoRow(context, "⏱️", "Thời lượng: ", Color.parseColor("#DDDDDD"), dp(5))
        infoLayout.addView(tvDuration)

        // Genre với icon
        val tvGenre = createInfoRow(context, "🎭", "Thể loại: ", Color.parseColor("#FFD700"), dp(5))
        infoLayout.addView(tvGenre)

        // Release Date với icon
        val tvRelease = createInfoRow(context, "📅", "Phát hành: ", Color.parseColor("#DDDDDD"), dp(5))
        infoLayout.addView(tvRelease)

        // Button Layout với spacing đẹp
        val buttonLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(14), 0, 0)
            }
        }

        // Edit Button với gradient
        val btnEdit = Button(context).apply {
            text = "✏️ SỬA"
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#E50914"),
                    Color.parseColor("#FF6B6B")
                )
            ).apply {
                cornerRadius = dp(10).toFloat()
            }
            layoutParams = LinearLayout.LayoutParams(
                0,
                dp(44),
                1f
            ).apply {
                setMargins(0, 0, dp(8), 0)
            }
            elevation = dp(5).toFloat()
        }
        buttonLayout.addView(btnEdit)

        // Delete Button với gradient
        val btnDelete = Button(context).apply {
            text = "🗑️ XÓA"
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#C62828"),
                    Color.parseColor("#E53935")
                )
            ).apply {
                cornerRadius = dp(10).toFloat()
            }
            layoutParams = LinearLayout.LayoutParams(
                0,
                dp(44),
                1f
            ).apply {
                setMargins(dp(8), 0, 0, 0)
            }
            elevation = dp(5).toFloat()
        }
        buttonLayout.addView(btnDelete)

        infoLayout.addView(buttonLayout)
        mainLayout.addView(infoLayout)
        backgroundLayer.addView(mainLayout)
        cardView.addView(backgroundLayer)

        return cardView
    }

    private fun createInfoRow(
        context: android.content.Context,
        icon: String,
        prefix: String,
        textColor: Int,
        topMargin: Int
    ): TextView {
        return TextView(context).apply {
            text = "$icon $prefix"
            textSize = 14f
            setTextColor(textColor)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, topMargin, 0, 0)
            }
            maxLines = 1
        }
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = filmList[position]

        holder.tvName.text = film.name
        holder.tvDirector.text = "🎬 Đạo diễn: ${film.director}"
        holder.tvDuration.text = "⏱️ Thời lượng: ${film.duration} phút"
        holder.tvGenre.text = "🎭 Thể loại: ${film.genre}"
        holder.tvRelease.text = "📅 Phát hành: ${film.releaseDate}"

        // Load ảnh bằng Glide
        Glide.with(holder.itemView.context)
            .load(film.url)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.imgFilm)

        // Nút Edit
        holder.btnEdit.setOnClickListener {
            onEdit(film)
        }

        // Nút Delete
        holder.btnDelete.setOnClickListener {
            onDelete(film)
        }
    }

    override fun getItemCount(): Int = filmList.size

    // Cập nhật danh sách phim
    fun updateList(newList: MutableList<FilmAdminActivity.Film>) {
        filmList = newList
        notifyDataSetChanged()
    }
}