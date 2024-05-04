package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.model.LoginPost
import com.ngxqt.mdm.data.model.LoginResponse
import com.ngxqt.mdm.databinding.FragmentLoginBinding
import com.ngxqt.mdm.ui.viewmodels.LoginViewModel
import com.ngxqt.mdm.util.Resource
import com.ngxqt.mdm.util.isEmailValid
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonLogin.setOnClickListener {
            onLogin()

        }
        viewModel.loginResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when (it) {
                    is Resource.Success -> {
                        binding.tvError.visibility = View.GONE
                        onLoginSuccess(it.data)
                    }
                    is Resource.Error -> {
                        binding.tvError.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        Log.e("LOGIN_OBSERVER_ERROR", it.message.toString())
                    }
                    is Resource.Loading -> {
                        binding.paginationProgressBar.visibility = View.VISIBLE
                    }
                }
            }

        })
    }

    private fun onLoginSuccess(data: LoginResponse?) {
        val token = data?.accessToken ?: ""
        val tokenType = data?.tokenType ?: ""
        val userInfo = data?.user
        if (token != ""){
            lifecycleScope.launch {
                viewModel.saveToken("$tokenType ${token.substringAfter("|")}")
                userInfo?.let { viewModel.saveUserInfo(it) }
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        } else {
            Toast.makeText(requireContext(), "Đăng Nhập Thất Bại", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onLogin() {
        val email = binding.editTextPhone.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        if (email.isNotEmpty() && password.isNotEmpty() && isEmailValid(email)) {
            val userLoginPost = LoginPost(email, password)
            viewModel.login(userLoginPost)
        } else {
            Toast.makeText(requireContext(), "Email Hoặc Mật Khẩu Không Đúng", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}