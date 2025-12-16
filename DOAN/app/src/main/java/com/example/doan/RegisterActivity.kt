package com.example.doan

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var birthDateEditText: EditText
    private lateinit var maleCheckBox: CheckBox
    private lateinit var femaleCheckBox: CheckBox
    private lateinit var otherCheckBox: CheckBox
    private lateinit var provinceSpinner: Spinner
    private lateinit var registerButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var togglePasswordButton: ImageButton
    private lateinit var toggleConfirmPasswordButton: ImageButton

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo Firestore
        firestore = FirebaseFirestore.getInstance()

        // Tạo giao diện bằng Kotlin
        setContentView(createLotteCinemaUI())

        setupSpinner()
        setupClickListeners()
    }

    private fun createLotteCinemaUI(): View {
        val rootLayout = ConstraintLayout(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#0D0D0D"))
        }

        // Background gradient overlay
        val gradientOverlay = View(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            background = createGradientDrawable()
        }
        rootLayout.addView(gradientOverlay)

        // Decorative circles
        val decorCircle1 = View(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(dpToPx(250), dpToPx(250))
            background = createCircleDrawable(Color.parseColor("#1AE50914"))
        }
        rootLayout.addView(decorCircle1)

        val decorCircle2 = View(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(dpToPx(180), dpToPx(180))
            background = createCircleDrawable(Color.parseColor("#0DE50914"))
        }
        rootLayout.addView(decorCircle2)

        // ScrollView cho form
        val scrollView = ScrollView(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
        }

        val scrollContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, dpToPx(50), 0, dpToPx(50))
        }

        // Logo container
        val logoContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(dpToPx(100), dpToPx(100)).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            background = createLogoBackground()
            elevation = dpToPx(12).toFloat()
        }
        scrollContent.addView(logoContainer)

        // Subtitle
        val subtitleTextView = TextView(this).apply {
            text = "CINEMA"
            textSize = 12f
            setTextColor(Color.parseColor("#E50914"))
            gravity = Gravity.CENTER
            letterSpacing = 0.3f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(12)
            }
        }
        scrollContent.addView(subtitleTextView)

        // Title "ĐĂNG KÝ"
        val titleTextView = TextView(this).apply {
            text = "ĐĂNG KÝ"
            textSize = 32f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            letterSpacing = 0.05f
            setShadowLayer(8f, 0f, 4f, Color.parseColor("#40000000"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(6)
            }
        }
        scrollContent.addView(titleTextView)

        // Divider line
        val dividerLine = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(dpToPx(60), dpToPx(4)).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = dpToPx(10)
                bottomMargin = dpToPx(28)
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

        // Form card
        val formCard = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = dpToPx(24)
                rightMargin = dpToPx(24)
            }
            background = createCardBackground()
            elevation = dpToPx(8).toFloat()
            setPadding(dpToPx(24), dpToPx(28), dpToPx(24), dpToPx(28))
        }

        val formLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Name field
        nameEditText = EditText(this).apply {
            hint = "Họ và tên"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            textSize = 15f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.parseColor("#666666"))
            setPadding(dpToPx(18), dpToPx(16), dpToPx(18), dpToPx(16))
            background = createInputBackground()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(52)
            ).apply {
                bottomMargin = dpToPx(14)
            }
        }
        formLayout.addView(nameEditText)

        // Email field
        emailEditText = EditText(this).apply {
            hint = "Địa chỉ Email"
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            textSize = 15f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.parseColor("#666666"))
            setPadding(dpToPx(18), dpToPx(16), dpToPx(18), dpToPx(16))
            background = createInputBackground()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(52)
            ).apply {
                bottomMargin = dpToPx(14)
            }
        }
        formLayout.addView(emailEditText)

        // Password container
        val passwordContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(52)
            ).apply {
                bottomMargin = dpToPx(14)
            }
        }

        passwordEditText = EditText(this).apply {
            hint = "Mật khẩu"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            textSize = 15f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.parseColor("#666666"))
            setPadding(dpToPx(18), dpToPx(16), dpToPx(45), dpToPx(16))
            background = createInputBackground()
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        passwordContainer.addView(passwordEditText)

        togglePasswordButton = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_view)
            background = null
            setColorFilter(Color.parseColor("#666666"))
            layoutParams = FrameLayout.LayoutParams(
                dpToPx(36),
                dpToPx(36)
            ).apply {
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
                rightMargin = dpToPx(8)
            }
            setOnClickListener {
                togglePasswordVisibility(passwordEditText, togglePasswordButton)
            }
        }
        passwordContainer.addView(togglePasswordButton)
        formLayout.addView(passwordContainer)

        // Confirm Password container
        val confirmPasswordContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(52)
            ).apply {
                bottomMargin = dpToPx(14)
            }
        }

        confirmPasswordEditText = EditText(this).apply {
            hint = "Xác nhận mật khẩu"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            textSize = 15f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.parseColor("#666666"))
            setPadding(dpToPx(18), dpToPx(16), dpToPx(45), dpToPx(16))
            background = createInputBackground()
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        confirmPasswordContainer.addView(confirmPasswordEditText)

        toggleConfirmPasswordButton = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_view)
            background = null
            setColorFilter(Color.parseColor("#666666"))
            layoutParams = FrameLayout.LayoutParams(
                dpToPx(36),
                dpToPx(36)
            ).apply {
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
                rightMargin = dpToPx(8)
            }
            setOnClickListener {
                togglePasswordVisibility(confirmPasswordEditText, toggleConfirmPasswordButton)
            }
        }
        confirmPasswordContainer.addView(toggleConfirmPasswordButton)
        formLayout.addView(confirmPasswordContainer)

        // Birth Date field
        birthDateEditText = EditText(this).apply {
            hint = "Ngày sinh (dd/mm/yyyy)"
            inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE
            textSize = 15f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.parseColor("#666666"))
            setPadding(dpToPx(18), dpToPx(16), dpToPx(18), dpToPx(16))
            background = createInputBackground()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(52)
            ).apply {
                bottomMargin = dpToPx(14)
            }
        }
        formLayout.addView(birthDateEditText)

        // Gender section
        val genderLabel = TextView(this).apply {
            text = "Giới tính"
            textSize = 13f
            setTextColor(Color.parseColor("#999999"))
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
        }
        formLayout.addView(genderLabel)

        val genderLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(14)
            }
        }

        maleCheckBox = CheckBox(this).apply {
            text = "Nam"
            setTextColor(Color.WHITE)
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            buttonTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#E50914"))
        }
        genderLayout.addView(maleCheckBox)

        femaleCheckBox = CheckBox(this).apply {
            text = "Nữ"
            setTextColor(Color.WHITE)
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            buttonTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#E50914"))
        }
        genderLayout.addView(femaleCheckBox)

        otherCheckBox = CheckBox(this).apply {
            text = "Khác"
            setTextColor(Color.WHITE)
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            buttonTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#E50914"))
        }
        genderLayout.addView(otherCheckBox)
        formLayout.addView(genderLayout)

        // Province section
        val provinceLabel = TextView(this).apply {
            text = "Tỉnh/Thành phố"
            textSize = 13f
            setTextColor(Color.parseColor("#999999"))
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
        }
        formLayout.addView(provinceLabel)

        provinceSpinner = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(52)
            ).apply {
                bottomMargin = dpToPx(24)
            }
            setPadding(dpToPx(18), dpToPx(16), dpToPx(18), dpToPx(16))
            background = createInputBackground()
        }
        formLayout.addView(provinceSpinner)

        // Register button
        registerButton = Button(this).apply {
            text = "ĐĂNG KÝ"
            textSize = 16f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            background = createButtonBackground()
            elevation = dpToPx(8).toFloat()
            stateListAnimator = null
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(52)
            )
            letterSpacing = 0.08f
        }
        formLayout.addView(registerButton)

        formCard.addView(formLayout)
        scrollContent.addView(formCard)

        // Login link
        loginTextView = TextView(this).apply {
            text = "Đã có tài khoản? Đăng nhập ngay"
            textSize = 14f
            setTextColor(Color.parseColor("#FF4458"))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = dpToPx(20)
            }
        }
        scrollContent.addView(loginTextView)

        scrollView.addView(scrollContent)
        rootLayout.addView(scrollView)

        // Apply constraints
        val constraintSet = ConstraintSet()
        constraintSet.clone(rootLayout)

        // Decorative circles
        constraintSet.connect(decorCircle1.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, dpToPx(-80))
        constraintSet.connect(decorCircle1.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dpToPx(-60))

        constraintSet.connect(decorCircle2.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, dpToPx(-40))
        constraintSet.connect(decorCircle2.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(-50))

        // ScrollView
        constraintSet.connect(scrollView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(scrollView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        constraintSet.connect(scrollView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(scrollView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        constraintSet.applyTo(rootLayout)

        return rootLayout
    }

    private fun togglePasswordVisibility(editText: EditText, button: ImageButton) {
        if (editText.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            button.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            button.setImageResource(android.R.drawable.ic_menu_view)
        }
        editText.setSelection(editText.text.length)
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
            cornerRadius = dpToPx(20).toFloat()
            setStroke(dpToPx(1), Color.parseColor("#2A2A2A"))
        }
    }

    private fun createInputBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.parseColor("#252525"))
            cornerRadius = dpToPx(12).toFloat()
            setStroke(dpToPx(1), Color.parseColor("#333333"))
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

    private fun setupSpinner() {
        val provinces = arrayOf("Hà Nội", "TP. Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ")

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provinces) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(Color.parseColor("#FFFFFF"))
                view.textSize = 15f
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(Color.parseColor("#FFFFFF"))
                view.setBackgroundColor(Color.parseColor("#252525"))
                view.setPadding(dpToPx(18), dpToPx(14), dpToPx(18), dpToPx(14))
                view.textSize = 15f
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        provinceSpinner.adapter = adapter
    }

    private fun setupClickListeners() {
        loginTextView.setOnClickListener { finish() }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val name = nameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val birthDate = birthDateEditText.text.toString().trim()

            val gender =
                if (maleCheckBox.isChecked) "Nam"
                else if (femaleCheckBox.isChecked) "Nữ"
                else "Khác"

            val province = provinceSpinner.selectedItem.toString()

            if (email.isEmpty() || name.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || birthDate.isEmpty()
            ) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Mật khẩu không trùng khớp!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            checkEmailExists(email) { exists ->
                if (exists) {
                    Toast.makeText(this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show()
                } else {
                    saveUserToFirestore(name, email, password, birthDate, gender, province)
                }
            }
        }
    }

    private fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        firestore.collection("Users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                callback(!result.isEmpty)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi kiểm tra email!", Toast.LENGTH_SHORT).show()
                callback(false)
            }
    }

    private fun saveUserToFirestore(
        name: String,
        email: String,
        password: String,
        birthDate: String,
        gender: String,
        province: String
    ) {
        val userId = firestore.collection("Users").document().id

        val user = User(name, email, password, birthDate, gender, province)

        firestore.collection("Users")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}

data class User(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val province: String = ""
)