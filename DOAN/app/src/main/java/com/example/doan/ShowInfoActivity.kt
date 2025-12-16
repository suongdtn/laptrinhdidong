package com.example.doan

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ShowInfoActivity : AppCompatActivity() {
    private lateinit var titleTextView: TextView
    private lateinit var daysTextView: TextView
    private lateinit var potTextView: TextView
    private lateinit var headerTitle: TextView
    private lateinit var eventImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create UI programmatically
        val mainLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.BLACK)
        }

        // Header
        val headerLayout = RelativeLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(56)
            )
            setBackgroundColor(Color.parseColor("#E50914"))
            setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
        }

        // Back Button
        val btnBack = ImageView(this).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                dpToPx(24),
                dpToPx(24)
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_START)
                addRule(RelativeLayout.CENTER_VERTICAL)
            }
            setImageResource(android.R.drawable.ic_menu_revert)
            setColorFilter(Color.WHITE)
            contentDescription = "Back button"
            setOnClickListener { finish() }
        }

        // Header Title
        headerTitle = TextView(this).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            text = "Sự kiện"
            setTextColor(Color.WHITE)
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        headerLayout.addView(btnBack)
        headerLayout.addView(headerTitle)

        // ScrollView for content
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }// Content Layout
        val contentLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
        }

        // Event Image
        eventImageView = ImageView(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(200)
            ).apply {
                bottomMargin = dpToPx(16)
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
            setBackgroundColor(Color.parseColor("#1A1A1A"))
        }

        // Title TextView
        titleTextView = TextView(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.WHITE)
        }

        // Days TextView
        daysTextView = TextView(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
            textSize = 18f
            setTextColor(Color.parseColor("#CCCCCC"))
        }

        // Pot TextView
        potTextView = TextView(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }
            textSize = 18f
            setTextColor(Color.parseColor("#CCCCCC"))
        }

        // Additional Info TextView
        val additionalInfo = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }
            text = "Chưa có thông tin"
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#AAAAAA"))
            gravity = Gravity.CENTER
        }

        contentLayout.addView(eventImageView)
        contentLayout.addView(titleTextView)
        contentLayout.addView(daysTextView)
        contentLayout.addView(potTextView)
        contentLayout.addView(additionalInfo)

        scrollView.addView(contentLayout)
        mainLayout.addView(headerLayout)
        mainLayout.addView(scrollView)

        setContentView(mainLayout)

        // Nhận dữ liệu từ Intent
        val eventTitle = intent.getStringExtra("eventTitle") ?: "Không có tiêu đề"
        val eventDays = intent.getStringExtra("eventDays") ?: "Không có thông tin"
        val eventImageUrl = intent.getStringExtra("eventImageUrl") ?: ""
        val eventPot = intent.getStringExtra("eventPot") ?: "Không có thông tin"

        // Hiển thị thông tin lên UI
        titleTextView.text = eventTitle
        daysTextView.text = eventDays
        headerTitle.text = eventTitle
        potTextView.text = eventPot

        // Tải hình ảnh bằng Glide
        Glide.with(this)
            .load(eventImageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(eventImageView)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}