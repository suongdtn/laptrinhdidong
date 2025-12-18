package com.example.doan

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// ✅ Data class với trường ID
data class Promotion(
    val title: String = "",
    val date: String = "",
    val imageUrl: String = "",
    val location: String = "",
    val id: String = "",
    val content: String = ""
)

class PromotionAdapter(
    private var promotions: List<Promotion>,
    private val onClick: (Promotion) -> Unit
) : RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder>() {

    class PromotionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val promotionImage: ImageView
        val promotionTitle: TextView
        val promotionDate: TextView
        val promotionlocation: TextView

        init {
            val mainLayout = view as LinearLayout
            promotionImage = mainLayout.getChildAt(0) as ImageView
            val textLayout = mainLayout.getChildAt(1) as LinearLayout
            promotionTitle = textLayout.getChildAt(0) as TextView
            promotionDate = textLayout.getChildAt(1) as TextView
            promotionlocation = textLayout.getChildAt(2) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val context = parent.context

        // Main horizontal layout
        val mainLayout = LinearLayout(context).apply {
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(16, context)
            }
            orientation = LinearLayout.HORIZONTAL
            setPadding(dpToPx(12, context), dpToPx(12, context), dpToPx(12, context), dpToPx(12, context))
            setBackgroundColor(Color.parseColor("#1A1A1A"))
        }

        // Image
        val promotionImage = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(80, context),
                dpToPx(80, context)
            ).apply {
                marginEnd = dpToPx(16, context)
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageResource(R.drawable.ic_launcher_background)
        }

        // Text container (vertical)
        val textLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }

        // Title
        val promotionTitle = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.WHITE)
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        // Date
        val promotionDate = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(4, context)
            }
            textSize = 14f
            setTextColor(Color.parseColor("#AAAAAA"))
        }

        // Location
        val promotionLocation = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(4, context)
            }
            textSize = 14f
            setTextColor(Color.parseColor("#AAAAAA"))
        }

        textLayout.addView(promotionTitle)
        textLayout.addView(promotionDate)
        textLayout.addView(promotionLocation)

        mainLayout.addView(promotionImage)
        mainLayout.addView(textLayout)

        return PromotionViewHolder(mainLayout)
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        val promo = promotions[position]

        holder.promotionTitle.text = promo.title
        holder.promotionDate.text = "📅 ${promo.date}"
        holder.promotionlocation.text = "📍 ${promo.location}"

        Glide.with(holder.itemView.context)
            .load(promo.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.promotionImage)

        holder.itemView.setOnClickListener {
            onClick(promo)
        }
    }

    override fun getItemCount(): Int = promotions.size

    fun updateList(newList: MutableList<Promotion>) {
        promotions = newList
        notifyDataSetChanged()
    }

    private fun dpToPx(dp: Int, context: android.content.Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}