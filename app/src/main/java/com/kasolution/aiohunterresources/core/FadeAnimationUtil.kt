package com.kasolution.aiohunterresources.core
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
object FadeAnimationUtil {
    fun startFadeAnimation(view: View) {
        // Crear la animación de fadeIn
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f).apply {
            duration = 1000
        }

        // Crear la animación de fadeOut
        val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f).apply {
            duration = 1000
            //startDelay = 1000 // Empieza después de 1 segundo
        }

        // Crear un AnimatorSet para combinar fadeIn y fadeOut
        val animatorSet = AnimatorSet().apply {
            playSequentially(fadeIn, fadeOut)
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Crear un loop infinito para la animación
        animatorSet.addListener(object : Animator.AnimatorListener {
            //            override fun onAnimationRepeat(p0: Animator) {}
//            override fun onAnimationEnd(animation: Animator?) {
//                animatorSet.start()
//            }
//            override fun onAnimationCancel(animation: Animator?) {}
//            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                animatorSet.start()
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }
        })

        // Iniciar la animación
        animatorSet.start()
    }

    fun stopFadeAnimation(view: View) {
        view.animate().cancel()
        view.alpha = 1.0f // O la transparencia deseada al finalizar
    }
}