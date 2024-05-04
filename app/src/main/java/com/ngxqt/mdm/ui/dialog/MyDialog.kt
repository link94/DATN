package com.ngxqt.mdm.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ngxqt.mdm.R
import com.ngxqt.mdm.databinding.DialogBottomBinding
import com.ngxqt.mdm.ui.adapters.DialogRecyclerViewAdapter

class MyDialog () : DialogFragment(){
    private lateinit var listener: OnPickerItemSelectedListener
    private var dialogRecyclerViewAdapter: DialogRecyclerViewAdapter? = null
    private var title: String? = null
    private var items: MutableList<String>? = null

    constructor(adapter: DialogRecyclerViewAdapter, title: String, listener: OnPickerItemSelectedListener) : this() {
        this.dialogRecyclerViewAdapter = adapter
        this.title = title
        this.listener = listener
    }
    constructor(items: MutableList<String>, title: String, listener: OnPickerItemSelectedListener) : this() {
        this.items = items
        this.title = title
        this.listener = listener
    }

    companion object {
        const val FILTER_DIALOG = "filterEquipments"
    }

    private var _bindingDialog: DialogBottomBinding? = null
    private val bindingDialog get() = _bindingDialog!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var dialog: Dialog? = null
        if (tag.equals(FILTER_DIALOG)) dialog = filterEquipmentsDialog()
        dialog?.apply {
            show()
            window!!.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                attributes.windowAnimations = R.style.DialogAnimation
                setGravity(Gravity.BOTTOM)
            }
        }
        return dialog!!
    }

    private fun filterEquipmentsDialog(): Dialog? {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyDialogStyle)
        _bindingDialog = DialogBottomBinding.inflate(LayoutInflater.from(activity))
        builder.setView(bindingDialog.root)

        bindingDialog.apply {
            dialogTitle.setText("${title}")
            dialogPicker.maxValue = items?.size?.minus(1) ?: 0
            dialogPicker.displayedValues = items?.toTypedArray()
            dialogPicker.wrapSelectorWheel = false
            dialogOk.setOnClickListener {
                listener?.onPickerItemSelected(bindingDialog.dialogPicker.value)
                dialog?.dismiss()
            }
        }

        return builder.create()
    }

    interface OnPickerItemSelectedListener{
        //fun onPickerItemSelected(status: String?)
        fun onPickerItemSelected(position: Int)
    }
}