package com.ngxqt.mdm.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.ui.model.HomeItemModel

class HomeGridAdapter(context: Context, itemModelArrayList: ArrayList<HomeItemModel?>?) :
    ArrayAdapter<HomeItemModel?>(context, 0, itemModelArrayList!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var listitemView = convertView
        if (convertView== null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(context).inflate(R.layout.item_home, parent, false)

        }

        val homeItemModel: HomeItemModel? = getItem(position)
        val courseTV = listitemView!!.findViewById<TextView>(R.id.idItemText)
        val courseIV = listitemView.findViewById<ImageView>(R.id.idItemLogo)

        courseTV.setText(homeItemModel!!.itemName)
        courseIV.setImageResource(homeItemModel.itemLogo!!)
        return listitemView
    }
}