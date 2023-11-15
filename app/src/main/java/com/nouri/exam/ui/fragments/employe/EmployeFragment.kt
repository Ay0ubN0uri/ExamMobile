package com.nouri.exam.ui.fragments.employe

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.net.Uri
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.viewbinding.ViewBinding
import com.a00n.universityapp.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.nouri.exam.R
import com.nouri.exam.data.entities.Employe
import com.nouri.exam.data.entities.Service
import com.nouri.exam.databinding.EmployeSaveEditBinding
import com.nouri.exam.databinding.FragmentEmployeBinding
import com.nouri.exam.ui.adapters.EmployeAdapter
import com.nouri.exam.ui.adapters.EmployeDropDownAdapter
import com.nouri.exam.ui.adapters.EmployeMultiSelectDropDownAdapter
import com.nouri.exam.ui.adapters.ServiceDropDownAdapter
import java.time.LocalDate
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class EmployeFragment : Fragment() {

    private lateinit var viewModel: EmployeViewModel
    private var _binding: FragmentEmployeBinding? = null
    private val binding get() = _binding!!
    private lateinit var popupBinding: EmployeSaveEditBinding
    private var imgUri: Uri? = null
    private val employeeAdapter by lazy { EmployeDropDownAdapter(mutableListOf()) }
    private val serviceAdapter by lazy { ServiceDropDownAdapter(mutableListOf()) }
    private var selectedManager: Employe? = null
    private var selectedService: Service? = null
    private lateinit var employeeMultiSelectDropDownAdapter: EmployeMultiSelectDropDownAdapter
    private var selectedEmployees = mutableSetOf<Employe>()
    private var dropdownEmployees = mutableListOf<Employe>()
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            imgUri = it
            popupBinding.personImageView.setImageURI(it)
        }
    }

    private val adapter by lazy {
        EmployeAdapter(
            onClickListener = { _, employe ->

            }
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmployeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[EmployeViewModel::class.java]
        binding.employeeRecycleView.adapter = adapter
        toggleViews(false)
        binding.emptyListLinearLayout.visibility = View.GONE
        showShimmer()
        viewModel.employeeAdded.observe(viewLifecycleOwner) {
            it.let { added ->
                var msg = "Error adding a new employee"
                if (added) {
                    msg = "Person Added Successfully"
                }
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
        binding.fab.setOnClickListener {
            showDialog(
                layoutToShow = R.layout.employe_save_edit,
                dialogTitle = "Add Employe",
                onCreateDialog = { dialogBinding ->
                    val today = Calendar.getInstance()
                    popupBinding = dialogBinding as EmployeSaveEditBinding
                    popupBinding.personImageView.setOnClickListener {
                        resultLauncher.launch("image/*")
                    }
                    popupBinding.birthdayButton.setOnClickListener {
                        val dp = DatePickerDialog(
                            requireContext(),
                            { _, year, month, dayOfMonth ->
                                val selectedDate =
                                    LocalDate.of(year, month + 1, dayOfMonth) // Month is 0-based
                                val today = LocalDate.now()

                                if (selectedDate.isBefore(today)) {
                                    popupBinding.employeBirthdayTextView.text =
                                        "$year-${month + 1}-$dayOfMonth"
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
                    popupBinding.managerAutoCompleteTextView.setAdapter(employeeAdapter)
                    popupBinding.serviceAutoCompleteTextView.setAdapter(serviceAdapter)
                    popupBinding.managerAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                        selectedManager =
                            employeeAdapter.getItem(position)
                    }
                    popupBinding.serviceAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                        selectedService =
                            serviceAdapter.getItem(position)
                    }
                },
                onPositiveButton = { dialogBinding, _, _ ->
                    val popupBinding = dialogBinding as EmployeSaveEditBinding
                    val employee = Employe(
                        nom = popupBinding.employeeNomEditText.text.toString(),
                        prenom = popupBinding.employeePrenomEditText.text.toString(),
                        dateNaissance = LocalDate.of(2001, 10, 10),
                        image = Constants.convertDrawableToByteArray(popupBinding.personImageView.drawable),
                        url = "",
                        service = selectedService,
                        chef = selectedManager,
                        employees = selectedEmployees.toList(),
                    )
                    Log.i("info", "onCreateView: $employee")
//                    val f = adapter.currentList
//                    f.add(employee)
//                    adapter.submitList(f)
                    viewModel.addEmploye(employee)
                })
        }
        viewModel.getEmployesList().observe(viewLifecycleOwner) { employees ->
            Handler(Looper.getMainLooper()).postDelayed({
                hideShimmer()
                if (employees != null) {
                    Log.i("info", "onCreateView: $employees")
                    if (employees.isEmpty()) {
                        toggleViews(true)
                    } else {
                        toggleViews(false)
                    }
                    adapter.submitList(employees)
//                    adapter.originalList = employees.toList()
                    employeeAdapter.setData(employees.toMutableList())
                    dropdownEmployees = employees.toMutableList()
                } else {
                    adapter.submitList(null)
                    toggleViews(true)
                    Log.i("info", "onCreateView: Error has occured")
                }
            }, 1000)
        }
        viewModel.getServicesList().observe(viewLifecycleOwner) { services ->
            if (services != null) {
                Log.i("info", "services: $services")
                if (services.isEmpty()) {
                    toggleViews(true)
                } else {
                    toggleViews(false)
                }
                serviceAdapter.setData(services.toMutableList())
            } else {
                adapter.submitList(null)
                toggleViews(true)
                Log.i("info", "onCreateView: Error has occured")
            }
        }
        viewModel.fetchEmployes()
        viewModel.fetchServices()
        return binding.root
    }


    private fun showDialog(
        layoutToShow: Int,
        dialogTitle: String,
        negativeButtonText: String = "Cancel",
        positiveButtonText: String = "Add",
        onNegativeButton: ((DialogInterface, Int) -> Unit)? = null,
        onPositiveButton: (ViewBinding, DialogInterface, Int) -> Unit,
        onCreateDialog: ((ViewBinding) -> Unit)? = null
    ) {
        val popup = LayoutInflater.from(requireContext())
            .inflate(layoutToShow, null, false)
        val popupBinding = EmployeSaveEditBinding.bind(popup)
        if (onCreateDialog != null) {
            onCreateDialog(popupBinding)
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(dialogTitle)
            .setView(popup)
            .setNegativeButton(negativeButtonText, onNegativeButton)
            .setPositiveButton(positiveButtonText) { arg1, arg2 ->
                onPositiveButton(popupBinding, arg1, arg2)
            }
            .create()
        dialog.show()
    }

    private fun toggleViews(visible: Boolean) {
        binding.emptyListLinearLayout.visibility = if (visible) View.VISIBLE else View.GONE
        binding.employeeRecycleView.visibility = if (visible) View.GONE else View.VISIBLE
    }

    private fun showShimmer() = binding.employeeRecycleView.showShimmer()
    private fun hideShimmer() = binding.employeeRecycleView.hideShimmer()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}