package uk.easys.test

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderViewAdapter
import uk.easys.test.Fun.Companion.betterStar
import uk.easys.test.Fun.Companion.c
import uk.easys.test.Fun.Companion.fNormal
import uk.easys.test.Fun.Companion.vis
import uk.easys.test.databinding.SingleBinding

class Single : AppCompatActivity() {
    lateinit var b: SingleBinding
    var booked = false
    var searching = false
    var liked1 = false
    var liked2 = false
    var liked3 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = SingleBinding.inflate(layoutInflater)
        setContentView(b.root)
        Fun.init(this)


        // Toolbar
        b.tbNav.setOnClickListener { }
        b.tbSearch.setOnClickListener { search(!searching) }
        b.tbShare.setOnClickListener { }
        val tbIconCF =
            PorterDuffColorFilter(ContextCompat.getColor(c, R.color.CSV), PorterDuff.Mode.SRC_IN)
        b.tbSearchIV.colorFilter = tbIconCF
        b.tbShareIV.colorFilter = tbIconCF
        b.SV.viewTreeObserver.addOnScrollChangedListener { tbShadow(b.SV.scrollY > 0f) }
        b.tbSearchET.typeface = fNormal

        // Product
        b.gallery.setSliderAdapter(
            GalleryAdapter(
                listOf(
                    GalleryAdapter.Photo(R.drawable.sh_1),
                    GalleryAdapter.Photo(R.drawable.sh_2),
                    GalleryAdapter.Photo(R.drawable.sh_3),
                    GalleryAdapter.Photo(R.drawable.sh_4),
                    GalleryAdapter.Photo(R.drawable.sh_5),
                    GalleryAdapter.Photo(R.drawable.sh_6),
                    GalleryAdapter.Photo(R.drawable.sh_7),
                )
            )
        )
        b.gallery.setIndicatorAnimation(IndicatorAnimationType.WORM)
        b.gallery.startAutoCycle()
        b.sAddToCart.setOnClickListener { }
        b.sAddToCartTV.setTypeface(fNormal, Typeface.BOLD)
        b.sBook.setOnClickListener {
            booked = !booked
            Fun.transPop(b.sBookIV, b.sBook, booked)
        }
        b.sProdInfo1.setTypeface(fNormal, Typeface.BOLD)
        b.sProdInfo2.typeface = fNormal
        b.oldPrice.typeface = fNormal
        b.newPrice.setTypeface(fNormal, Typeface.BOLD)
        b.sDesc1Title.typeface = fNormal
        b.brandName.typeface = fNormal
        b.brand.typeface = fNormal
        b.weight.typeface = fNormal
        b.sDesc2Main.typeface = fNormal
        b.sDesc2Title.typeface = fNormal
        b.sDesc2Main.typeface = fNormal
        b.sDesc2More.setOnClickListener { }
        b.spinner.adapter = FontAdapter(
            this, R.layout.spinner_1, resources.getStringArray(R.array.weights), fNormal
        )
        for (s in 0 until b.stars.childCount) if (b.stars[s] is ImageView) (b.stars[s] as ImageView).apply {
            drawable.colorFilter = betterStar()
            setOnTouchListener { v, event ->
                for (ss in 0 until b.stars.childCount) if (b.stars[ss] is ImageView) (b.stars[ss] as ImageView).apply {
                    var bb = ss <= s
                    setImageResource(if (bb) android.R.drawable.star_on else android.R.drawable.star_off)
                    drawable.colorFilter = betterStar()
                    alpha = if (bb) 1f else .7f
                }
                b.rate.text = "${s + 1}/5"
                true
            }
        }

        // Comments
        b.sCommentsTV.typeface = fNormal
        b.sCom1.typeface = fNormal
        b.sCom2.typeface = fNormal
        b.sCom3.typeface = fNormal
        b.sComDate1.typeface = fNormal
        b.sComDate2.typeface = fNormal
        b.sComDate3.typeface = fNormal
        b.sComAll.typeface = fNormal
        b.sComAll.setOnClickListener { }
        b.sComSend.typeface = fNormal
        b.sComSend.setOnClickListener { }
        b.sLike1IV.setOnClickListener {
            liked1 = !liked1
            Fun.transPop(b.sLike1IV, b.sLike1IV, liked1)
        }
        b.sLike2IV.setOnClickListener {
            liked2 = !liked2
            Fun.transPop(b.sLike2IV, b.sLike2IV, liked2)
        }
        b.sLike3IV.setOnClickListener {
            liked3 = !liked3
            Fun.transPop(b.sLike3IV, b.sLike3IV, liked3)
        }
        b.sComCL1.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick() {
                liked1 = !liked1
                Fun.transPop(b.sLike1IV, b.sLike1IV, liked1)
            }
        })
        b.sComCL2.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick() {
                liked2 = !liked2
                Fun.transPop(b.sLike2IV, b.sLike2IV, liked2)
            }
        })
        b.sComCL3.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick() {
                liked3 = !liked3
                Fun.transPop(b.sLike3IV, b.sLike3IV, liked3)
            }
        })

        // Real Send
        b.sComET.typeface = fNormal
        b.sComRealSend.typeface = fNormal
        b.sComRealSend.setOnClickListener { }
    }


    fun tbShadow(bool: Boolean) {
        vis(b.tbShadow, bool)
    }

    fun search(bool: Boolean) {
        searching = bool
        vis(b.tbShare, !bool)
        vis(b.tbSearchET, bool)
        if (!bool) b.tbSearchET.clearFocus()
    }
}
