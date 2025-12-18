package com.example.doan

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

data class Event(
    val title: String = "",
    val date: String = "",
    val imageUrl: String = "",
    val location: String = "",
    val content: String=""
)

class EventAdapter(
    private var events: List<Event>,
    private val onClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    // Tạo các ID tĩnh để tránh conflict
    companion object {
        private val IMAGE_ID = View.generateViewId()
        private val TITLE_ID = View.generateViewId()
        private val DATE_ID = View.generateViewId()
        private val LOCATION_ID = View.generateViewId()
    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventImage: ImageView = view.findViewById(IMAGE_ID)
        val eventTitle: TextView = view.findViewById(TITLE_ID)
        val eventDate: TextView = view.findViewById(DATE_ID)
        val eventLocation: TextView = view.findViewById(LOCATION_ID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = createEventItemView(parent.context)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]

        holder.eventTitle.text = event.title
        holder.eventDate.text = event.date
        holder.eventLocation.text = event.location

        Glide.with(holder.itemView.context)
            .load(event.imageUrl)
            .transform(RoundedCorners(dpToPx(holder.itemView.context, 12)))
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.eventImage)

        holder.itemView.setOnClickListener { onClick(event) }
    }

    override fun getItemCount() = events.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Event>) {
        events = newList
        notifyDataSetChanged()
    }

    private fun createEventItemView(context: Context): View {
        val cardView = CardView(context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dpToPx(context, 6), 0, dpToPx(context, 6))
            }
            radius = dpToPx(context, 18).toFloat()
            cardElevation = dpToPx(context, 8).toFloat()
            setCardBackgroundColor(Color.TRANSPARENT)

            // Ripple effect
            foreground = createRippleDrawable(context)
        }

        val backgroundLayer = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.parseColor("#1F1F1F"),
                    Color.parseColor("#2A2A2A")
                )
            ).apply {
                cornerRadius = dpToPx(context, 18).toFloat()
            }
        }

        val contentLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            setPadding(dpToPx(context, 14), dpToPx(context, 14), dpToPx(context, 14), dpToPx(context, 14))
            gravity = Gravity.CENTER_VERTICAL
        }

        // Image container với border gradient
        val imageContainer = createImageContainer(context)
        contentLayout.addView(imageContainer)

        // Info container
        val infoContainer = createInfoContainer(context)
        contentLayout.addView(infoContainer)

        backgroundLayer.addView(contentLayout)
        cardView.addView(backgroundLayer)

        return cardView
    }

    private fun createImageContainer(context: Context): View {
        val container = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(context, 130),
                dpToPx(context, 130)
            ).apply {
                marginEnd = dpToPx(context, 16)
            }
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                cornerRadius = dpToPx(context, 16).toFloat()
                setStroke(dpToPx(context, 3), Color.parseColor("#E50914"))
                setColor(Color.parseColor("#0D0D0D"))
            }
            elevation = dpToPx(context, 4).toFloat()
        }

        val imageView = ImageView(context).apply {
            id = IMAGE_ID
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(dpToPx(context, 5), dpToPx(context, 5), dpToPx(context, 5), dpToPx(context, 5))
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = GradientDrawable().apply {
                cornerRadius = dpToPx(context, 13).toFloat()
                setColor(Color.parseColor("#1A1A1A"))
            }
            clipToOutline = true
        }

        val badge = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(dpToPx(context, 8), dpToPx(context, 8), 0, 0)
            }
            text = "🔥"
            textSize = 20f
            setPadding(dpToPx(context, 4), dpToPx(context, 2), dpToPx(context, 4), dpToPx(context, 2))
            background = GradientDrawable().apply {
                cornerRadius = dpToPx(context, 8).toFloat()
                setColor(Color.parseColor("#CC000000"))
            }
            elevation = dpToPx(context, 2).toFloat()
        }

        container.addView(imageView)
        container.addView(badge)
        return container
    }

    private fun createInfoContainer(context: Context): View {
        val container = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val titleText = TextView(context).apply {
            id = TITLE_ID
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textSize = 19f
            setTextColor(Color.WHITE)
            setTypeface(typeface, Typeface.BOLD)
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
            letterSpacing = 0.02f
            setShadowLayer(3f, 0f, 1f, Color.parseColor("#80000000"))
        }

        val divider = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(context, 50),
                dpToPx(context, 3)
            ).apply {
                topMargin = dpToPx(context, 10)
                bottomMargin = dpToPx(context, 10)
            }
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#E50914"),
                    Color.parseColor("#FF6B6B"),
                    Color.TRANSPARENT
                )
            ).apply {
                cornerRadius = dpToPx(context, 2).toFloat()
            }
        }

        val dateLayout = createInfoRow(
            context,
            "📅",
            Color.parseColor("#DDDDDD"),
            DATE_ID
        )

        val locationLayout = createInfoRow(
            context,
            "📍",
            Color.parseColor("#FFD700"),
            LOCATION_ID,
            true
        )

        container.addView(titleText)
        container.addView(divider)
        container.addView(dateLayout)
        container.addView(locationLayout)

        return container
    }

    private fun createInfoRow(
        context: Context,
        icon: String,
        textColor: Int,
        textViewId: Int,
        isBold: Boolean = false
    ): View {
        val layout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(context, 5)
            }
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val iconContainer = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(context, 24),
                dpToPx(context, 24)
            ).apply {
                marginEnd = dpToPx(context, 8)
            }
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#33FFFFFF"))
            }
        }

        val iconText = TextView(context).apply {
            text = icon
            textSize = 11f
        }
        iconContainer.addView(iconText)

        val textView = TextView(context).apply {
            id = textViewId
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
            textSize = 14f
            setTextColor(textColor)
            if (isBold) {
                setTypeface(typeface, Typeface.BOLD)
            }
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        layout.addView(iconContainer)
        layout.addView(textView)

        return layout
    }

    private fun createRippleDrawable(context: Context): android.graphics.drawable.Drawable? {
        val outValue = TypedValue()
        context.theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValue,
            true
        )
        return context.getDrawable(outValue.resourceId)
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}