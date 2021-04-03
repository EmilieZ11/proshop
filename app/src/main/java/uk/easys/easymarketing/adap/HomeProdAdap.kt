package uk.easys.easymarketing.adap

import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uk.easys.easymarketing.Fun.Companion.c
import uk.easys.easymarketing.Fun.Companion.fRegular
import uk.easys.easymarketing.Fun.Companion.transPop
import uk.easys.easymarketing.R
import uk.easys.easymarketing.data.API

class HomeProdAdap(var list: List<API.Product>) :
    RecyclerView.Adapter<HomeProdAdap.MyViewHolder>() {
    class MyViewHolder(val v: ConstraintLayout) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_prod, parent, false) as ConstraintLayout
        val ll = v[0] as LinearLayout
        val title = ll[1] as TextView
        val prices = ll[2] as LinearLayout
        val newPrice = prices[0] as TextView
        val oldPrice = prices[1] as TextView

        // Media
        listOf(title, newPrice, oldPrice).forEach { it.typeface = fRegular }

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(h: MyViewHolder, i: Int) {
        val ll = h.v[0] as LinearLayout
        val white = ll[0] as ConstraintLayout
        val pic = white[0] as ImageView
        val bookCL = white[1] as ConstraintLayout
        val book = bookCL[0] as ImageView
        val title = ll[1] as TextView
        val prices = ll[2] as LinearLayout
        val newPrice = prices[0] as TextView
        val oldPrice = prices[1] as TextView

        // Media
        Glide.with(c).load(list[i].src).into(pic)
        bookCL.setOnClickListener {
            val last = list[h.layoutPosition].book
            list[h.layoutPosition].book = if (last == 1.toByte()) 0 else 1
            transPop(book, bookCL, list[h.layoutPosition].book == 1.toByte())
        }
        if (list[i].book == 1.toByte())
            (book.drawable as TransitionDrawable).startTransition(1)

        // Texts
        title.text = list[i].title
        newPrice.text = c.getString(R.string.newPrice, list[i].newPrice)
        oldPrice.text = c.getString(R.string.oldPrice, list[i].oldPrice)
    }

    override fun getItemCount() = list.size
}