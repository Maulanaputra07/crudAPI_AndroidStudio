package com.example.crudapi

import android.app.Activity
import android.content.Intent
import android.media.RouteListingPreference.Item
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    val register = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) refresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val buttonTambahBuku = findViewById<Button>(R.id.btnAdd)

        buttonTambahBuku.setOnClickListener {
            register.launch(Intent(this, TambahActivity::class.java))
        }

        findViewById<Button>(R.id.btnImage).setOnClickListener {
            startActivity(Intent(this, ImageActivity::class.java))
        }

        refresh()
    }

    fun refresh(){
        val recyclerViewData = findViewById<RecyclerView>(R.id.data_list)


        GlobalScope.launch(Dispatchers.IO){
            var connector = URL("http://10.0.2.2:5000/api/Buku").openConnection() as HttpURLConnection
            var data = connector.inputStream.bufferedReader().readText()
            Log.d("response data", data)
            var response = JSONArray(data)

            GlobalScope.launch(Dispatchers.Main) {
                recyclerViewData.adapter = AdapterBuku(response, this@MainActivity)
                recyclerViewData.layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }
    }

    class AdapterBuku(val dataBuku : JSONArray, val mainActivity: MainActivity) : RecyclerView.Adapter<AdapterBuku.HolderBuku>(){
        class HolderBuku(val viewItem: View, val mainActivity: MainActivity) : RecyclerView.ViewHolder(viewItem.rootView){
            fun BindData(data: JSONObject){
                val title = viewItem.findViewById<TextView>(R.id.title_book)

                title.text = data.getString("judul")

                val btnEdit = viewItem.findViewById<Button>(R.id.btnUpdate)
                btnEdit.setOnClickListener {
//                    mainActivity.register.launch()
                    val intent = Intent(viewItem.context, EditActivity::class.java)
                    intent.putExtra("id", data.getInt("id"))
                    intent.putExtra("judul", data.getString("judul"))
                    intent.putExtra("penulis", data.getString("penulis"))
                    intent.putExtra("tahunTerbit", data.getString("tahunTerbit").toInt())
                    mainActivity.register.launch(intent)
                }

                val btnDelete = viewItem.findViewById<Button>(R.id.btnDelete)

                btnDelete.setOnClickListener {
                    GlobalScope.launch(Dispatchers.IO) {
                        var connector = URL("http://10.0.2.2:5000/api/Buku/"+data.getInt("id")).openConnection() as HttpURLConnection
                        connector.requestMethod = "DELETE"
                        val responseCode = connector.responseCode

                        GlobalScope.launch(Dispatchers.Main) {
                            if(responseCode in 200..299){
                                mainActivity.refresh()
                            }
                        }
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderBuku {
            var layout = LayoutInflater.from(parent.context).inflate(R.layout.item_buku, parent, false)
            var view = HolderBuku(layout, mainActivity)
            return view
        }

        override fun getItemCount(): Int {
            return dataBuku.length()
        }

        override fun onBindViewHolder(holder: HolderBuku, position: Int) {
            val data = dataBuku.getJSONObject(position)
            holder.BindData(data)
        }
    }
}