package com.example.npush

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnRegister = findViewById<Button>(R.id.btn_registrarse)
        val btnLogin = findViewById<Button>(R.id.btn_ingresar)
        val etUser = findViewById<EditText>(R.id.et_username)
        val etPassword = findViewById<EditText>(R.id.et_password)
        btnRegister.setOnClickListener{
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener{
            iniciarSesion(etUser.text.toString(),etPassword.text.toString())
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->

            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, msg)
        })

    }
    private fun iniciarSesion(user: String, password: String) {
        val url = "http://10.0.2.2:3000/group/login"
        val queue: RequestQueue = Volley.newRequestQueue(this)
        val postData = JSONObject()
        try {
            postData.put("user", user)
            postData.put("password", password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, postData,
            Response.Listener { response ->
                Toast.makeText(applicationContext, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Notifications::class.java)
                val grupo = response.getString("response")
                intent.putExtra("grupo",grupo)
                startActivity(intent)
            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext, "Error en el inicio de sesión", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(jsonObjectRequest)
    }

}
