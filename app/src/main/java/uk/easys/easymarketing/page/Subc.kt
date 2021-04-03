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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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
import uk.easys.easymarketing.adap.SubcAdap
import uk.easys.easymarketing.data.API

class Subc : Fragment() {
    var subc: ConstraintLayout? = null
    lateinit var list: RecyclerView

    val model: Model by activityViewModels()

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun newInstance() = Subc()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        subc = inflater.inflate(R.layout.frag_subc, container, false) as ConstraintLayout?
        list = subc!![0] as RecyclerView

        loadPage()


        // List
        var span = 1
        if (dm.widthPixels > 0) span = dm.widthPixels / dp(180)
        if (span < 1) span = 1
        list.layoutManager = StaggeredGridLayoutManager(span, StaggeredGridLayoutManager.VERTICAL)

        // With Main
        model.tbMode.value = Main.ToolbarMode.FLAT_WHITE
        model.cartFactor.value = 1f

        return subc
    }

    override fun onResume() {
        super.onResume()
        model.currentPage.value = Fun.pages.indexOf(Fun.NavButton.findByName("cate"))
    }


    fun loadPage() {
        if (apiInterface == null) return
        model.loading.value = true
        apiInterface?.subc()?.enqueue(object : Callback<List<API.SubCat>?> {
            override fun onResponse(call: Call<List<API.SubCat>?>?, response: Response<List<API.SubCat>?>) {
                model.loading.value = false
                response.body()?.let { subcs ->
                    list.adapter = SubcAdap(subcs[0].list)
                }
            }

            override fun onFailure(call: Call<List<API.SubCat>?>, t: Throwable?) {
                Toast.makeText(c, "${t?.message}", Toast.LENGTH_LONG).show()
                model.loading.value = false
                call.cancel()
            }
        })
    }
}
