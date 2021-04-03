package uk.easys.easymarketing

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.animation.OvershootInterpolator
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import uk.easys.easymarketing.Fun.Companion.clp
import uk.easys.easymarketing.Fun.Companion.color
import uk.easys.easymarketing.Fun.Companion.dm
import uk.easys.easymarketing.Fun.Companion.fBold
import uk.easys.easymarketing.Fun.Companion.fRegular
import uk.easys.easymarketing.Fun.Companion.relMarginTopV
import uk.easys.easymarketing.Fun.Companion.vis
import uk.easys.easymarketing.Fun.Companion.vish
import uk.easys.easymarketing.databinding.IntroBinding

class Intro : AppCompatActivity() {
    lateinit var b: IntroBinding
    val model: Model by viewModels()
    val tfRatio = 394f / 950f
    val bfRatio = 450f / 1012f
    var tfH = 0f
    var bfH = 0f
    val codeTimeout = 120000L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = IntroBinding.inflate(layoutInflater)
        setContentView(b.root)

        Fun.init(this)
        Fun.transparentBars(this, status = true, nav = true)
        window.decorView.setBackgroundColor(color(R.color.CSV))


        // Effect Layers
        tfH = (clp(b.tf).matchConstraintPercentWidth * dm.widthPixels) * tfRatio
        bfH = (clp(b.bf).matchConstraintPercentWidth * dm.widthPixels) * bfRatio
        b.tf.translationY = -tfH
        b.bf.translationY = bfH

        // Loading
        Fun.onLoad(b.root, object : Fun.Do {
            override fun run() {
                AnimatorSet().apply {
                    duration = 800
                    startDelay = 200
                    interpolator = OvershootInterpolator()
                    playTogether(
                        ObjectAnimator.ofFloat(b.tf, "translationY", tfH * -0.19f),
                        ObjectAnimator.ofFloat(b.bf, "translationY", bfH * 0.3f),
                        ObjectAnimator.ofFloat(b.logo, "scaleX", 1f),
                        ObjectAnimator.ofFloat(b.logo, "scaleY", 1f),
                        ObjectAnimator.ofFloat(b.logo, "rotation", 0f),
                        ObjectAnimator.ofFloat(b.welcomeEn, "scaleX", 1f),
                        ObjectAnimator.ofFloat(b.welcomeEn, "scaleY", 1f),
                        ObjectAnimator.ofFloat(b.welcomeEn, "rotation", 0f),
                        ObjectAnimator.ofFloat(b.welcomeNative, "scaleX", 1f),
                        ObjectAnimator.ofFloat(b.welcomeNative, "scaleY", 1f),
                        ObjectAnimator.ofFloat(b.welcomeNative, "rotation", 0f)
                    )
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            check()
                        }
                    })
                    start()
                }

                relMarginTopV(b.etCL, 0.03f)
                relMarginTopV(b.enterPhone, 0.06f)
                relMarginTopV(b.codeSentCL, 0.01f)
                relMarginTopV(b.sendCode, 0.06f)
            }
        })

        // Logo
        b.welcomeEn.typeface = fBold
        b.welcomeNative.typeface = fBold

        // Login
        b.et.typeface = fBold
        b.countryCode.typeface = fBold
        b.enterPhone.typeface = fBold
        b.sendCode.typeface = fBold
        b.codeSent.typeface = fRegular
        b.chronometre.typeface = fRegular
        b.resendCode.typeface = fRegular
        b.sendCode.setOnClickListener { }
        Fun.numberHint(b.etHint, b.et)
        b.et.addTextChangedListener {
            for (h in 0 until b.etHint.childCount) vish(b.etHint[h], h >= b.et.text.length)
        }
        b.sendCode.setOnClickListener { if (!sent) send() else verify() }
        b.resendCode.setOnClickListener { send() }
        b.logo.setOnClickListener { logged = false }
    }


    var logged = true
    fun check() {
        if (!logged) login()
        else next()
    }

    fun login() {
        AnimatorSet().apply {
            duration = 666
            startDelay = 2000
            val newLogoBias = 0.17f
            (dm.heightPixels.toFloat() * (clp(b.logo).verticalBias - newLogoBias)).apply {
                b.etCL.translationY = this
                b.enterPhone.translationY = this
                b.sendCode.translationY = this
                b.codeSentCL.translationY = this
            }
            playTogether(
                ObjectAnimator.ofFloat(b.tf, "translationY", -tfH * 1.5f),
                ObjectAnimator.ofFloat(b.bf, "translationY", 0f),
                ObjectAnimator.ofFloat(b.welcomeEn, "alpha", 0f),
                ObjectAnimator.ofFloat(b.welcomeNative, "alpha", 0f),
                ObjectAnimator.ofFloat(b.etCL, "alpha", 1f),
                ObjectAnimator.ofFloat(b.etCL, "scaleX", 1f),
                ObjectAnimator.ofFloat(b.etCL, "scaleY", 1f),
                ObjectAnimator.ofFloat(b.etCL, "translationY", 0f),
                ObjectAnimator.ofFloat(b.enterPhone, "alpha", 1f),
                ObjectAnimator.ofFloat(b.enterPhone, "scaleX", 1f),
                ObjectAnimator.ofFloat(b.enterPhone, "scaleY", 1f),
                ObjectAnimator.ofFloat(b.enterPhone, "translationY", 0f),
                ObjectAnimator.ofFloat(b.sendCode, "alpha", 1f),
                ObjectAnimator.ofFloat(b.sendCode, "scaleX", 1f),
                ObjectAnimator.ofFloat(b.sendCode, "scaleY", 1f),
                ObjectAnimator.ofFloat(b.sendCode, "translationY", 0f),
                ObjectAnimator.ofFloat(b.codeSentCL, "alpha", 1f),
                ObjectAnimator.ofFloat(b.codeSentCL, "scaleX", 1f),
                ObjectAnimator.ofFloat(b.codeSentCL, "scaleY", 1f),
                ObjectAnimator.ofFloat(b.codeSentCL, "translationY", 0f)
            )
            start()
            object : CountDownTimer(startDelay, startDelay) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    vis(b.etCL)
                    ConstraintSet().apply {
                        clone(b.root)
                        TransitionManager.beginDelayedTransition(
                            b.root, AutoTransition().setDuration(duration)
                        )
                        setVerticalBias(R.id.logo, newLogoBias)
                        applyTo(b.root)
                    }
                    if (Build.VERSION.SDK_INT >= 21) window.statusBarColor = color(R.color.CP_WEAK3)
                }
            }.start()
        }
    }

    var sent = false
    var phone: String? = null
    fun send(con: String = "+964", num: String = b.et.text.toString()) {
        if (num.length < 10 && phone == null) return
        if (num.length == 10) phone = con + num
        toVerify()
    }

    fun toVerify(bb: Boolean = true) {
        sent = bb
        b.et.setText("")
        b.enterPhone.setText(if (sent) R.string.enterCode else R.string.enterPhone)
        b.sendCode.setText(if (sent) R.string.verifyCode else R.string.sendCode)
        Fun.setMaxLength(b.et, if (sent) 6 else 10)
        Fun.numberHint(b.etHint, b.et)
        vis(b.countryCode, !bb)
        vis(b.codeSentCL, bb)
        vis(b.chronometre)
        b.chronometre.apply {
            base = SystemClock.elapsedRealtime() + codeTimeout
            start()
            object : CountDownTimer(codeTimeout, codeTimeout) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    stop()
                    vis(this@apply, false)
                    vis(b.resendCode)
                }
            }.start()
        }
        vis(b.resendCode, false)
    }

    fun verify(code: String = b.et.text.toString()) {
        if (code.length < 6) return
        next()
    }

    fun next() {
        startActivity(Intent(this, Main::class.java))
    }
}
