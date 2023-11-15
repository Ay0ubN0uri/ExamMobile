package com.nouri.exam.ui.fragments.updateperson

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.a00n.universityapp.utils.Constants
import com.android.volley.toolbox.ImageRequest
import com.google.android.material.snackbar.Snackbar
import com.nouri.exam.data.entities.Gender
import com.nouri.exam.data.entities.Person
import com.nouri.exam.data.remote.MyRequestQueue
import com.nouri.exam.databinding.FragmentPersonUpdateBinding
import com.nouri.exam.ui.fragments.person.PersonViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class PersonUpdateFragment : Fragment() {


    private lateinit var viewModel: PersonUpdateViewModel
    private lateinit var personViewModel: PersonViewModel
    private var _binding: FragmentPersonUpdateBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<PersonUpdateFragmentArgs>()
    private var imgUri: Uri? = null
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            imgUri = it
            binding.personImageView.setImageURI(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonUpdateBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[PersonUpdateViewModel::class.java]
        personViewModel = ViewModelProvider(this)[PersonViewModel::class.java]
        binding.person = args.person
        val genders = Gender.values().map { it.name }
        binding.genderSpinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genders);
        binding.genderSpinner.setSelection(genders.indexOf(args.person.gender.name))
        val request = ImageRequest(args.person.url, { response ->
            Log.i("info", "onCreateView: $response")
            binding.personImageView.setImageBitmap(response)
        },
            0, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.ARGB_8888, {
                Log.i("info", "onCreateView: ${it.message}")
            })
        MyRequestQueue.getInstance(binding.root.context).addToRequestQueue(request)
//        Glide.with(this)
//            .load(args.person.url)
//            .centerCrop()
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .diskCacheStrategy(DiskCacheStrategy.NONE)
//            .skipMemoryCache(true)
//            .into(binding.personImageView)
        binding.personImageView.setOnClickListener {
            resultLauncher.launch("image/*")
        }
        personViewModel.personUpdated.observe(viewLifecycleOwner){
            it.let { udpated ->
                var msg = "Error updating a new person"
                if (udpated) {
                    msg = "Person Updated Successfully"
                }
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
                    .show()
                findNavController().navigateUp()
            }
        }
        binding.addPersonBtn.setOnClickListener {
            if (binding.personNameEditText.text.isEmpty()) {
                Snackbar.make(binding.root, "Name is empty ", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
//            if (!isImageValid()) {
//                Snackbar.make(binding.root, "please choose an image", Snackbar.LENGTH_LONG).show()
//                return@setOnClickListener
//            }
            val gender = Gender.valueOf(binding.genderSpinner.selectedItem as String)
//            val bitmap = when (binding.personImageView.drawable) {
//                is BitmapDrawable -> binding.personImageView.drawable as BitmapDrawable
//                is TransitionDrawable -> (binding.personImageView.drawable as TransitionDrawable).getDrawable(
//                    0
//                ) as BitmapDrawable
//
//                else -> null
//            }
//            val c = binding.personImageView.drawable as ColorDrawable
//            val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
//            bitmap.setPixel(0, 0, c.color)
            val person = Person(
                args.person.id, binding.personNameEditText.text.toString(), LocalDate.parse(
                    binding.personBirthdayTextView.text.toString(),
                    DateTimeFormatter.ofPattern("yyyy-MM-d")
                ), gender,
                Constants.convertDrawableToByteArray(binding.personImageView.drawable), ""
            )
            Log.i("info", "onCreateView: all is good $person")
            personViewModel.updatePerson(person)
        }
        val today = Calendar.getInstance()
        binding.birthdayButton.setOnClickListener {
            val dp = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = LocalDate.of(year, month + 1, dayOfMonth) // Month is 0-based
                    val today = LocalDate.now()

                    if (selectedDate.isBefore(today)) {
                        binding.personBirthdayTextView.text = "$year-${month + 1}-$dayOfMonth"
                    } else {
                        Snackbar.make(
                            binding.root,
                            "Please select a date before today",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                },
                args.person.birthday.year,
                args.person.birthday.monthValue,
                args.person.birthday.dayOfMonth
            )
            dp.show()
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}