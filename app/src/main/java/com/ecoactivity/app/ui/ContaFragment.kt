package com.ecoactivity.app.ui.conta

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.ecoactivity.app.R
import com.ecoactivity.app.databinding.FragmentContaBinding
import com.ecoactivity.app.ui.ContaViewModel


class ContaFragment : Fragment() {

    private var _binding: FragmentContaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val ContaViewModel =
            ViewModelProvider(this).get(ContaViewModel::class.java)

        _binding = FragmentContaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textConta
        ContaViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}