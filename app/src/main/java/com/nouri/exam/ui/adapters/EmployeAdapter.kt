package com.nouri.exam.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nouri.exam.R
import com.nouri.exam.data.entities.Employe
import com.nouri.exam.databinding.EmployeItemBinding

class EmployeAdapter(val onClickListener: (View, Employe) -> Unit) :
    ListAdapter<Employe, EmployeAdapter.EmployeViewHolder>(EmployeItemDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeViewHolder {
        val binding = EmployeItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EmployeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class EmployeViewHolder(private val binding: EmployeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(employe: Employe) {
            binding.employeMaterialCardView.setOnClickListener { view ->
                this@EmployeAdapter.onClickListener(view, employe)
            }
            Glide.with(binding.root)
                .load(employe.url)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.default_image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.employeImageView)
            var str = "No Manager"
            if(employe.chef != null){
                str ="${employe.chef.nom} ${employe.chef.prenom}"
            }
            binding.employeManagerTextView.text = str
            binding.employeListTextView.text = if (employe.employees.isEmpty()) "No employes" else employe.employees.joinToString(", ") { "${it.nom} ${it.prenom}" }
            binding.employe = employe
        }
    }
}