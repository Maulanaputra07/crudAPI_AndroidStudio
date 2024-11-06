package com.example.crudapi

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

class TambahActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah)

        val judul = findViewById<EditText>(R.id.etJudul)
        val penulis = findViewById<EditText>(R.id.etPenulis)
        val tahunTerbit = findViewById<EditText>(R.id.etTahubTerbit)

        val buttonAdd = findViewById<Button>(R.id.btnAddBook)

        buttonAdd.setOnClickListener {
            if(judul.text.toString().isNullOrEmpty()){
                Toast.makeText(this, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }else if(penulis.text.toString().isNullOrEmpty()){
                Toast.makeText(this, "Penulis tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else if(tahunTerbit.text.toString().isNullOrEmpty()){
                Toast.makeText(this, "Tahun terbit tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }else{
                val data = JSONObject()
                data.put("judul", judul.text)
                data.put("penulis", penulis.text)
                data.put("tahunTerbit", tahunTerbit.text.toString().toInt())

                GlobalScope.launch(Dispatchers.IO) {
                    val connector = URL("http://10.0.2.2:5000/api/Buku").openConnection() as HttpURLConnection
                    connector.requestMethod = "POST"
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
}