package com.nouri.exam.ui.fragments.addperson

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.a00n.universityapp.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.nouri.exam.R
import com.nouri.exam.data.entities.Gender
import com.nouri.exam.data.entities.Person
import com.nouri.exam.databinding.FragmentPersonAddBinding
import com.nouri.exam.ui.fragments.person.PersonViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class PersonAddFragment : Fragment() {


    private lateinit var viewModel: PersonAddViewModel
    private lateinit var personViewModel: PersonViewModel
    private var _binding: FragmentPersonAddBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentPersonAddBinding.inflate(inflater, container, false)
        val genders = Gender.values().map { it.name }
        binding.genderSpinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genders);
        binding.personImageView.setOnClickListener {
            resultLauncher.launch("image/*")
        }
        viewModel = ViewModelProvider(this)[PersonAddViewModel::class.java]
        personViewModel = ViewModelProvider(this)[PersonViewModel::class.java]
        personViewModel.personAdded.observe(viewLifecycleOwner) {
            it.let { added ->
                var msg = "Error adding a new person"
                if (added) {
                    msg = "Person Added Successfully"
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
            if (!isImageValid()) {
                Snackbar.make(binding.root, "please choose an image", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val gender = Gender.valueOf(binding.genderSpinner.selectedItem as String)
            val person = Person(
                0, binding.personNameEditText.text.toString(), LocalDate.parse(
                    binding.personBirthdayTextView.text.toString(),
                    DateTimeFormatter.ofPattern("yyyy-MM-d")
                ), gender,
                Constants.convertDrawableToByteArray(binding.personImageView.drawable), ""
            )
            Log.i("info", "onCreateView: all is good $person")
            personViewModel.addPerson(person)
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
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            )
            dp.show()
        }
//        val today = Calendar.getInstance()
//        binding.personBirthdayDatePicker.init(
//            today.get(Calendar.YEAR), today.get(Calendar.MONTH),
//            today.get(Calendar.DAY_OF_MONTH)
//
//        ) { view, year, month, day ->
//            val month = month + 1
//            val msg = "You Selected: $day/$month/$year"
//            Log.i("info", "onCreateView: $msg")
//        }
        return binding.root
    }

    private fun isImageValid(): Boolean {
        val drawableToCompare = resources.getDrawable(R.drawable.upload_image_placeholder, null)
        val imageViewBitmap = (binding.personImageView.drawable as? BitmapDrawable)?.bitmap
        val drawableBitmap = (drawableToCompare as? BitmapDrawable)?.bitmap
        return if (imageViewBitmap != null && drawableBitmap != null) {
            if (areBitmapsEqual(imageViewBitmap, drawableBitmap)) {
                Log.i("info", "submitForm: The image in the ImageView and the Drawable are equal.")
                false
            } else {
                Log.i("info", "submitForm: The images are not equal.")
                true
            }
        } else {
            Log.i(
                "info",
                "submitForm: Handle cases where either the ImageView or the Drawable does not have a Bitmap."
            )
            false
        }
    }

    private fun areBitmapsEqual(bitmap1: Bitmap, bitmap2: Bitmap): Boolean {
        return bitmap1.sameAs(bitmap2)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}