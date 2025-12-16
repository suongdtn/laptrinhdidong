package com.example.doan

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class FilmManagerActivity : AppCompatActivity() {

    private lateinit var editTextFilmName: EditText
    private lateinit var editTextDuration: EditText
    private lateinit var editTextReleaseDate: EditText
    private lateinit var editTextGenre: EditText
    private lateinit var editTextDirector: EditText
    private lateinit var editTextFilmmage: EditText
    private lateinit var buttonAddCinema: Button
    private lateinit var buttonAddFilm: Button
    private lateinit var recyclerViewCinemas: RecyclerView
    private lateinit var cinemaAdapter: CinemaAdapter

    private val firestore = FirebaseFirestore.getInstance()
    private val filmList = ArrayList<Film>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tạo giao diện bằng Kotlin
        val rootLayout = createUI()
        setContentView(rootLayout)

        cinemaAdapter = CinemaAdapter()
        recyclerViewCinemas.adapter = cinemaAdapter
        recyclerViewCinemas.layoutManager = LinearLayoutManager(this)
    }

    private fun createUI(): ScrollView {
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#0D0D0D"))
        }

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Header với gradient Lotte Cinema cao cấp
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#B00710"),
                    Color.parseColor("#E50914"),
                    Color.parseColor("#FF1F2A")
                )
            )
            setPadding(dp(24), dp(40), dp(24), dp(32))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            elevation = dp(8).toFloat()
        }

        // Icon decoration
        val iconRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(12))
        }

        val iconLeft = TextView(this).apply {
            text = "🎬"
            textSize = 24f
            setPadding(0, 0, dp(12), 0)
        }

        val iconRight = TextView(this).apply {
            text = "🎬"
            textSize = 24f
            setPadding(dp(12), 0, 0, 0)
        }

        iconRow.addView(iconLeft)

        val titleText = TextView(this).apply {
            text = "THÊM PHIM MỚI"
            textSize = 28f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            letterSpacing = 0.12f
            setShadowLayer(4f, 0f, 2f, Color.parseColor("#80000000"))
        }
        iconRow.addView(titleText)
        iconRow.addView(iconRight)

        headerLayout.addView(iconRow)

        val subtitleText = TextView(this).apply {
            text = "QUẢN LÝ LỊCH CHIẾU RẠP"
            textSize = 12f
            setTextColor(Color.parseColor("#FFD700"))
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.15f
            setShadowLayer(3f, 0f, 1f, Color.parseColor("#80000000"))
        }
        headerLayout.addView(subtitleText)

        // Accent line
        val accentLine = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(2)
            ).apply {
                topMargin = dp(16)
            }
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.TRANSPARENT,
                    Color.parseColor("#FFD700"),
                    Color.parseColor("#FFA500"),
                    Color.parseColor("#FFD700"),
                    Color.TRANSPARENT
                )
            )
        }
        headerLayout.addView(accentLine)

        rootLayout.addView(headerLayout)

        // Content Layout
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(20))
        }

        // Button Layout với style cao cấp
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(20))
            }
        }

        buttonAddCinema = Button(this).apply {
            text = "➕ THÊM RẠP"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#D4AF37"),
                    Color.parseColor("#F4C542")
                )
            ).apply {
                cornerRadius = dp(12).toFloat()
            }
            layoutParams = LinearLayout.LayoutParams(
                0,
                dp(52),
                1f
            ).apply {
                setMargins(0, 0, dp(10), 0)
            }
            elevation = dp(6).toFloat()
            setOnClickListener {
                cinemaAdapter.addCinema()
            }
        }
        buttonLayout.addView(buttonAddCinema)

        buttonAddFilm = Button(this).apply {
            text = "✨ THÊM PHIM"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#E50914"),
                    Color.parseColor("#FF6B6B")
                )
            ).apply {
                cornerRadius = dp(12).toFloat()
            }
            layoutParams = LinearLayout.LayoutParams(
                0,
                dp(52),
                1f
            ).apply {
                setMargins(dp(10), 0, 0, 0)
            }
            elevation = dp(6).toFloat()
            setOnClickListener {
                addFilm()
            }
        }
        buttonLayout.addView(buttonAddFilm)

        contentLayout.addView(buttonLayout)

        // Card chứa form với gradient và shadow
        val cardLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1F1F1F"))
                cornerRadius = dp(20).toFloat()
                setStroke(dp(2), Color.parseColor("#E50914"))
            }
            setPadding(dp(24), dp(24), dp(24), dp(24))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(20))
            }
            elevation = dp(10).toFloat()
        }

        // Form title với divider
        val formTitleLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, dp(20))
        }

        val formDivider = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                dp(4),
                dp(28)
            ).apply {
                marginEnd = dp(12)
            }
            background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    Color.parseColor("#E50914"),
                    Color.parseColor("#FF6B6B")
                )
            ).apply {
                cornerRadius = dp(2).toFloat()
            }
        }

        val formTitle = TextView(this).apply {
            text = "THÔNG TIN PHIM"
            textSize = 18f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.08f
        }

        formTitleLayout.addView(formDivider)
        formTitleLayout.addView(formTitle)
        cardLayout.addView(formTitleLayout)

        // EditText Fields với style cao cấp
        editTextFilmName = createStyledEditText("📝 Tên phim", InputType.TYPE_CLASS_TEXT)
        editTextFilmmage = createStyledEditText("🖼️ URL hình ảnh", InputType.TYPE_CLASS_TEXT)
        editTextDuration = createStyledEditText("⏱️ Thời gian (phút)", InputType.TYPE_CLASS_NUMBER)
        editTextReleaseDate = createStyledEditText("📅 Ngày chiếu (dd/MM/yyyy)", InputType.TYPE_CLASS_TEXT)
        editTextGenre = createStyledEditText("🎭 Thể loại", InputType.TYPE_CLASS_TEXT)
        editTextDirector = createStyledEditText("🎬 Đạo diễn", InputType.TYPE_CLASS_TEXT)

        cardLayout.addView(editTextFilmName)
        cardLayout.addView(editTextFilmmage)
        cardLayout.addView(editTextDuration)
        cardLayout.addView(editTextReleaseDate)
        cardLayout.addView(editTextGenre)
        cardLayout.addView(editTextDirector)

        contentLayout.addView(cardLayout)

        // RecyclerView Section với header đẹp
        val cinemaTitleLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(4), 0, 0, dp(16))
        }

        val cinemaDivider = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                dp(4),
                dp(28)
            ).apply {
                marginEnd = dp(12)
            }
            background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    Color.parseColor("#D4AF37"),
                    Color.parseColor("#F4C542")
                )
            ).apply {
                cornerRadius = dp(2).toFloat()
            }
        }

        val cinemaTitle = TextView(this).apply {
            text = "DANH SÁCH RẠP CHIẾU"
            textSize = 18f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.08f
        }

        cinemaTitleLayout.addView(cinemaDivider)
        cinemaTitleLayout.addView(cinemaTitle)
        contentLayout.addView(cinemaTitleLayout)

        // RecyclerView container
        val recyclerContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1F1F1F"))
                cornerRadius = dp(20).toFloat()
                setStroke(dp(2), Color.parseColor("#D4AF37"))
            }
            setPadding(dp(12), dp(12), dp(12), dp(12))
            elevation = dp(8).toFloat()
        }

        recyclerViewCinemas = RecyclerView(this).apply {
            setBackgroundColor(Color.TRANSPARENT)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            isNestedScrollingEnabled = false
        }
        recyclerContainer.addView(recyclerViewCinemas)
        contentLayout.addView(recyclerContainer)

        rootLayout.addView(contentLayout)
        scrollView.addView(rootLayout)

        return scrollView
    }

    private fun createStyledEditText(hintText: String, inputType: Int): EditText {
        return EditText(this).apply {
            hint = hintText
            this.inputType = inputType
            textSize = 16f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.parseColor("#888888"))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#2A2A2A"))
                cornerRadius = dp(12).toFloat()
                setStroke(dp(1), Color.parseColor("#444444"))
            }
            setPadding(dp(18), dp(16), dp(18), dp(16))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(14))
            }
            elevation = dp(2).toFloat()
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun addFilm() {
        val name = editTextFilmName.text.toString().trim()
        val duration = editTextDuration.text.toString().trim()
        val releaseDate = editTextReleaseDate.text.toString().trim()
        val genre = editTextGenre.text.toString().trim()
        val director = editTextDirector.text.toString().trim()
        val image = editTextFilmmage.text.toString().trim()

        if (name.isEmpty() || duration.isEmpty() || releaseDate.isEmpty()
            || genre.isEmpty() || director.isEmpty() || image.isEmpty()) {

            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        val film = Film(
            name = name,
            URL = image,
            duration = duration,
            releaseDate = releaseDate,
            genre = genre,
            director = director,
            cinemas = cinemaAdapter.getCinemas()
        )

        // Lưu Firestore
        firestore.collection("Films")
            .add(film)
            .addOnSuccessListener {
                Toast.makeText(this, "Đã thêm phim!", Toast.LENGTH_SHORT).show()
                clearInputFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Thêm phim thất bại!", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error: ${e.message}")
            }

        val json = Gson().toJson(filmList)
        Log.d("FilmManager", "📄 JSON Phim: $json")
    }

    private fun clearInputFields() {
        editTextFilmName.setText("")
        editTextDuration.setText("")
        editTextReleaseDate.setText("")
        editTextGenre.setText("")
        editTextDirector.setText("")
        editTextFilmmage.setText("")
    }

    data class Film(
        val name: String = "",
        val URL: String = "",
        val duration: String = "",
        val releaseDate: String = "",
        val genre: String = "",
        val director: String = "",
        val cinemas: List<Cinema> = emptyList()
    )
}