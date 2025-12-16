package com.example.doan

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class UserInfoActivity : AppCompatActivity() {

    private lateinit var tvUserName: EditText
    private lateinit var tvUserEmail: EditText
    private lateinit var tvBirthDate: EditText
    private lateinit var tvPass: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var tvProvince: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var rbMale: RadioButton
    private lateinit var rbFemale: RadioButton
    private lateinit var rbOther: RadioButton

    private val firestore = FirebaseFirestore.getInstance()

    // Màu sắc theme đồng bộ với Home & Ticket
    private val COLOR_BACKGROUND = Color.parseColor("#0D0D0D")
    private val COLOR_CARD = Color.parseColor("#1A1A1A")
    private val COLOR_PRIMARY = Color.parseColor("#E50914")
    private val COLOR_TEXT_PRIMARY = Color.parseColor("#FFFFFF")
    private val COLOR_TEXT_SECONDARY = Color.parseColor("#999999")
    private val COLOR_INPUT_BG = Color.parseColor("#1A1A1A")
    private val COLOR_BUTTON_UPDATE = Color.parseColor("#E50914")
    private val COLOR_BUTTON_DELETE = Color.parseColor("#B00710")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(COLOR_BACKGROUND)
        }

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
            background = createGradientDrawable()
            elevation = dpToPx(8).toFloat()
        }

        val btnBack = ImageView(this).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                dpToPx(32),
                dpToPx(32)
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_START)
                addRule(RelativeLayout.CENTER_VERTICAL)
                marginStart = dpToPx(16)
            }
            setImageResource(android.R.drawable.ic_menu_revert)
            setColorFilter(COLOR_TEXT_PRIMARY)
            contentDescription = "Back button"
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
            setOnClickListener { finish() }
        }

        val headerTitle = TextView(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            text = "THÔNG TIN CÁ NHÂN"
            setTextColor(COLOR_TEXT_PRIMARY)
            textSize = 22f
            setTypeface(null, android.graphics.Typeface.BOLD)
            letterSpacing = 0.1f
        }

        headerLayout.addView(btnBack)
        headerLayout.addView(headerTitle)

        // Content với padding đẹp
        val contentLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(16), dpToPx(20), dpToPx(16), dpToPx(20))
        }

        // Name Field
        val nameLayout = createModernFieldLayout(
            android.R.drawable.ic_menu_edit,
            "Họ và tên",
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        )
        tvUserName = nameLayout.getChildAt(1) as EditText

        // Email Field
        val emailLayout = createModernFieldLayout(
            android.R.drawable.ic_menu_send,
            "Email",
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        )
        tvUserEmail = emailLayout.getChildAt(1) as EditText
        tvUserEmail.isEnabled = false
        tvUserEmail.alpha = 0.5f

        // Password Field
        val passLayout = createModernFieldLayout(
            android.R.drawable.ic_lock_idle_lock,
            "Mật khẩu",
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        )
        tvPass = passLayout.getChildAt(1) as EditText

        // Birthday Field
        val birthdayLayout = createModernFieldLayout(
            android.R.drawable.ic_menu_my_calendar,
            "Ngày sinh (dd/mm/yyyy)",
            InputType.TYPE_CLASS_TEXT
        )
        tvBirthDate = birthdayLayout.getChildAt(1) as EditText

        // Gender Section với title
        val genderTitle = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(4)
                bottomMargin = dpToPx(8)
                marginStart = dpToPx(4)
            }
            text = "Giới tính"
            setTextColor(COLOR_TEXT_SECONDARY)
            textSize = 13f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        // Gender RadioGroup với style mới
        val genderContainer = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }
            orientation = LinearLayout.VERTICAL
            background = createRoundedBackground(COLOR_INPUT_BG, dpToPx(16))
            setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14))
            elevation = dpToPx(2).toFloat()
        }

        rgGender = RadioGroup(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
        }

        rbMale = createStyledRadioButton("Nam")
        rbFemale = createStyledRadioButton("Nữ")
        rbOther = createStyledRadioButton("Khác")

        rgGender.addView(rbMale)
        rgGender.addView(rbFemale)
        rgGender.addView(rbOther)
        genderContainer.addView(rgGender)

        // Province Field
        val provinceLayout = createModernFieldLayout(
            android.R.drawable.ic_menu_compass,
            "Tỉnh/Thành phố",
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        )
        tvProvince = provinceLayout.getChildAt(1) as EditText

        // Buttons Layout
        val buttonLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(24)
            }
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        btnUpdate = createModernButton("CẬP NHẬT", COLOR_BUTTON_UPDATE).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                dpToPx(56),
                1f
            ).apply {
                marginEnd = dpToPx(12)
            }
            setOnClickListener { updateAccount() }
        }

        btnDelete = createModernButton("XÓA TÀI KHOẢN", COLOR_BUTTON_DELETE).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                dpToPx(56),
                1f
            )
            setOnClickListener { confirmDeleteAccount() }
        }

        buttonLayout.addView(btnUpdate)
        buttonLayout.addView(btnDelete)

        contentLayout.addView(nameLayout)
        contentLayout.addView(emailLayout)
        contentLayout.addView(passLayout)
        contentLayout.addView(birthdayLayout)
        contentLayout.addView(genderTitle)
        contentLayout.addView(genderContainer)
        contentLayout.addView(provinceLayout)
        contentLayout.addView(buttonLayout)

        mainLayout.addView(headerLayout)
        mainLayout.addView(contentLayout)

        scrollView.addView(mainLayout)
        setContentView(scrollView)

        val userEmail = intent.getStringExtra("userEmail")

        if (!userEmail.isNullOrEmpty()) {
            tvUserEmail.setText(userEmail)
            loadUserData(userEmail)
        } else {
            tvUserEmail.setText("Không nhận được email")
        }
    }

    private fun createModernFieldLayout(iconRes: Int, hint: String, inputType: Int): LinearLayout {
        return LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }
            orientation = LinearLayout.HORIZONTAL
            background = createRoundedBackground(COLOR_INPUT_BG, dpToPx(16))
            setPadding(dpToPx(16), dpToPx(4), dpToPx(16), dpToPx(4))
            elevation = dpToPx(2).toFloat()

            val icon = ImageView(this@UserInfoActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(24),
                    dpToPx(24)
                ).apply {
                    gravity = Gravity.CENTER_VERTICAL
                    marginEnd = dpToPx(14)
                }
                setImageResource(iconRes)
                setColorFilter(COLOR_PRIMARY)
            }

            val editText = EditText(this@UserInfoActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(52)
                )
                this.hint = hint
                this.inputType = inputType
                setHintTextColor(COLOR_TEXT_SECONDARY)
                setTextColor(COLOR_TEXT_PRIMARY)
                setBackgroundColor(Color.TRANSPARENT)
                textSize = 16f
                setPadding(0, dpToPx(8), 0, dpToPx(8))
                imeOptions = android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI
            }

            addView(icon)
            addView(editText)
        }
    }

    private fun createStyledRadioButton(text: String): RadioButton {
        return RadioButton(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            this.text = text
            setTextColor(COLOR_TEXT_PRIMARY)
            textSize = 15f
            buttonTintList = android.content.res.ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(COLOR_PRIMARY, COLOR_TEXT_SECONDARY)
            )
        }
    }

    private fun createModernButton(text: String, bgColor: Int): Button {
        return Button(this).apply {
            this.text = text
            setTextColor(COLOR_TEXT_PRIMARY)
            textSize = 15f
            setTypeface(null, android.graphics.Typeface.BOLD)
            background = createButtonBackground(bgColor)
            elevation = dpToPx(6).toFloat()
            letterSpacing = 0.05f
            isAllCaps = true
        }
    }

    private fun createButtonBackground(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dpToPx(16).toFloat()
        }
    }

    private fun createRoundedBackground(color: Int, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
        }
    }

    private fun createGradientDrawable(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor("#B00710"),
                Color.parseColor("#E50914"),
                Color.parseColor("#FF1F2A")
            )
        )
    }

    private fun loadUserData(userEmail: String) {
        firestore.collection("Users")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]

                    tvUserName.setText(doc.getString("name") ?: "")
                    tvBirthDate.setText(doc.getString("birthDate") ?: "")
                    tvPass.setText(doc.getString("password") ?: "")
                    tvProvince.setText(doc.getString("province") ?: "")

                    when (doc.getString("gender")) {
                        "Nam" -> rgGender.check(rbMale.id)
                        "Nữ" -> rgGender.check(rbFemale.id)
                        "Khác" -> rgGender.check(rbOther.id)
                        else -> rgGender.clearCheck()
                    }

                } else {
                    Toast.makeText(this, "Không tìm thấy thông tin!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi lấy dữ liệu!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateAccount() {
        val name = tvUserName.text.toString()
        val birthDate = tvBirthDate.text.toString()
        val province = tvProvince.text.toString()

        val gender = when (rgGender.checkedRadioButtonId) {
            rbMale.id -> "Nam"
            rbFemale.id -> "Nữ"
            rbOther.id -> "Khác"
            else -> "Không có"
        }

        val userEmail = intent.getStringExtra("userEmail") ?: return

        val userUpdates = mapOf(
            "name" to name,
            "birthDate" to birthDate,
            "gender" to gender,
            "province" to province
        )

        firestore.collection("Users")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]

                    doc.reference.update(userUpdates)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    private fun confirmDeleteAccount() {
        val builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)

        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(24), dpToPx(20), dpToPx(24), dpToPx(20))
            setBackgroundColor(COLOR_CARD)
        }

        val title = TextView(this).apply {
            text = "Xác nhận xóa tài khoản"
            setTextColor(COLOR_TEXT_PRIMARY)
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, dpToPx(16))
        }

        val message = TextView(this).apply {
            text = "Bạn có chắc chắn muốn xóa tài khoản không?\nHành động này không thể hoàn tác."
            setTextColor(COLOR_TEXT_SECONDARY)
            textSize = 16f
            setPadding(0, 0, 0, dpToPx(24))
        }

        dialogView.addView(title)
        dialogView.addView(message)

        builder.setView(dialogView)
        builder.setPositiveButton("Xóa") { _, _ -> deleteAccount() }
        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(COLOR_BUTTON_DELETE)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(COLOR_TEXT_SECONDARY)
    }

    private fun deleteAccount() {
        val userEmail = intent.getStringExtra("userEmail") ?: return

        firestore.collection("Users")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]

                    doc.reference.delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Xóa tài khoản thành công!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Xóa tài khoản thất bại!", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi xóa dữ liệu!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}