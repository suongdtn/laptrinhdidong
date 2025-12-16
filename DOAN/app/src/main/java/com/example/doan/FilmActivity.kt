package com.example.doan

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

object cinemas {
    var userName: String = ""
    var iEmail: String = ""
    var province: String = ""
}

class FilmActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var filma: Button
    private lateinit var filmb: Button
    private lateinit var underline1: View
    private lateinit var underline2: View
    private val filmList = mutableListOf<Film>()
    private lateinit var posterAdapter: PosterAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val delay: Long = 3000

    private val firestore = FirebaseFirestore.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ROOT LAYOUT
        val root = RelativeLayout(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#0D0D0D"))
        }

        // 🔴 HEADER - TĂNG TO HƠN NỮA
        val header = RelativeLayout(this).apply {
            id = View.generateViewId()
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#E50914"),
                    Color.parseColor("#C41E3A")
                )
            )
            setPadding(dp(16), dp(14), dp(16), dp(14)) // TĂNG: từ 12,10 lên 16,14
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(68) // TĂNG: từ 56 lên 68
            )
            elevation = dp(4).toFloat()
        }

        val btnBack = ImageView(this).apply {
            id = View.generateViewId()
            setImageResource(android.R.drawable.ic_media_previous)
            setColorFilter(Color.WHITE)
            layoutParams = RelativeLayout.LayoutParams(
                dp(44), dp(44) // TĂNG: từ 36 lên 44
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_START)
                addRule(RelativeLayout.CENTER_VERTICAL)
            }
            setPadding(dp(6)) // TĂNG: từ 4 lên 6
            setOnClickListener { finish() }
        }
        header.addView(btnBack)

        // ===== TWO TABS - TĂNG KÍCH THƯỚC =====
        val tabLayout = LinearLayout(this).apply {
            id = View.generateViewId()
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
        }

        // TAB 1
        val tab1 = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(16), dp(6), dp(16), dp(6)) // TĂNG: từ 12,4 lên 16,6
        }
        filma = Button(this).apply {
            text = "ĐANG CHIẾU"
            textSize = 15f // TĂNG: từ 13f lên 15f
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.05f
            minHeight = 0
            minimumHeight = 0
            setPadding(0, 0, 0, dp(6)) // TĂNG: từ 4 lên 6
        }
        underline1 = View(this).apply {
            setBackgroundColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(dp(110), dp(4)) // TĂNG: từ 100,3 lên 110,4
        }
        tab1.addView(filma)
        tab1.addView(underline1)

        // TAB 2
        val tab2 = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(16), dp(6), dp(16), dp(6)) // TĂNG: từ 12,4 lên 16,6
        }

        filmb = Button(this).apply {
            text = "SẮP CHIẾU"
            textSize = 15f // TĂNG: từ 13f lên 15f
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.05f
            minHeight = 0
            minimumHeight = 0
            setPadding(0, 0, 0, dp(6)) // TĂNG: từ 4 lên 6
        }
        underline2 = View(this).apply {
            setBackgroundColor(Color.WHITE)
            visibility = View.INVISIBLE
            layoutParams = LinearLayout.LayoutParams(dp(110), dp(4)) // TĂNG: từ 100,3 lên 110,4
        }
        tab2.addView(filmb)
        tab2.addView(underline2)

        tabLayout.addView(tab1)
        tabLayout.addView(tab2)

        header.addView(tabLayout)
        root.addView(header)

        // 🔴 BANNER VIEWPAGER
        val bannerContainer = CardView(this).apply {
            id = View.generateViewId()
            radius = dp(16).toFloat()
            cardElevation = dp(8).toFloat()
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(220)
            ).apply {
                addRule(RelativeLayout.BELOW, header.id)
                topMargin = dp(12)
                leftMargin = dp(16)
                rightMargin = dp(16)
            }
        }

        viewPager = ViewPager2(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            clipToPadding = false
            clipChildren = false
        }
        bannerContainer.addView(viewPager)
        root.addView(bannerContainer)

        // 🔴 FILM LIST
        val listContainer = FrameLayout(this).apply {
            id = View.generateViewId()
            setBackgroundColor(Color.parseColor("#0D0D0D"))
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                addRule(RelativeLayout.BELOW, bannerContainer.id)
                topMargin = dp(12)
            }
        }

        recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@FilmActivity)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        listContainer.addView(recyclerView)
        root.addView(listContainer)

        setContentView(root)

        // ===== KEEP ORIGINAL LOGIC =====
        filmAdapter = FilmAdapter(filmList)
        recyclerView.adapter = filmAdapter

        cinemas.iEmail = intent.getStringExtra("userEmail").toString()
        cinemas.province = intent.getStringExtra("province") ?: "Không xác định"
        val cinema = intent.getStringExtra("cinema") ?: "Không xác định"
        cinemas.userName = cinema

        val posters = listOf(
            "https://st.download.com.vn/data/image/2024/01/25/mai-700.jpg",
            "https://cdn.galaxycine.vn/media/2021/2/26/1350x900_1614324927951.jpg",
            "https://cdn.mobilecity.vn/mobilecity-vn/images/2025/01/review-phim-404-chay-ngay-di-noi-dung.jpg"
        )

        posterAdapter = PosterAdapter(posters)
        viewPager.adapter = posterAdapter
        startAutoScroll()

        fetchFilmsFromFirestore()

        filma.setOnClickListener {
            filmList.clear()
            fetchFilmsFromFirestore()
            updateTabUI(true)
        }

        filmb.setOnClickListener {
            filmList.clear()
            fetchFilmsFromFirestore()
            updateTabUI(false)
        }

        updateTabUI(true)
    }

    private fun updateTabUI(isFirst: Boolean) {
        if (isFirst) {
            underline1.visibility = View.VISIBLE
            underline2.visibility = View.INVISIBLE
            filma.setTypeface(null, Typeface.BOLD)
            filmb.setTypeface(null, Typeface.NORMAL)
        } else {
            underline1.visibility = View.INVISIBLE
            underline2.visibility = View.VISIBLE
            filma.setTypeface(null, Typeface.NORMAL)
            filmb.setTypeface(null, Typeface.BOLD)
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun startAutoScroll() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                currentPage = (currentPage + 1) % posterAdapter.itemCount
                viewPager.setCurrentItem(currentPage, true)
                handler.postDelayed(this, delay)
            }
        }, delay)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun fetchFilmsFromFirestore() {
        firestore.collection("Films")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Lỗi tải dữ liệu Firestore!", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    filmList.clear()

                    for (doc in snapshot.documents) {
                        val name = doc.getString("name") ?: ""
                        val days = doc.getString("releaseDate") ?: ""
                        val imageUrl = doc.getString("url") ?: doc.getString("URL") ?: ""
                        val time = doc.getString("duration") ?: ""
                        val type = doc.getString("genre") ?: ""
                        val directors = doc.getString("director") ?: ""

                        android.util.Log.d("FilmActivity", "Film: $name, URL: $imageUrl")

                        filmList.add(
                            Film(
                                name,
                                "Thời lượng: $time",
                                "Khởi chiếu: $days",
                                "Thể loại: $type",
                                "Đạo diễn: $directors",
                                imageUrl
                            )
                        )
                    }
                    filmList.reverse()
                    filmAdapter.notifyDataSetChanged()
                }
            }
    }
}


// ===== DATA + ADAPTER =====

data class Film(
    val title: String,
    val duration: String,
    val releaseDate: String,
    val genre: String,
    val director: String,
    val posterUrl: String
)

class FilmAdapter(private val films: List<Film>) :
    RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    companion object {
        private const val BOOKING_ACTIVITY = "com.example.doan.BookingActivity"
        private const val SELECT_PROVINCE_ACTIVITY = "com.example.doan.SelectProvinceActivity"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val context = parent.context

        val card = CardView(context).apply {
            radius = dp(context, 16).toFloat()
            cardElevation = dp(context, 8).toFloat()
            setCardBackgroundColor(Color.parseColor("#1A1A1A"))
            setContentPadding(0, 0, 0, 0)
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(
                    dp(context, 16),
                    dp(context, 12),
                    dp(context, 16),
                    dp(context, 12)
                )
            }
        }

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(context, 16))
        }

        // Poster - GIỮ KÍCH THƯỚC TO
        val posterContainer = FrameLayout(context).apply {
            background = GradientDrawable().apply {
                setStroke(dp(context, 2), Color.parseColor("#E50914"))
                cornerRadius = dp(context, 12).toFloat()
            }
            setPadding(dp(context, 2))
        }

        val img = ImageView(context).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(
                dp(context, 140),
                dp(context, 200)
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = GradientDrawable().apply {
                cornerRadius = dp(context, 10).toFloat()
            }
            clipToOutline = true
        }
        posterContainer.addView(img)

        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(context, 16), 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val title = TextView(context).apply {
            id = View.generateViewId()
            textSize = 18f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            maxLines = 2
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(context, 8) }
        }

        val t1 = TextView(context).apply {
            textSize = 13f
            setTextColor(Color.parseColor("#CCCCCC"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(context, 4) }
        }
        val t2 = TextView(context).apply {
            textSize = 13f
            setTextColor(Color.parseColor("#CCCCCC"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(context, 4) }
        }
        val t3 = TextView(context).apply {
            textSize = 13f
            setTextColor(Color.parseColor("#CCCCCC"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(context, 4) }
        }
        val t4 = TextView(context).apply {
            textSize = 13f
            setTextColor(Color.parseColor("#CCCCCC"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(context, 12) }
        }

        val btn = Button(context).apply {
            id = View.generateViewId()
            text = "ĐẶT VÉ NGAY"
            textSize = 14f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#E50914"),
                    Color.parseColor("#FF1F2A")
                )
            ).apply {
                cornerRadius = dp(context, 10).toFloat()
            }
            elevation = dp(context, 4).toFloat()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(context, 44)
            )
        }

        textLayout.addView(title)
        textLayout.addView(t1)
        textLayout.addView(t2)
        textLayout.addView(t3)
        textLayout.addView(t4)
        textLayout.addView(btn)

        layout.addView(posterContainer)
        layout.addView(textLayout)

        card.addView(layout)

        return FilmViewHolder(card, img, title, t1, t2, t3, t4, btn)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = films[position]
        holder.bind(film)

        holder.buyButton.setOnClickListener {
            val context = holder.itemView.context

            val targetClass = if (cinemas.userName != "Không xác định") BOOKING_ACTIVITY
            else SELECT_PROVINCE_ACTIVITY

            val intent = Intent().setClassName(context, targetClass)
            intent.putExtra("cinema", cinemas.userName)
            intent.putExtra("FILM_TITLE", film.title)
            intent.putExtra("province", cinemas.province)
            intent.putExtra("userEmail", cinemas.iEmail)
            intent.putExtra("FILM_DETAILS",
                " ${film.duration}\n ${film.releaseDate}\n ${film.genre}\n ${film.director}"
            )
            intent.putExtra("FILM_POSTER_URL", film.posterUrl)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = films.size

    class FilmViewHolder(
        itemView: View,
        private val poster: ImageView,
        private val title: TextView,
        private val dur: TextView,
        private val rel: TextView,
        private val gen: TextView,
        private val dir: TextView,
        val buyButton: Button
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(film: Film) {
            title.text = film.title
            dur.text = film.duration
            rel.text = film.releaseDate
            gen.text = film.genre
            dir.text = film.director

            Glide.with(itemView.context)
                .load(film.posterUrl)
                .centerCrop()
                .into(poster)
        }
    }

    private fun dp(context: android.content.Context, value: Int): Int {
        return (value * context.resources.displayMetrics.density).toInt()
    }
}

class PosterAdapter(private val images: List<String>) :
    RecyclerView.Adapter<PosterAdapter.ViewHolder>() {

    class ViewHolder(itemView: View, val img: ImageView) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context

        val container = FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val img = ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            clipToOutline = true
        }
        container.addView(img)

        return ViewHolder(container, img)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(images[position])
            .centerCrop()
            .into(holder.img)
    }

    override fun getItemCount(): Int = images.size

    private fun dp(context: android.content.Context, value: Int): Int {
        return (value * context.resources.displayMetrics.density).toInt()
    }
}