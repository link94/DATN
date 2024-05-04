package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.RequestEquipmentInventoryPost
import com.ngxqt.mdm.databinding.FragmentInventoryNoteBinding
import com.ngxqt.mdm.ui.viewmodels.InventoryNoteViewModel
import com.ngxqt.mdm.util.BASE_URL_KA
import com.ngxqt.mdm.util.BiometricHelper
import com.ngxqt.mdm.util.BiometricHelper.initBiometric
import com.ngxqt.mdm.util.Resource
import com.ngxqt.mdm.util.observeOnce
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class InventoryNoteFragment : Fragment(), BiometricHelper.BiometricCallback {
    private val viewModel: InventoryNoteViewModel by viewModels()
    private var _binding: FragmentInventoryNoteBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<InventoryNoteFragmentArgs>()
    private var inventoryNote = false
    private lateinit var biometricPrompt: BiometricPrompt
    private var isTurnedOn: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInventoryNoteBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        biometricPrompt = initBiometric(requireActivity(), this)
        setEquipmentDetail()
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Kiếm Kê")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setEquipmentDetail(){
        UserPreferences(requireContext()).accessSettingBiometric.asLiveData().observeOnce(viewLifecycleOwner, Observer {
            isTurnedOn = it
        })
        val equipment = args.equipment
        binding.apply {
            val imgPath = equipment.path ?: equipment.urlImg?.substringAfterLast("/") ?: ""
            Glide.with(root)
                .load(BASE_URL_KA +"/public/uploads/"+imgPath)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.logo)
                .into(equipDetailImage)
            equipDetailTitle.text = equipment.title
            equipDetailStatus.text = equipment.status.let {
                if (it == "active") {"Đang Sử Dụng"}
                else if (it == "was_broken") {"Đang Báo Hỏng"}
                else if (it == "corrected") {"Đang Sửa Chữa"}
                else if (it == "liquidated") {"Đã Thanh Lý"}
                else if (it == "inactive") {"Ngừng Sử Dụng"}
                else if (it == "not_handed") {"Mới"}
                else {""}
            }
            equipDetailModel.text = equipment.model
            equipDetailSerial.text = equipment.serial
            equipDetailYearManufacture.text = equipment.yearManufacture
            equipDetailYearUse.text = equipment.yearUse
            equipDetailManufacturer.text = equipment.manufacturer
            equipDetailOrigin.text = equipment.origin
            if (equipment.status == "active"){
                equipDetailStatusCardview.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
            }
            btnInventory.setOnClickListener {
                val note = binding.editTextNote.text.toString().trim()
                if (note.isNotEmpty()){
                    if (isTurnedOn == true) BiometricHelper.authenticate(biometricPrompt)
                    else equipment.id?.let { requestInventory(it, note) }
                } else{
                    Toast.makeText(requireContext(), "Vui Lòng Nhập Ghi Chú Kiểm Kê", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun requestInventory(id: Int, note: String) {
        //call api
        val calendar = Calendar.getInstance()
        val date = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        val requestEquipmentInventoryPost = RequestEquipmentInventoryPost(date, note)
        val userPreferences = UserPreferences(requireContext())
        if (inventoryNote == false) {
            lifecycleScope.launch {
                userPreferences.accessTokenString()?.let { viewModel.inventoryNote(it,id, requestEquipmentInventoryPost) }
                binding.paginationProgressBar.visibility = View.VISIBLE
            }
        } else {
            Toast.makeText(requireContext(),"Đã Kiểm Kê Thiết Bị", Toast.LENGTH_SHORT).show()
        }
        //lắng nghe livedate response
        viewModel.inventoryNoteResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when (it) {
                    is Resource.Success -> {
                        if (it.data?.status == 200){
                            Toast.makeText(requireContext(),"Kiểm Kê Thành Công", Toast.LENGTH_SHORT).show()
                            inventoryNote =  true
                            binding.tvInventoryNoteError.visibility = View.GONE
                        } else {
                            Toast.makeText(requireContext(),"Kiểm Kê Thất Bại", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.tvInventoryNoteError.visibility = View.VISIBLE
                        binding.tvInventoryNoteError.setText("ERROR\n${it.message}")
                        Log.e("INVENTORYNOTE_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }

    override fun onAuthenticationSuccess() {
        Log.d("InventoryNoteFragment","onAuthenticationSuccess")
        val note = binding.editTextNote.text.toString().trim()
        args.equipment.id?.let { requestInventory(it, note) }
    }

    override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
        Toast.makeText(requireContext(), "$errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(requireContext(), "Xác thực thất bại", Toast.LENGTH_SHORT).show()
    }
}