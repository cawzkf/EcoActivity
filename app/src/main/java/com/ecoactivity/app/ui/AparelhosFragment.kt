package com.ecoactivity.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ecoactivity.app.databinding.FragmentAparelhosBinding

class AparelhosFragment : Fragment() {

    private var _binding: FragmentAparelhosBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val AparelhosViewModel =
            ViewModelProvider(this).get(AparelhosViewModel ::class.java)

        _binding = FragmentAparelhosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAparelhos
        AparelhosViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}