package com.example.doan

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore

object giatri {
    var vau: String = ""
    var thoigian: String = ""
}

class TicketActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        giatri.vau = intent.getStringExtra("userEmail").toString()
        giatri.thoigian = "1"

        // Tạo root layout
        val rootLayout = LinearLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0D0D0D"))
        }

        // Header
        val headerLayout = createHeader()
        rootLayout.addView(headerLayout)

        // TabLayout
        val tabLayout = createTabLayout()
        rootLayout.addView(tabLayout)

        // ViewPager2
        val viewPager = ViewPager2(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            adapter = TicketPagerAdapter(this@TicketActivity)
        }
        rootLayout.addView(viewPager)

        // Tab selection listener
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        giatri.thoigian = "1"
                        viewPager.adapter = TicketPagerAdapter(this@TicketActivity)
                    }
                    1 -> {
                        giatri.thoigian = "0"
                        viewPager.adapter = TicketPagerAdapter(this@TicketActivity)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        setContentView(rootLayout)
    }

    private fun createHeader(): LinearLayout {
        return LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#B00710"),
                    Color.parseColor("#E50914"),
                    Color.parseColor("#FF1F2A")
                )
            )
            setPadding(dpToPx(16), dpToPx(20), dpToPx(16), dpToPx(20))
            gravity = Gravity.CENTER_VERTICAL
            elevation = dpToPx(8).toFloat()

            // Back button
            val btnBack = ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(dpToPx(32), dpToPx(32))
                setColorFilter(Color.WHITE)
                setImageResource(android.R.drawable.ic_menu_revert)
                setOnClickListener { finish() }
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
            }
            addView(btnBack)

            // Title
            val title = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginStart = dpToPx(16)
                }
                text = "VÉ CỦA TÔI"
                setTextColor(Color.WHITE)
                textSize = 22f
                setTypeface(null, Typeface.BOLD)
                letterSpacing = 0.1f
            }
            addView(title)
        }
    }

    private fun createTabLayout(): TabLayout {
        return TabLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(Color.parseColor("#1A1A1A"))
            setTabTextColors(Color.parseColor("#999999"), Color.parseColor("#E50914"))
            setSelectedTabIndicatorColor(Color.parseColor("#E50914"))
            elevation = dpToPx(4).toFloat()
            tabTextColors = android.content.res.ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_selected),
                    intArrayOf(android.R.attr.state_selected)
                ),
                intArrayOf(
                    Color.parseColor("#999999"),
                    Color.parseColor("#FFFFFF")
                )
            )

            // Set indicator height
            try {
                val field = TabLayout::class.java.getDeclaredField("tabIndicatorHeight")
                field.isAccessible = true
                field.set(this, dpToPx(4))
            } catch (e: Exception) {
                // Ignore if can't set
            }

            addTab(newTab().setText("VÉ ĐANG ĐẶT"))
            addTab(newTab().setText("VÉ ĐÃ ĐẶT"))

            // Style tabs
            for (i in 0 until tabCount) {
                getTabAt(i)?.let { tab ->
                    val textView = TextView(context).apply {
                        text = tab.text
                        textSize = 15f
                        setTypeface(null, Typeface.BOLD)
                        gravity = Gravity.CENTER
                        setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
                    }
                    tab.customView = textView
                }
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}

class TicketPagerAdapter(activity: AppCompatActivity)
    : androidx.viewpager2.adapter.FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 1
    override fun createFragment(position: Int) = TicketListFragment.newInstance(false)
}

class TicketListFragment : androidx.fragment.app.Fragment() {

    private val ticketList = mutableListOf<Ticket>()
    private lateinit var ticketAdapter: TicketAdapter
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        fun newInstance(isHistory: Boolean) = TicketListFragment().apply {
            arguments = Bundle().apply { putBoolean("isHistory", isHistory) }
        }
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Tạo RelativeLayout làm root (thay thế cho ticket_list.xml)
        val rootLayout = android.widget.RelativeLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#0D0D0D"))
        }

        // Tạo RecyclerView
        val recyclerView = RecyclerView(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            layoutManager = LinearLayoutManager(context)
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
            setBackgroundColor(Color.parseColor("#0D0D0D"))
        }

        rootLayout.addView(recyclerView)

        ticketAdapter = TicketAdapter(ticketList)
        recyclerView.adapter = ticketAdapter

        loadTicketsFromFirestore()

        return rootLayout
    }

    private fun loadTicketsFromFirestore() {
        ticketList.clear()

        firestore.collection("Ticker")
            .whereEqualTo("email", giatri.vau)
            .get()
            .addOnSuccessListener { docs ->
                if (!docs.isEmpty) {
                    for (doc in docs) {
                        val movie = doc.getString("phim") ?: ""
                        val date = doc.getString("ngaydat") ?: ""
                        val seat = doc.getString("ghe") ?: ""
                        val lich = doc.getString("lich") ?: ""
                        val rap = doc.getString("rap") ?: ""
                        val status = doc.getString("tt") ?: ""

                        if (giatri.thoigian == "0" && status == "Đã xác nhận") {
                            ticketList.add(Ticket(movieName = movie, showTime = date, lich = lich, ghe = seat, rap = rap))
                        }
                        if (giatri.thoigian == "1" && status == "Chờ xác nhận") {
                            ticketList.add(Ticket(movieName = movie, showTime = date, lich = lich, ghe = seat, rap = rap))
                        }
                    }
                }
                ticketAdapter.notifyDataSetChanged()
            }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}

class TicketAdapter(private val tickets: List<Ticket>)
    : RecyclerView.Adapter<TicketViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        return TicketViewHolder(parent.context)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(tickets[position])
    }

    override fun getItemCount(): Int = tickets.size
}

class TicketViewHolder(context: android.content.Context) : RecyclerView.ViewHolder(
    createTicketItemView(context)
) {
    private val ticketTitle: TextView = itemView.findViewWithTag("title")
    private val ticketDate: TextView = itemView.findViewWithTag("date")
    private val ticketlich: TextView = itemView.findViewWithTag("lich")
    private val ticketghe: TextView = itemView.findViewWithTag("ghe")
    private val ticketrap: TextView = itemView.findViewWithTag("rap")

    fun bind(ticket: Ticket) {
        ticketTitle.text = ticket.movieName
        ticketDate.text = "📅 ${ticket.showTime}"
        ticketlich.text = "🕐 ${ticket.lich}"
        ticketghe.text = "💺 Ghế: ${ticket.ghe}"
        ticketrap.text = "🎬 ${ticket.rap}"
    }

    companion object {
        fun createTicketItemView(context: android.content.Context): View {
            val dp = context.resources.displayMetrics.density

            return CardView(context).apply {
                layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = (16 * dp).toInt()
                }
                radius = 16 * dp
                cardElevation = 8 * dp
                setCardBackgroundColor(Color.parseColor("#1A1A1A"))

                val container = LinearLayout(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    orientation = LinearLayout.VERTICAL

                    // Header với gradient đỏ
                    val header = LinearLayout(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        orientation = LinearLayout.HORIZONTAL
                        background = GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            intArrayOf(
                                Color.parseColor("#B00710"),
                                Color.parseColor("#E50914")
                            )
                        ).apply {
                            cornerRadii = floatArrayOf(
                                16 * dp, 16 * dp,  // top left
                                16 * dp, 16 * dp,  // top right
                                0f, 0f,            // bottom right
                                0f, 0f             // bottom left
                            )
                        }
                        setPadding((16 * dp).toInt(), (14 * dp).toInt(), (16 * dp).toInt(), (14 * dp).toInt())

                        addView(TextView(context).apply {
                            tag = "title"
                            layoutParams = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                1f
                            )
                            setTextColor(Color.WHITE)
                            textSize = 18f
                            setTypeface(null, Typeface.BOLD)
                            letterSpacing = 0.05f
                        })
                    }
                    addView(header)

                    // Content với nền tối
                    val contentLayout = LinearLayout(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        orientation = LinearLayout.VERTICAL
                        setPadding((16 * dp).toInt(), (16 * dp).toInt(), (16 * dp).toInt(), (16 * dp).toInt())
                        setBackgroundColor(Color.parseColor("#1A1A1A"))

                        addView(createInfoTextView(context, "date"))
                        addView(createInfoTextView(context, "lich"))
                        addView(createInfoTextView(context, "ghe"))
                        addView(createInfoTextView(context, "rap"))
                    }
                    addView(contentLayout)
                }
                addView(container)
            }
        }

        private fun createInfoTextView(context: android.content.Context, tag: String): TextView {
            val dp = context.resources.displayMetrics.density
            return TextView(context).apply {
                this.tag = tag
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = (10 * dp).toInt()
                }
                setTextColor(Color.parseColor("#E8E8E8"))
                textSize = 15f
                setTypeface(null, Typeface.NORMAL)
            }
        }
    }
}

data class Ticket(
    val movieName: String = "",
    val showTime: String = "",
    val lich: String = "",
    val ghe: String = "",
    val rap: String = ""
)