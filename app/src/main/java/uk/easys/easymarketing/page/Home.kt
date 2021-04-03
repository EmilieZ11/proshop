package uk.easys.easymarketing.page

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.easys.easymarketing.Fun
import uk.easys.easymarketing.Fun.Companion.apiInterface
import uk.easys.easymarketing.Fun.Companion.c
import uk.easys.easymarketing.Fun.Companion.dp
import uk.easys.easymarketing.Fun.Companion.drawable
import uk.easys.easymarketing.Fun.Companion.fRegular
import uk.easys.easymarketing.Fun.Companion.mp
import uk.easys.easymarketing.Fun.Companion.pages
import uk.easys.easymarketing.Fun.Companion.vis
import uk.easys.easymarketing.Fun.Companion.wc
import uk.easys.easymarketing.Main
import uk.easys.easymarketing.Model
import uk.easys.easymarketing.R
import uk.easys.easymarketing.adap.HomeCateAdap
import uk.easys.easymarketing.adap.HomeSlider
import uk.easys.easymarketing.adap.HomeProdAdap
import uk.easys.easymarketing.data.API
import uk.easys.easymarketing.data.Page

class Home : Fragment() {
    var home: ConstraintLayout? = null
    lateinit var bSV: ScrollView
    lateinit var sliderBG: View
    lateinit var prodShine: View
    lateinit var slider: ViewPager2
    lateinit var bLL: LinearLayout
    lateinit var indicators: ConstraintLayout
    lateinit var indicator: View

    var firstResume = true
    val model: Model by activityViewModels()

    companion object {
        lateinit var catTitles: List<Int>

        @Suppress("unused")
        @JvmStatic
        fun newInstance() = Home()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        home = inflater.inflate(R.layout.frag_home, container, false) as ConstraintLayout?
        if (home == null) return home
        bSV = home!!.findViewById(R.id.SV)
        sliderBG = home!!.findViewById(R.id.sliderBG)
        prodShine = home!!.findViewById(R.id.prodShine)
        slider = home!!.findViewById(R.id.slider)
        bLL = home!!.findViewById(R.id.LL)
        indicators = home!!.findViewById(R.id.indicators)
        indicator = home!!.findViewById(R.id.indicator)

        loadPage()
        firstResume = true
        catTitles = Fun.catTitle()


        // Loading
        Fun.onLoad(home!!, object : Fun.Do {
            override fun run() {
                // Slider
                val heightFactor = (0.83f * sliderFactor)
                (sliderBG.layoutParams as ConstraintLayout.LayoutParams).height =
                    ((Fun.dm.widthPixels * heightFactor) + resources.getDimension(R.dimen.hSVLayerTop)).toInt()
                Fun.relHeight(slider, heightFactor)
                Fun.relHeight(prodShine, heightFactor)
                prodShine.background = Fun.prodShine(Fun.dm.widthPixels * heightFactor)
                slider.pivotX = Fun.dm.widthPixels / 2f
                slider.pivotY = Fun.dm.widthPixels * heightFactor
                (bLL.layoutParams as ConstraintLayout.LayoutParams).apply {
                    topMargin = (Fun.dm.widthPixels * heightFactor).toInt()
                }
                prodShine.pivotX = Fun.dm.widthPixels / 2f
                prodShine.pivotY = Fun.dm.widthPixels * heightFactor
                fixSlider()
            }
        })

        // Scroll View
        bSV.viewTreeObserver.addOnScrollChangedListener {
            var factor =
                (1f / slider.height.toFloat()) * (-(bSV.scrollY.toFloat()) + slider.height.toFloat())
            if (factor < 0f) factor = 0f
            if (factor > 1f) factor = 1f
            fixSlider(factor)
            vis(slider, factor > 0f)
            vis(prodShine, factor > 0f)
        }
        bLL.setOnClickListener {//DOESN'T WORK ON SCROLLER CHILDREN....
            Main.fragHandler?.obtainMessage(0)?.sendToTarget()
        }

        // With Main
        model.tbMode.value = Main.ToolbarMode.CONCAVE

        return home
    }

    override fun onResume() {
        super.onResume()
        model.currentPage.value = pages.indexOf(Fun.NavButton.findByName("home"))
        if (!firstResume) fixSlider()
        firstResume = false
    }


    fun loadPage() {
        if (apiInterface == null) return
        model.loading.value = true
        apiInterface?.home()?.enqueue(object : Callback<Page?> {
            override fun onResponse(call: Call<Page?>?, response: Response<Page?>) {
                model.loading.value = false
                response.body()?.let { page ->
                    loadSlider(page.slider.list)
                    indicateSlider(0, 0f)
                    for (s in page.content.list) arrangeList(s)
                }
            }

            override fun onFailure(call: Call<Page?>, t: Throwable?) {
                model.loading.value = false
                Toast.makeText(c, "${t?.message}", Toast.LENGTH_LONG).show()
                call.cancel()
            }
        })
    }

    var indiIds = arrayListOf<Int>()
    fun loadSlider(sliderData: List<API.Product>) {
        slider.adapter = HomeSlider(requireActivity(), sliderData)
        slider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(pos: Int, posOffset: Float, posOffsetPixels: Int) {
                super.onPageScrolled(pos, posOffset, posOffsetPixels)
                indicateSlider(pos, posOffset)
            }
        })
        indiIds.clear()
        for (i in sliderData) indiIds.add(View.generateViewId())
        for (i in sliderData.indices) indicators.addView(View(c).apply {
            val size = resources.getDimension(R.dimen.indicatorSize).toInt()
            id = indiIds[i]
            layoutParams = ConstraintLayout.LayoutParams(size, size).apply {
                val par = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = par
                if (i == 0) startToStart = par
                else startToEnd = indiIds[i - 1]
                val mar = resources.getDimension(R.dimen.indicatorMargin).toInt()
                setMargins(mar, 0, mar, 0)
            }
            background = drawable(R.drawable.indicator_1)
        }, indicators.childCount - 1)
        indicators.layoutParams.apply {
            val x =
                resources.getDimension(R.dimen.indicatorSize) + (resources.getDimension(R.dimen.indicatorMargin) * 2)
            width = (x * sliderData.size).toInt()
        }
    }

    var sliderFactor = 1f
    fun fixSlider(newFactor: Float? = null) {
        if (newFactor != null) {
            if (newFactor.isNaN()) return
            sliderFactor = newFactor
            model.cartFactor.value = newFactor
        }
        indicators.alpha = sliderFactor
        slider.scaleX = sliderFactor
        slider.scaleY = sliderFactor
        if (!model.loading.value!!) slider.alpha = sliderFactor
        prodShine.scaleX = sliderFactor
        prodShine.scaleY = sliderFactor
        if (!model.loading.value!!) prodShine.alpha = sliderFactor
        Main.fragHandler?.obtainMessage(1)?.sendToTarget()
    }

    fun indicateSlider(pos: Int, offset: Float) {
        var ll = indicator.layoutParams as ConstraintLayout.LayoutParams// Don't use "apply{}"
        ll.apply {
            startToStart = indiIds[pos]
            endToEnd =
                indiIds[pos + (if (offset == 0f && pos != (indicators.childCount - 1)) 0 else 1)]
            val wid = resources.getDimension(R.dimen.indicatorSize)
            val mar = resources.getDimension(R.dimen.indicatorMargin)
            val x = (wid * 2) + mar
            if (offset <= 0.5f) {
                horizontalBias = 0f
                width = (wid + ((x - wid) * (offset * 2f))).toInt()
            } else {
                horizontalBias = 1f
                val distPercent = -((offset - 0.5f) * 2f) + 1f
                width = (wid + ((x - wid) * distPercent)).toInt()
            }
        }
        indicator.layoutParams = ll
    }

    fun arrangeList(s: Page.Section) {
        bLL.addView(ConstraintLayout(c).apply {
            val thisCL = this
            layoutParams = LinearLayout.LayoutParams(mp, wc)
                .apply { setMargins(0, dp(20), 0, 0) }
            if (s.type != "A") {
                val titleId = View.generateViewId()
                addView(LinearLayout(c).apply {
                    layoutParams = ConstraintLayout.LayoutParams(wc, wc).apply {
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        marginStart = dp(25)
                    }
                    id = titleId
                    orientation = LinearLayout.HORIZONTAL

                    addView(ImageView(c).apply {
                        layoutParams = LinearLayout.LayoutParams(dp(28), dp(28))
                        Glide.with(c).load(s.icon).into(this)
                    })
                    addView(TextView(ContextThemeWrapper(c, R.style.conTitle), null, 0).apply {
                        layoutParams = LinearLayout.LayoutParams(wc, wc)
                            .apply { marginStart = dp(15) }
                        setTypeface(fRegular, Typeface.BOLD)
                        text = s.title
                    })
                })
                addView(TextView(ContextThemeWrapper(c, R.style.conViewAll), null, 0).apply {
                    layoutParams = ConstraintLayout.LayoutParams(wc, wc).apply {
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        marginEnd = dp(22)
                    }
                    typeface = fRegular
                    setOnClickListener {
                        if (s.type == "C") this@Home.findNavController().navigate(R.id.toCate)
                    }
                })
                addView(RecyclerView(ContextThemeWrapper(c, R.style.section), null, 0).apply {
                    layoutParams = ConstraintLayout.LayoutParams(mp, wc).apply {
                        topToBottom = titleId
                        setMargins(0, dp(20), 0, 0)
                    }
                    setPaddingRelative(dp(7), 0, dp(7), 0)
                    clipToPadding = false
                    when (s.type) {
                        "C" -> adapter = HomeCateAdap(s.categories)
                        "P" -> adapter = HomeProdAdap(s.products)
                        else -> vis(thisCL, false)
                    }
                })
            } else addView(LinearLayout(c).apply {
                layoutParams = ConstraintLayout.LayoutParams(mp, wc)
                orientation = LinearLayout.HORIZONTAL
                weightSum = s.ads.size.toFloat()
                for (a in s.ads) addView(ImageView(c).apply {
                    layoutParams = LinearLayout.LayoutParams(0, wc, 1f)
                    adjustViewBounds = true
                    Glide.with(c).load(a.src).into(this)
                    setOnClickListener { }
                })
            })
        })
    }
}
