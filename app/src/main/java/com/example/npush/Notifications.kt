package com.example.npush

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.npush.R
import org.json.JSONObject

class Notifications : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notifications)
        val grupo = intent.getStringExtra("grupo")
        val listView = findViewById<ListView>(R.id.my_listview)
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2:3000/notification/get-notifications"
        val postData = JSONObject()
        postData.put("groupName",grupo)

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.POST,
            url,
            postData,
            Response.Listener<JSONObject> { response ->
                val list = ArrayList<String>()
                val iterator = response.keys()
                while (iterator.hasNext()) {
                    val key = iterator.next()
                    val item = response.getJSONObject(key)
                    // asume que cada objeto tiene un campo "mensaje" y "titulo"
                    val combined = item.getString("titulo") + "\n" + item.getString("mensaje")
                    list.add(combined)
                }
                val adapter = object : ArrayAdapter<String>(this@Notifications, android.R.layout.simple_list_item_1, list) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val textView = super.getView(position, convertView, parent) as TextView
                        val img = ContextCompat.getDrawable(context, R.drawable.avatar)
                        img?.setBounds(0, 0, 60, 60)
                        textView.setCompoundDrawables(img, null, null, null)

                        return textView
                    }
                }
                listView.adapter = adapter
            },
            Response.ErrorListener { error ->

            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                return postData.toString().toByteArray()
            }
        }

        queue.add(jsonObjectRequest)
    }
}





