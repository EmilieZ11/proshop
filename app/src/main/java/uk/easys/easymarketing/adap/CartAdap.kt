package uk.easys.easymarketing.adap

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
import uk.easys.easymarketing.Fun.Companion.color
import uk.easys.easymarketing.Fun.Companion.fBold
import uk.easys.easymarketing.Fun.Companion.fRegular
import uk.easys.easymarketing.R
import uk.easys.easymarketing.data.API

class CartAdap(var orders: List<API.Order>, var products: List<API.Product>) :
    RecyclerView.Adapter<CartAdap.MyViewHolder>() {
    class MyViewHolder(val v: LinearLayout) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false) as LinearLayout
        val cl2 = v[1] as ConstraintLayout
        val main = cl2[0] as LinearLayout
        val title = main[0] as TextView
        val prices = main[1] as LinearLayout
        val newPrice = prices[0] as TextView
        val oldPrice = prices[1] as TextView
        val edition = main[2] as LinearLayout
        val number = edition[1] as TextView
        val cl3 = v[2] as ConstraintLayout
        val delete = cl3[0] as TextView

        // Media
        listOf(title, oldPrice, number, delete).forEach { it.typeface = fRegular }
        listOf(newPrice).forEach { it.typeface = fBold }

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(h: MyViewHolder, i: Int) {
        val p = findProductByOrder(orders[i], products) ?: return
        val cl1 = h.v[0] as ConstraintLayout
        val pic = cl1[0] as ImageView
        val cl2 = h.v[1] as ConstraintLayout
        val main = cl2[0] as LinearLayout
        val title = main[0] as TextView
        val prices = main[1] as LinearLayout
        val newPrice = prices[0] as TextView
        val oldPrice = prices[1] as TextView
        val edition = main[2] as LinearLayout
        val adder = edition[0] as ImageView
        val number = edition[1] as TextView
        val remover = edition[2] as ImageView
        val cl3 = h.v[2] as ConstraintLayout
        val delete = cl3[0] as TextView

        // Media
        Glide.with(c).load(p.src).into(pic)
        if (i % 2 == 1) h.v.setBackgroundColor(color(R.color.CSV))

        // Texts
        title.text = p.title
        newPrice.text = c.getString(R.string.newPrice, p.newPrice)
        oldPrice.text = c.getString(R.string.oldPrice, p.oldPrice)
        number.text = orders[0].number.toString()

        // Clicks
        delete.setOnClickListener { }
        adder.setOnClickListener {
            number.text = (number.text.toString().toInt() + 1).toString()
        }
        remover.setOnClickListener {
            if (number.text.toString() == "0") return@setOnClickListener
            number.text = (number.text.toString().toInt() - 1).toString()
        }
    }

    override fun getItemCount() = orders.size


    companion object {
        fun findProductByOrder(order: API.Order, list: List<API.Product>): API.Product? {
            var found: API.Product? = null
            for (p in list) if (order.product == p.id) found = p
            return found
        }
    }
}