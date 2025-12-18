package com.example.doan

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * BaseActivity chứa Bottom Navigation chung cho tất cả các màn hình
 * Các Activity con chỉ cần kế thừa từ BaseActivity này
 */
abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var bottomNavigation: BottomNavigationView
    protected var userEmail: String? = null

    companion object {
        const val HOME_ACTIVITY = "com.example.doan.HomeActivity"
        const val FILM_ACTIVITY = "com.example.doan.FilmActivity"
        const val EVENT_ACTIVITY = "com.example.doan.EventActivity"
        const val PROMOTION_ACTIVITY = "com.example.doan.PromotionActivity"
    }

    /**
     * Phương thức abstract để các Activity con trả về nav item ID của chính nó
     */
    abstract fun getNavigationMenuItemId(): Int

    /**
     * Khởi tạo Bottom Navigation
     * Gọi phương thức này trong onCreate() của Activity con
     */
    protected fun setupBottomNavigation(bottomNav: BottomNavigationView, email: String?) {
        bottomNavigation = bottomNav
        userEmail = email

        // Thiết lập giao diện
        bottomNavigation.apply {
            inflateMenu(R.menu.bottom_nav_menu)
            background = createBottomNavBackground()
            elevation = dp(12).toFloat()
            itemIconTintList = ContextCompat.getColorStateList(this@BaseActivity, R.color.bottom_nav_color)
            itemTextColor = ContextCompat.getColorStateList(this@BaseActivity, R.color.bottom_nav_color)
        }

        // Set item được chọn cho trang hiện tại
        bottomNavigation.selectedItemId = getNavigationMenuItemId()

        // Xử lý sự kiện click
        bottomNavigation.setOnItemSelectedListener { item ->
            handleNavigationItemSelected(item.itemId)
        }
    }

    /**
     * Xử lý khi người dùng click vào item trong Bottom Navigation
     */
    private fun handleNavigationItemSelected(itemId: Int): Boolean {
        // Nếu đang ở trang hiện tại, không làm gì
        if (itemId == getNavigationMenuItemId()) {
            return true
        }

        // Chuyển sang trang khác
        when (itemId) {
            R.id.nav_home -> {
                navigateToActivity(HOME_ACTIVITY)
                return true
            }
            R.id.nav_phim -> {
                navigateToActivity(FILM_ACTIVITY)
                return true
            }
            R.id.nav_su_kien -> {
                navigateToActivity(EVENT_ACTIVITY)
                return true
            }
            R.id.nav_khuyen_mai -> {
                navigateToActivity(PROMOTION_ACTIVITY)
                return true
            }
        }
        return false
    }

    /**
     * Chuyển sang Activity khác
     */
    private fun navigateToActivity(targetActivity: String) {
        val intent = Intent().setClassName(this, targetActivity)
        userEmail?.let { intent.putExtra("userEmail", it) }
        startActivity(intent)

        // Thêm animation chuyển trang mượt mà
        overridePendingTransition(0, 0)

        // Kết thúc Activity hiện tại để tránh tích lũy Activity
        finish()
    }

    /**
     * Tạo background gradient cho Bottom Navigation
     */
    private fun createBottomNavBackground(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor("#B00710"),
                Color.parseColor("#E50914"),
                Color.parseColor("#FF1F2A")
            )
        ).apply {
            cornerRadii = floatArrayOf(
                dp(20).toFloat(), dp(20).toFloat(),
                dp(20).toFloat(), dp(20).toFloat(),
                0f, 0f, 0f, 0f
            )
        }
    }

    /**
     * Chuyển đổi dp sang pixel
     */
    protected fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    /**
     * Hiển thị Toast message
     */
    protected fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}