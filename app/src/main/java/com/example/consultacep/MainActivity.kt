package com.example.consultacep

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextCEP = findViewById<EditText>(R.id.editTextCEP)
        val buttonConsultar = findViewById<Button>(R.id.buttonConsultar)
        val textViewResultado = findViewById<TextView>(R.id.textViewResultado)

        buttonConsultar.setOnClickListener {
            val cep = editTextCEP.text.toString().trim()
            if (cep.length == 8) {
                consultarCEP(cep, textViewResultado)
            } else {
                textViewResultado.visibility = View.VISIBLE
                textViewResultado.text = "Por favor, insira um CEP válido de 8 dígitos."
            }
        }
    }

    private fun consultarCEP(cep: String, textViewResultado: TextView) {
        val url = "https://viacep.com.br/ws/$cep/json/"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    textViewResultado.visibility = View.VISIBLE
                    textViewResultado.text = "Erro ao consultar o CEP. Verifique sua conexão com a internet."
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val body = it.string()
                    val jsonObject = JSONObject(body)

                    runOnUiThread {
                        if (jsonObject.has("erro")) {
                            textViewResultado.visibility = View.VISIBLE
                            textViewResultado.text = "CEP não encontrado."
                        } else {
                            val logradouro = jsonObject.getString("logradouro")
                            val bairro = jsonObject.getString("bairro")
                            val cidade = jsonObject.getString("localidade")
                            val estado = jsonObject.getString("uf")

                            textViewResultado.visibility = View.VISIBLE
                            textViewResultado.text = """
                                Logradouro: $logradouro
                                Bairro: $bairro
                                Cidade: $cidade
                                Estado: $estado
                            """.trimIndent()
                        }
                    }
                }
            }
        })
    }
}
