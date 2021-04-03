package uk.easys.easymarketing.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.easys.easymarketing.Fun
import uk.easys.easymarketing.Fun.Companion.apiInterface
import uk.easys.easymarketing.Fun.Companion.c
import uk.easys.easymarketing.Fun.Companion.dm
import uk.easys.easymarketing.Fun.Companion.dp
import uk.easys.easymarketing.Main
import uk.easys.easymarketing.Model
import uk.easys.easymarketing.R
import uk.easys.easymarketing.adap.CateAdap
import uk.easys.easymarketing.data.API

class Cate : Fragment() {//#EEF9FF
    var cate: ConstraintLayout? = null
    lateinit var list: RecyclerView

    val model: Model by activityViewModels()

    companion object {
        lateinit var catTitles: List<Int>

        @Suppress("unused")
        @JvmStatic
        fun newInstance() = Cate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        cate = inflater.inflate(R.layout.frag_cate, container, false) as ConstraintLayout?
        list = cate!![0] as RecyclerView

        loadPage()
        catTitles = Fun.catTitle()


        // List
        var span = 1
        if (dm.widthPixels > 0) span = dm.widthPixels / dp(180)
        if (span < 1) span = 1
        list.layoutManager = GridLayoutManager(c, span)

        // With Main
        model.tbMode.value = Main.ToolbarMode.CONVEX
        model.cartFactor.value = 1f

        return cate
    }

    override fun onResume() {
        super.onResume()
        model.currentPage.value = Fun.pages.indexOf(Fun.NavButton.findByName("cate"))
    }

    fun loadPage() {
        if (apiInterface == null) return
        model.loading.value = true
        apiInterface?.cate()?.enqueue(object : Callback<List<API.Category>?> {
            override fun onResponse(call: Call<List<API.Category>?>?, response: Response<List<API.Category>?>) {
                model.loading.value = false
                response.body()?.let { cats -> list.adapter = CateAdap(this@Cate, cats) }
            }

            override fun onFailure(call: Call<List<API.Category>?>, t: Throwable?) {
                Toast.makeText(c, "${t?.message}", Toast.LENGTH_LONG).show()
                model.loading.value = false
                call.cancel()
            }
        })
    }
}
