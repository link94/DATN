package com.ngxqt.mdm.ui.fragments

import android.Manifest
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
import com.budiyev.android.codescanner.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.databinding.FragmentScanBinding
import com.ngxqt.mdm.ui.viewmodels.EquipmentsViewModel
import com.ngxqt.mdm.util.Resource
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScanFragment : Fragment() {
    private val viewModel: EquipmentsViewModel by viewModels()
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner
    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()

    }

    private fun setToolbar(){
        //requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.VISIBLE
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).menu.findItem(R.id.menu_scan).isChecked = true
        binding.toolbar.toolbarTitle.setText("Scan")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun checkPermissions(){
        val rxPermissions = RxPermissions(this)

        disposable = rxPermissions
            .request(
                Manifest.permission.CAMERA
            )
            .subscribe { granted ->
                if (granted) {
                    binding.scannerView.visibility = View.VISIBLE
                    scanQR()
                } else {
                    Toast.makeText(requireContext(),"Hãy Chấp Thuận Quyền Truy Cập Camera",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun scanQR(){
        codeScanner = CodeScanner(requireActivity(), binding.scannerView)

        codeScanner.apply {
            // Parameters (default values)
            camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
            formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
            // ex. listOf(BarcodeFormat.QR_CODE)
            autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
            scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
            isAutoFocusEnabled = true // Whether to enable auto focus or not
            isFlashEnabled = false // Whether to enable flash or not

            // Callbacks
            decodeCallback = DecodeCallback {
                getActivity()?.runOnUiThread {
                    val intValue = it.toString().toIntOrNull()
                    if (intValue != null) {
                        searchEquipmentsById(intValue)
                    } else {
                        Toast.makeText(requireContext(), "Mã QR Không Chính Xác", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
                getActivity()?.runOnUiThread {
                    Toast.makeText(requireActivity(), "Camera initialization error: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        codeScanner.startPreview()
        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
            binding.tvScanError.visibility = View.INVISIBLE
        }
    }

    private fun searchEquipmentsById(equipmentId: Int) {
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.searchEquipmentsById(it,equipmentId) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.searchEquipmentsByIdResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        binding.paginationProgressBar.visibility = View.GONE
                        val equipment = it.data?.data
                        if (equipment != null){
                            binding.tvScanError.visibility = View.GONE
                            val action = ScanFragmentDirections.actionScanFragmentToEquipmentDetailFragment(equipment)
                            findNavController().navigate(action)
                            Log.d("SEARCHEQUIPBYID_SUCCESS", "OK")
                        }else{
                            Toast.makeText(requireContext(), "Không Tìm Thấy Thiết Bị Chứa Từ Khóa", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.tvScanError.visibility = View.VISIBLE
                        binding.tvScanError.setText("ERROR\n${it.message}")
                        Log.e("SEARCHEQUIPBYID_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }
}