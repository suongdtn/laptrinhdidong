package com.example.doan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SelectSeatActivity : AppCompatActivity() {

    private lateinit var seatAdapter: SeatAdapter
    private lateinit var btnConfirm: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ===== TẠO UI BẰNG CODE =====
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.BLACK)
            setPadding(dpToPx(16), dpToPx(32), dpToPx(16), dpToPx(16))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Header với nút back và tiêu đề
        val headerLayout = RelativeLayout(this).apply {
            setBackgroundColor(Color.RED)
            setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(56)
            )
        }

        val btnBack = ImageView(this).apply {
            id = View.generateViewId()
            setImageResource(R.drawable.baseline_keyboard_backspace_24)
            layoutParams = RelativeLayout.LayoutParams(dpToPx(24), dpToPx(24)).apply {
                addRule(RelativeLayout.ALIGN_PARENT_START)
                addRule(RelativeLayout.CENTER_VERTICAL)
            }
            setOnClickListener { finish() }
        }

        val tvTitle = TextView(this).apply {
            text = "Chọn Ghế"
            textSize = 24f
            setTextColor(Color.WHITE)
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
        }

        headerLayout.addView(btnBack)
        headerLayout.addView(tvTitle)
        mainLayout.addView(headerLayout)

        // TextView hiển thị tên rạp
        val tvCinema = TextView(this).apply {
            text = "Rạp:"
            textSize = 18f
            setTextColor(Color.WHITE)
            setPadding(0, 0, 0, dpToPx(8))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(16)
            }
        }
        mainLayout.addView(tvCinema)

        val seatContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(dpToPx(380), dpToPx(495))
        }

        // GridView ghế
        val gridView = GridView(this).apply {
            id = View.generateViewId()
            numColumns = 5
            horizontalSpacing = dpToPx(8)
            verticalSpacing = dpToPx(8)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(336)
            ).apply {
                bottomMargin = dpToPx(16)
            }
        }
        seatContainer.addView(gridView)

        // Chú thích ghế
        val legendLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }
        }

        // Ghế đã chọn
        val selectedBox = LinearLayout(this).apply {
            setBackgroundColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(dpToPx(40), dpToPx(40)).apply {
                marginEnd = dpToPx(8)
            }
        }
        val selectedLabel = TextView(this).apply {
            text = "Ghế đã chọn"
            textSize = 16f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = dpToPx(24)
            }
        }

        // Ghế chưa chọn
        val availableBox = LinearLayout(this).apply {
            setBackgroundColor(Color.DKGRAY)
            layoutParams = LinearLayout.LayoutParams(dpToPx(40), dpToPx(40)).apply {
                marginEnd = dpToPx(8)
            }
        }
        val availableLabel = TextView(this).apply {
            text = "Ghế chưa chọn"
            textSize = 16f
            setTextColor(Color.WHITE)
        }

        legendLayout.addView(selectedBox)
        legendLayout.addView(selectedLabel)
        legendLayout.addView(availableBox)
        legendLayout.addView(availableLabel)
        seatContainer.addView(legendLayout)

        // Màn hình
        val tvScreen = TextView(this).apply {
            text = "MÀN HÌNH"
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setBackgroundColor(Color.RED)
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(dpToPx(333), dpToPx(52)).apply {
                topMargin = dpToPx(8)
            }
        }
        seatContainer.addView(tvScreen)
        mainLayout.addView(seatContainer)

        // Nút xác nhận
        btnConfirm = Button(this).apply {
            text = "Xác nhận đặt vé"
            textSize = 18f
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(16)
            }
        }
        mainLayout.addView(btnConfirm)

        setContentView(mainLayout)

        // ===== LOGIC XỬ LÝ =====
        val cinema = intent.getStringExtra("cinema") ?: ""
        val title = intent.getStringExtra("FILM_TITLE") ?: ""
        val lich = intent.getStringExtra("LICH") ?: ""
// Thêm hai biến này nếu bạn cần hiển thị Tỉnh/Địa điểm và Email
        val province = intent.getStringExtra("province") ?: ""
        val iEmail = intent.getStringExtra("userEmail") ?: ""

        tvCinema.text = "Rạp: $cinema"

        // Danh sách ghế
        val seats = mutableListOf<String>()
        for (r in 'A'..'F') for (c in 1..5) seats.add("$r$c")

        seatAdapter = SeatAdapter(this, seats)
        gridView.adapter = seatAdapter

        // Load ghế đã đặt từ Firestore
        db.collection("Ticker")
            .whereEqualTo("phim", title)
            .whereEqualTo("rap", cinema)
            .whereEqualTo("lich", lich)
            .get()
            .addOnSuccessListener { result ->
                val gheDaDat = mutableListOf<String>()

                for (doc in result) {
                    val ghe = doc.getString("ghe")
                    ghe?.split(",")?.map { it.trim() }?.let {
                        gheDaDat.addAll(it)
                    }
                }

                seatAdapter.disableSeats(gheDaDat)
            }

        // Click ghế
        gridView.setOnItemClickListener { _, _, pos, _ ->
            val seat = seats[pos]

            if (seatAdapter.getDisabledSeats().contains(seat)) {
                Toast.makeText(this, "Ghế đã được đặt", Toast.LENGTH_SHORT).show()
                return@setOnItemClickListener
            }

            seatAdapter.toggleSeatSelection(seat)
            btnConfirm.visibility =
                if (seatAdapter.getSelectedSeats().isNotEmpty()) View.VISIBLE else View.GONE
        }




// ... (Đoạn xử lý GridView và Confirm) ...

        btnConfirm.setOnClickListener {
            val selected = seatAdapter.getSelectedSeats()
            val intent = Intent(this, ConfirmActivity::class.java)

            // Đảm bảo truyền TẤT CẢ thông tin cần cho màn hình xác nhận
            intent.putExtra("Ghe", selected.joinToString(", ")) // Vị trí ghế
            intent.putExtra("FILM_TITLE", title) // Phim
            intent.putExtra("LICH", lich) // Suất chiếu
            intent.putExtra("cinema", cinema) // Rạp
            intent.putExtra("province", province) // Địa điểm/Tỉnh
            intent.putExtra("userEmail", iEmail) // Email người dùng

            // Bạn cần tính totalPrice hoặc numSeats để truyền sang ConfirmActivity cho logic tính giá
            // Trong ConfirmActivity, giá vé được tính lại từ chuỗi ghế. Chỉ cần truyền các thông tin trên là đủ.

            startActivity(intent)
        }
    }

    // Hàm chuyển dp sang px
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}