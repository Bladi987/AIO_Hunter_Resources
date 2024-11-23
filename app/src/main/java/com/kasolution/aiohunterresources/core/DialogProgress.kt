package com.kasolution.aiohunterresources.core

import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.databinding.ProgressDialogCustomBinding

object DialogProgress {
    private lateinit var dialog: Dialog
    fun show(context: Context, message: CharSequence? = null): Dialog {
        val inflater = LayoutInflater.from(context)
        val binding = ProgressDialogCustomBinding.inflate(inflater)
        binding.cpTitle.text = message

        // Customize progress bar
        binding.cpPbar.indeterminateDrawable.setColorFilter(
            ContextCompat.getColor(context, R.color.white),
            PorterDuff.Mode.SRC_IN
        )
        // Card Color
        binding.cpCardview.setCardBackgroundColor(Color.parseColor("#70000000"))

        // Progress Bar Color
        setColorFilter(
            binding.cpPbar.indeterminateDrawable,
            ResourcesCompat.getColor(context.resources, R.color.white, null)
        )

        // Customize message text color
        binding.cpTitle.setTextColor(Color.WHITE)

        dialog = CustomDialog(context)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.show()

        return dialog
    }

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {

            //drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_ATOP)
            DrawableCompat.setTint(drawable, color)
        }
    }

    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            // Set Semi-Transparent Color for Dialog Background
            window?.decorView?.rootView?.setBackgroundResource(R.color.dialogBackground)
            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.consumeSystemWindowInsets()
            }
        }
    }

    fun dismiss() {
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }
}