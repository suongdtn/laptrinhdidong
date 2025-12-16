package com.example.doan

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class EventadminAdapter(
    private var list: MutableList<Event>,
    private val onEdit: (Event) -> Unit,
    private val onDelete: (Event) -> Unit
) : RecyclerView.Adapter<EventadminAdapter.ViewHolder>() {

    inner class ViewHolder(val root: LinearLayout) : RecyclerView.ViewHolder(root) {
        val cardContainer: LinearLayout
        val img: ImageView
        val contentLayout: LinearLayout
        val tvTitle: TextView
        val tvDate: TextView
        val tvLocation: TextView
        val btnEdit: Button
        val btnDelete: Button

        init {
            root.orientation = LinearLayout.VERTICAL
            root.setPadding(0, 0, 0, 20)
            root.setBackgroundColor(Color.TRANSPARENT)

            // Card container với shadow effect
            cardContainer = LinearLayout(root.context).apply {
                orientation = LinearLayout.VERTICAL
                background = createCardBackground()
                elevation = 6f
            }

            // Image container
            val imageContainer = FrameLayout(root.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    480
                )
            }

            img = ImageView(root.context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            // Gradient overlay
            val overlay = View(root.context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                background = GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    intArrayOf(Color.parseColor("#AA000000"), Color.TRANSPARENT)
                )
            }

            imageContainer.addView(img)
            imageContainer.addView(overlay)

            // Content layout
            contentLayout = LinearLayout(root.context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 28, 32, 28)
                setBackgroundColor(Color.parseColor("#1E1E1E"))
            }

            tvTitle = TextView(root.context).apply {
                textSize = 19f
                setTextColor(Color.WHITE)
                setTypeface(null, Typeface.BOLD)
                setPadding(0, 0, 0, 16)
                maxLines = 2
                letterSpacing = 0.02f
            }

            // Date container
            val dateContainer = LinearLayout(root.context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 0, 0, 12)
            }

            val dateIcon = TextView(root.context).apply {
                text = "📅"
                textSize = 16f
                setPadding(0, 0, 12, 0)
            }

            tvDate = TextView(root.context).apply {
                textSize = 14f
                setTextColor(Color.parseColor("#B0B0B0"))
            }

            dateContainer.addView(dateIcon)
            dateContainer.addView(tvDate)

            // Location container
            val locationContainer = LinearLayout(root.context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 0, 0, 24)
            }

            val locationIcon = TextView(root.context).apply {
                text = "📍"
                textSize = 16f
                setPadding(0, 0, 12, 0)
            }

            tvLocation = TextView(root.context).apply {
                textSize = 14f
                setTextColor(Color.parseColor("#B0B0B0"))
            }

            locationContainer.addView(locationIcon)
            locationContainer.addView(tvLocation)

            // Divider
            val divider = View(root.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    2
                ).apply {
                    topMargin = 8
                    bottomMargin = 20
                }
                setBackgroundColor(Color.parseColor("#2A2A2A"))
            }

            // Button layout
            val btnLayout = LinearLayout(root.context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.END
            }

            btnEdit = Button(root.context).apply {
                text = "Sửa"
                setTextColor(Color.WHITE)
                textSize = 14f
                setTypeface(null, Typeface.BOLD)
                setPadding(56, 28, 56, 28)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 16
                }
                background = createEditButtonBackground()
                elevation = 3f
            }

            btnDelete = Button(root.context).apply {
                text = "Xóa"
                setTextColor(Color.WHITE)
                textSize = 14f
                setTypeface(null, Typeface.BOLD)
                setPadding(56, 28, 56, 28)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                background = createDeleteButtonBackground()
                elevation = 3f
            }

            btnLayout.addView(btnEdit)
            btnLayout.addView(btnDelete)

            contentLayout.addView(tvTitle)
            contentLayout.addView(dateContainer)
            contentLayout.addView(locationContainer)
            contentLayout.addView(divider)
            contentLayout.addView(btnLayout)

            cardContainer.addView(imageContainer)
            cardContainer.addView(contentLayout)

            root.addView(cardContainer)
        }

        private fun createCardBackground(): GradientDrawable {
            return GradientDrawable().apply {
                setColor(Color.parseColor("#1E1E1E"))
                cornerRadius = 24f
            }
        }

        private fun createEditButtonBackground(): GradientDrawable {
            return GradientDrawable().apply {
                colors = intArrayOf(Color.parseColor("#1E88E5"), Color.parseColor("#1976D2"))
                cornerRadius = 16f
                gradientType = GradientDrawable.LINEAR_GRADIENT
                orientation = GradientDrawable.Orientation.LEFT_RIGHT
            }
        }

        private fun createDeleteButtonBackground(): GradientDrawable {
            return GradientDrawable().apply {
                colors = intArrayOf(Color.parseColor("#D32F2F"), Color.parseColor("#C62828"))
                cornerRadius = 16f
                gradientType = GradientDrawable.LINEAR_GRADIENT
                orientation = GradientDrawable.Orientation.LEFT_RIGHT
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LinearLayout(parent.context)
        layout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return ViewHolder(layout)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val e = list[position]
        holder.tvTitle.text = e.title
        holder.tvDate.text = "Ngày tổ chức: ${e.date}"
        holder.tvLocation.text = "Địa điểm: ${e.location}"

        if (e.imageUrl.isNotEmpty()) {
            Glide.with(holder.img.context)
                .load(e.imageUrl)
                .centerCrop()
                .into(holder.img)
        } else {
            holder.img.setBackgroundColor(Color.parseColor("#2A2A2A"))
        }

        holder.btnEdit.setOnClickListener { onEdit(e) }
        holder.btnDelete.setOnClickListener { onDelete(e) }
    }

    fun updateList(newList: MutableList<Event>) {
        list = newList
        notifyDataSetChanged()
    }
}