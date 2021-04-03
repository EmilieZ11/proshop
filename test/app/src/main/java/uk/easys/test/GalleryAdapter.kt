package uk.easys.test

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import com.smarteist.autoimageslider.SliderViewAdapter

class GalleryAdapter(var items: List<Photo>) :
    SliderViewAdapter<GalleryAdapter.GalleryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup) = GalleryHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.gallery, null) as ConstraintLayout
    )

    override fun onBindViewHolder(holder: GalleryHolder, pos: Int) {
        val photo = items[pos]
        holder.iv.setImageResource(photo.id)
    }

    override fun getCount() = items.size

    class GalleryHolder(
        var cl: ConstraintLayout,
        var iv: ImageView = cl[0] as ImageView
    ) : SliderViewAdapter.ViewHolder(cl)

    data class Photo(val id: Int)
}
