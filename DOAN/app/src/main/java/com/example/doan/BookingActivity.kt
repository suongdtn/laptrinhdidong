package com.example.doan

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class BookingActivity : AppCompatActivity() {
    private lateinit var filmTitle: TextView
    private lateinit var filmDetails: TextView
    private lateinit var moviePoster: ImageView
    private lateinit var buyTicketButton: Button
    private lateinit var lich1: Button
    private lateinit var lich2: Button
    private lateinit var lich3: Button
    private lateinit var lich4: Button
    private lateinit var lich5: Button
    private lateinit var buttons: List<Button>
    private lateinit var noScheduleText: TextView
    private lateinit var timeSlotsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cinema = intent.getStringExtra("cinema") ?: "Không xác định"
        val title = intent.getStringExtra("FILM_TITLE")
        val details = intent.getStringExtra("FILM_DETAILS")
        val posterUrl = intent.getStringExtra("FILM_POSTER_URL")
        val province = intent.getStringExtra("province") ?: "Không xác định"
        val iEmail = intent.getStringExtra("userEmail")

        setContentView(createLayout(cinema, title, details, posterUrl, province, iEmail))

        setupListeners(cinema, title, details, posterUrl, province, iEmail)
        loadMovieData(title, cinema)
    }

    private fun createLayout(cinema: String, title: String?, details: String?,
                             posterUrl: String?, province: String, iEmail: String?): ScrollView {
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.BLACK)
        }

        mainLayout.addView(createHeader())

        val scrollContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        scrollContent.addView(createPosterSection(posterUrl))
        scrollContent.addView(createInfoSection(title, details))

        return ScrollView(this).apply {
            addView(mainLayout)
            mainLayout.addView(scrollContent)
            isFillViewport = true
        }
    }

    private fun createHeader(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor("#D32F2F"))
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(56)
            )
            elevation = dp(4).toFloat()

            addView(ImageView(this@BookingActivity).apply {
                setImageResource(android.R.drawable.ic_menu_revert)
                setColorFilter(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(dp(28), dp(28))
                setOnClickListener { finish() }
            })

            addView(TextView(this@BookingActivity).apply {
                text = "Chi tiết phim"
                setTextColor(Color.WHITE)
                textSize = 20f
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginStart = dp(16)
                }
            })

            addView(View(this@BookingActivity).apply {
                layoutParams = LinearLayout.LayoutParams(dp(28), dp(28))
            })
        }
    }

    private fun createPosterSection(posterUrl: String?): FrameLayout {
        return FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(280)
            )

            moviePoster = ImageView(this@BookingActivity).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setBackgroundColor(Color.parseColor("#1A1A1A"))
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
            addView(moviePoster)

            if (posterUrl != null) {
                Glide.with(this@BookingActivity)
                    .load(posterUrl)
                    .into(moviePoster)
            }
        }
    }

    private fun createInfoSection(title: String?, details: String?): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.BLACK)
            setPadding(dp(16), dp(16), dp(16), dp(16))

            filmTitle = TextView(this@BookingActivity).apply {
                text = title ?: "Tên phim mẫu"
                textSize = 26f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.WHITE)
                letterSpacing = 0.01f
            }
            addView(filmTitle)

            filmDetails = TextView(this@BookingActivity).apply {
                text = details ?: "Mô tả phim mẫu"
                setTextColor(Color.parseColor("#B0B0B0"))
                textSize = 15f
                setPadding(0, dp(8), 0, 0)
                setLineSpacing(dp(4).toFloat(), 1f)
            }
            addView(filmDetails)

            addView(View(this@BookingActivity).apply {
                setBackgroundColor(Color.parseColor("#2A2A2A"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(1)
                ).apply {
                    topMargin = dp(20)
                    bottomMargin = dp(16)
                }
            })

            addView(TextView(this@BookingActivity).apply {
                text = "Chọn ngày chiếu"
                setTextColor(Color.WHITE)
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setPadding(0, 0, 0, dp(12))
            })

            addView(createDateSelector())

            addView(TextView(this@BookingActivity).apply {
                text = "Chọn suất chiếu"
                setTextColor(Color.WHITE)
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setPadding(0, dp(24), 0, dp(12))
            })

            // Container cho time slots và message không có lịch
            val scheduleContainer = LinearLayout(this@BookingActivity).apply {
                orientation = LinearLayout.VERTICAL
            }

            timeSlotsContainer = createTimeSlots()
            scheduleContainer.addView(timeSlotsContainer)

            noScheduleText = TextView(this@BookingActivity).apply {
                text = "Không có lịch chiếu"
                setTextColor(Color.parseColor("#FF6B6B"))
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
                visibility = View.GONE
                setPadding(0, dp(24), 0, dp(24))
            }
            scheduleContainer.addView(noScheduleText)

            addView(scheduleContainer)

            buyTicketButton = Button(this@BookingActivity).apply {
                text = "ĐẶT VÉ NGAY"
                textSize = 17f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#D32F2F"))
                isAllCaps = true
                letterSpacing = 0.05f
                elevation = dp(4).toFloat()
                visibility = View.GONE
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(56)
                ).apply {
                    topMargin = dp(24)
                    bottomMargin = dp(16)
                }
            }
            addView(buyTicketButton)
        }
    }

    private fun createDateSelector(): HorizontalScrollView {
        return HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false

            val dateLayout = LinearLayout(this@BookingActivity).apply {
                orientation = LinearLayout.HORIZONTAL

                val dates = listOf("CN\n15/12", "T2\n16/12", "T3\n17/12",
                    "T4\n18/12", "T5\n19/12", "T6\n20/12", "T7\n21/12")

                buttons = dates.mapIndexed { index, date ->
                    Button(this@BookingActivity).apply {
                        text = date
                        textSize = 13f
                        setBackgroundColor(Color.parseColor("#1A1A1A"))
                        setTextColor(Color.WHITE)
                        isAllCaps = false
                        layoutParams = LinearLayout.LayoutParams(dp(70), dp(70)).apply {
                            if (index < dates.size - 1) marginEnd = dp(8)
                        }
                    }
                }
                buttons.forEach { addView(it) }
            }
            addView(dateLayout)
        }
    }

    private fun createTimeSlots(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL

            val row1 = LinearLayout(this@BookingActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                weightSum = 2f

                lich1 = createTimeButton("07:00", 1f, dp(8))
                lich2 = createTimeButton("10:00", 1f, 0)
                addView(lich1)
                addView(lich2)
            }
            addView(row1)

            val row2 = LinearLayout(this@BookingActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                weightSum = 2f
                setPadding(0, dp(10), 0, 0)

                lich3 = createTimeButton("13:00", 1f, dp(8))
                lich4 = createTimeButton("16:00", 1f, 0)
                addView(lich3)
                addView(lich4)
            }
            addView(row2)

            lich5 = createTimeButton("19:00", 1f, 0).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(56)
                ).apply {
                    topMargin = dp(10)
                }
            }
            addView(lich5)

            lich1.visibility = View.GONE
            lich2.visibility = View.GONE
            lich3.visibility = View.GONE
            lich4.visibility = View.GONE
            lich5.visibility = View.GONE
        }
    }

    private fun createTimeButton(time: String, weight: Float, marginEnd: Int): Button {
        return Button(this).apply {
            text = time
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setBackgroundColor(Color.parseColor("#1A1A1A"))
            setTextColor(Color.WHITE)
            isAllCaps = false
            layoutParams = LinearLayout.LayoutParams(0, dp(56), weight).apply {
                this.marginEnd = marginEnd
            }
        }
    }

    private fun setupListeners(cinema: String, title: String?, details: String?,
                               posterUrl: String?, province: String, iEmail: String?) {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val selectedIndex = when (dayOfWeek) {
            Calendar.SUNDAY -> 0
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            else -> -1
        }

        if (selectedIndex != -1) {
            buttons[selectedIndex].setBackgroundColor(Color.GRAY)
            buttons[selectedIndex].setTextColor(Color.WHITE)
        }

        buyTicketButton.setOnClickListener {
            Toast.makeText(this, "Đặt vé cho $title thành công!", Toast.LENGTH_SHORT).show()
        }

        val timeButtons = listOf(lich1, lich2, lich3, lich4, lich5)
        timeButtons.forEach { btn ->
            btn.setOnClickListener {
                val intent = Intent(this, SelectSeatActivity::class.java).apply {
                    putExtra("cinema", cinema)
                    putExtra("userEmail", iEmail)
                    putExtra("province", province)
                    putExtra("FILM_TITLE", title)
                    putExtra("FILM_DETAILS", details)
                    putExtra("FILM_POSTER_URL", posterUrl)
                    putExtra("LICH", btn.text.toString())
                }
                startActivity(intent)
            }
        }
    }

    private fun loadMovieData(title: String?, cinema: String) {
        val CN = mutableListOf<Int>()
        val T2 = mutableListOf<Int>()
        val T3 = mutableListOf<Int>()
        val T4 = mutableListOf<Int>()
        val T5 = mutableListOf<Int>()
        val T6 = mutableListOf<Int>()
        val T7 = mutableListOf<Int>()

        val db = FirebaseFirestore.getInstance()
        db.collection("Films")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    for (document in snapshots.documents) {
                        val name = document.getString("name") ?: ""

                        if (name == title) {
                            val cinemas = document.get("cinemas") as? List<Map<String, Any>> ?: emptyList()

                            for (cinemaData in cinemas) {
                                val theater = cinemaData["theater"] as? String ?: ""

                                if (theater == cinema) {
                                    val schedule = cinemaData["schedule"] as? Map<String, Any> ?: emptyMap()

                                    (schedule["Chủ Nhật"] as? List<Long>)?.forEach { CN.add(it.toInt()) }
                                    (schedule["Thứ Hai"] as? List<Long>)?.forEach { T2.add(it.toInt()) }
                                    (schedule["Thứ Ba"] as? List<Long>)?.forEach { T3.add(it.toInt()) }
                                    (schedule["Thứ Tư"] as? List<Long>)?.forEach { T4.add(it.toInt()) }
                                    (schedule["Thứ Năm"] as? List<Long>)?.forEach { T5.add(it.toInt()) }
                                    (schedule["Thứ Sáu"] as? List<Long>)?.forEach { T6.add(it.toInt()) }
                                    (schedule["Thứ Bảy"] as? List<Long>)?.forEach { T7.add(it.toInt()) }

                                    setupDateButtons(CN, T2, T3, T4, T5, T6, T7)
                                    break
                                }
                            }
                            break
                        }
                    }
                }
            }
    }

    private fun setupDateButtons(CN: List<Int>, T2: List<Int>, T3: List<Int>,
                                 T4: List<Int>, T5: List<Int>, T6: List<Int>, T7: List<Int>) {
        val schedules = listOf(CN, T2, T3, T4, T5, T6, T7)
        val timeButtons = listOf(lich1, lich2, lich3, lich4, lich5)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                // Reset tất cả button ngày về màu mặc định
                buttons.forEach { btn ->
                    btn.setBackgroundColor(Color.parseColor("#1A1A1A"))
                    btn.setTextColor(Color.WHITE)
                }

                // Set button được chọn thành màu đỏ
                button.setBackgroundColor(Color.parseColor("#D32F2F"))
                button.setTextColor(Color.WHITE)

                // Ẩn tất cả button giờ chiếu trước
                hideButtons()

                // Hiển thị các button giờ chiếu theo lịch
                val availableTimes = schedules[index]

                // Nếu có lịch chiếu trong ngày này
                if (availableTimes.isNotEmpty()) {
                    // Ẩn text "Không có lịch chiếu"
                    noScheduleText.visibility = View.GONE
                    timeSlotsContainer.visibility = View.VISIBLE

                    availableTimes.forEach { time ->
                        when (time) {
                            1 -> {
                                lich1.visibility = View.VISIBLE
                                lich1.setBackgroundColor(Color.parseColor("#1A1A1A"))
                                lich1.setTextColor(Color.WHITE)
                                lich1.isEnabled = true
                            }
                            2 -> {
                                lich2.visibility = View.VISIBLE
                                lich2.setBackgroundColor(Color.parseColor("#1A1A1A"))
                                lich2.setTextColor(Color.WHITE)
                                lich2.isEnabled = true
                            }
                            3 -> {
                                lich3.visibility = View.VISIBLE
                                lich3.setBackgroundColor(Color.parseColor("#1A1A1A"))
                                lich3.setTextColor(Color.WHITE)
                                lich3.isEnabled = true
                            }
                            4 -> {
                                lich4.visibility = View.VISIBLE
                                lich4.setBackgroundColor(Color.parseColor("#1A1A1A"))
                                lich4.setTextColor(Color.WHITE)
                                lich4.isEnabled = true
                            }
                            5 -> {
                                lich5.visibility = View.VISIBLE
                                lich5.setBackgroundColor(Color.parseColor("#1A1A1A"))
                                lich5.setTextColor(Color.WHITE)
                                lich5.isEnabled = true
                            }
                        }
                    }
                } else {
                    // Nếu không có lịch nào, hiện text "Không có lịch chiếu"
                    timeSlotsContainer.visibility = View.GONE
                    noScheduleText.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun hideButtons() {
        lich1.visibility = View.GONE
        lich2.visibility = View.GONE
        lich3.visibility = View.GONE
        lich4.visibility = View.GONE
        lich5.visibility = View.GONE
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}