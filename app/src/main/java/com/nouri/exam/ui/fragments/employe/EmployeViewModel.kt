package com.nouri.exam.ui.fragments.employe

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.a00n.universityapp.utils.Constants
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nouri.exam.data.entities.Employe
import com.nouri.exam.data.entities.Service
import com.nouri.exam.data.remote.MyRequestQueue
import com.nouri.exam.ui.fragments.person.LocalDateDeserializer
import com.nouri.exam.utils.MultipartRequest
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class EmployeJson(
    val nom: String,
    val prenom: String,
    val dateNaissance: String,
    val service: IdOnly?,
    val chef: IdOnly?,
    val employees: List<IdOnly>
) {
    data class IdOnly(val id: Int)
}

@RequiresApi(Build.VERSION_CODES.O)
class EmployeViewModel(private val application: Application) : AndroidViewModel(application) {

    private val employeesList = MutableLiveData<List<Employe>?>()
    private val servicesList = MutableLiveData<List<Service>?>()
    val employeeAdded = MutableLiveData<Boolean>()
    val employeeUpdated = MutableLiveData<Boolean>()


    val gson =
        Gson().newBuilder().registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
            .create()

    fun getServicesList(): MutableLiveData<List<Service>?> {
        return servicesList
    }
    fun getEmployesList(): MutableLiveData<List<Employe>?> {
        return employeesList
    }

    fun convertEmployeToJson(employee: Employe): EmployeJson {
        return EmployeJson(
            nom = employee.nom,
            prenom = employee.prenom,
            dateNaissance = employee.dateNaissance.toString(),
            chef = employee.chef?.let { EmployeJson.IdOnly(it.id) },
            service = employee.service?.let { EmployeJson.IdOnly(it.id) },
            employees = employee.employees.map { EmployeJson.IdOnly(it.id) }
        )
    }

    fun addEmploye(employee: Employe) {
        val url: String = Constants.EMPLOYEE_URL
        val jsonEmploye = gson.toJson(convertEmployeToJson(employee))
        Log.i("a00n", "addEmploye: $jsonEmploye")
        val jsonobj = JSONObject()
        jsonobj.apply {
            put("nom", employee.nom)
            put("prenom", employee.nom)
            put("dateNaissance", "2023-10-10")
            put("chef", JSONObject().put("id", employee.chef?.id))
        }
        Log.i("a00n", "addEmploye: $jsonobj")
        val request = JsonObjectRequest(
            Request.Method.POST, url, JSONObject(jsonEmploye),
            { response ->
                Log.i("info", "a00n: ${response.toString()}")
                val e: Employe =
                    gson.fromJson(response.toString(), object : TypeToken<Employe>() {}.type)
                Log.i("info", "fuck: ${employee.image.size}")
                val imgRequest = MultipartRequest(
//                    "${Constants.EMPLOYEE_URL}/${e.id}/upload",
                    "http://192.168.43.106:8080/api/v1/employes/${e.id}/upload",
                    employee.image,
                    listener = {
                        Log.i("info", "sucess: $it")
                        employeeAdded.value = true
                    },
                    errorListener = {
                        Log.i("info", "error: ${it.message}")
                    }
                )
                MyRequestQueue.getInstance(application.applicationContext)
                    .addToRequestQueue(imgRequest)
                fetchEmployes()
            },
            {
                employeeAdded.value = false
                Log.i("info", "Error: ${it.message}")
            }
        )
        MyRequestQueue.getInstance(application.applicationContext).addToRequestQueue(request)
    }

    fun fetchEmployes() {
        val url: String = Constants.EMPLOYEE_URL

        val stringReq = StringRequest(
            Request.Method.GET, url,
            { response ->
                val employees: List<Employe> = gson.fromJson(
                    response.toString(),
                    object : TypeToken<List<Employe>>() {}.type
                )
                employeesList.value = employees
            },
            {
                employeesList.value = null
                Log.i("info", "getUsers: ${it.message}")
            }
        )
        MyRequestQueue.getInstance(application.applicationContext).addToRequestQueue(stringReq)
    }

    fun fetchServices() {
        val url: String = Constants.SERVICE_URL

        val stringReq = StringRequest(
            Request.Method.GET, url,
            { response ->
                val services: List<Service> = gson.fromJson(
                    response.toString(),
                    object : TypeToken<List<Service>>() {}.type
                )
                servicesList.value = services
            },
            {
                employeesList.value = null
                Log.i("info", "getUsers: ${it.message}")
            }
        )
        MyRequestQueue.getInstance(application.applicationContext).addToRequestQueue(stringReq)
    }
}