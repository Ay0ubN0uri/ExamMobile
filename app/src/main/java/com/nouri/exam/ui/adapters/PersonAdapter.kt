package com.nouri.exam.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nouri.exam.R
import com.nouri.exam.data.entities.Person
import com.nouri.exam.databinding.PersonItemBinding

class PersonAdapter :
    ListAdapter<Person, PersonAdapter.PersonViewHolder>(PersonItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val binding = PersonItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PersonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class PersonViewHolder(private val binding: PersonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(person: Person) {
//            val request = ImageRequest(person.url, { response ->
//                Log.i("info", "onCreateView: $response")
//                binding.personImageView.setImageBitmap(response)
//            },
//                0, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.ARGB_8888, {
//                    Log.i("info", "onCreateView: ${it.message}")
//                })
//            MyRequestQueue.getInstance(binding.root.context).addToRequestQueue(request)


//            binding.personImageView.load(person.url){
//                this.crossfade(600)
//                this.error(R.drawable.default_image)
//            }
            Glide.with(binding.root)
                .load(person.url)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
//                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
//                .timeout(60000)
                .into(binding.personImageView)
            binding.person = person
        }
    }
}