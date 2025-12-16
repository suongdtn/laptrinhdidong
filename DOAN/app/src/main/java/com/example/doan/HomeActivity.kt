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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var rootLayout: ConstraintLayout
    private lateinit var backgroundImage: ImageView
    private lateinit var gradientOverlay: View
    private lateinit var viewPager: ViewPager2
    private lateinit var menuButton: ImageButton
    private lateinit var dimBackground: View
    private lateinit var menuLayout: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var btnInfo: LinearLayout
    private lateinit var btnHistory: LinearLayout
    private lateinit var btnCinema: LinearLayout
    private lateinit var btnLogout: LinearLayout

    private lateinit var firestore: FirebaseFirestore
    private var isMenuVisible = false

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var adapter: MovieAdapter

    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            if (::adapter.isInitialized && adapter.itemCount > 0) {
                val nextItem = (viewPager.currentItem + 1) % adapter.itemCount
                viewPager.setCurrentItem(nextItem, true)
                Glide.with(backgroundImage.context)
                    .load(adapter.getImageResource(nextItem))
                    .into(backgroundImage)
                handler.postDelayed(this, 3000)
            }
        }
    }

    companion object {
        private const val SELECT_PROVINCE_ACTIVITY = "com.example.doan.SelectProvinceActivity"
        private const val FILM_ACTIVITY = "com.example.doan.FilmActivity"
        private const val EVENT_ACTIVITY = "com.example.doan.EventActivity"
        private const val PROMOTION_ACTIVITY = "com.example.doan.PromotionActivity"
        private const val USER_INFO_ACTIVITY = "com.example.doan.UserInfoActivity"
        private const val TICKET_ACTIVITY = "com.example.doan.TicketActivity"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        buildUI()

        val userEmail = intent.getStringExtra("userEmail")
        setupListeners(userEmail)
        fetchMovies(userEmail)
    }


    private fun buildUI() {
        rootLayout = ConstraintLayout(this).apply {
            id = View.generateViewId()
            setBackgroundColor(Color.parseColor("#0D0D0D"))
        }
        setContentView(rootLayout)

        // BACKGROUND IMAGE - TĂNG ALPHA ĐỂ SÁNG HƠN ----------------------------------------------------
        backgroundImage = ImageView(this).apply {
            id = View.generateViewId()
            scaleType = ImageView.ScaleType.CENTER_CROP
            alpha = 0.65f  // Tăng từ 0.35f lên 0.65f để sáng hơn nhiều
        }
        rootLayout.addView(backgroundImage)

        // GRADIENT OVERLAY - GIẢM ĐỘ ĐẬM ĐỂ BACKGROUND SÁNG HƠN ---------------------------------------------------
        gradientOverlay = View(this).apply {
            id = View.generateViewId()
            background = createGradientOverlay()
        }
        rootLayout.addView(gradientOverlay)

        // DIM BACKGROUND -----------------------------------------------------
        dimBackground = View(this).apply {
            id = View.generateViewId()
            setBackgroundColor(Color.parseColor("#CC000000"))
            visibility = View.GONE
            elevation = dp(10).toFloat()
        }
        rootLayout.addView(dimBackground)

        // MENU BUTTON (hamburger) - --------------------------------------------
        menuButton = ImageButton(this).apply {
            id = View.generateViewId()
            setImageResource(R.drawable.baseline_dehaze_24)
            background = createMenuButtonBackground()
            elevation = dp(6).toFloat()
            setColorFilter(Color.WHITE)  // Đổi icon thành màu trắng

            // TĂNG VÙNG CHẠM
            setPadding(dp(12), dp(12), dp(12), dp(12))
            scaleType = ImageView.ScaleType.CENTER
        }

        rootLayout.addView(menuButton)

        // MENU LAYOUT --------------------------------------------------------
        menuLayout = buildSideMenu()
        rootLayout.addView(menuLayout)

        // VIEWPAGER (film slider) - TĂNG KÍCH THƯỚC --------------------------------------------
        viewPager = ViewPager2(this).apply {
            id = View.generateViewId()
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 1
            elevation = dp(2).toFloat()
        }
        rootLayout.addView(viewPager)

        // BOTTOM NAVIGATION --------------------------------------------------
        bottomNavigation = BottomNavigationView(this).apply {
            id = View.generateViewId()
            inflateMenu(R.menu.bottom_nav_menu)
            background = createBottomNavBackground()
            elevation = dp(12).toFloat()
            itemIconTintList = ContextCompat.getColorStateList(this@HomeActivity, R.color.white)
            itemTextColor = ContextCompat.getColorStateList(this@HomeActivity, R.color.white)
        }
        rootLayout.addView(bottomNavigation)

        // APPLY CONSTRAINTS ---------------------------------------------------
        applyConstraints()


    }

    private fun buildSideMenu(): LinearLayout {
        val layout = LinearLayout(this).apply {
            id = View.generateViewId()
            orientation = LinearLayout.VERTICAL
            background = createMenuBackground()
            elevation = dp(16).toFloat()
            visibility = View.GONE
            setPadding(dp(0), dp(25), dp(0), dp(25))
        }

        // Header menu với gradient
        val menuHeader = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#E50914"),
                    Color.parseColor("#B00710")
                )
            ).apply {
                cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, dp(16).toFloat(), dp(16).toFloat(), 0f, 0f)
            }
            setPadding(dp(20), dp(20), dp(20), dp(20))
            gravity = Gravity.CENTER
        }

        val menuTitle = TextView(this).apply {
            text = "MENU"
            textSize = 22f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            letterSpacing = 0.15f
        }
        menuHeader.addView(menuTitle)
        layout.addView(menuHeader)

        // Divider
        val divider = View(this).apply {
            setBackgroundColor(Color.parseColor("#2A2A2A"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(1)
            ).apply {
                topMargin = dp(0)
                bottomMargin = dp(8)
            }
        }
        layout.addView(divider)

        fun menuItem(icon: Int, text: String): LinearLayout {
            val itemLayout = LinearLayout(this@HomeActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(24), dp(18), dp(24), dp(18))
                isClickable = true
                isFocusable = true
                foreground = ContextCompat.getDrawable(this@HomeActivity, android.R.drawable.list_selector_background)
            }

            val iconContainer = FrameLayout(this@HomeActivity).apply {
                layoutParams = LinearLayout.LayoutParams(dp(48), dp(48))
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(Color.parseColor("#1AE50914"))
                }
            }

            val iconView = ImageView(this@HomeActivity).apply {
                setImageResource(icon)
                setColorFilter(Color.parseColor("#E50914"))
                layoutParams = FrameLayout.LayoutParams(dp(28), dp(28)).apply {
                    gravity = Gravity.CENTER
                }
            }
            iconContainer.addView(iconView)

            val textView = TextView(this@HomeActivity).apply {
                setTextColor(Color.parseColor("#E8E8E8"))
                textSize = 17f
                setTypeface(null, Typeface.BOLD)
                this.text = text
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = dp(16)
                }
            }

            itemLayout.addView(iconContainer)
            itemLayout.addView(textView)

            return itemLayout
        }

        btnInfo = menuItem(R.drawable.ic_thong_tin, "Thông tin")
        btnHistory = menuItem(R.drawable.ic_ve_cua_toi, "Vé của tôi")
        btnCinema = menuItem(R.drawable.ic_rap, "Rạp")
        btnLogout = menuItem(R.drawable.ic_dang_xuat, "Đăng xuất")

        layout.addView(btnInfo)
        layout.addView(btnHistory)
        layout.addView(btnCinema)
        layout.addView(btnLogout)

        return layout
    }

    private fun applyConstraints() {
        val set = ConstraintSet()
        set.clone(rootLayout)

        // Background Image
        set.connect(backgroundImage.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        set.connect(backgroundImage.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        set.connect(backgroundImage.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(backgroundImage.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        // Gradient Overlay
        set.connect(gradientOverlay.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        set.connect(gradientOverlay.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        set.connect(gradientOverlay.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(gradientOverlay.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        // Dim Background
        set.connect(dimBackground.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        set.connect(dimBackground.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        set.connect(dimBackground.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(dimBackground.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        // Menu Button
        set.constrainWidth(menuButton.id, dp(52))
        set.constrainHeight(menuButton.id, dp(52))

        set.connect(
            menuButton.id,
            ConstraintSet.TOP,
            ConstraintSet.PARENT_ID,
            ConstraintSet.TOP,
            dp(40)
        )

        set.connect(
            menuButton.id,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            dp(20)
        )

        // Menu Layout
        set.constrainWidth(menuLayout.id, dp(280))
        set.connect(menuLayout.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        set.connect(menuLayout.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        set.connect(menuLayout.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)

        // ViewPager - Bây giờ nối trực tiếp từ top của parent
        set.constrainHeight(viewPager.id, dp(0))
        set.connect(viewPager.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, dp(12))
        set.connect(viewPager.id, ConstraintSet.BOTTOM, bottomNavigation.id, ConstraintSet.TOP, dp(8))
        set.connect(viewPager.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(viewPager.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        // Bottom Navigation
        set.connect(bottomNavigation.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        set.connect(bottomNavigation.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(bottomNavigation.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        set.constrainHeight(bottomNavigation.id, dp(70))

        set.applyTo(rootLayout)
    }

    // =====================================================================
    //                       DRAWABLE HELPERS
    // =====================================================================

    private fun createGradientOverlay(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.parseColor("#40000000"),  // Giảm từ #80000000 xuống #40000000 (ít đen hơn)
                Color.parseColor("#80000000")   // Giảm từ #CC000000 xuống #80000000 (ít đen hơn)
            )
        )
    }

    private fun createMenuButtonBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            colors = intArrayOf(
                Color.parseColor("#E50914"),
                Color.parseColor("#B00710")
            )
            gradientType = GradientDrawable.RADIAL_GRADIENT
            gradientRadius = dp(19).toFloat()
        }
    }

    private fun createMenuBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.parseColor("#1C1C1C"))
            cornerRadii = floatArrayOf(0f, 0f, dp(20).toFloat(), dp(20).toFloat(), dp(20).toFloat(), dp(20).toFloat(), 0f, 0f)
            setStroke(dp(1), Color.parseColor("#2A2A2A"))
        }
    }

    private fun createBottomNavBackground(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor("#B00710"),
                Color.parseColor("#E50914"),
                Color.parseColor("#FF1F2A")
            )
        ).apply {
            cornerRadii = floatArrayOf(dp(20).toFloat(), dp(20).toFloat(), dp(20).toFloat(), dp(20).toFloat(), 0f, 0f, 0f, 0f)
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    // =====================================================================
    //
    // =====================================================================

    private fun setupListeners(userEmail: String?) {
        menuButton.setOnClickListener { toggleMenu() }
        dimBackground.setOnClickListener { toggleMenu() }

        btnInfo.setOnClickListener {
            showToast("Thông tin")
            start(USER_INFO_ACTIVITY, userEmail)
        }

        btnHistory.setOnClickListener {
            showToast("Vé của tôi")
            start(TICKET_ACTIVITY, userEmail)
        }

        btnCinema.setOnClickListener {
            showToast("Rạp")
            start(SELECT_PROVINCE_ACTIVITY, userEmail)
        }

        btnLogout.setOnClickListener {
            showToast("Đăng xuất")
            finish()
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_phim -> {
                    showToast("Phim")
                    start(FILM_ACTIVITY, userEmail)
                    true
                }
                R.id.nav_mua_ve -> {
                    showToast("Sự kiện")
                    start(EVENT_ACTIVITY)
                    true
                }
                R.id.nav_rap -> {
                    showToast("Rạp")
                    start(SELECT_PROVINCE_ACTIVITY, userEmail)
                    true
                }
                R.id.nav_khuyen_mai -> {
                    showToast("Khuyến mãi")
                    start(PROMOTION_ACTIVITY)
                    true
                }
                else -> false
            }
        }
    }

    private fun start(target: String, userEmail: String? = null) {
        val intent = Intent().setClassName(this, target)
        userEmail?.let { intent.putExtra("userEmail", it) }
        startActivity(intent)
    }

    private fun fetchMovies(userEmail: String?) {
        firestore.collection("Films")
            .get()
            .addOnSuccessListener { result ->

                val movieList = result.documents.mapNotNull { doc ->
                    val name = doc.getString("name") ?: return@mapNotNull null
                    val url = doc.getString("url") ?: return@mapNotNull null
                    val duration = doc.getString("duration") ?: return@mapNotNull null
                    Movie(name, duration, url)
                }

                if (movieList.isNotEmpty()) {
                    adapter = MovieAdapter(movieList) { movie ->
                        val intent = Intent()
                            .setClassName(this, SELECT_PROVINCE_ACTIVITY)
                        intent.putExtra("userEmail", userEmail)
                        intent.putExtra("province", "")
                        intent.putExtra("FILM_TITLE", movie.title)
                        intent.putExtra("FILM_DETAILS", "${movie.title}\n${movie.info}")
                        intent.putExtra("FILM_POSTER_URL", movie.imageResId)
                        startActivity(intent)
                    }

                    viewPager.adapter = adapter

                    // QUAN TRỌNG: Force ViewPager2 và RecyclerView bên trong để mỗi item full width
                    viewPager.post {
                        viewPager.getChildAt(0)?.let { recyclerView ->
                            recyclerView.setPadding(0, 0, 0, 0)
                            (recyclerView as? androidx.recyclerview.widget.RecyclerView)?.apply {
                                clipToPadding = false
                                clipChildren = false
                            }
                        }
                    }

                    Glide.with(this).load(adapter.getImageResource(0)).into(backgroundImage)

                    // Lắng nghe khi chuyển trang để đổi background
                    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            Glide.with(this@HomeActivity)
                                .load(adapter.getImageResource(position))
                                .into(backgroundImage)
                        }
                    })

                    handler.postDelayed(autoScrollRunnable, 3000)
                } else {
                    showToast("Không có dữ liệu phim.")
                }
            }
            .addOnFailureListener {
                showToast("Lỗi tải dữ liệu phim!")
            }
    }

    private fun toggleMenu() {
        isMenuVisible = !isMenuVisible
        menuLayout.visibility = if (isMenuVisible) View.VISIBLE else View.GONE
        dimBackground.visibility = if (isMenuVisible) View.VISIBLE else View.GONE
        menuButton.setImageResource(
            if (isMenuVisible) R.drawable.baseline_keyboard_backspace_24
            else R.drawable.baseline_dehaze_24
        )
    }

    private fun showToast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(autoScrollRunnable)
    }
}