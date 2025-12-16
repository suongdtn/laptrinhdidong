package com.example.doan

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class EventManagementActivity : AppCompatActivity() {

    private lateinit var edtTitle: EditText
    private lateinit var edtDate: EditText
    private lateinit var edtLocation: EditText
    private lateinit var edtImage: EditText
    private lateinit var btnUpdate: Button
    private lateinit var recyclerView: RecyclerView

    private lateinit var eventAdapter: EventadminAdapter
    private val eventList = mutableListOf<Event>()
    private lateinit var database: FirebaseFirestore

    private var currentEditingEvent: Event? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseFirestore.getInstance()

        /* ================= SCROLL VIEW ROOT ================= */
        val scrollView = ScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#1A1A1A"))
        }

        /* ================= MAIN LAYOUT ================= */
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, 0)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        /* ================= HEADER ================= */
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#E53935"))
            setPadding(32, 48, 32, 32)
        }

        val tvTitle = TextView(this).apply {
            text = "CINEMA ADMIN"
            textSize = 16f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            letterSpacing = 0.1f
        }
        header.addView(tvTitle)
        root.addView(header)

        /* ================= FORM CONTAINER ================= */
        val formContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1A1A1A"))
            setPadding(32, 32, 32, 32)
        }

        /* ================= SECTION TITLE ================= */
        val sectionTitle = TextView(this).apply {
            text = "Thêm Sự Kiện Mới"
            textSize = 18f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 24)
            gravity = Gravity.START
        }
        formContainer.addView(sectionTitle)

        /* ================= INPUTS ================= */
        edtTitle = createStyledEditText("🎬 Nhập tiêu đề sự kiện")
        edtDate = createStyledEditText("📅 Nhập ngày tổ chức (VD: 01/01/2024)")
        edtLocation = createStyledEditText("📍 Nhập địa điểm")
        edtImage = createStyledEditText("🖼️ Nhập link hình ảnh")

        formContainer.addView(edtTitle)
        formContainer.addView(edtDate)
        formContainer.addView(edtLocation)
        formContainer.addView(edtImage)

        /* ================= BUTTON ================= */
        btnUpdate = Button(this).apply {
            text = "✨ CẬP NHẬT SỰ KIỆN"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 40, 0, 40)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            }
            background = createButtonBackground()
        }
        formContainer.addView(btnUpdate)

        root.addView(formContainer)

        /* ================= LIST SECTION ================= */
        val listContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1A1A1A"))
            setPadding(32, 16, 32, 32)
        }

        val listTitle = TextView(this).apply {
            text = "Danh Sách Sự Kiện"
            textSize = 18f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 24)
            gravity = Gravity.START
        }
        listContainer.addView(listTitle)

        /* ================= RECYCLER VIEW ================= */
        recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@EventManagementActivity)
            setBackgroundColor(Color.TRANSPARENT)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        listContainer.addView(recyclerView)

        root.addView(listContainer)
        scrollView.addView(root)
        setContentView(scrollView)

        /* ================= ADAPTER ================= */
        eventAdapter = EventadminAdapter(
            eventList,
            ::editEvent,
            ::deleteEvent
        )
        recyclerView.adapter = eventAdapter

        btnUpdate.setOnClickListener {
            if (isEditMode) updateEvent() else addEvent()
        }

        loadEventData()
    }

    /* ================= UI HELPER ================= */
    private fun createStyledEditText(hint: String): EditText {
        return EditText(this).apply {
            this.hint = hint
            setHintTextColor(Color.parseColor("#666666"))
            setTextColor(Color.parseColor("#CCCCCC"))
            setPadding(48, 36, 48, 36)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
            background = createEditTextBackground()
        }
    }

    private fun createEditTextBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.parseColor("#2A2A2A"))
            cornerRadius = 16f
            setStroke(2, Color.parseColor("#333333"))
        }
    }

    private fun createButtonBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.parseColor("#E53935"))
            cornerRadius = 16f
        }
    }

    /* ================= CRUD ================= */
    private fun addEvent() {
        val title = edtTitle.text.toString()
        val date = edtDate.text.toString()
        val location = edtLocation.text.toString()
        val image = edtImage.text.toString()

        if (title.isEmpty() || date.isEmpty() || location.isEmpty()) {
            toast("Vui lòng nhập đầy đủ thông tin!")
            return
        }

        val data = hashMapOf(
            "title" to title,
            "days" to date,
            "pot" to location,
            "image" to image
        )

        database.collection("Event")
            .add(data)
            .addOnSuccessListener {
                toast("Đã thêm sự kiện!")
                clearForm()
                loadEventData()
            }
            .addOnFailureListener {
                toast("Thêm thất bại!")
            }
    }

    private fun editEvent(event: Event) {
        isEditMode = true
        currentEditingEvent = event

        edtTitle.setText(event.title)
        edtDate.setText(event.date)
        edtLocation.setText(event.location)
        edtImage.setText(event.imageUrl)

        btnUpdate.text = "✨ CẬP NHẬT SỰ KIỆN"

        // Scroll to top
        val scrollView = (window.decorView.findViewById<ViewGroup>(android.R.id.content)
            .getChildAt(0) as? ScrollView)
        scrollView?.smoothScrollTo(0, 0)
    }

    private fun updateEvent() {
        val event = currentEditingEvent ?: return

        val title = edtTitle.text.toString()
        val date = edtDate.text.toString()
        val location = edtLocation.text.toString()
        val image = edtImage.text.toString()

        database.collection("Event")
            .whereEqualTo("title", event.title)
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                    doc.reference.update(
                        mapOf(
                            "title" to title,
                            "days" to date,
                            "pot" to location,
                            "image" to image
                        )
                    )
                }
                toast("Cập nhật thành công!")
                clearForm()
                resetEditMode()
                loadEventData()
            }
            .addOnFailureListener {
                toast("Cập nhật thất bại!")
            }
    }

    private fun deleteEvent(event: Event) {
        database.collection("Event")
            .whereEqualTo("title", event.title)
            .get()
            .addOnSuccessListener {
                for (doc in it) doc.reference.delete()
                eventList.remove(event)
                eventAdapter.updateList(eventList)
                toast("Đã xóa sự kiện!")
            }
    }

    private fun loadEventData() {
        database.collection("Event")
            .addSnapshotListener { value, _ ->
                eventList.clear()
                value?.forEach {
                    eventList.add(
                        Event(
                            it.getString("title") ?: "",
                            it.getString("days") ?: "",
                            it.getString("image") ?: "",
                            it.getString("pot") ?: ""
                        )
                    )
                }
                eventList.reverse()
                eventAdapter.updateList(eventList)
            }
    }

    /* ================= UTIL ================= */
    private fun clearForm() {
        edtTitle.text.clear()
        edtDate.text.clear()
        edtLocation.text.clear()
        edtImage.text.clear()
    }

    private fun resetEditMode() {
        isEditMode = false
        currentEditingEvent = null
        btnUpdate.text = "✨ CẬP NHẬT SỰ KIỆN"
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}