package uk.easys.easymarketing.adap

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blure.complexview.ComplexView
import com.bumptech.glide.Glide
import uk.easys.easymarketing.Fun
import uk.easys.easymarketing.Fun.Companion.c
import uk.easys.easymarketing.R
import uk.easys.easymarketing.data.API

class HomeSlider(fa: FragmentActivity, var data: List<API.Product>) :
    FragmentStateAdapter(fa) {
    companion object {
        lateinit var sliderData: List<API.Product>
        var currentItem = 0
    }

    init {
        sliderData = data
    }

    override fun getItemCount() = data.size

    override fun createFragment(pos: Int): Fragment {
        currentItem = pos
        return HomeSlide()
    }

    class HomeSlide : Fragment() {
        // Fragment must NOT have a custom constructor!
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, state: Bundle?
        ): View {
            val p = sliderData[currentItem]
            val inf = inflater.inflate(R.layout.home_slide, container, false) as ConstraintLayout
            val ll = inf[0] as LinearLayout
            val pic = ll[0] as ImageView
            val tvLL = ll[1] as LinearLayout
            val but = tvLL[0] as ComplexView
            val butTV = but[0] as TextView
            val tv2 = tvLL[1] as TextView
            val tv3 = tvLL[2] as TextView
            val tv4 = tvLL[3] as TextView
            val tv5 = tvLL[4] as TextView

            // Media
            Glide.with(c).load(p.src).into(pic)
            tv2.setTypeface(Fun.fBold, Typeface.BOLD)
            listOf(butTV, tv3, tv4, tv5).forEach { it.typeface = Fun.fRegular }

            // Texts
            butTV.text = c.getString(R.string.discount, p.discount)
            tv2.text = p.title
            tv3.text = p.desc
            tv4.text = c.getString(R.string.oldPrice, p.oldPrice)
            tv5.text = c.getString(R.string.newPrice, p.newPrice)
            return inf
        }
    }
}
