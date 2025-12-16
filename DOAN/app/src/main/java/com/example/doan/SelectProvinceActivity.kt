package com.example.doan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SelectProvinceActivity : AppCompatActivity() {

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
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(Color.parseColor("#E50914"))
            setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12))
        }

        // Back Button
        val btnBack = ImageView(this).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_START)
                addRule(RelativeLayout.CENTER_VERTICAL)
            }
            setImageResource(android.R.drawable.ic_menu_revert)
            setColorFilter(Color.WHITE)
            contentDescription = "Back button"
            setOnClickListener {
                finish()
            }
        }

        // Title TextView
        val tvTitle = TextView(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            text = "Chọn tỉnh"
            setTextColor(Color.WHITE)
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        headerLayout.addView(btnBack)
        headerLayout.addView(tvTitle)

        // ListView
        val listView = ListView(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
            divider = null
            dividerHeight = dpToPx(16)
        }

        mainLayout.addView(headerLayout)
        mainLayout.addView(listView)

        setContentView(mainLayout)

        val title = intent.getStringExtra("FILM_TITLE")
        val details = intent.getStringExtra("FILM_DETAILS")
        val posterUrl = intent.getStringExtra("FILM_POSTER_URL")
        val iEmail = intent.getStringExtra("userEmail")

        val provinces = listOf("Hà Nội", "TP. Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ")

        // Custom adapter for dark theme
        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            provinces
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val textView = TextView(context).apply {
                    text = getItem(position)
                    setTextColor(Color.WHITE)
                    textSize = 16f
                    setPadding(dpToPx(40), dpToPx(40), dpToPx(40), dpToPx(40))
                    setBackgroundColor(Color.parseColor("#1A1A1A"))
                    layoutParams = AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT,
                        AbsListView.LayoutParams.WRAP_CONTENT
                    )
                }
                return textView
            }
        }

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedProvince = provinces[position]
            val intent = Intent(this, SelectCinemaActivity::class.java)

            // Đảm bảo truyền lại tất cả các thông tin phim và user
            intent.putExtra("province", selectedProvince)
            intent.putExtra("userEmail", iEmail)
            intent.putExtra("FILM_TITLE", title)
            intent.putExtra("FILM_DETAILS", details)
            intent.putExtra("FILM_POSTER_URL", posterUrl)

            startActivity(intent)

        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}