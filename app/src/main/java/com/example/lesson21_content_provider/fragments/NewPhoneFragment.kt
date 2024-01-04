package com.example.lesson21_content_provider.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import com.example.lesson21_content_provider.databinding.FragmentNewPhoneBinding

class NewPhoneFragment(private val resultName: String) : Fragment() {
    private lateinit var _binding: FragmentNewPhoneBinding
    private var name: String = ""
    private var phone: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveBtnChangeState()
        initListeners()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewPhoneBinding.inflate(LayoutInflater.from(this.requireContext()))
        return _binding.root
    }

    private fun saveBtnChangeState() {
        _binding.saveBtn.isEnabled = name != "" && phone != ""
    }

    private fun initListeners() {
        _binding.nameEdit.addTextChangedListener {
            name = it.toString()
            saveBtnChangeState()
        }

        _binding.phoneEdit.addTextChangedListener {
            phone = it.toString()
            saveBtnChangeState()
        }

        _binding.saveBtn.setOnClickListener {
            setFragmentResult(resultName, Bundle().apply {
                putString("name", name)
                putString("phone", phone)
            })
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(resultName: String) = NewPhoneFragment(resultName)
    }
}