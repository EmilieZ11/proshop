package uk.easys.easymarketing

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.CountDownTimer
import android.text.InputFilter
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import uk.easys.easymarketing.data.API
import uk.easys.easymarketing.more.Fonts
import java.util.*
import kotlin.math.abs

class Fun {
    companion object {
        lateinit var c: Context
        lateinit var fRegular: Typeface
        lateinit var fBold: Typeface
        lateinit var pages: List<NavButton>
        const val mp = ViewGroup.LayoutParams.MATCH_PARENT
        const val wc = ViewGroup.LayoutParams.WRAP_CONTENT
        const val pi = ConstraintLayout.LayoutParams.PARENT_ID
        var dm = DisplayMetrics()
        var apiInterface: API.APIInterface? = null


        fun init(that: AppCompatActivity) {
            c = that.applicationContext
            dm = c.resources.displayMetrics
            if (!::pages.isInitialized) pages = listOf(
                NavButton("home", R.drawable.icon_home_1, R.id.toHome),
                NavButton("cate", R.drawable.icon_category_1, R.id.toCate),
                NavButton("prof", R.drawable.icon_profile_1, R.id.toProf),
                NavButton("supp", R.drawable.icon_category_1, R.id.toSupp),
                NavButton("abut", R.drawable.icon_profile_1, R.id.toAbut),
                NavButton("faqs", R.drawable.icon_category_1, R.id.toFaqs)
            )
            if (apiInterface == null)
                apiInterface = API.client()?.create(API.APIInterface::class.java)

            // Fonts
            fRegular = fonts(Fonts.REGULAR)
            fBold = fonts(Fonts.BOLD)
        }

        fun fonts(which: Fonts): Typeface = Typeface.createFromAsset(c.assets, which.path)

        fun vis(v: View, b: Boolean = true) {
            v.visibility = if (b) View.VISIBLE else View.GONE
        }

        fun vish(v: View, b: Boolean = true) {
            v.visibility = if (b) View.VISIBLE else View.INVISIBLE
        }

        fun color(res: Int) = ContextCompat.getColor(c, res)

        fun drawable(res: Int) = ContextCompat.getDrawable(c, res)

        fun onLoad(view: View, func: Do): CountDownTimer =
            object : CountDownTimer(10000, 50) {
                override fun onFinish() {}
                override fun onTick(millisUntilFinished: Long) {
                    if (view.width <= 0) return
                    func.run()
                    this.cancel()
                }
            }.start()

        fun dp(px: Int = 0) = (dm.density * px.toFloat()).toInt()

        fun now(): Long = Calendar.getInstance().timeInMillis

        fun prodShine(height: Float, factor: Float = 0.5f) = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            gradientType = GradientDrawable.RADIAL_GRADIENT
            colors = intArrayOf(color(R.color.CPV), color(R.color.TP))
            gradientRadius = height * factor
        }

        fun relHeight(v: View, factor: Float, add: Int = 0): Int {
            (v.layoutParams as ConstraintLayout.LayoutParams).apply {
                (dm.widthPixels.toFloat() * factor).toInt().apply {
                    height = this + add
                    return@relHeight this
                }
            }
        }

        fun fade(v: View, bb: Boolean = true): ObjectAnimator =
            ObjectAnimator.ofFloat(v, "alpha", if (bb) 0f else 1f, if (bb) 1f else 0f).apply {
                duration = 1516
                start()
            }

        fun transPop(iv: ImageView, holder: View, bb: Boolean, transDur: Int = 123) {
            (iv.drawable as TransitionDrawable).apply {
                if (bb) {
                    startTransition(transDur)
                    AnimatorSet().apply {
                        duration = 192
                        interpolator = AccelerateInterpolator()
                        playTogether(
                            ObjectAnimator.ofFloat(holder, "scaleX", 1.25f),
                            ObjectAnimator.ofFloat(holder, "scaleY", 1.25f)
                        )
                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                AnimatorSet().apply {
                                    duration = 192
                                    interpolator = DecelerateInterpolator()
                                    playTogether(
                                        ObjectAnimator.ofFloat(holder, "scaleX", 1f),
                                        ObjectAnimator.ofFloat(holder, "scaleY", 1f)
                                    )
                                    start()
                                }
                            }
                        })
                        start()
                    }
                } else reverseTransition(transDur)
            }
        }

        fun durFromDist(o: Float, n: Float, factor: Float = 0.33f) = (abs(o - n) * factor).toLong()

        fun bmpRound(bmp: Bitmap): Bitmap {
            var output = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
            var canvas = Canvas(output)
            canvas.drawRoundRect(
                RectF(Rect(0, 0, bmp.width, bmp.height)),
                bmp.width / 2f, bmp.height / 2f,
                Paint().apply { flags = Paint.ANTI_ALIAS_FLAG })
            var paintImage =
                Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP) }
            canvas.drawBitmap(bmp, 0f, 0f, paintImage)
            return output
        }

        fun loadAvatar(url: String?, iv: ImageView) {
            if (url != null) Glide.with(c).asBitmap().load(url).into(
                object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        iv.setImageResource(R.drawable.default_user_1)
                    }

                    override fun onResourceReady(
                        resource: Bitmap, transition: Transition<in Bitmap>?
                    ) {
                        iv.setImageBitmap(bmpRound(resource))
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        iv.setImageResource(R.drawable.default_user_1)
                    }
                })
            else iv.setImageResource(R.drawable.default_user_1)
        }

        fun relMarginTop(v: View, factor: Float = 0.1f) {
            val vlp = v.layoutParams as ViewGroup.MarginLayoutParams
            vlp.topMargin = (dm.widthPixels.toFloat() * factor).toInt()
            v.layoutParams = vlp
        }

        fun relMarginTopV(v: View, factor: Float = 0.1f) {
            val vlp = v.layoutParams as ViewGroup.MarginLayoutParams
            vlp.topMargin = (dm.heightPixels.toFloat() * factor).toInt()
            v.layoutParams = vlp
        }

        fun filter(color: Int) =
            PorterDuffColorFilter(ContextCompat.getColor(c, color), PorterDuff.Mode.SRC_IN)

        fun setWindowFlag(that: AppCompatActivity, bits: Int, on: Boolean) {
            val winParams = that.window.attributes
            winParams.flags =
                if (on) (winParams.flags or bits) else (winParams.flags and bits.inv())
            that.window.attributes = winParams
        }

        @Suppress("DEPRECATION")
        fun transparentBars(that: AppCompatActivity, status: Boolean = true, nav: Boolean = true) {
            if (!status && !nav) return
            var param1 = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            var param2 = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
            var param3 = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            if (!status) {
                param1 = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                param3 = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            }
            if (!nav) {
                param1 = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                param2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                } else (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                param3 = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            }
            if (Build.VERSION.SDK_INT in 19..20) setWindowFlag(that, param1, true)
            if (Build.VERSION.SDK_INT >= 19) that.window.decorView.systemUiVisibility = param2
            if (Build.VERSION.SDK_INT >= 21) {
                setWindowFlag(that, param3, false)
                if (status) that.window.statusBarColor = Color.TRANSPARENT
                if (nav) that.window.navigationBarColor = Color.TRANSPARENT
            }
        }

        fun clp(v: View): ConstraintLayout.LayoutParams =
            v.layoutParams as ConstraintLayout.LayoutParams

        fun retrieveMaxLength(et: EditText): Int? {
            var filter: InputFilter.LengthFilter? = null
            for (f in et.filters) if (f is InputFilter.LengthFilter) filter = f
            if (filter == null) return null
            return Gson().fromJson(Gson().toJson(filter), FakeInputMax::class.java).mMax
        }

        fun numberHint(group: LinearLayout, et: EditText) {
            var max: Int? = retrieveMaxLength(et) ?: return
            group.removeAllViews()
            var fuck = 0f
            for (i in 0 until max!!) group.addView(View(c).apply {
                fuck = et.textSize
                layoutParams = LinearLayout.LayoutParams(
                    (fuck * 0.5f).toInt(), (fuck * 0.17f).toInt()
                ).apply {
                    topMargin = fuck.toInt()
                    leftMargin = (fuck * 0.17f).toInt()
                    rightMargin = leftMargin
                }
                setBackgroundResource(R.drawable.number_hint_1)
            })
            val etLP = et.layoutParams
            etLP.width = (fuck * max * 0.84f).toInt()
            et.layoutParams = etLP
        }

        fun setMaxLength(et: EditText, max: Int) {
            et.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(max))
        }

        fun catTitle() = listOf(
            color(R.color.cat_title_1), color(R.color.cat_title_2),
            color(R.color.cat_title_3), color(R.color.cat_title_4)
        )
    }


    interface Do {
        fun run()
    }

    class LimitedStack<Any>(val max: Int) : ArrayList<Any>() {
        override fun add(element: Any): Boolean {
            super.add(0, element)
            while (size > max) removeAt(max)
            return true
        }
    }

    data class FakeInputMax(var mMax: Int)

    class NavButton(
        var name: String,
        var icon: Int,
        var action: Int,
        var sub: List<Sub>? = null
    ) {
        companion object {
            fun findByName(name: String, list: List<NavButton> = pages): NavButton? {
                var button: NavButton? = null
                for (b in list) if (b.name == name) button = b
                return button
            }
        }

        data class Sub(var name: String, var icon: Int)
    }
}
