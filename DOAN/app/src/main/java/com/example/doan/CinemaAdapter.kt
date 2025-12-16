package com.example.doan

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

data class Cinema(
    var location: String = "",
    var theater: String = "",
    var schedule: MutableMap<String, MutableList<Int>> = mutableMapOf(
        "Chủ Nhật" to mutableListOf(),
        "Thứ Hai" to mutableListOf(),
        "Thứ Ba" to mutableListOf(),
        "Thứ Tư" to mutableListOf(),
        "Thứ Năm" to mutableListOf(),
        "Thứ Sáu" to mutableListOf(),
        "Thứ Bảy" to mutableListOf()
    )
)

class CinemaAdapter : RecyclerView.Adapter<CinemaAdapter.CinemaViewHolder>() {

    private val cinemaList = mutableListOf<Cinema>()

    inner class CinemaViewHolder(val containerLayout: LinearLayout) : RecyclerView.ViewHolder(containerLayout) {
        lateinit var provinceSpinner: Spinner
        lateinit var cinemaSpinner: Spinner
        val checkBoxes = mutableMapOf<String, CheckBox>()
        val slotCheckBoxesMap = mutableMapOf<String, List<CheckBox>>()

        fun bind(cinema: Cinema) {
            val provinces = arrayOf("Hà Nội", "TP. Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ")

            // ===== CUSTOM ADAPTER: WHITE BACKGROUND + BLACK TEXT =====
            val provinceAdapter = object : ArrayAdapter<String>(
                itemView.context,
                android.R.layout.simple_spinner_item,
                provinces
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val tv = super.getView(position, convertView, parent) as TextView
                    tv.setBackgroundColor(Color.WHITE)
                    tv.setTextColor(Color.BLACK)
                    tv.textSize = 16f
                    return tv
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val tv = super.getDropDownView(position, convertView, parent) as TextView
                    tv.setBackgroundColor(Color.WHITE)
                    tv.setTextColor(Color.BLACK)
                    tv.textSize = 16f
                    tv.setPadding(20, 20, 20, 20)
                    return tv
                }
            }

            provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            provinceSpinner.adapter = provinceAdapter

            // Set saved selection
            val savedIndex = provinces.indexOf(cinema.location)
            if (savedIndex >= 0) provinceSpinner.setSelection(savedIndex)

            // ===== WHEN SELECT PROVINCE =====
            provinceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    cinema.location = provinces[position]

                    val cinemas = when (cinema.location) {
                        "Hà Nội" -> arrayOf("CGV Vincom", "Lotte Landmark", "BHD Star")
                        "TP. Hồ Chí Minh" -> arrayOf(
                            "CGV Sư Vạn Hạnh",
                            "Mega GS",
                            "BHD Phạm Ngọc Thạch"
                        )

                        "Đà Nẵng" -> arrayOf("CGV Đà Nẵng", "Lotte Đà Nẵng")
                        else -> arrayOf("Chưa có rạp")
                    }

                    // ===== CUSTOM ADAPTER FOR CINEMA SPINNER =====
                    val cinemaAdapter = object : ArrayAdapter<String>(
                        itemView.context,
                        android.R.layout.simple_spinner_item,
                        cinemas
                    ) {
                        override fun getView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val tv = super.getView(position, convertView, parent) as TextView
                            tv.setBackgroundColor(Color.WHITE)
                            tv.setTextColor(Color.BLACK)
                            tv.textSize = 16f
                            return tv
                        }

                        override fun getDropDownView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val tv =
                                super.getDropDownView(position, convertView, parent) as TextView
                            tv.setBackgroundColor(Color.WHITE)
                            tv.setTextColor(Color.BLACK)
                            tv.textSize = 16f
                            tv.setPadding(20, 20, 20, 20)
                            return tv
                        }
                    }

                    cinemaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    cinemaSpinner.adapter = cinemaAdapter

                    cinema.theater = cinemas.firstOrNull() ?: ""
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            // ===== REST OF YOUR CHECKBOX LOGIC GIỮ NGUYÊN =====
            checkBoxes.forEach { (day, dayCheckBox) ->
                dayCheckBox.isChecked = cinema.schedule[day]?.isNotEmpty() ?: false
                dayCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    if (!isChecked) {
                        cinema.schedule[day]?.clear()
                        slotCheckBoxesMap[day]?.forEach {
                            it.isEnabled = false
                            it.isChecked = false
                        }
                    } else {
                        slotCheckBoxesMap[day]?.forEach { it.isEnabled = true }
                    }
                }

                slotCheckBoxesMap[day]?.forEachIndexed { index, slotCheckBox ->
                    slotCheckBox.isEnabled = dayCheckBox.isChecked
                    slotCheckBox.isChecked = cinema.schedule[day]?.contains(index + 1) ?: false
                    slotCheckBox.setOnCheckedChangeListener { _, isChecked ->
                        val selectedSlots = cinema.schedule[day] ?: mutableListOf()
                        if (isChecked) {
                            if (!selectedSlots.contains(index + 1)) selectedSlots.add(index + 1)
                        } else {
                            selectedSlots.remove(index + 1)
                        }
                        cinema.schedule[day] = selectedSlots.sorted().toMutableList()
                    }
                }
            }
        }
    }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CinemaViewHolder {
        val itemView = createItemView(parent)
        return CinemaViewHolder(itemView)
    }

    override fun getItemCount(): Int = cinemaList.size

    fun addCinema() {
        cinemaList.add(Cinema())
        notifyItemInserted(cinemaList.size - 1)
    }

    fun getCinemas(): List<Cinema> = cinemaList

    private fun createItemView(parent: ViewGroup): LinearLayout {
        val context = parent.context

        val mainLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = createRoundedBackground("#1A1A1A", dp(12))
            setPadding(dp(20), dp(20), dp(20), dp(20))
            elevation = dp(4).toFloat()
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(16)
                leftMargin = dp(16)
                rightMargin = dp(16)
            }

            // Section header
            addView(TextView(context).apply {
                text = "🎬 THÔNG TIN RẠP CHIẾU"
                setTextColor(Color.parseColor("#E53935"))
                textSize = 14f
                setTypeface(null, Typeface.BOLD)
                letterSpacing = 0.1f
                setPadding(0, 0, 0, dp(16))
            })

            // Province Spinner Section
            val provinceSection = createSpinnerSection(context, "📍 Chọn Tỉnh/Thành Phố")
            addView(provinceSection.first)

            // Cinema Spinner Section
            val cinemaSection = createSpinnerSection(context, "🏢 Chọn Rạp Chiếu")
            addView(cinemaSection.first)

            // Divider
            addView(View(context).apply {
                setBackgroundColor(Color.parseColor("#ffffff"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(1)
                ).apply {
                    topMargin = dp(20)
                    bottomMargin = dp(16)
                }
            })

            // Schedule section
            addView(TextView(context).apply {
                text = "📅 Chọn Lịch Chiếu:"
                setTextColor(Color.WHITE)
                textSize = 15f
                setTypeface(null, Typeface.BOLD)
                setPadding(0, 0, 0, dp(12))
            })

            // Days layout
            val daysLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
            }

            val days = listOf("Chủ Nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy")
            days.forEach { day ->
                val dayRow = createDayRow(context, day)
                daysLayout.addView(dayRow.first)
            }

            addView(daysLayout)

            // Store references in the tag
            tag = ViewReferences(
                provinceSpinner = provinceSection.second,
                cinemaSpinner = cinemaSection.second,
                daysLayout = daysLayout
            )
        }

        return mainLayout
    }

    private fun createSpinnerSection(context: android.content.Context, label: String): Pair<LinearLayout, Spinner> {
        val spinner = Spinner(context).apply {
            background = createRoundedBackground("#2A2A2A", dp(8))
            setPadding(dp(16), dp(16), dp(16), dp(16))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(56)
            )
        }

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(16)
            }

            addView(TextView(context).apply {
                text = label
                setTextColor(Color.parseColor("#ffffff"))
                textSize = 14f
                setTypeface(null, Typeface.BOLD)
                setPadding(0, 0, 0, dp(8))
            })

            addView(spinner)
        }

        return Pair(container, spinner)
    }

    private fun createDayRow(context: android.content.Context, day: String): Pair<LinearLayout, DayRowViews> {
        val dayCheckBox = CheckBox(context).apply {
            text = day
            setTextColor(Color.WHITE)
            textSize = 15f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                dp(130),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            // Đặt màu checkbox: trắng khi chưa chọn, đỏ khi đã chọn
            buttonTintList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(
                    Color.parseColor("#E53935"), // Màu đỏ khi checked
                    Color.WHITE // Màu trắng khi unchecked
                )
            )
        }

        val slotCheckBoxes = mutableListOf<CheckBox>()
        val slotContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        for (i in 1..5) {
            val slotCheckBox = CheckBox(context).apply {
                text = i.toString()
                setTextColor(Color.parseColor("#FFC107"))
                textSize = 14f
                setTypeface(null, Typeface.BOLD)
                setPadding(dp(4), dp(4), dp(4), dp(4))
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                isEnabled = false
                // Đặt màu checkbox: trắng khi chưa chọn, đỏ khi đã chọn
                buttonTintList = ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_checked)
                    ),
                    intArrayOf(
                        Color.parseColor("#E53935"), // Màu đỏ khi checked
                        Color.WHITE // Màu trắng khi unchecked
                    )
                )
            }
            slotCheckBoxes.add(slotCheckBox)
            slotContainer.addView(slotCheckBox)
        }

        val rowLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            background = createRoundedBackground("#0F0F0F", dp(10))
            setPadding(dp(12), dp(12), dp(12), dp(12))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(10)
            }

            addView(dayCheckBox)
            addView(slotContainer)
        }

        return Pair(rowLayout, DayRowViews(dayCheckBox, slotCheckBoxes))
    }

    override fun onBindViewHolder(holder: CinemaViewHolder, position: Int) {
        val viewRefs = holder.containerLayout.tag as? ViewReferences

        if (viewRefs != null) {
            // Setup spinner references
            holder.provinceSpinner = viewRefs.provinceSpinner
            holder.cinemaSpinner = viewRefs.cinemaSpinner

            // Setup day checkboxes and slot checkboxes
            val days = listOf("Chủ Nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy")
            days.forEachIndexed { index, day ->
                val dayRow = viewRefs.daysLayout.getChildAt(index) as LinearLayout
                val dayCheckBox = dayRow.getChildAt(0) as CheckBox
                val slotContainer = dayRow.getChildAt(1) as LinearLayout

                holder.checkBoxes[day] = dayCheckBox

                val slotCheckBoxes = mutableListOf<CheckBox>()
                for (i in 0 until 5) {
                    slotCheckBoxes.add(slotContainer.getChildAt(i) as CheckBox)
                }
                holder.slotCheckBoxesMap[day] = slotCheckBoxes
            }

            holder.bind(cinemaList[position])
        }
    }

    private fun createRoundedBackground(colorHex: String, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor(colorHex))
            cornerRadius = radius.toFloat()
        }
    }

    private fun dp(value: Int): Int {
        return (value * android.content.res.Resources.getSystem().displayMetrics.density).toInt()
    }

    data class ViewReferences(
        val provinceSpinner: Spinner,
        val cinemaSpinner: Spinner,
        val daysLayout: LinearLayout
    )

    data class DayRowViews(
        val dayCheckBox: CheckBox,
        val slotCheckBoxes: List<CheckBox>
    )
}