package com.example.crudapi

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class EditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_edit)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        val id = intent.getIntExtra("id", 0)
        val judul = intent.getStringExtra("judul")
        val penulis = intent.getStringExtra("penulis")
        val tahunTerbit = intent.getIntExtra("tahunTerbit", 0)
        findViewById<EditText>(R.id.etEditJudul).setText(judul)
        findViewById<EditText>(R.id.etEditPenulis).setText(penulis)
        findViewById<EditText>(R.id.etEditTahunTerbit).setText(tahunTerbit.toString())

        val etJudul = findViewById<EditText>(R.id.etEditJudul)
        val etPenulis = findViewById<EditText>(R.id.etEditPenulis)
        val etTahubTerbit = findViewById<EditText>(R.id.etEditTahunTerbit)

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val data = JSONObject()
            data.put("judul", etJudul.text.toString())
            data.put("penulis", etPenulis.text.toString())
            data.put("tahunTerbit", etTahubTerbit.text.toString().toInt())

            GlobalScope.launch(Dispatchers.IO) {
                val connector = URL("http://10.0.2.2:5000/api/Buku/" + id).openConnection() as HttpURLConnection
                connector.requestMethod= "PUT"
                connector.setRequestProperty("Content-Type", "application/json")
                connector.outputStream.write(data.toString().toByteArray())

                val responCode = connector.responseCode

                GlobalScope.launch(Dispatchers.Main) {
                    if(responCode in 200..299){
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }
        }
    }
}