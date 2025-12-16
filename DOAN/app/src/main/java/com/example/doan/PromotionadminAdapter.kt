package com.example.doan

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PromotionadminAdapter(
    private var promotionList: MutableList<Promotion>,
    private val onEdit: (Promotion) -> Unit,
    private val onDelete: (Promotion) -> Unit
) : RecyclerView.Adapter<PromotionadminAdapter.PromotionViewHolder>() {

    class PromotionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitle: TextView
        val txtDate: TextView
        val txtLocation: TextView
        val btnEdit: Button
        val btnDelete: Button

        init {
            val mainLayout = view as LinearLayout
            txtTitle = mainLayout.getChildAt(0) as TextView
            txtDate = mainLayout.getChildAt(1) as TextView
            txtLocation = mainLayout.getChildAt(2) as TextView
            val buttonLayout = mainLayout.getChildAt(3) as LinearLayout
            btnEdit = buttonLayout.getChildAt(0) as Button
            btnDelete = buttonLayout.getChildAt(1) as Button
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val context = parent.context

        // Main vertical layout
        val mainLayout = LinearLayout(context).apply {
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(dpToPx(8, context), dpToPx(8, context), dpToPx(8, context), dpToPx(8, context))
            }
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1A1A1A"))
            setPadding(dpToPx(12, context), dpToPx(12, context), dpToPx(12, context), dpToPx(12, context))
            elevation = dpToPx(4, context).toFloat()
        }

        // Title TextView
        val txtTitle = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(4, context)
            }
            text = "Tiêu đề khuyến mãi"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.WHITE)
        }

        // Date TextView
        val txtDate = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = "Thời gian khuyến mãi"
            textSize = 14f
            setTextColor(Color.parseColor("#AAAAAA"))
        }

        val txtLocation = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8, context)
            }
            text = "Địa điểm"
            textSize = 14f
            setTextColor(Color.parseColor("#AAAAAA"))
        }

        // Button container (horizontal)
        val buttonLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
        }

        // Edit Button
        val btnEdit = Button(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = dpToPx(8, context)
            }
            text = "Sửa"
            setBackgroundColor(Color.parseColor("#0D47A1")) // Blue
            setTextColor(Color.WHITE)
        }

        // Delete Button
        val btnDelete = Button(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = "Xóa"
            setBackgroundColor(Color.parseColor("#C62828")) // Red
            setTextColor(Color.WHITE)
        }

        buttonLayout.addView(btnEdit)
        buttonLayout.addView(btnDelete)

        mainLayout.addView(txtTitle)
        mainLayout.addView(txtDate)
        mainLayout.addView(txtLocation)
        mainLayout.addView(buttonLayout)

        return PromotionViewHolder(mainLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        val promotion = promotionList[position]

        // GIỮ NGUYÊN CODE CŨ
        holder.txtTitle.text = promotion.title
        holder.txtDate.text = "Thời gian khuyến mãi: ${promotion.date}"
        holder.txtLocation.text = "Địa điểm: ${promotion.location}"

        holder.btnEdit.setOnClickListener { onEdit(promotion) }
        holder.btnDelete.setOnClickListener { onDelete(promotion) }
    }

    override fun getItemCount(): Int = promotionList.size

    fun updateList(newList: MutableList<Promotion>) {
        promotionList = newList
        notifyDataSetChanged()
    }

    private fun dpToPx(dp: Int, context: android.content.Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}