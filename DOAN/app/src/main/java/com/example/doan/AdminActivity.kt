package com.example.doan

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    // TextViews để cập nhật dữ liệu
    private var revenueText: TextView? = null
    private var usersCountText: TextView? = null
    private var filmsCountText: TextView? = null
    private var eventsCountText: TextView? = null
    private var promotionsCountText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val iEmail = intent.getStringExtra("userEmail")
        setContentView(createLotteCinemaUI(iEmail))

        // Load dữ liệu thống kê
        loadStatistics()
    }

    private fun loadStatistics() {
        // Tính tổng doanh thu từ Ticker
        db.collection("Ticker")
            .get()
            .addOnSuccessListener { documents ->
                var totalRevenue = 0.0
                for (document in documents) {
                    val price = document.getString("gia")?.toDoubleOrNull() ?: 0.0
                    totalRevenue += price
                }
                revenueText?.text = formatCurrency(totalRevenue)
            }
            .addOnFailureListener {
                revenueText?.text = "0 ₫"
            }

        // Đếm số lượng Users
        db.collection("Users")
            .get()
            .addOnSuccessListener { documents ->
                usersCountText?.text = documents.size().toString()
            }
            .addOnFailureListener {
                usersCountText?.text = "0"
            }

        // Đếm số lượng Films
        db.collection("Films")
            .get()
            .addOnSuccessListener { documents ->
                filmsCountText?.text = documents.size().toString()
            }
            .addOnFailureListener {
                filmsCountText?.text = "0"
            }

        // Đếm số lượng Events
        db.collection("Event")
            .get()
            .addOnSuccessListener { documents ->
                eventsCountText?.text = documents.size().toString()
            }
            .addOnFailureListener {
                eventsCountText?.text = "0"
            }

        // Đếm số lượng Promotions
        db.collection("Promotion")
            .get()
            .addOnSuccessListener { documents ->
                promotionsCountText?.text = documents.size().toString()
            }
            .addOnFailureListener {
                promotionsCountText?.text = "0"
            }
    }

    private fun formatCurrency(amount: Double): String {
        return String.format("%,.0f ₫", amount)
    }

    private fun createLotteCinemaUI(userEmail: String?): View {
        val rootLayout = ConstraintLayout(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#0D0D0D"))
        }

        // Background gradient overlay
        val gradientOverlay = View(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            background = createGradientDrawable()
        }
        rootLayout.addView(gradientOverlay)

        // Decorative circles
        val decorCircle1 = View(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(dpToPx(300), dpToPx(300))
            background = createCircleDrawable(Color.parseColor("#1AE50914"))
        }
        rootLayout.addView(decorCircle1)

        val decorCircle2 = View(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(dpToPx(200), dpToPx(200))
            background = createCircleDrawable(Color.parseColor("#0DE50914"))
        }
        rootLayout.addView(decorCircle2)

        // ScrollView
        val scrollView = ScrollView(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            isVerticalScrollBarEnabled = false
        }

        val scrollContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, dpToPx(40), 0, dpToPx(40))
        }

        // Header với logo
        val logoContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(dpToPx(100), dpToPx(100)).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = dpToPx(16)
            }
            background = createLogoBackground()
            elevation = dpToPx(12).toFloat()
        }
        scrollContent.addView(logoContainer)

        // Subtitle
        val subtitleTextView = TextView(this).apply {
            text = "ADMIN PANEL"
            textSize = 12f
            setTextColor(Color.parseColor("#E50914"))
            gravity = Gravity.CENTER
            letterSpacing = 0.3f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
        }
        scrollContent.addView(subtitleTextView)

        // Title
        val titleTextView = TextView(this).apply {
            text = "QUẢN TRỊ HỆ THỐNG"
            textSize = 28f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            letterSpacing = 0.05f
            setShadowLayer(8f, 0f, 4f, Color.parseColor("#40000000"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(10)
            }
        }
        scrollContent.addView(titleTextView)

        // Divider line
        val dividerLine = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(dpToPx(80), dpToPx(4)).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = dpToPx(32)
            }
            background = GradientDrawable().apply {
                colors = intArrayOf(
                    Color.parseColor("#E50914"),
                    Color.parseColor("#FF4458")
                )
                orientation = GradientDrawable.Orientation.LEFT_RIGHT
                cornerRadius = dpToPx(2).toFloat()
            }
        }
        scrollContent.addView(dividerLine)

        // Statistics Section
        scrollContent.addView(createStatisticsSection())

        // Content container với padding
        val contentContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(dpToPx(24), 0, dpToPx(24), 0)
        }

        contentContainer.addView(createContentSection(userEmail))
        contentContainer.addView(createBusinessSection(userEmail))
        contentContainer.addView(createMarketingSection())
        contentContainer.addView(createLogoutButton())

        scrollContent.addView(contentContainer)
        scrollView.addView(scrollContent)
        rootLayout.addView(scrollView)

        return rootLayout
    }

    private fun createStatisticsSection(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(24), 0, dpToPx(24), dpToPx(24))

            // Tiêu đề thống kê
            addView(TextView(this@AdminActivity).apply {
                text = "📊 THỐNG KÊ TỔNG QUAN"
                setTextColor(Color.parseColor("#FF4458"))
                textSize = 15f
                setTypeface(null, Typeface.BOLD)
                letterSpacing = 0.1f
                setPadding(0, 0, 0, dpToPx(16))
            })

            // Revenue Card (Full Width)
            addView(createRevenueCard())

            // Stats Grid (2x2)
            addView(createStatsGrid())
        }
    }

    private fun createRevenueCard(): FrameLayout {
        return FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }
            background = createCardBackground()
            elevation = dpToPx(8).toFloat()
            setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(24))

            addView(LinearLayout(this@AdminActivity).apply {
                orientation = LinearLayout.VERTICAL

                addView(TextView(this@AdminActivity).apply {
                    text = "💰 TỔNG DOANH THU"
                    setTextColor(Color.parseColor("#999999"))
                    textSize = 12f
                    setTypeface(null, Typeface.BOLD)
                    letterSpacing = 0.15f
                })

                revenueText = TextView(this@AdminActivity).apply {
                    text = "Đang tải..."
                    setTextColor(Color.parseColor("#E50914"))
                    textSize = 32f
                    setTypeface(null, Typeface.BOLD)
                    setPadding(0, dpToPx(8), 0, 0)
                }
                addView(revenueText)
            })
        }
    }

    private fun createStatsGrid(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL

            // Row 1: Users và Films
            addView(LinearLayout(this@AdminActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                weightSum = 2f

                addView(createStatCard("👥", "NGƯỜI DÙNG"))
                addView(createStatCard("🎬", "PHIM"))
            })

            // Row 2: Events và Promotions
            addView(LinearLayout(this@AdminActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                weightSum = 2f

                addView(createStatCard("🎯", "SỰ KIỆN"))
                addView(createStatCard("🎁", "KHUYẾN MÃI"))
            })
        }
    }

    private fun createStatCard(icon: String, label: String): SquareFrameLayout {
        return SquareFrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                if (label == "NGƯỜI DÙNG" || label == "SỰ KIỆN") {
                    rightMargin = dpToPx(8)
                } else {
                    leftMargin = dpToPx(8)
                }
                bottomMargin = dpToPx(16)
            }
            background = createCardBackground()
            elevation = dpToPx(6).toFloat()
            setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12))

            addView(LinearLayout(this@AdminActivity).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )

                // Icon
                addView(TextView(this@AdminActivity).apply {
                    text = icon
                    textSize = 32f
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                })

                // Label
                addView(TextView(this@AdminActivity).apply {
                    text = label
                    setTextColor(Color.parseColor("#999999"))
                    textSize = 10f
                    setTypeface(null, Typeface.BOLD)
                    letterSpacing = 0.1f
                    gravity = Gravity.CENTER
                    setPadding(0, dpToPx(6), 0, dpToPx(4))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                })

                // Count value - GÁN REFERENCE NGAY KHI TẠO
                val countText = TextView(this@AdminActivity).apply {
                    text = "0"
                    setTextColor(Color.parseColor("#E50914"))
                    textSize = 22f
                    setTypeface(null, Typeface.BOLD)
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                // GÁN VÀO BIẾN CLASS TRƯỚC KHI addView
                when (label) {
                    "NGƯỜI DÙNG" -> usersCountText = countText
                    "PHIM" -> filmsCountText = countText
                    "SỰ KIỆN" -> eventsCountText = countText
                    "KHUYẾN MÃI" -> promotionsCountText = countText
                }

                addView(countText)
            })
        }
    }

    private fun createContentSection(userEmail: String?): LinearLayout {
        return createSection(
            icon = "🎬",
            title = "QUẢN LÝ NỘI DUNG"
        ).apply {
            addView(createModernButton("Quản lý Phim") {
                startActivity(Intent(this@AdminActivity, FilmManagerActivity::class.java))
            })
            addView(createModernButton("Danh sách Phim") {
                startActivity(Intent(this@AdminActivity, FilmAdminActivity::class.java))
            })
        }
    }

    private fun createBusinessSection(userEmail: String?): LinearLayout {
        return createSection(
            icon = "💼",
            title = "QUẢN LÝ KINH DOANH"
        ).apply {
            addView(createModernButton("Quản lý Vé") {
                val intent = Intent(this@AdminActivity, SelectProvinceActivity::class.java)
                intent.putExtra("userEmail", userEmail)
                startActivity(intent)
            })
            addView(createModernButton("Quản lý Thanh toán") {
                startActivity(Intent(this@AdminActivity, ConfirmationActivity::class.java))
            })
        }
    }

    private fun createMarketingSection(): LinearLayout {
        return createSection(
            icon = "🎯",
            title = "MARKETING VÀ SỰ KIỆN"
        ).apply {
            addView(createModernButton("Quản lý Sự kiện") {
                startActivity(Intent(this@AdminActivity, EventManagementActivity::class.java))
            })
            addView(createModernButton("Quản lý Khuyến mãi") {
                startActivity(Intent(this@AdminActivity, PromotionManagementActivity::class.java))
            })
        }
    }

    private fun createSection(icon: String, title: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = createCardBackground()
            setPadding(dpToPx(24), dpToPx(20), dpToPx(24), dpToPx(20))
            elevation = dpToPx(6).toFloat()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }

            addView(LinearLayout(this@AdminActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL

                addView(TextView(this@AdminActivity).apply {
                    text = icon
                    textSize = 18f
                    setPadding(0, 0, dpToPx(10), 0)
                })

                addView(TextView(this@AdminActivity).apply {
                    text = title
                    setTextColor(Color.parseColor("#FF4458"))
                    textSize = 13f
                    setTypeface(null, Typeface.BOLD)
                    letterSpacing = 0.1f
                })

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dpToPx(16)
                }
            })
        }
    }

    private fun createModernButton(text: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            setTextColor(Color.WHITE)
            textSize = 15f
            setTypeface(null, Typeface.BOLD)
            isAllCaps = false
            background = createButtonBackground()
            elevation = dpToPx(4).toFloat()
            stateListAnimator = null
            letterSpacing = 0.05f

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(52)
            ).apply {
                bottomMargin = dpToPx(12)
            }

            setOnClickListener { onClick() }
        }
    }

    private fun createLogoutButton(): Button {
        return Button(this).apply {
            text = "ĐĂNG XUẤT"
            setTextColor(Color.WHITE)
            textSize = 15f
            setTypeface(null, Typeface.BOLD)
            isAllCaps = true
            background = createLogoutButtonBackground()
            elevation = dpToPx(4).toFloat()
            stateListAnimator = null
            letterSpacing = 0.1f

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(52)
            ).apply {
                topMargin = dpToPx(8)
            }

            setOnClickListener {
                finish()
            }
        }
    }

    private fun createGradientDrawable(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.parseColor("#1A0505"),
                Color.parseColor("#0D0D0D"),
                Color.parseColor("#000000")
            )
        )
    }

    private fun createCircleDrawable(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
        }
    }

    private fun createLogoBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            colors = intArrayOf(
                Color.parseColor("#E50914"),
                Color.parseColor("#B00710")
            )
            gradientType = GradientDrawable.RADIAL_GRADIENT
            gradientRadius = dpToPx(50).toFloat()
        }
    }

    private fun createCardBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.parseColor("#1C1C1C"))
            cornerRadius = dpToPx(16).toFloat()
            setStroke(dpToPx(1), Color.parseColor("#2A2A2A"))
        }
    }

    private fun createButtonBackground(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor("#E50914"),
                Color.parseColor("#FF1F2A")
            )
        ).apply {
            cornerRadius = dpToPx(12).toFloat()
        }
    }

    private fun createLogoutButtonBackground(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor("#B00710"),
                Color.parseColor("#8B0000")
            )
        ).apply {
            cornerRadius = dpToPx(12).toFloat()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}

// ===== CUSTOM VIEW ĐỂ TẠO CARD VUÔNG =====
class SquareFrameLayout(context: Context) : FrameLayout(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Lấy chiều rộng
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        // Set chiều cao bằng chiều rộng để tạo hình vuông
        val width = measuredWidth
        setMeasuredDimension(width, width)
    }
}