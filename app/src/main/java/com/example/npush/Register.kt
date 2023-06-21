package com.example.npush

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Register : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        loadSpinnerData("http://10.0.2.2:3000/group/get-groups")
        val btnRegister = findViewById<Button>(R.id.btn_registrarse)
        val etUsuario = findViewById<EditText>(R.id.et_username)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val spGrupo = findViewById<Spinner>(R.id.spinner)
        btnRegister.setOnClickListener {
            registerUser(etUsuario.text.toString().trim(),etPassword.text.toString().trim(), spGrupo.selectedItem.toString())
        }
    }


    private fun registerUser(user: String, password: String, grupo: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result

            val url = "http://10.0.2.2:3000/group/set-user"
            val queue: RequestQueue = Volley.newRequestQueue(this)
            val postData = JSONObject()
            postData.put("TAG", token)
            postData.put("user", user)
            postData.put("password", password)
            postData.put("group", grupo)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, postData,
                Response.Listener { response ->
                    Toast.makeText(applicationContext, "Registro exitoso", Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(applicationContext, "Error en el registro", Toast.LENGTH_SHORT).show()
                }
            )

            queue.add(jsonObjectRequest)
        })
    }
    private fun loadSpinnerData(url: String) {
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val keysIterator = jsonObject.keys()
                    val namesList = ArrayList<String>()

                    while (keysIterator.hasNext()) {
                        val key = keysIterator.next()
                        Log.w("llave", key)
                        namesList.add(key)
                    }

                    updateSpinner(namesList)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        ) { error -> error.printStackTrace() }

        requestQueue.add(stringRequest)
    }

    private fun updateSpinner(data: ArrayList<String>) {
        val spinner: Spinner = findViewById(R.id.spinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        spinner.adapter = adapter
    }





}
