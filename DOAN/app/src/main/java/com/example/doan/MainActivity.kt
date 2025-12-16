package com.example.doan

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextView: TextView
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var togglePasswordButton: ImageButton

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firestore
        firestore = FirebaseFirestore.getInstance()

        // Tạo giao diện bằng code
        setContentView(createLotteCinemaUI())

        // Xử lý đăng nhập
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }

        // Chuyển đến trang Đăng ký
        registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Chuyển đến trang Quên mật khẩu
        forgotPasswordTextView.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun createLotteCinemaUI(): View {
        val rootLayout = ConstraintLayout(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#000000"))
        }

        // Enhanced gradient background với nhiều lớp màu
        val gradientOverlay = View(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            background = createEnhancedGradientDrawable()
        }
        rootLayout.addView(gradientOverlay)

        // Decorative circles với nhiều lớp và opacity khác nhau
        val decorCircle1 = View(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(dpToPx(400), dpToPx(400))
            background = createCircleDrawable(Color.parseColor("#25E50914"))
        }
        rootLayout.addView(decorCircle1)

        val decorCircle2 = View(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(dpToPx(280), dpToPx(280))
            background = createCircleDrawable(Color.parseColor("#15FF6B6B"))
        }
        rootLayout.addView(decorCircle2)

        val decorCircle3 = View(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(dpToPx(180), dpToPx(180))
            background = createCircleDrawable(Color.parseColor("#20E50914"))
        }
        rootLayout.addView(decorCircle3)

        val decorCircle4 = View(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(dpToPx(220), dpToPx(220))
            background = createCircleDrawable(Color.parseColor("#18FF1F2A"))
        }
        rootLayout.addView(decorCircle4)

        // Logo container với gradient và glow effect
        val logoOuterGlow = FrameLayout(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(dpToPx(140), dpToPx(140))
            background = createGlowBackground()
            alpha = 0.6f
        }
        rootLayout.addView(logoOuterGlow)

        val logoContainer = FrameLayout(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(dpToPx(110), dpToPx(110))
            background = createEnhancedLogoBackground()
            elevation = dpToPx(16).toFloat()
        }
        rootLayout.addView(logoContainer)

        // Logo text "L" trong circle
        val logoText = TextView(this).apply {
            text = "LST"
            textSize = 45f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setShadowLayer(12f, 0f, 6f, Color.parseColor("#80000000"))
        }
        logoContainer.addView(logoText)

        // Brand name với style cao cấp
        val brandNameTextView = TextView(this).apply {
            id = View.generateViewId()
            text = "CINEMA"
            textSize = 16f
            setTextColor(Color.parseColor("#FFFFFF"))
            gravity = Gravity.CENTER
            letterSpacing = 0.35f
            setTypeface(null, Typeface.BOLD)
            alpha = 0.9f
        }
        rootLayout.addView(brandNameTextView)

        // Subtitle với accent color
        val subtitleTextView = TextView(this).apply {
            id = View.generateViewId()
            text = "Experience the Magic"
            textSize = 12f
            setTextColor(Color.parseColor("#FF4458"))
            gravity = Gravity.CENTER
            letterSpacing = 0.15f
            alpha = 0.85f
        }
        rootLayout.addView(subtitleTextView)

        // Welcome text
        val welcomeTextView = TextView(this).apply {
            id = View.generateViewId()
            text = "Chào mừng trở lại"
            textSize = 15f
            setTextColor(Color.parseColor("#AAAAAA"))
            gravity = Gravity.CENTER
            letterSpacing = 0.05f
        }
        rootLayout.addView(welcomeTextView)

        // Title "ĐĂNG NHẬP" với enhanced style
        val titleTextView = TextView(this).apply {
            id = View.generateViewId()
            text = "ĐĂNG NHẬP"
            textSize = 38f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            letterSpacing = 0.12f
            setShadowLayer(16f, 0f, 8f, Color.parseColor("#60000000"))
        }
        rootLayout.addView(titleTextView)

        // Enhanced divider với gradient
        val dividerLine = View(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(dpToPx(80), dpToPx(4))
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.TRANSPARENT,
                    Color.parseColor("#E50914"),
                    Color.parseColor("#FF4458"),
                    Color.TRANSPARENT
                )
            ).apply {
                cornerRadius = dpToPx(2).toFloat()
            }
        }
        rootLayout.addView(dividerLine)

        // Glassmorphism card với blur effect simulation
        val formCard = FrameLayout(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = dpToPx(24)
                rightMargin = dpToPx(24)
            }
            background = createGlassmorphicCardBackground()
            elevation = dpToPx(12).toFloat()
            setPadding(dpToPx(24), dpToPx(32), dpToPx(24), dpToPx(32))
        }

        val formLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Email input với icon và enhanced design
        val emailContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(62)
            ).apply {
                bottomMargin = dpToPx(16)
            }
            background = createEnhancedInputBackground()
        }

        val emailIcon = TextView(this).apply {
            text = "✉"
            textSize = 20f
            setTextColor(Color.parseColor("#E50914"))
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                dpToPx(50),
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        emailContainer.addView(emailIcon)

        emailEditText = EditText(this).apply {
            hint = "Địa chỉ Email"
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            textSize = 15f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.parseColor("#666666"))
            setPadding(dpToPx(54), dpToPx(18), dpToPx(20), dpToPx(18))
            background = null
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        emailContainer.addView(emailEditText)
        formLayout.addView(emailContainer)

        // Password container với icon
        val passwordContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(62)
            ).apply {
                bottomMargin = dpToPx(20)
            }
            background = createEnhancedInputBackground()
        }

        val passwordIcon = TextView(this).apply {
            text = "🔒"
            textSize = 18f
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                dpToPx(50),
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        passwordContainer.addView(passwordIcon)

        passwordEditText = EditText(this).apply {
            hint = "Mật khẩu"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            textSize = 15f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.parseColor("#666666"))
            setPadding(dpToPx(54), dpToPx(18), dpToPx(54), dpToPx(18))
            background = null
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        passwordContainer.addView(passwordEditText)

        togglePasswordButton = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_view)
            background = createIconButtonBackground()
            setColorFilter(Color.parseColor("#E50914"))
            layoutParams = FrameLayout.LayoutParams(
                dpToPx(44),
                dpToPx(44)
            ).apply {
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
                rightMargin = dpToPx(6)
            }
            elevation = dpToPx(2).toFloat()
            setOnClickListener {
                togglePasswordVisibility()
            }
        }
        passwordContainer.addView(togglePasswordButton)

        formLayout.addView(passwordContainer)

        // Options layout với improved spacing
        val optionsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(32)
            }
        }

        registerTextView = TextView(this).apply {
            text = "→ Đăng ký ngay"
            textSize = 14f
            setTextColor(Color.parseColor("#FF6B7A"))
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        optionsLayout.addView(registerTextView)

        forgotPasswordTextView = TextView(this).apply {
            text = "Quên mật khẩu?"
            textSize = 14f
            setTextColor(Color.parseColor("#FF6B7A"))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        optionsLayout.addView(forgotPasswordTextView)
        formLayout.addView(optionsLayout)

        // Premium login button với glow effect
        val buttonGlowContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val buttonGlow = View(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dpToPx(62)
            )
            background = createButtonGlow()
            alpha = 0.5f
        }
        buttonGlowContainer.addView(buttonGlow)

        loginButton = Button(this).apply {
            text = "ĐĂNG NHẬP ➔"
            textSize = 16f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            background = createPremiumButtonBackground()
            elevation = dpToPx(12).toFloat()
            stateListAnimator = null
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dpToPx(60)
            )
            letterSpacing = 0.12f
        }
        buttonGlowContainer.addView(loginButton)

        formLayout.addView(buttonGlowContainer)

        formCard.addView(formLayout)
        rootLayout.addView(formCard)

        // Decorative line accents
        val accentLine1 = View(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(dpToPx(100), dpToPx(2))
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.TRANSPARENT, Color.parseColor("#40E50914"))
            )
        }
        rootLayout.addView(accentLine1)

        val accentLine2 = View(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(dpToPx(80), dpToPx(2))
            background = GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT,
                intArrayOf(Color.TRANSPARENT, Color.parseColor("#40FF4458"))
            )
        }
        rootLayout.addView(accentLine2)

        // Áp dụng constraints
        val constraintSet = ConstraintSet()
        constraintSet.clone(rootLayout)

        // Decorative circles positioning
        constraintSet.connect(decorCircle1.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, dpToPx(-120))
        constraintSet.connect(decorCircle1.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dpToPx(-100))

        constraintSet.connect(decorCircle2.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, dpToPx(-80))
        constraintSet.connect(decorCircle2.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(-100))

        constraintSet.connect(decorCircle3.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, dpToPx(-40))
        constraintSet.connect(decorCircle3.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(-60))

        constraintSet.connect(decorCircle4.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, dpToPx(-70))
        constraintSet.connect(decorCircle4.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dpToPx(-80))

        // Logo glow
        constraintSet.connect(logoOuterGlow.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, dpToPx(55))
        constraintSet.connect(logoOuterGlow.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(logoOuterGlow.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        // Logo container
        constraintSet.connect(logoContainer.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, dpToPx(60))
        constraintSet.connect(logoContainer.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(logoContainer.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        // Brand name
        constraintSet.connect(brandNameTextView.id, ConstraintSet.TOP, logoContainer.id, ConstraintSet.BOTTOM, dpToPx(18))
        constraintSet.connect(brandNameTextView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(brandNameTextView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        // Subtitle
        constraintSet.connect(subtitleTextView.id, ConstraintSet.TOP, brandNameTextView.id, ConstraintSet.BOTTOM, dpToPx(4))
        constraintSet.connect(subtitleTextView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(subtitleTextView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        // Welcome text
        constraintSet.connect(welcomeTextView.id, ConstraintSet.TOP, subtitleTextView.id, ConstraintSet.BOTTOM, dpToPx(32))
        constraintSet.connect(welcomeTextView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(welcomeTextView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        // Title
        constraintSet.connect(titleTextView.id, ConstraintSet.TOP, welcomeTextView.id, ConstraintSet.BOTTOM, dpToPx(8))
        constraintSet.connect(titleTextView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(titleTextView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        // Divider
        constraintSet.connect(dividerLine.id, ConstraintSet.TOP, titleTextView.id, ConstraintSet.BOTTOM, dpToPx(14))
        constraintSet.connect(dividerLine.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(dividerLine.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        // Form card
        constraintSet.connect(formCard.id, ConstraintSet.TOP, dividerLine.id, ConstraintSet.BOTTOM, dpToPx(32))
        constraintSet.connect(formCard.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(formCard.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        // Accent lines
        constraintSet.connect(accentLine1.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, dpToPx(40))
        constraintSet.connect(accentLine1.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(24))

        constraintSet.connect(accentLine2.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, dpToPx(60))
        constraintSet.connect(accentLine2.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dpToPx(24))

        constraintSet.applyTo(rootLayout)

        return rootLayout
    }

    private fun togglePasswordVisibility() {
        if (passwordEditText.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            togglePasswordButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        } else {
            passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            togglePasswordButton.setImageResource(android.R.drawable.ic_menu_view)
        }
        passwordEditText.setSelection(passwordEditText.text.length)
    }

    private fun createEnhancedGradientDrawable(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.parseColor("#1A0000"),
                Color.parseColor("#0D0000"),
                Color.parseColor("#000000"),
                Color.parseColor("#050505")
            )
        )
    }

    private fun createCircleDrawable(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
        }
    }

    private fun createGlowBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            colors = intArrayOf(
                Color.parseColor("#60E50914"),
                Color.parseColor("#20E50914"),
                Color.TRANSPARENT
            )
            gradientType = GradientDrawable.RADIAL_GRADIENT
            gradientRadius = dpToPx(70).toFloat()
        }
    }

    private fun createEnhancedLogoBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            colors = intArrayOf(
                Color.parseColor("#FF1F2A"),
                Color.parseColor("#E50914"),
                Color.parseColor("#B00710")
            )
            gradientType = GradientDrawable.RADIAL_GRADIENT
            gradientRadius = dpToPx(55).toFloat()
        }
    }

    private fun createGlassmorphicCardBackground(): GradientDrawable {
        return GradientDrawable().apply {
            colors = intArrayOf(
                Color.parseColor("#25FFFFFF"),
                Color.parseColor("#15FFFFFF")
            )
            gradientType = GradientDrawable.LINEAR_GRADIENT
            orientation = GradientDrawable.Orientation.TL_BR
            cornerRadius = dpToPx(24).toFloat()
            setStroke(dpToPx(1), Color.parseColor("#30FFFFFF"))
        }
    }

    private fun createEnhancedInputBackground(): GradientDrawable {
        return GradientDrawable().apply {
            colors = intArrayOf(
                Color.parseColor("#20FFFFFF"),
                Color.parseColor("#12FFFFFF")
            )
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            cornerRadius = dpToPx(14).toFloat()
            setStroke(dpToPx(1), Color.parseColor("#25FFFFFF"))
        }
    }

    private fun createIconButtonBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor("#20E50914"))
        }
    }

    private fun createPremiumButtonBackground(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor("#E50914"),
                Color.parseColor("#FF1F2A"),
                Color.parseColor("#FF3D47")
            )
        ).apply {
            cornerRadius = dpToPx(14).toFloat()
        }
    }

    private fun createButtonGlow(): GradientDrawable {
        return GradientDrawable().apply {
            colors = intArrayOf(
                Color.parseColor("#80E50914"),
                Color.parseColor("#40E50914")
            )
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            cornerRadius = dpToPx(20).toFloat()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun loginUser(email: String, password: String) {
        if (email == "admin" && password == "123456") {
            val intent = Intent(this, AdminActivity::class.java)
            intent.putExtra("userEmail", email)
            startActivity(intent)
            passwordEditText.setText("")
            return
        }

        firestore.collection("Users")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("userEmail", email)
                    startActivity(intent)
                    passwordEditText.setText("")
                } else {
                    Toast.makeText(
                        this,
                        "Sai email hoặc mật khẩu",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
}