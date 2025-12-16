package com.example.doan

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class FilmAdminActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var filmAdapter: FilmAdminAdapter
    private val filmList = mutableListOf<Film>()
    private lateinit var database: FirebaseFirestore

    private lateinit var edtFilmName: EditText
    private lateinit var edtFilmDirector: EditText
    private lateinit var edtFilmDuration: EditText
    private lateinit var edtFilmGenre: EditText
    private lateinit var edtFilmReleaseDate: EditText
    private lateinit var edtFilmImage: EditText

    // Biến này sẽ là null khi thêm mới, và chứa ID khi chỉnh sửa
    private var editingFilmId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ===== MAIN LAYOUT =====
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1a1a1a"))
        }

        // ===== HEADER =====
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#E53935"))
            setPadding(48, 48, 48, 48)
        }

        header.addView(TextView(this).apply {
            text = "QUẢN LÝ PHIM"
            setTextColor(Color.WHITE)
            textSize = 24f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        })

        header.addView(TextView(this).apply {
            text = "CINEMA ADMIN"
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        })

        mainLayout.addView(header)

        // ===== SCROLL =====
        val scrollView = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        // Nút thêm mới - để người dùng bắt đầu nhập liệu



        content.addView(title("Thông tin phim"))

        edtFilmName = createEditText("Tên phim")
        edtFilmDirector = createEditText("Đạo diễn")
        edtFilmDuration = createEditText("Thời lượng (phút)")
        edtFilmGenre = createEditText("Thể loại")
        edtFilmReleaseDate = createEditText("Ngày phát hành")
        edtFilmImage = createEditText("URL hình ảnh")

        content.addView(edtFilmName)
        content.addView(edtFilmDirector)
        content.addView(edtFilmDuration)
        content.addView(edtFilmGenre)
        content.addView(edtFilmReleaseDate)
        content.addView(edtFilmImage)

        // ===== BUTTON SAVE / UPDATE =====
        val btnSave = Button(this).apply {
            text = "💾 LƯU / CẬP NHẬT"
            setBackgroundColor(Color.parseColor("#43A047"))
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setOnClickListener {
                if (edtFilmName.text.isBlank()) {
                    Toast.makeText(this@FilmAdminActivity, "Tên phim không được để trống!", Toast.LENGTH_SHORT).show()
                } else {
                    saveFilm() // Gọi hàm saveFilm đã chỉnh sửa
                }
            }
        }

        // ===== BUTTON CANCEL =====
        val btnCancel = Button(this).apply {
            text = "🚫 HỦY"
            setBackgroundColor(Color.parseColor("#555555"))
            setTextColor(Color.WHITE)
            setOnClickListener {
                clearForm()
                enableForm(false) // Tắt form
                editingFilmId = null
                Toast.makeText(this@FilmAdminActivity, "Đã hủy thao tác", Toast.LENGTH_SHORT).show()
            }
        }

        // Layout chứa 2 nút Save/Cancel
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            // Dùng LayoutParams để 2 nút chia đều chiều ngang
            val params = LinearLayout.LayoutParams(
                0, // Chiều rộng là 0
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // Trọng số là 1.0 để chia đều
            ).apply {
                setMargins(8, 0, 8, 0) // Tạo khoảng cách giữa các nút
            }
            btnSave.layoutParams = params
            btnCancel.layoutParams = params
            setPadding(0, 16, 0, 16)
        }

        buttonLayout.addView(btnSave)
        buttonLayout.addView(btnCancel)
        content.addView(buttonLayout)

        content.addView(title("Danh sách phim"))

        recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@FilmAdminActivity)
        }
        content.addView(recyclerView)

        scrollView.addView(content)
        mainLayout.addView(scrollView)
        setContentView(mainLayout)

        // ===== FIREBASE =====
        database = FirebaseFirestore.getInstance()

        filmAdapter = FilmAdminAdapter(
            filmList,
            ::editFilm,
            ::deleteFilm
        )
        recyclerView.adapter = filmAdapter

        loadFilmData()
        enableForm(false) // Vô hiệu hóa form khi khởi động
    }

    // ================== FUNCTIONS ==================

    private fun createEditText(hint: String) = EditText(this).apply {
        this.hint = hint
        setHintTextColor(Color.GRAY)
        setTextColor(Color.WHITE)
        setBackgroundColor(Color.parseColor("#2a2a2a"))
        setPadding(32, 32, 32, 32)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 8, 0, 8) // Thêm margin cho đẹp
        }
        isEnabled = false
    }

    private fun title(text: String) = TextView(this).apply {
        this.text = text
        setTextColor(Color.WHITE)
        textSize = 18f
        setTypeface(null, Typeface.BOLD)
        setPadding(0, 32, 0, 16)
    }

    private fun enableForm(enable: Boolean) {
        edtFilmName.isEnabled = enable
        edtFilmDirector.isEnabled = enable
        edtFilmDuration.isEnabled = enable
        edtFilmGenre.isEnabled = enable
        edtFilmReleaseDate.isEnabled = enable
        edtFilmImage.isEnabled = enable
    }

    // Hàm này được gọi khi click nút sửa trên RecyclerView
    private fun editFilm(film: Film) {
        editingFilmId = film.id
        enableForm(true) // Bật form

        edtFilmName.setText(film.name)
        edtFilmDirector.setText(film.director)
        edtFilmDuration.setText(film.duration)
        edtFilmGenre.setText(film.genre)
        edtFilmReleaseDate.setText(film.releaseDate)
        edtFilmImage.setText(film.url)

        Toast.makeText(this, "Đang sửa: ${film.name}", Toast.LENGTH_SHORT).show()
    }

    // Hàm này xử lý cả Thêm mới và Cập nhật
    private fun saveFilm() {
        val name = edtFilmName.text.toString().trim()
        val director = edtFilmDirector.text.toString().trim()
        val duration = edtFilmDuration.text.toString().trim()
        val genre = edtFilmGenre.text.toString().trim()
        val releaseDate = edtFilmReleaseDate.text.toString().trim()
        val url = edtFilmImage.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Tên phim không được để trống", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo đối tượng Film/Map data chung
        val filmData = Film(
            name = name,
            director = director,
            duration = duration,
            genre = genre,
            releaseDate = releaseDate,
            url = url
        )

        val filmsCollection = database.collection("Films")

        if (editingFilmId != null) {
            // CHẾ ĐỘ CẬP NHẬT: editingFilmId đã có
            filmsCollection.document(editingFilmId!!)
                .set(filmData) // Dùng set(filmData) hoặc set(data.toMap()) để update
                .addOnSuccessListener {
                    Toast.makeText(
                        this@FilmAdminActivity,
                        "Cập nhật phim thành công!",
                        Toast.LENGTH_SHORT
                    ).show()
                    clearFormAndDisable()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this@FilmAdminActivity,
                        "Lỗi cập nhật: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            // CHẾ ĐỘ THÊM MỚI: editingFilmId là null
            filmsCollection.add(filmData) // Dùng add() để Firestore tự tạo ID
                .addOnSuccessListener {
                    Toast.makeText(
                        this@FilmAdminActivity,
                        "Thêm phim mới thành công!",
                        Toast.LENGTH_SHORT
                    ).show()
                    clearFormAndDisable()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this@FilmAdminActivity,
                        "Lỗi thêm mới: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun deleteFilm(film: Film) {
        database.collection("Films").document(film.id).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Đã xóa phim: ${film.name}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi xóa phim: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadFilmData() {
        database.collection("Films")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                filmList.clear()
                value?.documents?.forEach { document ->
                    // Sử dụng .toObject<Film>() nếu bạn muốn an toàn hơn
                    val film = document.toObject(Film::class.java)?.copy(id = document.id)
                    if (film != null) {
                        filmList.add(film)
                    }
                }
                filmAdapter.updateList(filmList)
            }
    }

    // Gộp 3 hành động cần thiết sau khi Thêm/Cập nhật
    private fun clearFormAndDisable() {
        clearForm()
        enableForm(false)
        editingFilmId = null
    }

    private fun clearForm() {
        edtFilmName.text.clear()
        edtFilmDirector.text.clear()
        edtFilmDuration.text.clear()
        edtFilmGenre.text.clear()
        edtFilmReleaseDate.text.clear()
        edtFilmImage.text.clear()
    }

    // Chú ý: Cần đảm bảo Film AdminAdapter.kt và Film.kt tồn tại
    data class Film(
        val id: String = "",
        val name: String = "",
        val director: String = "",
        val duration: String = "",
        val genre: String = "",
        val releaseDate: String = "",
        val url: String = ""
    )
}