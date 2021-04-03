package uk.easys.easymarketing

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.*
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.navigation.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.easys.easymarketing.Fun.Companion.apiInterface
import uk.easys.easymarketing.Fun.Companion.c
import uk.easys.easymarketing.Fun.Companion.color
import uk.easys.easymarketing.Fun.Companion.dm
import uk.easys.easymarketing.Fun.Companion.drawable
import uk.easys.easymarketing.Fun.Companion.fBold
import uk.easys.easymarketing.Fun.Companion.fRegular
import uk.easys.easymarketing.Fun.Companion.fade
import uk.easys.easymarketing.Fun.Companion.filter
import uk.easys.easymarketing.Fun.Companion.mp
import uk.easys.easymarketing.Fun.Companion.pages
import uk.easys.easymarketing.Fun.Companion.pi
import uk.easys.easymarketing.Fun.Companion.relMarginTop
import uk.easys.easymarketing.Fun.Companion.vis
import uk.easys.easymarketing.Fun.Companion.wc
import uk.easys.easymarketing.adap.CartAdap
import uk.easys.easymarketing.data.API
import uk.easys.easymarketing.data.API.User
import uk.easys.easymarketing.databinding.MainBinding

class Main : AppCompatActivity() {
    lateinit var b: MainBinding
    val model: Model by viewModels()

    companion object {
        var fragHandler: Handler? = null
    }


    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = MainBinding.inflate(layoutInflater)
        setContentView(b.root)

        Fun.init(this)
        Fun.transparentBars(this, status = true, nav = false)
        model.currentPage.value = pages.indexOf(Fun.NavButton.findByName("home"))
        loadUser()
        loadCart()


        // Handlers
        fragHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> {// Close Cart
                        if (cartDragged >= 0f) return
                        cartFlow?.cancel()
                        cartFlow = null
                        cartFlow = AnimatorSet().apply {
                            duration = Fun.durFromDist(cartDragged, 0f)
                            cartDragged = 0f
                            val pos = cartPos()
                            playTogether(
                                ObjectAnimator.ofFloat(b.bottomSheet, "translationY", pos),
                                ObjectAnimator.ofFloat(b.cart, "translationY", pos)
                            )
                            start()
                        }
                    }
                    1 -> cartMove()
                }
            }
        }

        // Loading
        model.loading.observe(this, { bb ->
            if (bb) vis(b.loading) else fade(b.loading, false)
            listOf(
                b.toolbar, b.navHostFragment, b.lFakeRadius, b.rFakeRadius, b.bottomSheet, b.cart
            ).forEach { if (bb) it.alpha = 0f else fade(it) }
        })
        Fun.onLoad(b.root, object : Fun.Do {
            override fun run() {
                // Toolbar
                val status = resources.getDimension(R.dimen.statusBarSize).toInt()
                val tbPadT = (dm.widthPixels * 0.025f).toInt() + status
                val tbPadX = (dm.widthPixels * 0.025f).toInt()
                b.toolbar.setPadding(tbPadX, tbPadT, tbPadX, 0)
                var tbIconH = Fun.relHeight(b.toolbar, 0.155f, status) - tbPadT + status
                for (tb in 0 until b.toolbar.childCount) if (b.toolbar[tb] is ConstraintLayout)
                    (b.toolbar[tb] as ConstraintLayout).layoutParams.apply { width = tbIconH }

                // Cart
                bottomSheetHeight = resources.getDimension(R.dimen.bottomSheetSize)
                cartHeight = b.root.height *
                        (b.cart.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight
            }
        })

        // Toolbar
        b.tbNav.setOnClickListener { b.root.openDrawer(GravityCompat.START) }
        b.tbSearch.setOnClickListener { search(!searching) }
        b.tbSearchET.typeface = fRegular
        model.tbMode.observe(this, {
            vis(b.lFakeRadius, it == ToolbarMode.CONCAVE)
            vis(b.rFakeRadius, it == ToolbarMode.CONCAVE)
            when (it) {
                ToolbarMode.CONVEX -> b.toolbar.setBackgroundResource(R.drawable.tb_convex_1)
                ToolbarMode.FLAT_WHITE -> b.toolbar.background = null
                else -> b.toolbar.setBackgroundColor(color(R.color.CP))
            }
            for (tb in 0 until b.toolbar.childCount) if (b.toolbar[tb] is ConstraintLayout)
                ((b.toolbar[tb] as ConstraintLayout)[0] as ImageView).apply {
                    if (it != ToolbarMode.FLAT_WHITE) clearColorFilter()
                    else colorFilter = filter(R.color.CSO_WEAK3)
                }
        })

        // Navigation Menu
        b.nav.layoutParams.apply { width = (dm.widthPixels.toFloat() * 0.8f).toInt() }
        relMarginTop(b.userAvatar, 0.08f)
        relMarginTop(b.userName, 0.035f)
        relMarginTop(b.userTel, 0.01f)
        relMarginTop(b.navDivider, 0.07f)
        relMarginTop(b.navButtons, 0.03f)
        model.userAvatar.observe(this, { url -> Fun.loadAvatar(url, b.userAvatar) })
        model.userFName.observe(this, { fName ->
            b.userName.text = getString(R.string.visName, fName, model.userLName.value)
        })
        model.userLName.observe(this, { lName ->
            b.userName.text = getString(R.string.visName, model.userFName.value, lName)
        })
        model.userTel.observe(this, { tel -> b.userTel.text = tel })
        b.userName.typeface = fBold
        b.userTel.typeface = fRegular
        for (p in pages.indices) b.navButtons.addView(
            navButton(
                p, resources.getStringArray(R.array.pages)[p],
                pages[p].icon, pages[p].action, false, pages[p].sub
            )
        )
        b.navNotif.setOnClickListener { }
        model.currentPage.observe(this, { i ->
            for (bb in 0 until b.navButtons.childCount) (b.navButtons[bb] as ConstraintLayout).apply {
                background = drawable(R.drawable.nav_button_1)
                (this[0] as ImageView).clearColorFilter()
                (this[1] as TextView).setTextColor(color(R.color.CSO))
            }
            (b.navButtons[i] as ConstraintLayout).apply {
                background = drawable(R.drawable.nav_button_hover_1)
                (this[0] as ImageView).colorFilter = filter(R.color.CP)
                (this[1] as TextView).setTextColor(color(R.color.navBtnSelected))
            }
        })

        // Cart
        b.bottomSheet.setOnTouchListener { _, ev ->
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastDragDirs.clear()
                    cartFlow?.cancel()
                    cartFlow = null
                    cartY = ev.y
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    cartMove(cartY - ev.y)
                    cartY = ev.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    cartFlow = AnimatorSet().apply {
                        var falses = arrayListOf<Boolean>()
                        var trues = arrayListOf<Boolean>()
                        for (d in lastDragDirs) if (d) trues.add(d) else falses.add(d)
                        val newDragged = if (falses.size > trues.size) -cartHeight else 0f
                        duration = Fun.durFromDist(cartDragged, newDragged)
                        cartDragged = newDragged
                        val pos = cartPos()
                        playTogether(
                            ObjectAnimator.ofFloat(b.bottomSheet, "translationY", pos),
                            ObjectAnimator.ofFloat(b.cart, "translationY", pos)
                        )
                        start()
                    }
                    true
                }
                else -> false
            }
        }
        b.cart.setOnClickListener { }
        b.hBSTV.typeface = fRegular
        b.hBS2TV.typeface = fRegular
        b.hBSPrice.typeface = fBold
        b.hBS2Price.typeface = fBold
        b.hBSCurr.typeface = fBold
        b.hBS2Curr.typeface = fBold
        b.cartEmptyTV.typeface = fBold
        b.sumOfItems.typeface = fRegular
        b.cartPay.typeface = fRegular
        b.cartPay.setOnClickListener { }
    }

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {
        if (b.navHostFragment.findNavController().popBackStack()) return
        if (b.root.isDrawerOpen(Gravity.START)) {
            b.root.closeDrawer(Gravity.START); return; }
        moveTaskToBack(true)
        Process.killProcess(Process.myPid())
        kotlin.system.exitProcess(1)
    }


    var searching = false
    fun search(bool: Boolean) {
        searching = bool
        vis(b.tbSearchET, bool)
        if (!bool) b.tbSearchET.clearFocus()
        else b.tbSearchET.requestFocus()
        (c.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            if (bool) showSoftInput(b.tbSearchET, InputMethodManager.SHOW_IMPLICIT)
            else hideSoftInputFromWindow(b.tbSearchET.windowToken, 0)
        }
        b.tbSearchIV.setImageResource(if (bool) R.drawable.close_1 else R.drawable.search_1)
    }

    var loadingUser = false
    fun loadUser() {
        if (apiInterface == null) return
        loadingUser = true
        apiInterface?.user()?.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>?, response: Response<User?>) {
                loadingUser = false
                response.body()?.let { user ->
                    model.userId.value = user.id
                    model.userFName.value = user.fname
                    model.userFName.value = user.fname
                    model.userLName.value = user.lname
                    model.userAvatar.value = user.photo
                    model.userMail.value = user.mail
                    model.userTel.value = user.tel
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable?) {
                loadingUser = false
                Toast.makeText(c, "${t?.message}", Toast.LENGTH_LONG).show()
                call.cancel()
            }
        })
    }

    var loadingCart = false
    fun loadCart() {
        if (apiInterface == null) return
        loadingCart = true
        apiInterface?.cart()?.enqueue(object : Callback<API.Cart?> {
            override fun onResponse(call: Call<API.Cart?>?, response: Response<API.Cart?>) {
                loadingCart = false
                response.body()?.let { cart ->
                    vis(b.cartEmpty, cart.orders.isEmpty())
                    if (cart.orders.isNotEmpty())
                        b.rvCart.adapter = CartAdap(cart.orders, cart.products)

                    var summer = 0f
                    for (o in cart.orders) CartAdap.findProductByOrder(o, cart.products)?.let {
                        summer += it.newPrice.toFloat()
                    }
                    b.hBSPrice.text = summer.toInt().toString()
                    b.hBS2Price.text = summer.toInt().toString()
                    b.sumOfItems.text = getString(R.string.sumOfItems, cart.orders.size)
                }
            }

            override fun onFailure(call: Call<API.Cart?>, t: Throwable?) {
                loadingCart = false
                Toast.makeText(c, "${t?.message}", Toast.LENGTH_LONG).show()
                call.cancel()
            }
        })
    }

    var cartY = 0f
    var cartDragged = 0f
    var cartFlow: AnimatorSet? = null
    var cartHeight = 0f
    var bottomSheetHeight = 0f
    var lastDragDirs = Fun.LimitedStack<Boolean>(10)
    fun cartMove(dragged: Float? = null) {
        if (dragged != null) {
            if (dragged == 0f) return
            cartDragged += -dragged
            if (cartDragged < -cartHeight) cartDragged = -cartHeight
            if (cartDragged > 0f) cartDragged = 0f
            lastDragDirs.add(dragged < 0f)
        }
        cartPos().apply {
            b.bottomSheet.translationY = this
            b.cart.translationY = this
        }
    }

    fun cartPos() = ((bottomSheetHeight * model.cartFactor.value!!) + cartHeight + cartDragged)

    fun navButton(
        i: Int, name: String, icon: Int, action: Int,
        isSub: Boolean = false,
        sub: List<Fun.NavButton.Sub>? = null
    ): ConstraintLayout =
        (layoutInflater.inflate(
            R.layout.nav_button, b.navButtons, false
        ) as ConstraintLayout).apply {
            var current = i == model.currentPage.value
            if (isSub) current = false
            layoutParams = (layoutParams as LinearLayout.LayoutParams).apply {
                val verMar = (dm.widthPixels.toFloat() * 0.005f).toInt()
                topMargin = verMar
                bottomMargin = verMar
            }
            var horPad = (dm.widthPixels.toFloat() * 0.02f).toInt()
            var verPad = (dm.widthPixels.toFloat() * 0.032f).toInt()
            setPaddingRelative(horPad, verPad, horPad, verPad)
            val iconId = View.generateViewId()
            val nameId = View.generateViewId()
            background =
                drawable(if (current) R.drawable.nav_button_hover_1 else R.drawable.nav_button_1)
            (this[0] as ImageView).apply {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    topToTop = nameId
                    bottomToBottom = nameId
                }
                id = iconId
                setImageResource(icon)
                if (current) colorFilter = filter(R.color.CP)
            }
            (this[1] as TextView).apply {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams)
                    .apply { startToEnd = iconId }
                id = nameId
                text = name
                typeface = fRegular
                textSize = dm.widthPixels.toFloat() * 0.013f
                setTextColor(color(if (current) R.color.navBtnSelected else R.color.CSO))
            }
            if (sub == null) setOnClickListener {
                b.navHostFragment.findNavController().navigate(action)
                b.root.closeDrawers()
            }

            if (sub != null) {
                val subs = LinearLayout(c).apply {
                    layoutParams = ConstraintLayout.LayoutParams(mp, wc).apply {
                        topToBottom = nameId
                        startToStart = pi
                        topMargin = verPad
                    }
                    pivotY = 0f
                    scaleY = 0f
                    visibility = View.GONE
                    orientation = LinearLayout.VERTICAL
                    for (s in sub.indices) addView(
                        navButton(s, sub[s].name, sub[s].icon, View.generateViewId(), true)
                    )
                }
                addView(subs)

                setOnClickListener {
                    val doOpen = subs.scaleY == 0f
                    ObjectAnimator.ofFloat(subs, "scaleY", if (doOpen) 1f else 0f).apply {
                        duration = 191
                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationStart(animation: Animator?) {
                                if (doOpen) vis(subs, true)
                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                if (!doOpen) vis(subs, false)
                            }
                        })
                        start()
                    }
                }
            }
        }


    @Suppress("unused")
    enum class ToolbarMode { CONCAVE, CONVEX, FLAT, FLAT_WHITE }
}
