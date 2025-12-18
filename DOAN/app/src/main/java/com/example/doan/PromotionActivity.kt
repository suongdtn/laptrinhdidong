package com.example.doan

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class PromotionActivity : BaseActivity() {  // ✅ Kế thừa từ BaseActivity

    private lateinit var recyclerView: RecyclerView
    private lateinit var promotionAdapter: PromotionAdapter
    private lateinit var searchEditText: EditText
    private val promotionList = mutableListOf<Promotion>()
    private val filteredList = mutableListOf<Promotion>()
    private val firestore = FirebaseFirestore.getInstance()

    // Màu sắc theme đồng bộ
    private val COLOR_BACKGROUND = Color.parseColor("#0D0D0D")
    private val COLOR_TEXT_PRIMARY = Color.parseColor("#FFFFFF")

    override fun getNavigationMenuItemId(): Int = R.id.nav_khuyen_mai

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // ✅ Lấy userEmail từ Intent (sử dụng biến từ BaseActivity)
            userEmail = intent.getStringExtra("userEmail")
            if (userEmail.isNullOrEmpty()) {
                Log.w("PromotionActivity", "userEmail is null or empty")
                Toast.makeText(this, "Lỗi: Email người dùng không hợp lệ", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            val mainLayout = createRootLayout()
            setContentView(mainLayout)

            // Adapter
            promotionAdapter = PromotionAdapter(filteredList) { promotion ->
                Log.d(
                    "PromotionActivity",
                    "Selected promotion: Title: ${promotion.title}, Days: ${promotion.date}, Pot: ${promotion.location}"
                )

                val intent = Intent(this, ShowInfoActivity::class.java)
                intent.putExtra("eventTitle", promotion.title)
                intent.putExtra("eventDays", promotion.date)
                intent.putExtra("eventImageUrl", promotion.imageUrl)
                intent.putExtra("eventPot", promotion.location)
                intent.putExtra("eventContent", promotion.content)
                intent.putExtra("userEmail", userEmail ?: "")
                startActivity(intent)
            }

            recyclerView.adapter = promotionAdapter

            // Search TextWatcher
            searchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterPromotions(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            // Gọi Firestore
            fetchPromotionsFromFirestore()
        } catch (e: Exception) {
            Log.e("PromotionActivity", "Error in onCreate", e)
            Toast.makeText(this, "Lỗi khởi tạo: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun createRootLayout(): View {
        // ✅ Đổi sang RelativeLayout để dễ thêm Bottom Navigation
        val mainLayout = RelativeLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    COLOR_BACKGROUND,
                    Color.parseColor("#1A1A1A")
                )
            )
        }

        // ✅ Container cho header, search bar và RecyclerView, với margin dưới để tránh che Bottom Nav
        val contentLayout = LinearLayout(this).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_TOP)
                bottomMargin = dpToPx(80)  // Tránh bị Bottom Nav che
            }
            orientation = LinearLayout.VERTICAL
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

        // Search Bar Container
        val searchContainer = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
            setBackgroundColor(COLOR_BACKGROUND)
            gravity = Gravity.CENTER_VERTICAL
        }

        // Search Box
        val searchBox = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                dpToPx(48),
                1f
            )
            orientation = LinearLayout.HORIZONTAL
            background = createSearchBackground()
            setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8))
            gravity = Gravity.CENTER_VERTICAL
            elevation = dpToPx(2).toFloat()
        }

        // Search Icon
        val searchIcon = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = "🔍"
            textSize = 18f
            setPadding(0, 0, dpToPx(12), 0)
        }

        // Search EditText
        searchEditText = EditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            hint = "Tìm kiếm khuyến mãi..."
            setHintTextColor(Color.parseColor("#666666"))
            setTextColor(Color.WHITE)
            textSize = 16f
            background = null
            setPadding(0, 0, 0, 0)
        }

        searchBox.addView(searchIcon)
        searchBox.addView(searchEditText)
        searchContainer.addView(searchBox)

        // RecyclerView
        recyclerView = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(16))
            layoutManager = LinearLayoutManager(this@PromotionActivity)
            isVerticalScrollBarEnabled = false
        }

        contentLayout.addView(headerLayout)
        contentLayout.addView(searchContainer)
        contentLayout.addView(recyclerView)
        mainLayout.addView(contentLayout)

        // ✅ Tạo và thêm Bottom Navigation
        bottomNavigation = BottomNavigationView(this).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(80)
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
        }
        mainLayout.addView(bottomNavigation)

        // ✅ Setup Bottom Navigation với userEmail từ BaseActivity
        setupBottomNavigation(bottomNavigation, userEmail)

        return mainLayout
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

    private fun createSearchBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.parseColor("#1E1E1E"))
            cornerRadius = dpToPx(24).toFloat()
            setStroke(dpToPx(1), Color.parseColor("#333333"))
        }
    }

    private fun filterPromotions(query: String) {
        filteredList.clear()

        if (query.isEmpty()) {
            filteredList.addAll(promotionList)
        } else {
            val lowerCaseQuery = query.lowercase()
            filteredList.addAll(
                promotionList.filter { promotion ->
                    promotion.title.lowercase().contains(lowerCaseQuery) ||
                            promotion.location.lowercase().contains(lowerCaseQuery) ||
                            promotion.date.lowercase().contains(lowerCaseQuery)
                }
            )
        }

        promotionAdapter.updateList(filteredList)
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
                        val content = doc.getString("content") ?: "Không có nội dung chi tiết"
                        val docId = doc.id

                        val promotion = Promotion(
                            title = title,
                            date = days,
                            imageUrl = imageUrl,
                            location = location,
                            id = docId,
                            content = content
                        )

                        promotionList.add(promotion)
                    }
                }

                promotionList.reverse()
                filteredList.clear()
                filteredList.addAll(promotionList)
                promotionAdapter.updateList(filteredList)
            }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}