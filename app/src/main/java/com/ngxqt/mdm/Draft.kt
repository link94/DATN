package com.ngxqt.mdm

class Draft {
    /*private fun getAllEquipments(){
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getAllEquipments(it) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getAllEquipmentsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        equipmentsAdapter.submitList(it.data?.data)
                        binding.tvEquipmentsError.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.tvEquipmentsError.visibility = View.VISIBLE
                        binding.tvEquipmentsError.setText("ERROR ${it.message}\nHÃY THỬ KIỂM TRA KẾT NỐI INTERNET")
                        Log.e("GETALLEQUIP_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }

    private fun searchEquipments(keyword: String) {
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.searchEquipments(it,keyword) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.searchEquipmentsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        binding.tvEquipmentsError.visibility = View.GONE
                        val data = it.data?.data
                        if (data?.isNotEmpty() == true){
                            binding.recyclerViewEquipments.adapter = equipmentsAdapter
                            equipmentsAdapter.submitList(data)
                            binding.tvEquipmentsError.visibility = View.GONE
                        }else{
                            Toast.makeText(requireContext(), "Không Tìm Thấy Thiết Bị Chứa Từ Khóa", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.tvEquipmentsError.visibility = View.VISIBLE
                        binding.tvEquipmentsError.setText("ERROR ${it.message}\nHÃY THỬ KIỂM TRA KẾT NỐI INTERNET")
                        Log.e("GETALLEQUIP_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }

    private fun statisticalEquipments(status: String) {
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            binding.paginationProgressBar.visibility = View.VISIBLE
            userPreferences.accessTokenString()?.let { viewModel.statisticalEquipments(it,status) }
        }
        //Get LiveData
        viewModel.statisticalEquipmentsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        binding.tvEquipmentsError.visibility = View.GONE
                        val data = it.data?.equipment
                        if (data?.isNotEmpty() == true){
                            equipmentsAdapter.submitList(data)
                            binding.tvEquipmentsError.visibility = View.GONE
                        }else{
                            Toast.makeText(requireContext(), "Không Tìm Thấy Thiết Bị Chứa Từ Khóa", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.tvEquipmentsError.visibility = View.VISIBLE
                        binding.tvEquipmentsError.setText("ERROR ${it.message}\nHÃY THỬ KIỂM TRA KẾT NỐI INTERNET")
                        Log.e("GETALLEQUIP_OBSERVER_ERROR", it.data.toString())
                    }
                    is Resource.Loading -> {
                        binding.paginationProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }*/

    /*private fun searchEquipments(keyword: String) {
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.searchEquipments(it,keyword) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.searchEquipmentsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        binding.tvBrokenError.visibility = View.GONE
                        onResponseSuccess(it.data?.data)
                    }
                    is Resource.Error -> {
                        binding.tvBrokenError.visibility = View.VISIBLE
                        binding.tvBrokenError.setText("ERROR\n${it.message}")
                        Log.e("GETALLEQUIP_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }*/

    /*private fun onResponseSuccess(data: MutableList<Equipment>?) {
        if (data?.isNotEmpty() == true){
            val equipments: MutableList<Equipment>? = mutableListOf()
            for (item in data){
                if (item.status == "active"){
                    equipments?.add(item)
                }
            }
            if (equipments?.isNotEmpty() == true){
                equipmentsAdapter.submitList(equipments)
            }
            else{
                Toast.makeText(requireContext(), "Hãy Nhập Thiết Bị Còn Đang Sử Dụng", Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(requireContext(), "Không Tìm Thấy Thiết Bị Chứa Từ Khóa", Toast.LENGTH_SHORT).show()
        }
    }*/

    /*private val exceptionHandler = CoroutineExceptionHandler {_,throwable ->
        Log.e("GETALLEQUIP_API_ERROR","exception handler ${throwable.message}")
        _getAllEquipmentsResponseLiveData.postValue(Event(Resource.Error(throwable.message.toString())))
    }
    fun getAllEquipments(authorization: String) = viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
        safeGetAllEquipments(authorization)
    }
    private suspend fun safeGetAllEquipments(authorization: String) {
        val response = mdmRepository.getAllEquipments(authorization)
        _getAllEquipmentsResponseLiveData.postValue(Event(handleGetAllUsersResponse(response)))
    }*/

    /*private fun getAllUsers(){
        // Call API Get All User
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getAllUsers(it) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getAllUsersResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        staffAdapter.submitList(it.data?.data)
                        binding.tvStaffError.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.tvStaffError.visibility = View.VISIBLE
                        binding.tvStaffError.setText("ERROR\n${it.message}")
                        Log.e("GETALLUSERS_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }*/
}