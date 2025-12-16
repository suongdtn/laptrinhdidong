package com.example.doan

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

data class Item(val id: String, val name: String)

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var database: FirebaseFirestore
    private val itemList = mutableListOf<Item>()
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = createRootLayout()
        setContentView(rootLayout)

        database = FirebaseFirestore.getInstance()
        fetchDataFromFirestore()
    }

    private fun createRootLayout(): View {
        val scrollView = ScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#0A0A0A"))
        }

        val mainLayout = LinearLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
        }

        mainLayout.addView(createHeader())
        mainLayout.addView(createContentSection())

        scrollView.addView(mainLayout)
        return scrollView
    }

    private fun createHeader(): View {
        val headerLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(80)
            )
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                colors = intArrayOf(
                    Color.parseColor("#ED1C24"),
                    Color.parseColor("#C62828")
                )
                orientation = GradientDrawable.Orientation.TOP_BOTTOM
            }
            elevation = dpToPx(6).toFloat()
        }

        val titleText = TextView(this).apply {
            text = "DANH SÁCH CHỜ XÁC NHẬN"
            textSize = 24f
            setTextColor(Color.WHITE)
            setTypeface(typeface, Typeface.BOLD)
            letterSpacing = 0.08f
            gravity = Gravity.CENTER
        }

        val subtitleText = TextView(this).apply {
            text = "LOTTE CINEMA ADMIN"
            textSize = 11f
            setTextColor(Color.parseColor("#FFD700"))
            setTypeface(typeface, Typeface.BOLD)
            letterSpacing = 0.15f
            setPadding(0, dpToPx(4), 0, 0)
            gravity = Gravity.CENTER
        }

        headerLayout.addView(titleText)
        headerLayout.addView(subtitleText)

        return headerLayout
    }

    private fun createContentSection(): View {
        val contentLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
        }

        // Section header
        val sectionHeader = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val headerAccent = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(4),
                dpToPx(30)
            ).apply {
                marginEnd = dpToPx(12)
            }
            setBackgroundColor(Color.parseColor("#FFD700"))
        }

        val headerTitle = TextView(this).apply {
            text = "Vé Chờ Xác Nhận"
            textSize = 20f
            setTextColor(Color.WHITE)
            setTypeface(typeface, Typeface.BOLD)
        }

        sectionHeader.addView(headerAccent)
        sectionHeader.addView(headerTitle)
        contentLayout.addView(sectionHeader)

        // Progress Bar
        progressBar = ProgressBar(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = dpToPx(32)
                bottomMargin = dpToPx(32)
            }
            indeterminateDrawable.setColorFilter(
                Color.parseColor("#ED1C24"),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }

        val progressContainer = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER
            addView(progressBar)
        }
        contentLayout.addView(progressContainer)

        // Empty message
        emptyText = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(32)
                bottomMargin = dpToPx(32)
            }
            text = "Không có vé nào cần xác nhận"
            textSize = 16f
            setTextColor(Color.parseColor("#666666"))
            gravity = Gravity.CENTER
            visibility = View.GONE
        }
        contentLayout.addView(emptyText)

        // RecyclerView
        recyclerView = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutManager = LinearLayoutManager(this@ConfirmationActivity)
            isNestedScrollingEnabled = false
        }

        adapter = ItemAdapter(itemList) { item ->
            confirmItem(item)
        }
        recyclerView.adapter = adapter

        contentLayout.addView(recyclerView)

        return contentLayout
    }

    private fun fetchDataFromFirestore() {
        progressBar.visibility = View.VISIBLE
        emptyText.visibility = View.GONE

        database.collection("Ticker")
            .get()
            .addOnSuccessListener { documents ->
                itemList.clear()

                for (doc in documents) {
                    val id = doc.id
                    val phim = doc.getString("phim") ?: "Không có"
                    val ngaydat = doc.getString("ngaydat") ?: "Không có"
                    val ghe = doc.getString("ghe") ?: "Không có"
                    val lich = doc.getString("lich") ?: "Không có"
                    val rap = doc.getString("rap") ?: "Không có"
                    val tt = doc.getString("tt") ?: "Không có"

                    if (tt == "Chờ xác nhận") {
                        itemList.add(Item(id, "$phim\n$ngaydat\n$ghe\n$lich\n$rap"))
                    }
                }

                progressBar.visibility = View.GONE

                if (itemList.isEmpty()) {
                    emptyText.visibility = View.VISIBLE
                } else {
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Lỗi tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun confirmItem(item: Item) {
        database.collection("Ticker")
            .document(item.id)
            .update("tt", "Đã xác nhận")
            .addOnSuccessListener {
                Toast.makeText(this, "Đã xác nhận vé!", Toast.LENGTH_SHORT).show()
                itemList.remove(item)
                adapter.notifyDataSetChanged()

                if (itemList.isEmpty()) {
                    emptyText.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi xác nhận: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    class ItemAdapter(
        private val itemList: MutableList<Item>,
        private val onConfirmClick: (Item) -> Unit
    ) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

        class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cardView: CardView = itemView as CardView
            val textItemName: TextView
            val btnXacNhan: Button

            init {
                val layout = cardView.getChildAt(0) as LinearLayout
                textItemName = layout.getChildAt(0) as TextView
                btnXacNhan = layout.getChildAt(2) as Button
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val context = parent.context

            val cardView = CardView(context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = (12 * context.resources.displayMetrics.density).toInt()
                }
                radius = (12 * context.resources.displayMetrics.density)
                cardElevation = (4 * context.resources.displayMetrics.density)
                setCardBackgroundColor(Color.parseColor("#1A1A1A"))
            }

            val rowLayout = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(
                    (16 * context.resources.displayMetrics.density).toInt(),
                    (16 * context.resources.displayMetrics.density).toInt(),
                    (16 * context.resources.displayMetrics.density).toInt(),
                    (16 * context.resources.displayMetrics.density).toInt()
                )
            }

            val textView = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                textSize = 15f
                setTextColor(Color.WHITE)
                lineHeight = (22 * context.resources.displayMetrics.density).toInt()
            }

            val spacer = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (12 * context.resources.displayMetrics.density).toInt(),
                    1
                )
            }

            val button = Button(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (48 * context.resources.displayMetrics.density).toInt()
                )
                text = "Xác nhận"
                textSize = 14f
                setTextColor(Color.WHITE)
                isAllCaps = false
                setTypeface(typeface, Typeface.BOLD)

                background = GradientDrawable().apply {
                    colors = intArrayOf(
                        Color.parseColor("#ED1C24"),
                        Color.parseColor("#FF6B6B")
                    )
                    orientation = GradientDrawable.Orientation.LEFT_RIGHT
                    cornerRadius = (24 * context.resources.displayMetrics.density)
                }

                setPadding(
                    (24 * context.resources.displayMetrics.density).toInt(),
                    0,
                    (24 * context.resources.displayMetrics.density).toInt(),
                    0
                )
            }

            rowLayout.addView(textView)
            rowLayout.addView(spacer)
            rowLayout.addView(button)
            cardView.addView(rowLayout)

            return ItemViewHolder(cardView)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item = itemList[position]
            holder.textItemName.text = item.name
            holder.btnXacNhan.setOnClickListener { onConfirmClick(item) }
        }

        override fun getItemCount(): Int = itemList.size
    }
}