package com.example.doan

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var emailma: EditText
    private lateinit var emailmk: EditText
    private lateinit var emailmkl: EditText
    private lateinit var resetPasswordButton: Button
    private lateinit var resetma: Button
    private lateinit var ma: Button
    private lateinit var loginTextView: TextView
    private lateinit var db: FirebaseFirestore
    private lateinit var thongbao: TextView

    private var maXacNhan = ""
    private var currentEmail = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance()

        // Tạo giao diện
        setContentView(createUI())

        maXacNhan = taoMaXacNhan()

        // Xử lý nút gửi mã xác nhận
        resetPasswordButton.setOnClickListener {
            currentEmail = emailEditText.text.toString().trim()

            if (currentEmail.isNotEmpty()) {
                // Kiểm tra email có tồn tại trong Firestore không
                db.collection("Users")
                    .whereEqualTo("email", currentEmail)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            // Email tồn tại, gửi mã xác nhận
                            thongbao.text = ""
                            maXacNhan = taoMaXacNhan()

                            // Gửi email trong thread riêng
                            Thread {
                                try {
                                    val sender = GMailSender(
                                        "suongdtn.23da@vku.udn.vn",
                                        "evyg uasq bdkb wduy"
                                    )

                                    // Tạo nội dung email đẹp hơn
                                    val emailSubject = "Mã xác nhận đặt lại mật khẩu"
                                    val emailBody = """
                                        Xin chào,
                                        
                                        Mã xác nhận ứng dụng đặt vé xem phim của bạn là: $maXacNhan
                                        
                                        Vui lòng nhập mã này vào ứng dụng để đặt lại mật khẩu.
                                        Mã này có hiệu lực trong 10 phút.
                                        
                                        Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.
                                        
                                        Trân trọng,
                                        Đội ngũ hỗ trợ Ứng dụng Đặt vé xem phim
                                    """.trimIndent()

                                    sender.sendMail(
                                        currentEmail,
                                        emailSubject,
                                        emailBody
                                    )

                                    // Cập nhật UI trên main thread
                                    runOnUiThread {
                                        Toast.makeText(
                                            applicationContext,
                                            "Đã gửi mã xác nhận đến email của bạn",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    runOnUiThread {
                                        Toast.makeText(
                                            applicationContext,
                                            "Lỗi khi gửi email",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }.start()

                            // Hiển thị các trường nhập mã và mật khẩu mới
                            emailEditText.visibility = View.GONE
                            resetPasswordButton.visibility = View.GONE
                            emailma.visibility = View.VISIBLE
                            resetma.visibility = View.VISIBLE
                            ma.visibility = View.VISIBLE
                            emailmkl.visibility = View.VISIBLE
                            emailmk.visibility = View.VISIBLE

                        } else {
                            // Email không tồn tại
                            thongbao.text = "Tài khoản không tồn tại"
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ForgotPassword", "Error checking email", exception)
                        Toast.makeText(
                            applicationContext,
                            "Lỗi khi kiểm tra email!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                thongbao.text = "Vui lòng nhập email"
            }
        }

        // Xử lý nút gửi lại mã
        resetma.setOnClickListener {
            maXacNhan = taoMaXacNhan()

            Thread {
                try {
                    val sender = GMailSender(
                        "suongdtn.23da@vku.udn.vn",
                        "evyg uasq bdkb wduy"
                    )

                    // Tạo nội dung email cho gửi lại mã
                    val emailSubject = "Mã xác nhận đặt lại mật khẩu (Gửi lại)"
                    val emailBody = """
                        Xin chào,
                        
                        Mã xác nhận ứng dụng đặt vé xem phim của bạn là: $maXacNhan
                        
                        Đây là mã xác nhận mới. Vui lòng sử dụng mã này để đặt lại mật khẩu.
                        Mã này có hiệu lực trong 10 phút.
                        
                        Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.
                        
                        Trân trọng,
                        Đội ngũ hỗ trợ Ứng dụng Đặt vé xem phim
                    """.trimIndent()

                    sender.sendMail(
                        currentEmail,
                        emailSubject,
                        emailBody
                    )

                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "Đã gửi lại mã xác nhận",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "Lỗi khi gửi email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.start()
        }

        // Xử lý nút xác nhận và đổi mật khẩu
        ma.setOnClickListener {
            val newPassword = emailmk.text.toString().trim()
            val confirmPassword = emailmkl.text.toString().trim()
            val verificationCode = emailma.text.toString().trim()

            when {
                newPassword.isEmpty() || confirmPassword.isEmpty() || verificationCode.isEmpty() -> {
                    Toast.makeText(
                        applicationContext,
                        "Vui lòng điền đầy đủ thông tin",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                verificationCode != maXacNhan -> {
                    Toast.makeText(
                        applicationContext,
                        "Mã xác nhận không đúng",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                newPassword != confirmPassword -> {
                    Toast.makeText(
                        applicationContext,
                        "Mật khẩu xác nhận không khớp",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    // Cập nhật mật khẩu trong Firestore
                    db.collection("Users")
                        .whereEqualTo("email", currentEmail)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                // Lấy document đầu tiên (vì email là unique)
                                val document = documents.documents[0]

                                // Cập nhật password
                                document.reference.update("password", newPassword)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            applicationContext,
                                            "Đổi mật khẩu thành công!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish() // Quay lại màn hình đăng nhập
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("ForgotPassword", "Error updating password", exception)
                                        Toast.makeText(
                                            applicationContext,
                                            "Cập nhật mật khẩu thất bại!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Không tìm thấy tài khoản",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("ForgotPassword", "Error finding user", exception)
                            Toast.makeText(
                                applicationContext,
                                "Lỗi khi tìm tài khoản!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
        }

        // Xử lý nút quay lại đăng nhập
        loginTextView.setOnClickListener {
            finish()
        }
    }

    private fun createUI(): View {
        // Container chính với màu nền đen giống đăng nhập
        val mainContainer = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#1A0000"))
        }

        // ScrollView
        val scrollView = ScrollView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            isFillViewport = true
        }

        // Content container
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dpToPx(32), dpToPx(50), dpToPx(32), dpToPx(40))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Logo Section - Giống đăng nhập
        val logoContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(40)
            }
        }

        // Logo Circle
        val logoCircle = TextView(this).apply {
            text = "LST"
            textSize = 36f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(dpToPx(120), dpToPx(120))

            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(Color.parseColor("#ED1C24"))
            }
        }

        val cinemaText = TextView(this).apply {
            text = "CINEMA"
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            letterSpacing = 0.3f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(16)
            }
        }

        val taglineText = TextView(this).apply {
            text = "Experience the Magic"
            textSize = 12f
            setTextColor(Color.parseColor("#999999"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(4)
            }
        }

        logoContainer.addView(logoCircle)
        logoContainer.addView(cinemaText)
        logoContainer.addView(taglineText)
        contentLayout.addView(logoContainer)

        // Title Section
        val titleContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(32)
            }
        }

        val welcomeText = TextView(this).apply {
            text = "Quên mật khẩu"
            textSize = 14f
            setTextColor(Color.parseColor("#AAAAAA"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
        }

        val titleText = TextView(this).apply {
            text = "KHÔI PHỤC TÀI KHOẢN"
            textSize = 24f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            letterSpacing = 0.1f
        }

        titleContainer.addView(welcomeText)
        titleContainer.addView(titleText)
        contentLayout.addView(titleContainer)

        // Form Card - Giống đăng nhập
        val formCard = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            radius = dpToPx(16).toFloat()
            cardElevation = 0f
            setCardBackgroundColor(Color.parseColor("#2A2A2A"))
        }

        val formContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(24), dpToPx(32), dpToPx(24), dpToPx(32))
        }

        // Email Input
        emailEditText = createStyledEditText("📧  Địa chỉ Email", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        formContainer.addView(emailEditText)

        // Password Input (initially hidden)
        emailmk = createStyledEditText("🔒  Mật khẩu mới", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD).apply {
            visibility = View.GONE
        }
        formContainer.addView(emailmk)

        // Confirm Password Input (initially hidden)
        emailmkl = createStyledEditText("🔒  Nhập lại mật khẩu", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD).apply {
            visibility = View.GONE
        }
        formContainer.addView(emailmkl)

        // Verification Code Input (initially hidden)
        emailma = createStyledEditText("🔐  Mã xác nhận", InputType.TYPE_CLASS_TEXT).apply {
            visibility = View.GONE
        }
        formContainer.addView(emailma)

        // Thông báo lỗi
        thongbao = TextView(this).apply {
            text = ""
            textSize = 13f
            setTextColor(Color.parseColor("#ED1C24"))
            gravity = Gravity.START
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
        }
        formContainer.addView(thongbao)

        // Reset Password Button
        resetPasswordButton = createStyledButton("GỬI MÃ XÁC NHẬN")
        formContainer.addView(resetPasswordButton)

        // Resend Code Button (initially hidden)
        resetma = createStyledButton("GỬI LẠI MÃ").apply {
            visibility = View.GONE
        }
        formContainer.addView(resetma)

        // Verify Button (initially hidden)
        ma = createStyledButton("XÁC NHẬN & ĐỔI MẬT KHẨU").apply {
            visibility = View.GONE
        }
        formContainer.addView(ma)

        formCard.addView(formContainer)
        contentLayout.addView(formCard)

        // Back to Login - Căn trái giống đăng nhập
        val backToLoginContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(24)
                leftMargin = dpToPx(8)
            }
        }

        val arrowIcon = TextView(this).apply {
            text = "←"
            textSize = 18f
            setTextColor(Color.parseColor("#ED1C24"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = dpToPx(8)
            }
        }

        loginTextView = TextView(this).apply {
            text = "Quay lại Đăng nhập"
            textSize = 15f
            setTextColor(Color.parseColor("#ED1C24"))
        }

        backToLoginContainer.addView(arrowIcon)
        backToLoginContainer.addView(loginTextView)
        contentLayout.addView(backToLoginContainer)

        scrollView.addView(contentLayout)
        mainContainer.addView(scrollView)

        return mainContainer
    }

    private fun createStyledEditText(hint: String, inputType: Int): EditText {
        return EditText(this).apply {
            this.hint = hint
            this.inputType = inputType
            textSize = 15f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.parseColor("#666666"))
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(52)
            ).apply {
                bottomMargin = dpToPx(16)
            }

            // Custom background giống đăng nhập
            background = createEditTextBackground()
        }
    }

    private fun createEditTextBackground(): android.graphics.drawable.Drawable {
        return android.graphics.drawable.GradientDrawable().apply {
            setColor(Color.parseColor("#1F1F1F"))
            setStroke(dpToPx(1), Color.parseColor("#3A3A3A"))
            cornerRadius = dpToPx(8).toFloat()
        }
    }

    private fun createStyledButton(text: String): Button {
        return Button(this).apply {
            this.text = text
            textSize = 15f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            isAllCaps = false

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(52)
            ).apply {
                topMargin = dpToPx(8)
            }

            // Nút đỏ giống đăng nhập
            background = createButtonBackground()
            elevation = 0f
        }
    }

    private fun createButtonBackground(): android.graphics.drawable.Drawable {
        return android.graphics.drawable.GradientDrawable().apply {
            setColor(Color.parseColor("#ED1C24"))
            cornerRadius = dpToPx(8).toFloat()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun taoMaXacNhan(length: Int = 6): String {
        val kyTu = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length)
            .map { kyTu.random() }
            .joinToString("")
    }
}