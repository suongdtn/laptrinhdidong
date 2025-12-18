package com.example.doan

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import com.google.firebase.database.ValueEventListener
class ConfirmActivity : AppCompatActivity() {

    private lateinit var databaseFS: CollectionReference
    private lateinit var databasetickerFS: CollectionReference
    private lateinit var databaseRT: DatabaseReference

    private var tenn: String = ""
    private lateinit var iEmail: String
    private lateinit var formattedDate: String
    private lateinit var title: String
    private lateinit var lich: String
    private lateinit var ghe: String
    private lateinit var cinema: String
    private var gia: Int = 0
    private var soLuongGhe: Int = 0
    private val BASE_PRICE_PER_TICKET = 50000
    private val TRANSACTION_FEE = 2000

    // Màu sắc theme đồng bộ
    private val COLOR_BACKGROUND = Color.parseColor("#0D0D0D")
    private val COLOR_CARD = Color.parseColor("#1A1A1A")
    private val COLOR_PRIMARY = Color.parseColor("#E50914")
    private val COLOR_TEXT_PRIMARY = Color.parseColor("#FFFFFF")
    private val COLOR_TEXT_SECONDARY = Color.parseColor("#999999")
    private val COLOR_DIVIDER = Color.parseColor("#2A2A2A")
    private val COLOR_BUTTON = Color.parseColor("#E50914")

    // UI Components
    private lateinit var txtMovieName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtPhoneNumber: TextView
    private lateinit var txtMovieTitle: TextView
    private lateinit var txtShowTime: TextView
    private lateinit var txtRoom: TextView
    private lateinit var txtCinema: TextView
    private lateinit var txtSeats: TextView
    private lateinit var txtPrice: TextView
    private lateinit var txtTransactionFee: TextView
    private lateinit var btnprice: TextView
    private lateinit var btnConfirm: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tạo giao diện hoàn toàn bằng Kotlin
        setContentView(createMainLayout())

        // Nhận dữ liệu từ Intent
        val movieName = intent.getStringExtra("movieName") ?: "Không xác định"
        val showTime = intent.getStringExtra("showTime") ?: ""
        cinema = intent.getStringExtra("cinema") ?: "Không xác định"
        val seats = intent.getStringExtra("seats") ?: ""
        val price = intent.getIntExtra("totalPrice", 0)
        title = intent.getStringExtra("FILM_TITLE") ?: ""
        lich = intent.getStringExtra("LICH") ?: ""
        ghe = intent.getStringExtra("Ghe") ?: ""
        iEmail = intent.getStringExtra("userEmail") ?: ""
        val details = intent.getStringExtra("FILM_DETAILS")
        val posterUrl = intent.getStringExtra("FILM_POSTER_URL")
        val province = intent.getStringExtra("province") ?: "Không xác định"
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        formattedDate = currentDate.format(formatter)

        // Khởi tạo Firebase
        databasetickerFS = FirebaseFirestore.getInstance().collection("Ticker")
        databaseFS = FirebaseFirestore.getInstance().collection("Users")
        databaseRT = FirebaseDatabase.getInstance().getReference("Users")

        // Truy vấn tên người dùng từ Firestore
        databaseFS.whereEqualTo("email", iEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDocument = querySnapshot.documents.first()
                    val name = userDocument.getString("name")
                    txtMovieName.text = name ?: ""
                    tenn = name ?: ""
                } else {
                    Toast.makeText(applicationContext, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Lỗi khi lấy dữ liệu người dùng từ Firestore!", Toast.LENGTH_SHORT).show()
            }

        // Tính toán giá vé
        if (ghe.isNotEmpty()) {
            soLuongGhe = ghe.split(", ").filter { it.isNotEmpty() }.size
        }
        gia = (BASE_PRICE_PER_TICKET * soLuongGhe) + TRANSACTION_FEE

        // Cập nhật giao diện
        updateUI(province)
    }

    private fun createMainLayout(): ScrollView {
        val scrollView = ScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(COLOR_BACKGROUND)
        }

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, 0, 0, dp(20))
        }

        // Header
        mainLayout.addView(createHeader())

        // Content container
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        // Payment method section
        contentLayout.addView(createPaymentMethodSection())

        // Customer info card
        contentLayout.addView(createCustomerInfoCard())

        // Movie info card
        contentLayout.addView(createMovieInfoCard())

        // Price details card
        contentLayout.addView(createPriceDetailsCard())

        // Total price
        contentLayout.addView(createTotalPriceSection())

        // Confirm button
        contentLayout.addView(createConfirmButton())

        mainLayout.addView(contentLayout)
        scrollView.addView(mainLayout)

        return scrollView
    }

    private fun createHeader(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#B00710"),
                    Color.parseColor("#E50914"),
                    Color.parseColor("#FF1F2A")
                )
            )
            setPadding(dp(16), dp(20), dp(16), dp(20))
            gravity = Gravity.CENTER_VERTICAL
            elevation = dp(8).toFloat()

            // Back button
            val btnBack = ImageView(this@ConfirmActivity).apply {
                layoutParams = LinearLayout.LayoutParams(dp(32), dp(32))
                setColorFilter(COLOR_TEXT_PRIMARY)
                setImageResource(android.R.drawable.ic_menu_revert)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setPadding(dp(4), dp(4), dp(4), dp(4))
                setOnClickListener { finish() }
            }
            addView(btnBack)

            // Title
            val title = TextView(this@ConfirmActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
                text = "XÁC NHẬN THANH TOÁN"
                textSize = 22f
                setTypeface(null, Typeface.BOLD)
                setTextColor(COLOR_TEXT_PRIMARY)
                gravity = Gravity.CENTER
                letterSpacing = 0.1f
            }
            addView(title)

            // Spacer for balance
            val spacer = View(this@ConfirmActivity).apply {
                layoutParams = LinearLayout.LayoutParams(dp(32), dp(32))
            }
            addView(spacer)
        }
    }

    private fun createPaymentMethodSection(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(16))
            }
            background = createRoundedBackground(COLOR_CARD, dp(16))
            setPadding(dp(16), dp(14), dp(16), dp(14))
            gravity = Gravity.CENTER_VERTICAL
            elevation = dp(2).toFloat()

            // Icon placeholder
            val icon = TextView(this@ConfirmActivity).apply {
                layoutParams = LinearLayout.LayoutParams(dp(40), dp(40))
                text = "💳"
                textSize = 24f
                gravity = Gravity.CENTER
            }
            addView(icon)

            // Payment method text
            val methodText = TextView(this@ConfirmActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginStart = dp(12)
                }
                text = "Thanh toán qua ngân hàng"
                textSize = 16f
                setTextColor(COLOR_TEXT_PRIMARY)
            }
            addView(methodText)

            // Change button
            val changeBtn = TextView(this@ConfirmActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                text = "Thay đổi"
                textSize = 14f
                setTextColor(COLOR_PRIMARY)
            }
            addView(changeBtn)
        }
    }

    private fun createCustomerInfoCard(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(16))
            }
            background = createRoundedBackground(COLOR_CARD, dp(16))
            setPadding(dp(16), dp(16), dp(16), dp(16))
            elevation = dp(2).toFloat()

            // Section title
            addView(createSectionTitle("THÔNG TIN KHÁCH HÀNG"))

            // Customer name
            txtMovieName = createInfoRow("Họ tên", "")
            addView(txtMovieName)

            addView(createDivider())

            // Email
            txtEmail = createInfoRow("Email", "")
            addView(txtEmail)

            addView(createDivider())

            // Booking date
            txtPhoneNumber = createInfoRow("Ngày đặt vé", "")
            addView(txtPhoneNumber)
        }
    }

    private fun createMovieInfoCard(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(16))
            }
            background = createRoundedBackground(COLOR_CARD, dp(16))
            setPadding(dp(16), dp(16), dp(16), dp(16))
            elevation = dp(2).toFloat()

            // Section title
            addView(createSectionTitle("THÔNG TIN PHIM"))

            // Movie title
            txtMovieTitle = createInfoRow("Phim", "")
            addView(txtMovieTitle)

            addView(createDivider())

            // Show time
            txtShowTime = createInfoRow("Suất chiếu", "")
            addView(txtShowTime)

            addView(createDivider())

            // Seats
            txtRoom = createInfoRow("Vị trí ghế", "")
            addView(txtRoom)

            addView(createDivider())

            // Cinema
            txtCinema = createInfoRow("Rạp", "")
            addView(txtCinema)

            addView(createDivider())

            // Location
            txtSeats = createInfoRow("Địa điểm", "")
            addView(txtSeats)
        }
    }

    private fun createPriceDetailsCard(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(16))
            }
            background = createRoundedBackground(COLOR_CARD, dp(16))
            setPadding(dp(16), dp(16), dp(16), dp(16))
            elevation = dp(2).toFloat()

            // Section title
            addView(createSectionTitle("CHI TIẾT GIÁ"))

            // Ticket price
            txtPrice = createInfoRow("Giá vé", "")
            addView(txtPrice)

            addView(createDivider())

            // Transaction fee
            txtTransactionFee = createInfoRow("Phí giao dịch", "")
            addView(txtTransactionFee)
        }
    }

    private fun createTotalPriceSection(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(16))
            }
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#B00710"),
                    Color.parseColor("#E50914")
                )
            ).apply {
                cornerRadius = dp(16).toFloat()
            }
            setPadding(dp(16), dp(16), dp(16), dp(16))
            gravity = Gravity.CENTER_VERTICAL
            elevation = dp(4).toFloat()

            val label = TextView(this@ConfirmActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
                text = "TỔNG THANH TOÁN"
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(COLOR_TEXT_PRIMARY)
            }
            addView(label)

            btnprice = TextView(this@ConfirmActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textSize = 20f
                setTypeface(null, Typeface.BOLD)
                setTextColor(COLOR_TEXT_PRIMARY)
            }
            addView(btnprice)
        }
    }

    private fun createConfirmButton(): Button {
        btnConfirm = Button(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(56)
            )
            text = "XÁC NHẬN ĐẶT VÉ"
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(COLOR_TEXT_PRIMARY)
            background = createRoundedBackground(COLOR_BUTTON, dp(16))
            elevation = dp(6).toFloat()
            letterSpacing = 0.05f
            setOnClickListener {
                Log.d("CinemaInfo", tenn)
                showQRPaymentDialog()  // Thay đổi: Gọi hàm mới
            }
        }
        return btnConfirm
    }

    private fun createSectionTitle(title: String): TextView {
        return TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(12))
            }
            text = title
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
            setTextColor(COLOR_PRIMARY)
        }
    }

    private fun createInfoRow(label: String, value: String): TextView {
        return TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(8), 0, dp(8))
            }
            text = if (value.isEmpty()) label else "$label: $value"
            textSize = 15f
            setTextColor(COLOR_TEXT_PRIMARY)
            setPadding(0, dp(4), 0, dp(4))
        }
    }

    private fun createDivider(): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(1)
            )
            setBackgroundColor(COLOR_DIVIDER)
        }
    }

    private fun createRoundedBackground(color: Int, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            cornerRadius = radius.toFloat()
        }
    }

    private fun dp(value: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun updateUI(province: String) {
        txtEmail.text = "Email: $iEmail"
        txtPhoneNumber.text = "Ngày đặt vé: $formattedDate"
        txtMovieTitle.text = "Phim: $title"
        txtRoom.text = "Vị trí ghế: $ghe"
        txtShowTime.text = "Suất chiếu: $lich"
        txtCinema.text = "Rạp: $cinema"
        txtSeats.text = "Địa điểm: $province"
        txtPrice.text = "Giá vé ($soLuongGhe vé): ${formatCurrency(BASE_PRICE_PER_TICKET * soLuongGhe)}"
        txtTransactionFee.text = "Phí giao dịch: ${formatCurrency(TRANSACTION_FEE)}"
        btnprice.text = formatCurrency(gia)
    }

    private fun formatCurrency(amount: Int): String {
        return "${String.format("%,d", amount).replace(',', '.')} đ"
    }

    private fun showQRPaymentDialog() {
        val dialog = AlertDialog.Builder(this).create()

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(24), dp(24), dp(24))
            background = createRoundedBackground(COLOR_CARD, dp(16))
            gravity = Gravity.CENTER_HORIZONTAL
        }

        // Tiêu đề
        val titleView = TextView(this).apply {
            text = "QUÉT MÃ QR ĐỂ THANH TOÁN"
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setTextColor(COLOR_TEXT_PRIMARY)
            setPadding(0, 0, 0, dp(16))
        }

        // Mã QR
        val qrImageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(200), dp(200))
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setPadding(0, 0, 0, dp(16))
        }

        // Tạo dữ liệu cho QR (JSON với email, tổng tiền, và ID giao dịch ngẫu nhiên)
        val transactionId = generateTransactionId()
        val qrData = JSONObject().apply {
            put("email", iEmail)
            put("amount", gia)
            put("transactionId", transactionId)
        }.toString()

        // Tạo bitmap QR
        val qrBitmap = generateQRCode(qrData, 200, 200)
        qrImageView.setImageBitmap(qrBitmap)

        // TextView đếm ngược
        val countdownText = TextView(this).apply {
            text = "Thời gian còn lại: 30 giây"
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(COLOR_TEXT_SECONDARY)
            setPadding(0, 0, 0, dp(16))
        }

        // Nút xác nhận
        val confirmBtn = Button(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(50)
            ).apply {
                setMargins(0, 0, 0, dp(12))
            }
            text = "XÁC NHẬN THANH TOÁN"
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(COLOR_TEXT_PRIMARY)
            background = createRoundedBackground(COLOR_PRIMARY, dp(12))
            elevation = dp(4).toFloat()
            isEnabled = true
            setOnClickListener {
                // Đặt vé nếu bấm trong thời gian
                saveUserToDatabase(tenn, iEmail, formattedDate, title, lich, ghe, cinema, gia.toString())
                dialog.dismiss()
            }
        }

        layout.addView(titleView)
        layout.addView(qrImageView)
        layout.addView(countdownText)
        layout.addView(confirmBtn)

        dialog.setView(layout)
        dialog.setCancelable(false)  // Không cho phép đóng dialog bằng cách khác
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        // Đếm ngược 30 giây
        object : CountDownTimer(30000, 1000) {  // 30 giây, cập nhật mỗi 1 giây
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                countdownText.text = "Thời gian còn lại: $secondsLeft giây"
            }

            override fun onFinish() {
                countdownText.text = "Thời gian đã hết!"
                confirmBtn.isEnabled = false  // Vô hiệu hóa nút
                confirmBtn.setTextColor(COLOR_TEXT_SECONDARY)
                confirmBtn.background = createRoundedBackground(Color.parseColor("#2A2A2A"), dp(12))
                Toast.makeText(this@ConfirmActivity, "Thời gian thanh toán đã hết. Đặt vé thất bại.", Toast.LENGTH_LONG).show()
                dialog.dismiss()  // Đóng dialog sau 2 giây
            }
        }.start()
    }

    // Hàm tạo mã QR
    private fun generateQRCode(data: String, width: Int, height: Int): Bitmap? {
        val qrCodeWriter = QRCodeWriter()
        return try {
            val bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    // Hàm tạo ID giao dịch ngẫu nhiên
    private fun generateTransactionId(): String {
        return UUID.randomUUID().toString().substring(0, 8).uppercase()
    }

    private fun saveUserToDatabase(ten: String, email: String, ngaydat: String, phim: String, lich: String, ghe: String, rap: String, gia: String) {
        var tt = ""
        tt = if (ten == "admin")
            "Đã xác nhận"
        else
            "Chờ xác nhận"

        val user = items(ten, email, ngaydat, phim, lich, ghe, rap, gia, tt)

        databasetickerFS.add(user)
            .addOnSuccessListener {
                if (ten != "admin") {
                    val emailBody = """
                    Chào $ten,
                    
                    Cảm ơn bạn đã đăng ký vé xem phim! Dưới đây là thông tin đặt vé của bạn:
                    
                    Phim: $phim
                    Ngày đặt: $ngaydat
                    Lịch: $lich
                    Ghế: $ghe
                    Rạp: $rap
                    Giá: $gia
                    Trạng thái: $tt
                    
                    Xin cảm ơn!
                    """.trimIndent()
                    sendVerificationEmail(email, emailBody)
                }

                Toast.makeText(this, "Đặt vé thành công!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Đặt vé thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendVerificationEmail(email: String, info: String) {
        databaseRT.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SuspiciousIndentation")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Thread {
                            try {
                                val sender = GMailSender(
                                    "trannhatluan2k1@gmail.com",
                                    "oavn gbra wuji tpgs"
                                )
                                sender.sendMail(
                                    email,
                                    "Vé xem film của bạn",
                                    info
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }.start()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Lỗi khi lấy dữ liệu!", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

data class items(
    val ten: String = "",
    val email: String = "",
    val ngaydat: String = "",
    val phim: String = "",
    val lich: String = "",
    val ghe: String = "",
    val rap: String = "",
    val gia: String = "",
    val tt: String = "Chờ xác nhận"
)