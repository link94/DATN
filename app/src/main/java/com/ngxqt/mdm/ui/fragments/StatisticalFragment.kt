package com.ngxqt.mdm.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.ngxqt.mdm.databinding.FragmentStatisticalBinding
import com.ngxqt.mdm.ui.viewmodels.StatisticalViewModel

class StatisticalFragment : Fragment() {
    private lateinit var binding: FragmentStatisticalBinding

    companion object {
        fun newInstance() = StatisticalFragment()
    }

    private val viewModel: StatisticalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticalBinding.inflate(inflater, container, false)
        return binding.root
    }

    //    val viewModel: StatisticalViewModel by viewModels() // Thay đổi thành ViewModel của bạn
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val viewModel: StatisticalViewModel by viewModels() // Thay đổi thành ViewModel của bạn
        val pieChart = binding.pieChart

        // Khởi tạo dữ liệu cho PieChart
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(40f, "Đang sửa chữa"))
        entries.add(PieEntry(10f, "Ngưng sử dụng"))
        entries.add(PieEntry(100f, "Đã thanh lý"))
        entries.add(PieEntry(529f, "Đang sử dụng"))
        entries.add(PieEntry(99f, "Đang báo hỏng"))
        entries.add(PieEntry(8f, "Mới"))


        // Khởi tạo dataset
        val dataSet = PieDataSet(entries, "Equiments")
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        dataSet.setValueTextColor(Color.BLACK)
        dataSet.setValueTextSize(16f)

        // Tạo PieData object từ dataSet
        val data = PieData(dataSet)
        pieChart.description.isEnabled = false
        pieChart.animateY(1000)
        pieChart.centerText = "Equipments"

        // Thiết lập dữ liệu cho PieChart
        pieChart.data = data

        // Hiển thị PieChart

        pieChart.setCenterTextSize(16f)
        pieChart.invalidate()
    }
}