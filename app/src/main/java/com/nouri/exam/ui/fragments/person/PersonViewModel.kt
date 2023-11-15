package com.nouri.exam.ui.fragments.person

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.a00n.universityapp.utils.Constants
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.nouri.exam.data.entities.Person
import com.nouri.exam.data.remote.MyRequestQueue
import com.nouri.exam.utils.MultipartRequest
import org.json.JSONObject
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class LocalDateDeserializer : JsonDeserializer<LocalDate> {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate {
        val dateString = json?.asString
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(dateString, formatter)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
class PersonViewModel(private val application: Application) : AndroidViewModel(application) {

    private val personsList = MutableLiveData<List<Person>?>()
    val personAdded = MutableLiveData<Boolean>()
    val personUpdated = MutableLiveData<Boolean>()

    val gson =
        Gson().newBuilder().registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
            .create()


    fun getPersonsList(): MutableLiveData<List<Person>?> {
        return personsList
    }


    fun addPerson(person: Person) {
        val url: String = Constants.ADD_PERSON_URL
        val jsonPerson = gson.toJson(person)
        Log.i("info", "addPerson: $jsonPerson")
        val jsonobj = JSONObject()
        jsonobj.apply {
            put("name", person.name)
            put("birthday", person.birthday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            put("gender", person.gender.name)
        }
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonobj,
            { response ->
//                personAdded.value = true
                Log.i("info", "addPerson: ${response.toString()}")
                val p: Person =
                    gson.fromJson(response.toString(), object : TypeToken<Person>() {}.type)
                val imgRequest = MultipartRequest(
                    "${Constants.ALL_PERSONS_URL}/${p.id}/upload",
                    person.image,
                    listener = {
                        Log.i("info", "onCreateView: $it")
                        personAdded.value = true
                    },
                    errorListener = {
                        Log.i("info", "onCreateView: ${it.message}")
                    }
                )
                MyRequestQueue.getInstance(application.applicationContext)
                    .addToRequestQueue(imgRequest)
                fetchPersons()
            },
            {
                personAdded.value = false
                Log.i("info", "Error: ${it.message}")
            }
        )

        MyRequestQueue.getInstance(application.applicationContext).addToRequestQueue(request)
    }

    fun deletePerson(person: Person) {
        val url: String = "${Constants.DELETE_PERSON_URL}/${person.id}"
        val stringReq = StringRequest(
            Request.Method.DELETE, url,
            { response ->
                Log.i("info", "deletePerson: ${response.toString()}")
            },
            {
                Log.i("info", "Error: ${it.message}")
            }
        )
        MyRequestQueue.getInstance(application.applicationContext).addToRequestQueue(stringReq)
    }

    fun fetchPersons() {
        val url: String = Constants.ALL_PERSONS_URL

        val stringReq = StringRequest(
            Request.Method.GET, url,
            { response ->
//                val persons: List<Person> = Gson().fromJson(
//                    response.toString(),
//                    object : TypeToken<List<Person>>() {}.type
//                )
                val persons: List<Person> =
                    gson.fromJson(response.toString(), object : TypeToken<List<Person>>() {}.type)
                personsList.value = persons
            },
            {
                personsList.value = null
                Log.i("info", "getUsers: ${it.message}")
            }
        )
        MyRequestQueue.getInstance(application.applicationContext).addToRequestQueue(stringReq)
    }

    fun updatePerson(person: Person) {
        val url: String = "${Constants.UPDATE_PERSON_URL}/${person.id}"
        val jsonPerson = gson.toJson(person)
        val jsonobj = JSONObject()
        jsonobj.apply {
            put("name", person.name)
            put("birthday", person.birthday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            put("gender", person.gender.name)
        }
        val request = JsonObjectRequest(
            Request.Method.PUT, url, jsonobj,
            { response ->
                Log.i("info", "deletePerson: ${response.toString()}")
                val p: Person =
                    gson.fromJson(response.toString(), object : TypeToken<Person>() {}.type)
                val imgRequest = MultipartRequest(
                    "${Constants.ALL_PERSONS_URL}/${p.id}/upload",
                    person.image,
                    listener = {
                        Log.i("info", "onCreateView: $it")
                        personUpdated.value = true
                    },
                    errorListener = {
                        Log.i("info", "onCreateView: ${it.message}")
                    }
                )
                MyRequestQueue.getInstance(application.applicationContext)
                    .addToRequestQueue(imgRequest)
                fetchPersons()
            },
            {
                personUpdated.value = false
                Log.i("info", "Error: ${it.message}")
            }
        )
        MyRequestQueue.getInstance(application.applicationContext).addToRequestQueue(request)
    }
}