package uk.easys.test

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.graphics.drawable.TransitionDrawable
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class Fun {
    companion object {
        lateinit var c: Context
        lateinit var fNormal: Typeface


        fun init(that: AppCompatActivity) {
            c = that.applicationContext
            fNormal = fonts(Fonts.NORMAL)
        }

        fun fonts(which: Fonts): Typeface =
            Typeface.createFromAsset(c.assets, which.path)

        fun vis(v: View, b: Boolean = true) {
            v.visibility = if (b) View.VISIBLE else View.GONE
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

        fun betterStar() = PorterDuffColorFilter(
            ContextCompat.getColor(c, R.color.star_1), PorterDuff.Mode.SRC_IN
        )
    }

    enum class Fonts(val path: String) {
        NORMAL("yekan.ttf")
    }
}