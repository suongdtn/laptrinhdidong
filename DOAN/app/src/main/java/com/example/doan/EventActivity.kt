package com.example.doan

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class EventActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private val eventList = mutableListOf<Event>()
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val rootLayout = createRootLayout()
            setContentView(rootLayout)

            database = FirebaseFirestore.getInstance()
            fetchEventsFromFirestore()
        } catch (e: Exception) {
            Log.e("EventActivity", "Error in onCreate", e)
            Toast.makeText(this, "Lỗi khởi tạo: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun createRootLayout(): View {
        val rootLayout = LinearLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    Color.parseColor("#0D0D0D"),
                    Color.parseColor("#1A1A1A")
                )
            )
        }

        rootLayout.addView(createHeader())
        rootLayout.addView(createRecyclerView())

        return rootLayout
    }

    private fun createHeader(): View {
        val headerLayout = RelativeLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(70)
            )
            background = createHeaderGradient()
            setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
            elevation = dpToPx(8).toFloat()
        }

        val backButtonContainer = FrameLayout(this).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                dpToPx(44),
                dpToPx(44)
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_START)
                addRule(RelativeLayout.CENTER_VERTICAL)
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#40FFFFFF"))
            }
        }

        val btnBack = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                dpToPx(24),
                dpToPx(24)
            ).apply {
                gravity = Gravity.CENTER
            }
            try {
                setImageResource(R.drawable.baseline_keyboard_backspace_24)
            } catch (e: Exception) {
                Log.e("EventActivity", "Icon not found", e)
            }
            setColorFilter(Color.WHITE)

            val outValue = TypedValue()
            context.theme.resolveAttribute(
                android.R.attr.selectableItemBackgroundBorderless,
                outValue,
                true
            )
            setBackgroundResource(outValue.resourceId)

            setOnClickListener { finish() }
        }
        backButtonContainer.addView(btnBack)
        headerLayout.addView(backButtonContainer)

        val titleWithIcon = LinearLayout(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val iconLeft = TextView(this).apply {
            text = "✨"
            textSize = 18f
            setPadding(0, 0, dpToPx(8), 0)
        }

        val titleText = TextView(this).apply {
            text = "SỰ KIỆN NỔI BẬT"
            textSize = 20f
            setTextColor(Color.WHITE)
            setTypeface(typeface, Typeface.BOLD)
            letterSpacing = 0.08f
            setShadowLayer(4f, 0f, 2f, Color.parseColor("#80000000"))
        }

        val iconRight = TextView(this).apply {
            text = "✨"
            textSize = 18f
            setPadding(dpToPx(8), 0, 0, 0)
        }

        titleWithIcon.addView(iconLeft)
        titleWithIcon.addView(titleText)
        titleWithIcon.addView(iconRight)
        headerLayout.addView(titleWithIcon)

        val accentLine = View(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(2)
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
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

        return headerLayout
    }

    private fun createRecyclerView(): View {
        recyclerView = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            layoutManager = LinearLayoutManager(this@EventActivity)
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
            clipToPadding = false
            clipChildren = false
            isNestedScrollingEnabled = true
            overScrollMode = View.OVER_SCROLL_NEVER

            eventAdapter = EventAdapter(eventList) { event ->
                try {
                    Log.d("EventActivity", "Selected Event: Title: ${event.title}, Days: ${event.date}, Pot: ${event.location}")

                    val intent = Intent(this@EventActivity, ShowInfoActivity::class.java)
                    intent.putExtra("eventTitle", event.title)
                    intent.putExtra("eventDays", event.date)
                    intent.putExtra("eventImageUrl", event.imageUrl)
                    intent.putExtra("eventPot", event.location)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("EventActivity", "Error opening event", e)
                    Toast.makeText(this@EventActivity, "Lỗi mở sự kiện", Toast.LENGTH_SHORT).show()
                }
            }
            adapter = eventAdapter
        }

        return recyclerView
    }

    private fun createHeaderGradient(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor("#B00710"),
                Color.RED,
                Color.parseColor("#FF1F2A")
            )
        )
    }

    private fun fetchEventsFromFirestore() {
        try {
            database.collection("Event")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("EventActivity", "Error fetching events", error)
                        Toast.makeText(this, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        eventList.clear()

                        for (doc in snapshot.documents) {
                            try {
                                // ĐÃ SỬA: Map đúng tên trường từ Firestore
                                val title = doc.getString("title") ?: ""
                                val days = doc.getString("days") ?: ""
                                val imageUrl = doc.getString("image") ?: ""
                                val pot = doc.getString("pot") ?: ""

                                // Tạo Event với tên field đúng
                                val event = Event(
                                    title = title,
                                    date = days,      // days từ Firestore -> date trong Event
                                    imageUrl = imageUrl,
                                    location = pot    // pot từ Firestore -> location trong Event
                                )
                                eventList.add(event)

                                Log.d("EventActivity", "Loaded event: $title, date: $days, location: $pot")
                            } catch (e: Exception) {
                                Log.e("EventActivity", "Error parsing event", e)
                            }
                        }

                        eventList.reverse()
                        eventAdapter.updateList(eventList)

                        Log.d("EventActivity", "Total events loaded: ${eventList.size}")

                        if (eventList.isEmpty()) {
                            Toast.makeText(this, "Không có sự kiện nào", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("EventActivity", "Error setting up listener", e)
            Toast.makeText(this, "Lỗi kết nối Firebase: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}