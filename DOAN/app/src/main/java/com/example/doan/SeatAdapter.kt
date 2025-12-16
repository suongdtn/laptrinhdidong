package com.example.doan

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class SeatAdapter(
    private val context: Context,
    private val seats: List<String>
) : BaseAdapter() {

    private val selectedSeats = mutableListOf<String>()
    private val disabledSeats = mutableListOf<String>()

    // Màu sắc theme đồng bộ
    private val COLOR_SEAT_EMPTY = Color.parseColor("#1A1A1A")           // Ghế trống - Xám tối
    private val COLOR_SEAT_SELECTED = Color.parseColor("#E50914")        // Ghế đang chọn - Đỏ Lotte
    private val COLOR_SEAT_DISABLED = Color.parseColor("#4A4A4A")        // Ghế đã đặt - Xám đậm
    private val COLOR_TEXT_EMPTY = Color.parseColor("#999999")           // Chữ ghế trống
    private val COLOR_TEXT_SELECTED = Color.parseColor("#FFFFFF")        // Chữ ghế đang chọn
    private val COLOR_TEXT_DISABLED = Color.parseColor("#666666")        // Chữ ghế đã đặt
    private val COLOR_BORDER_EMPTY = Color.parseColor("#2A2A2A")         // Border ghế trống
    private val COLOR_BORDER_SELECTED = Color.parseColor("#FF1F2A")      // Border ghế chọn
    private val COLOR_BORDER_DISABLED = Color.parseColor("#3A3A3A")      // Border ghế đã đặt

    override fun getCount(): Int = seats.size

    override fun getItem(position: Int): Any = seats[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val seat = seats[position]

        val seatView = TextView(context).apply {
            text = seat
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10))
            setTypeface(null, android.graphics.Typeface.BOLD)

            // Đặt background và màu chữ dựa trên trạng thái
            when {
                disabledSeats.contains(seat) -> {
                    // Ghế đã đặt - Xám đậm với border
                    background = createSeatBackground(
                        COLOR_SEAT_DISABLED,
                        COLOR_BORDER_DISABLED,
                        dpToPx(2)
                    )
                    setTextColor(COLOR_TEXT_DISABLED)
                    alpha = 0.6f
                }
                selectedSeats.contains(seat) -> {
                    // Ghế đang chọn - Đỏ Lotte với border sáng và elevation
                    background = createSeatBackground(
                        COLOR_SEAT_SELECTED,
                        COLOR_BORDER_SELECTED,
                        dpToPx(3)
                    )
                    setTextColor(COLOR_TEXT_SELECTED)
                    elevation = dpToPx(4).toFloat()
                    scaleX = 1.05f
                    scaleY = 1.05f
                }
                else -> {
                    // Ghế trống - Xám tối với border mỏng
                    background = createSeatBackground(
                        COLOR_SEAT_EMPTY,
                        COLOR_BORDER_EMPTY,
                        dpToPx(1)
                    )
                    setTextColor(COLOR_TEXT_EMPTY)
                    elevation = dpToPx(1).toFloat()
                }
            }

            layoutParams = ViewGroup.LayoutParams(
                dpToPx(56),
                dpToPx(56)
            )

            // Vô hiệu hóa ghế đã đặt - CHỈ set isEnabled, KHÔNG set isClickable
            isEnabled = !disabledSeats.contains(seat)
        }

        return seatView
    }

    // Tạo background cho ghế với border radius và stroke
    private fun createSeatBackground(bgColor: Int, borderColor: Int, borderWidth: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(bgColor)
            cornerRadius = dpToPx(12).toFloat()
            setStroke(borderWidth, borderColor)
        }
    }

    // Chuyển đổi trạng thái ghế khi click
    fun toggleSeatSelection(seat: String) {
        if (disabledSeats.contains(seat)) {
            return // Không cho phép chọn ghế đã đặt
        }

        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat)
        } else {
            selectedSeats.add(seat)
        }
        notifyDataSetChanged()
    }

    // Lấy danh sách ghế đã chọn
    fun getSelectedSeats(): List<String> {
        return selectedSeats.toList()
    }

    // Lấy danh sách ghế đã bị vô hiệu hóa
    fun getDisabledSeats(): List<String> {
        return disabledSeats.toList()
    }

    // Vô hiệu hóa các ghế đã đặt
    fun disableSeats(seats: List<String>) {
        disabledSeats.clear()
        disabledSeats.addAll(seats)
        notifyDataSetChanged()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}