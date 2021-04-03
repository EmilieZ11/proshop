package uk.easys.test

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FontAdapter<String>(
    var activity: AppCompatActivity, val resId: Int, val arr: Array<String>, val font: Typeface
) : ArrayAdapter<String>(activity, resId, arr) {

    override fun getDropDownView(pos: Int, convertView: View?, parent: ViewGroup) =
        customView(pos, parent)

    override fun getView(pos: Int, convertView: View?, parent: ViewGroup) = customView(pos, parent)

    fun customView(pos: Int, parent: ViewGroup): View {// convertView: View?
        var tv = activity.layoutInflater.inflate(resId, parent, false) as TextView
        tv.text = arr[pos].toString()
        tv.typeface = font
        return tv
    }
}
