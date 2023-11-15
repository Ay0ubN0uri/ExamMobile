package com.nouri.exam.ui.adapters

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.nouri.exam.data.entities.Employe

class EmployeMultiSelectDropDownAdapter(private var data: MutableList<Employe> = mutableListOf<Employe>()) :
    BaseAdapter(),
    Filterable {
    private var originalData: List<Employe> = ArrayList(data)
    var selectedItems = mutableSetOf<Employe>()
    private val employeeFilter = EmployeFilter()

    fun setOriginalData(employees: List<Employe>) {
        originalData = employees
    }


    fun setData(employees: MutableList<Employe>) {
        data = employees
        originalData = data
    }

    override fun getCount(): Int {
        return data.size // Return the number of items in the filtered data
    }

    override fun getItem(position: Int): Employe {
        return data[position] // Get an item from the filtered data at a given position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong() // Get the ID of an item in the filtered data at a given position
    }

    // Method responsible for creating and updating the view for each item in the dropdown
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_dropdown_item_1line, parent, false)

        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text =
            getItem(position).nom // Set the text of the dropdown item to the current item in the filtered data
        return view
    }


    override fun getFilter(): Filter {
        return employeeFilter
    }

    private inner class EmployeFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            Log.i("info", "ay0ub: $originalData")
            val filteredResults = mutableListOf<Employe>()
            val results = FilterResults()
            if (TextUtils.isEmpty(constraint)) {
                filteredResults.addAll(originalData)
            } else {
                Log.i("info", "performFiltering: $selectedItems")
                for (item in originalData) {
                    if (!selectedItems.contains(item) && item.nom.toLowerCase()
                            .contains(constraint.toString().toLowerCase())
                    ) {
                        // Add items to the filtered list that match the constraint and are not selected yet
                        filteredResults.add(item)
                    }
                }
            }
            results.values = filteredResults
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val filteredList = results?.values as? List<Employe>
            Log.i("info", "publishResults: $filteredList")
            if (filteredList != null) {
                data.clear()
                data.addAll(filteredList)
                notifyDataSetChanged()
            }
        }

    }
}