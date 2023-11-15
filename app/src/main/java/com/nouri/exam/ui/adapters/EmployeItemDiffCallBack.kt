package com.nouri.exam.ui.adapters

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.nouri.exam.data.entities.Employe

class EmployeItemDiffCallBack : DiffUtil.ItemCallback<Employe>() {
    override fun areItemsTheSame(oldItem: Employe, newItem: Employe): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Employe, newItem: Employe): Boolean {
        return oldItem == newItem
    }
}