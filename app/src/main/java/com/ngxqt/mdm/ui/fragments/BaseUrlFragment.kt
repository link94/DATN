package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.databinding.FragmentBaseUrlBinding
import com.ngxqt.mdm.ui.viewmodels.BaseUrlViewModel
import com.ngxqt.mdm.util.isUrlValid
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BaseUrlFragment : Fragment() {
    private val  viewModel: BaseUrlViewModel by viewModels()
    private var _binding: FragmentBaseUrlBinding? = null
    private val binding get() = _binding!!
    private var backPressedCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBaseUrlBinding.inflate(inflater, container, false)
        val view = binding.root
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BaseUrlFragment","onViewCreated")
        setBackPressed()
        binding.buttonSaveUrl.setOnClickListener {
            val baseUrl = binding.editTextBaseUrl.text.toString().trim()
            if (baseUrl.isNotEmpty() && isUrlValid(baseUrl)) saveBaseUrl(baseUrl)
            else Toast.makeText(requireContext(),"Địa Chỉ Trang Web Không Đúng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveBaseUrl(baseUrl: String) {
        lifecycleScope.launch {
            val saveDeferred = viewModel.saveBaseUrl(baseUrl)
            saveDeferred.await()
            Log.d("BaseUrlFragment", "RUN HERE")
            UserPreferences(requireContext()).accessBaseUrlString().let {
                Log.d("BaseUrlFragment", "token ${it}")
                if (it.isNullOrEmpty()) Toast.makeText(requireContext(),"BaseURL is ${it}", Toast.LENGTH_SHORT).show()
                else findNavController().navigate(R.id.action_baseUrlFragment5_to_loginFragment)
            }
        }
    }



    private fun setBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (backPressedCount == 1){
                requireActivity().finishAffinity()
            } else {
                backPressedCount++
                Toast.makeText(requireContext(), "Nhấn hai lần để thoát ứng dụng", Toast.LENGTH_SHORT).show()
            }
        }
    }
}