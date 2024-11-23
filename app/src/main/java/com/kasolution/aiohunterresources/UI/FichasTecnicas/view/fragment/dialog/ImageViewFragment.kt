package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.core.CustomPicasso
import com.kasolution.aiohunterresources.databinding.FragmentImageViewBinding
import com.squareup.picasso.Callback
import java.lang.Exception

class ImageViewFragment : DialogFragment() {
    private var _binding: FragmentImageViewBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageViewBinding.inflate(layoutInflater, container, false)
        // Carga la animación desde el archivo XML
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom)

        // Aplica la animación a la vista del fragmento
        binding.root?.startAnimation(anim)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            var imagelink = it.getString("imagelink")!!
            showImage(imagelink)
        }
        // Configurar la ventana del diálogo como transparente
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
    private fun showImage(imagen: String) {
        val customPicasso = CustomPicasso.getInstance(requireContext())
        customPicasso.load("https://drive.google.com/uc?export=view&id=${imagen}").into(
            binding.imgShowPicture,
            object : Callback {
                override fun onSuccess() {
                    binding.imgShowPicture.animate().alpha(1f).duration = 300
                }

                override fun onError(e: Exception?) {
                }
            })
    }

}