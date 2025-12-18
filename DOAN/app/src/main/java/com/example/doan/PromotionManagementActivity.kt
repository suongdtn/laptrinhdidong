package com.example.doan

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class PromotionManagementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var promotionAdapter: PromotionadminAdapter
    private val promotionList = mutableListOf<Promotion>()
    private lateinit var database: CollectionReference
    private lateinit var edtPromotionTitle: EditText
    private lateinit var edtPromotionDate: EditText
    private lateinit var edtPromotionLocation: EditText
    private lateinit var edtPromotionImage: EditText
    private lateinit var edtPromotionContent: EditText // ✅ THÊM MỚI
    private lateinit var btnUpdate: Button
    private lateinit var btnCancel: Button

    private var editingPromotionId: String? = null
    private var isEditMode = false

    companion object {
        private const val TAG = "PromotionManagement"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1a1a1a"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // ========== HEADER ==========
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#E53935"))
            setPadding(48, 48, 48, 48)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val titleText = TextView(this).apply {
            text = "QUẢN LÝ KHUYẾN MÃI"
            setTextColor(Color.WHITE)
            textSize = 24f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        val subtitleText = TextView(this).apply {
            text = "PROMOTION ADMIN"
            setTextColor(Color.WHITE)
            textSize = 12f
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 0)
        }

        headerLayout.addView(titleText)
        headerLayout.addView(subtitleText)
        mainLayout.addView(headerLayout)

        // ========== SCROLL VIEW ==========
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        // TIÊU ĐỀ THÔNG TIN
        val infoTitle = TextView(this).apply {
            text = "Thông Tin Khuyến Mãi"
            setTextColor(Color.WHITE)
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 48)
        }
        contentLayout.addView(infoTitle)

        // CÁC TRƯỜNG INPUT
        edtPromotionTitle = createEditText("🎉 Tiêu đề khuyến mãi")
        contentLayout.addView(edtPromotionTitle)

        edtPromotionDate = createEditText("📅 Thời gian khuyến mãi")
        contentLayout.addView(edtPromotionDate)

        edtPromotionLocation = createEditText("📍 Địa điểm")
        contentLayout.addView(edtPromotionLocation)

        edtPromotionImage = createEditText("🖼 URL hình ảnh")
        contentLayout.addView(edtPromotionImage)

        // ✅ THÊM TRƯỜNG NỘI DUNG
        edtPromotionContent = createMultilineEditText("📝 Nội dung chi tiết khuyến mãi")
        contentLayout.addView(edtPromotionContent)

        // LAYOUT CHO 2 NÚT
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 24, 0, 72)
            }
        }

        // NÚT THÊM/CẬP NHẬT
        btnUpdate = Button(this).apply {
            text = "➕ THÊM"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setBackgroundColor(Color.parseColor("#E53935"))
            layoutParams = LinearLayout.LayoutParams(
                0,
                168,
                1f
            ).apply {
                setMargins(0, 0, 12, 0)
            }
            setOnClickListener {
                if (isEditMode) {
                    updatePromotion()
                } else {
                    addPromotion()
                }
            }
        }

        // NÚT HỦY
        btnCancel = Button(this).apply {
            text = "🚫 HỦY"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setBackgroundColor(Color.parseColor("#555555"))
            layoutParams = LinearLayout.LayoutParams(
                0,
                168,
                1f
            ).apply {
                setMargins(12, 0, 0, 0)
            }
            setOnClickListener {
                clearInputFields()
                resetEditMode()
                Toast.makeText(this@PromotionManagementActivity, "Đã hủy", Toast.LENGTH_SHORT).show()
            }
        }

        buttonLayout.addView(btnUpdate)
        buttonLayout.addView(btnCancel)
        contentLayout.addView(buttonLayout)

        // TIÊU ĐỀ DANH SÁCH
        val listTitle = TextView(this).apply {
            text = "Danh Sách Khuyến Mãi"
            setTextColor(Color.WHITE)
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 48)
        }
        contentLayout.addView(listTitle)

        // RECYCLERVIEW
        recyclerView = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutManager = LinearLayoutManager(this@PromotionManagementActivity)
        }
        contentLayout.addView(recyclerView)

        scrollView.addView(contentLayout)
        mainLayout.addView(scrollView)

        setContentView(mainLayout)

        // FIREBASE
        database = FirebaseFirestore.getInstance().collection("Promotion")
        Log.d(TAG, "Firebase initialized")

        promotionAdapter = PromotionadminAdapter(promotionList, ::editPromotion, ::deletePromotion)
        recyclerView.adapter = promotionAdapter

        loadPromotionData()
    }

    private fun createEditText(hint: String): EditText {
        return EditText(this).apply {
            this.hint = hint
            setHintTextColor(Color.parseColor("#666666"))
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#2a2a2a"))
            setPadding(36, 36, 36, 36)
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 36)
            }
        }
    }

    // ✅ THÊM HÀM TẠO EDITTEXT MULTILINE
    private fun createMultilineEditText(hint: String): EditText {
        return EditText(this).apply {
            this.hint = hint
            setHintTextColor(Color.parseColor("#666666"))
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#2a2a2a"))
            setPadding(36, 36, 36, 36)
            textSize = 16f
            minLines = 5
            maxLines = 10
            gravity = Gravity.TOP or Gravity.START
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 36)
            }
        }
    }

    private fun addPromotion() {
        val title = edtPromotionTitle.text.toString().trim()
        val date = edtPromotionDate.text.toString().trim()
        val location = edtPromotionLocation.text.toString().trim()
        val image = edtPromotionImage.text.toString().trim()
        val content = edtPromotionContent.text.toString().trim() // ✅ THÊM MỚI

        Log.d(TAG, "addPromotion - Title: $title, Date: $date, Location: $location, Content: $content")

        if (title.isEmpty() || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            Log.w(TAG, "Missing required fields")
            return
        }

        savePromotionToDatabase(title, date, location, image, content) // ✅ THÊM THAM SỐ
    }

    private fun savePromotionToDatabase(title: String, days: String, location: String, image: String, content: String) {
        val promotionData = hashMapOf(
            "title" to title,
            "days" to days,
            "location" to location,
            "image" to image,
            "content" to content // ✅ THÊM MỚI
        )

        Log.d(TAG, "Saving to Firestore: $promotionData")

        database.add(promotionData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Document added with ID: ${documentReference.id}")
                Toast.makeText(this, "Thêm khuyến mãi thành công!", Toast.LENGTH_SHORT).show()
                clearInputFields()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document", e)
                Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun loadPromotionData() {
        Log.d(TAG, "Loading promotion data...")

        database.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error loading data", error)
                Toast.makeText(this, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            promotionList.clear()

            if (snapshot != null) {
                Log.d(TAG, "Snapshot size: ${snapshot.size()}")
                for (doc in snapshot.documents) {
                    val title = doc.getString("title") ?: ""
                    val days = doc.getString("days") ?: ""
                    val location = doc.getString("location") ?: ""
                    val image = doc.getString("image") ?: ""
                    val content = doc.getString("content") ?: "" // ✅ THÊM MỚI

                    val promotion = Promotion(title, days, image, location, doc.id, content) // ✅ THÊM THAM SỐ
                    promotionList.add(promotion)
                    Log.d(TAG, "Loaded: $title")
                }
            }

            promotionList.reverse()
            promotionAdapter.updateList(promotionList)
            Log.d(TAG, "Adapter updated with ${promotionList.size} items")
        }
    }

    private fun editPromotion(promotion: Promotion) {
        isEditMode = true
        editingPromotionId = promotion.id

        edtPromotionTitle.setText(promotion.title)
        edtPromotionDate.setText(promotion.date)
        edtPromotionLocation.setText(promotion.location)
        edtPromotionImage.setText(promotion.imageUrl)
        edtPromotionContent.setText(promotion.content) // ✅ THÊM MỚI

        btnUpdate.text = "✏️ CẬP NHẬT"
        btnUpdate.setBackgroundColor(Color.parseColor("#1976D2"))

        Toast.makeText(this, "Đang chỉnh sửa: ${promotion.title}", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Edit mode enabled for: ${promotion.id}")
    }

    private fun updatePromotion() {
        val title = edtPromotionTitle.text.toString().trim()
        val date = edtPromotionDate.text.toString().trim()
        val location = edtPromotionLocation.text.toString().trim()
        val image = edtPromotionImage.text.toString().trim()
        val content = edtPromotionContent.text.toString().trim() // ✅ THÊM MỚI

        if (title.isEmpty() || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        if (editingPromotionId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID promotion!", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = hashMapOf(
            "title" to title,
            "days" to date,
            "location" to location,
            "image" to image,
            "content" to content // ✅ THÊM MỚI
        )

        Log.d(TAG, "Updating document: $editingPromotionId")

        database.document(editingPromotionId!!)
            .update(updatedData as Map<String, Any>)
            .addOnSuccessListener {
                Log.d(TAG, "Document updated successfully")
                Toast.makeText(this, "Cập nhật khuyến mãi thành công!", Toast.LENGTH_SHORT).show()
                clearInputFields()
                resetEditMode()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating document", e)
                Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun deletePromotion(promotion: Promotion) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa khuyến mãi '${promotion.title}'?")
            .setPositiveButton("Xóa") { _, _ ->
                if (promotion.id.isNotEmpty()) {
                    Log.d(TAG, "Deleting document: ${promotion.id}")
                    database.document(promotion.id).delete()
                        .addOnSuccessListener {
                            Log.d(TAG, "Document deleted successfully")
                            Toast.makeText(this, "Đã xóa khuyến mãi!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error deleting document", e)
                            Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(this, "Lỗi: Không tìm thấy ID!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun clearInputFields() {
        edtPromotionTitle.setText("")
        edtPromotionDate.setText("")
        edtPromotionLocation.setText("")
        edtPromotionImage.setText("")
        edtPromotionContent.setText("") // ✅ THÊM MỚI
    }

    private fun resetEditMode() {
        isEditMode = false
        editingPromotionId = null
        btnUpdate.text = "➕ THÊM"
        btnUpdate.setBackgroundColor(Color.parseColor("#E53935"))
        Log.d(TAG, "Edit mode disabled")
    }
}