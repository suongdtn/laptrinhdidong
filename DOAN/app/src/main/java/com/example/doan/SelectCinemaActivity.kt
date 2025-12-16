package com.example.doan

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SelectCinemaActivity : AppCompatActivity() {

    // Màu sắc theme đồng bộ
    private val COLOR_BACKGROUND = Color.parseColor("#0D0D0D")
    private val COLOR_CARD = Color.parseColor("#1A1A1A")
    private val COLOR_PRIMARY = Color.parseColor("#E50914")
    private val COLOR_TEXT_PRIMARY = Color.parseColor("#FFFFFF")
    private val COLOR_TEXT_SECONDARY = Color.parseColor("#999999")
    private val COLOR_BORDER = Color.parseColor("#2A2A2A")

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
            setOnClickListener {
                finish()
            }
        }

        // TextView Province/Rạp
        val tvProvince = TextView(this).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            text = "Rạp"
            setTextColor(COLOR_TEXT_PRIMARY)
            textSize = 22f
            setTypeface(null, Typeface.BOLD)
            letterSpacing = 0.1f
        }

        headerLayout.addView(btnBack)
        headerLayout.addView(tvProvince)

        // Container cho ListView với padding
        val contentLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
        }

        // ListView
        val listViewCinemas = ListView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(0, 0, 0, 0)
            divider = null
            dividerHeight = dpToPx(12)
            selector = createListSelector()
        }

        contentLayout.addView(listViewCinemas)
        mainLayout.addView(headerLayout)
        mainLayout.addView(contentLayout)
        setContentView(mainLayout)

        // Logic xử lý
        val title = intent.getStringExtra("FILM_TITLE")
        val details = intent.getStringExtra("FILM_DETAILS")
        val posterUrl = intent.getStringExtra("FILM_POSTER_URL")
        val iEmail = intent.getStringExtra("userEmail")
        val province = intent.getStringExtra("province") ?: "Không xác định"

        tvProvince.text = "$province"

        val cinemas = when (province) {
            "Hà Nội" -> listOf("CGV Vincom", "Lotte Landmark", "BHD Star")
            "TP. Hồ Chí Minh" -> listOf("CGV Sư Vạn Hạnh", "Mega GS", "BHD Phạm Ngọc Thạch")
            "Đà Nẵng" -> listOf("CGV Đà Nẵng", "Lotte Đà Nẵng")
            else -> listOf("Chưa có rạp tại khu vực này")
        }

        // Custom adapter cho ListView với theme đẹp
        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            cinemas
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val itemLayout = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT,
                        AbsListView.LayoutParams.WRAP_CONTENT
                    )
                    background = createCinemaCardBackground()
                    setPadding(dpToPx(20), dpToPx(18), dpToPx(20), dpToPx(18))
                    gravity = Gravity.CENTER_VERTICAL
                    elevation = dpToPx(2).toFloat()
                }

                // Icon rạp
                val iconView = TextView(context).apply {
                    text = "🎬"
                    textSize = 28f
                    layoutParams = LinearLayout.LayoutParams(
                        dpToPx(44),
                        dpToPx(44)
                    ).apply {
                        marginEnd = dpToPx(16)
                    }
                    gravity = Gravity.CENTER
                }

                // Tên rạp
                val textView = TextView(context).apply {
                    text = getItem(position)
                    setTextColor(COLOR_TEXT_PRIMARY)
                    textSize = 17f
                    setTypeface(null, Typeface.BOLD)
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }

                // Arrow icon
                val arrowView = TextView(context).apply {
                    text = "›"
                    textSize = 32f
                    setTextColor(COLOR_PRIMARY)
                    setTypeface(null, Typeface.BOLD)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    gravity = Gravity.CENTER
                }

                itemLayout.addView(iconView)
                itemLayout.addView(textView)
                itemLayout.addView(arrowView)

                return itemLayout
            }
        }

        listViewCinemas.adapter = adapter

        listViewCinemas.setOnItemClickListener { _, _, position, _ ->
            val selectedCinema = cinemas[position]

            // Luôn tạo Intent đến BookingActivity
            val intent = Intent(this, BookingActivity::class.java)

            // Luôn truyền lại TẤT CẢ dữ liệu có được
            intent.putExtra("cinema", selectedCinema)
            intent.putExtra("province", province)
            intent.putExtra("userEmail", iEmail)
            intent.putExtra("FILM_TITLE", title)
            intent.putExtra("FILM_DETAILS", details)
            intent.putExtra("FILM_POSTER_URL", posterUrl)

            startActivity(intent)
        }
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

    private fun createCinemaCardBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(COLOR_CARD)
            cornerRadius = dpToPx(16).toFloat()
            setStroke(dpToPx(1), COLOR_BORDER)
        }
    }

    private fun createListSelector(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor("#1AE50914"))
            cornerRadius = dpToPx(16).toFloat()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}