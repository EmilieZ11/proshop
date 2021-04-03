package uk.easys.easymarketing.adap

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uk.easys.easymarketing.Fun.Companion.c
import uk.easys.easymarketing.Fun.Companion.dm
import uk.easys.easymarketing.Fun.Companion.fRegular
import uk.easys.easymarketing.R
import uk.easys.easymarketing.data.API
import uk.easys.easymarketing.page.Home.Companion.catTitles

class HomeCateAdap(var list: List<API.Category>) :
    RecyclerView.Adapter<HomeCateAdap.MyViewHolder>() {
    class MyViewHolder(val v: ConstraintLayout) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_cate, parent, false) as ConstraintLayout
        val title = v[1] as TextView
        title.typeface = fRegular
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(h: MyViewHolder, i: Int) {
        val pic = h.v[0] as ImageView
        val title = h.v[1] as TextView
        Glide.with(c).load(list[i].src).into(pic)
        title.text = list[i].title
        val r = dm.density * 20
        title.background =
            ShapeDrawable(RoundRectShape(floatArrayOf(r, r, r, r, r, r, r, r), null, null)).apply {
                paint.color = catTitles[i % 4]
            }
    }

    override fun getItemCount() = list.size
}
