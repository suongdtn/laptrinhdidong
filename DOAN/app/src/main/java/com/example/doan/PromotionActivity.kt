package com.example.doan

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class PromotionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var promotionAdapter: PromotionAdapter
    private val promotionList = mutableListOf<Promotion>()
    private val firestore = FirebaseFirestore.getInstance()

    // Màu sắc theme đồng bộ
    private val COLOR_BACKGROUND = Color.parseColor("#0D0D0D")
    private val COLOR_TEXT_PRIMARY = Color.parseColor("#FFFFFF")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tạo UI programmatically
        val mainLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(COLOR_BACKGROUND)
        }

        // Header với gradient đồng bộ
        val headerLayout = RelativeLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(70)
            )
            background = createGradientBackground()
            setPadding(dpToPx(16), dpToPx(20), dpToPx(16), dpToPx(20))
            elevation = dpToPx(8).toFloat()
        }

        // Nút Back
        val btnBack = ImageView(this).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                dpToPx(32),
                dpToPx(32)
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_START)
                addRule(RelativeLayout.CENTER_VERTICAL)
            }
            setImageResource(android.R.drawable.ic_menu_revert)
            setColorFilter(COLOR_TEXT_PRIMARY)
            contentDescription = "Back button"
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
            setOnClickListener { finish() }
        }

        // TextView Title
        val tvTitle = TextView(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            text = "KHUYẾN MÃI"
            textSize = 22f
            setTextColor(COLOR_TEXT_PRIMARY)
            setTypeface(null, Typeface.BOLD)
            letterSpacing = 0.1f
        }

        headerLayout.addView(btnBack)
        headerLayout.addView(tvTitle)

        // RecyclerView
        recyclerView = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
            layoutManager = LinearLayoutManager(this@PromotionActivity)
            isVerticalScrollBarEnabled = false
        }

        mainLayout.addView(headerLayout)
        mainLayout.addView(recyclerView)

        setContentView(mainLayout)

        // Adapter
        promotionAdapter = PromotionAdapter(promotionList) { promotion ->
            Log.d(
                "promotionActivity",
                "Selected promotion: Title: ${promotion.title}, Days: ${promotion.date}, Pot: ${promotion.location}"
            )

            val intent = Intent(this, ShowInfoActivity::class.java)
            intent.putExtra("eventTitle", promotion.title)
            intent.putExtra("eventDays", promotion.date)
            intent.putExtra("eventImageUrl", promotion.imageUrl)
            intent.putExtra("eventPot", promotion.location)
            startActivity(intent)
        }

        recyclerView.adapter = promotionAdapter

        // Gọi Firestore
        fetchPromotionsFromFirestore()
    }

    private fun createGradientBackground(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor("#B00710"),
                Color.parseColor("#E50914"),
                Color.parseColor("#FF1F2A")
            )
        )
    }

    private fun fetchPromotionsFromFirestore() {
        firestore.collection("Promotion")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                promotionList.clear()

                if (snapshot != null) {
                    for (doc in snapshot.documents) {
                        val title = doc.getString("title") ?: ""
                        val days = doc.getString("days") ?: ""
                        val imageUrl = doc.getString("image") ?: ""
                        val location = doc.getString("location") ?: ""

                        val promotion = Promotion(title, days, imageUrl, location)
                        promotionList.add(promotion)
                    }
                }

                promotionList.reverse()
                promotionAdapter.updateList(promotionList)
            }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}