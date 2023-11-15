package com.nouri.exam.ui.fragments.person

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.a00n.universityapp.utils.SwipeGesture
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.nouri.exam.R
import com.nouri.exam.databinding.FragmentPersonBinding
import com.nouri.exam.ui.adapters.PersonAdapter
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@RequiresApi(Build.VERSION_CODES.O)
class PersonFragment : Fragment() {

    private lateinit var viewModel: PersonViewModel
    private var _binding: FragmentPersonBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { PersonAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[PersonViewModel::class.java]
        binding.personRecycleView.adapter = adapter
        toggleViews(false)
        binding.emptyListLinearLayout.visibility = View.GONE
        showShimmer()
        lifecycleScope.launch {
            Glide.get(requireContext()).clearMemory()
        }
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_nav_person_to_personAddFragment)
        }
        viewModel.personAdded.observe(viewLifecycleOwner) {
            it.let { added ->
                var msg = "Error adding a new person"
                if (added) {
                    msg = "Person Added Successfully"
                }
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
        viewModel.personUpdated.observe(viewLifecycleOwner) {
            it.let { added ->
                var msg = "Error updating a new person"
                if (added) {
                    msg = "Person Updated Successfully"
                }
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
        viewModel.getPersonsList().observe(viewLifecycleOwner) { persons ->
            Handler(Looper.getMainLooper()).postDelayed({
                hideShimmer()
                if (persons != null) {
                    Log.i("info", "onCreateView: $persons")
                    if (persons.isEmpty()) {
                        toggleViews(true)
                    } else {
                        toggleViews(false)
                    }
                    adapter.submitList(persons)
                } else {
                    adapter.submitList(null)
                    toggleViews(true)
                    Log.i("info", "onCreateView: Error has occured")
                }
            }, 1000)
        }
        viewModel.fetchPersons()
        addSwipeDelete()
        return binding.root
    }

    private fun addSwipeDelete() {
        val swipeGesture = object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val originalList = adapter.currentList
                val list = originalList.toMutableList()
                list.removeAt(viewHolder.adapterPosition)
                adapter.submitList(list)
                val snackbar = Snackbar.make(
                    binding.root,
                    "Person deleted successfully.",
                    Snackbar.LENGTH_LONG
                )
                snackbar.setAction("Undo") {
                    adapter.submitList(originalList)
//                    toggleViews(false)
                }
                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        Log.i("info", "onDismissed: $event")
                        if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_CONSECUTIVE) {
                            val diff = (originalList.toSet() subtract list.toSet()).toList()
                            if (diff.isNotEmpty()) {
                                viewModel.deletePerson(diff[0])
                                Log.i("info", "deleting: ${diff[0]}")
                            }
                        }
                    }
                })
                if (list.isEmpty()) {
                    toggleViews(true)
                }
                snackbar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeGesture)
        itemTouchHelper.attachToRecyclerView(binding.personRecycleView)
    }

    private fun toggleViews(visible: Boolean) {
        binding.emptyListLinearLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }


    private fun showShimmer() = binding.personRecycleView.showShimmer()
    private fun hideShimmer() = binding.personRecycleView.hideShimmer()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    fun convertDrawableToByteArray(drawable: Drawable?): ByteArray {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

}


//Log.i("info", "onCreateView: $it")
//val url = person[0].url
//val request = ImageRequest(url, { response ->
//    Log.i("info", "onCreateView: $response")
//    binding.imgView.setImageBitmap(response)
//},
//    0, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.ARGB_8888, {
//        Log.i("info", "onCreateView: ${it.message}")
//    })
//MyRequestQueue.getInstance(requireContext()).addToRequestQueue(request)
//binding.nv.setImageUrl(person[0].url,MyRequestQueue(requireContext()).imageLoader)
//Glide.with(this@PersonFragment)
//.load(person.get(0).url)
//.centerCrop()
//.transition(DrawableTransitionOptions.withCrossFade())
//.placeholder(R.drawable.empty_list)
//.into(binding.imgView)
//binding.imgView.load(person.get(0).url){
//    this.crossfade(600)
//    this.error(R.drawable.empty_list)
//}


//        binding.nv.setDefaultImageResId(R.drawable.empty_list)
//        viewModel.getPersonsList().observe(viewLifecycleOwner) {
//            it?.let { person ->
//                Log.i("info", "onCreateView: $it")
//            }
//        }
//        viewModel.fetchPersons()


//        val byteArray = convertDrawableToByteArray(resources.getDrawable(R.drawable.empty_list))
//        val request = MultipartRequest(
//            "http://192.168.43.106:8080/api/v1/persons/2/upload",
//            resources.getDrawable(R.drawable.end),
//            listener = {
//                Log.i("info", "onCreateView: $it")
//            },
//            errorListener = {
//                Log.i("info", "onCreateView: ${it.message}")
//            }
//        )
//        MyRequestQueue.getInstance(requireContext()).addToRequestQueue(request)