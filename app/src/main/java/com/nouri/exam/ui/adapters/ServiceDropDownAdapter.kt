package com.nouri.exam.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.nouri.exam.R
import com.nouri.exam.data.entities.Service

class ServiceDropDownAdapter(private var data: MutableList<Service>) : BaseAdapter(),
    Filterable {
    fun setData(filieres: MutableList<Service>) {
        data = filieres
    }

    override fun getCount(): Int {
        return data.count()
    }

    override fun getItem(position: Int): Service {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.dropdown_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.dropdownTextView)
        textView.text = getItem(position).nom
        return view
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val res = FilterResults()
                res.values = data
                return res
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                Log.i("info", "publishResults: hello man")
            }

        }
    }
}