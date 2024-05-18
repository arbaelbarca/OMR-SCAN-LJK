package com.apps.arbaelbarca.omrscanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apps.arbaelbarca.omrscanner.data.model.request.RequestPostLjk
import com.apps.arbaelbarca.omrscanner.data.network.ApiClient
import com.apps.arbaelbarca.omrscanner.databinding.ActivityResultsBinding
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Objects

class Results : AppCompatActivity() {
    var MY_STORAGE_PERMISSION_CODE = 102
    lateinit var binding: ActivityResultsBinding

    var answerFalse = "0"
    var answerTrue = "0"
    var scoreResult = "0"

    val getListAnswer: MutableList<RequestPostLjk.Jawaban> = mutableListOf()
    var jsonArray: JsonArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Objects.requireNonNull(supportActionBar)?.title = "Scanned Results"

        binding.apply {
            getListAnswer.clear()

            answers.text = readFile(MainActivity.file!!)
            answers.visibility = View.VISIBLE
            score.visibility = View.GONE
            btnCheck.setOnClickListener {
                sendAndCheckAnswer()
//                Toast.makeText(applicationContext, "The Score is $s/$maxscore", Toast.LENGTH_SHORT).show()
            }


            btnKirimJawaban.setOnClickListener {
                checkSubmit()
            }
        }

    }

    private fun sendAndCheckAnswer() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_STORAGE_PERMISSION_CODE)
        }
        val path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS + "/OMR/"
        )
        val f = File(path, "key.txt")
        val key = readFile(f).split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val answers = readFile(MainActivity.file!!).split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()


        val maxscore = answers.size.toDouble()
        if (maxscore == 0.0 || key.size == 0) {
            Toast.makeText(applicationContext, "No Answer Key Found.\nPlease Set Up an Answer Key First", Toast.LENGTH_SHORT).show()
        }

        var s = 0.0
        try {
            var i = 0
            while (i < maxscore) {
                var ind = key[i].indexOf('.')
                val actual = key[i].substring(ind + 1).trim { it <= ' ' }
                ind = answers[i].indexOf('.')

                getListAnswer.addAll(
                    listOf(
                        RequestPostLjk.Jawaban(answers[i])
                    )
                )


                val found = answers[i].substring(ind + 1).trim { it <= ' ' }
                if (actual.equals(found, ignoreCase = true)) ++s
                i++

            }
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "No Answer Key Found.\nPlease Set Up an Answer Key First", Toast.LENGTH_SHORT).show()
            Log.d("ERROR", e.message.toString())
            finishAffinity()
            startActivity(Intent(this@Results, StartActivity::class.java))
        }

        binding.apply {
            score?.visibility = View.VISIBLE
            val getScore = s / maxscore * 100
            scoreResult = getScore.toString()
            score.text = "Score is: $scoreResult%"
            btnCheck.visibility = View.GONE
            llScore.visibility = View.VISIBLE
            btnKirimJawaban.visibility = View.VISIBLE


            val getAnswerFalse = maxscore - s
            answerFalse = getAnswerFalse.toString()
            answerTrue = s.toString()

            tvItemTotalAnswerTrue.text = answerTrue
            tvItemTotalAnswerFalse.text = answerFalse
        }


    }

    fun checkSubmit() {
        binding.pbSubmit.visibility = View.VISIBLE

        val getMatkul = binding.tvInputMataKuliah.text.toString()
        val getNik = binding.tvInputNik.text.toString()
        val getNama = binding.tvInputNama.text.toString()

        if (getMatkul.isNotEmpty()
            && getNik.isNotEmpty()
            && getNama.isNotEmpty()
        ) {
            sendApiSubmit()
        } else {
            binding.pbSubmit.visibility = View.GONE
            Toast.makeText(this, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }
    }

    fun getCurrentDatetime(): String {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return df.format(c.time)
    }

    private fun sendApiSubmit() {

        println("respon Gson submit ${Gson().toJson(getListAnswer)}")

        val getMatkul = binding.tvInputMataKuliah.text.toString()
        val getNik = binding.tvInputNik.text.toString()
        val getNama = binding.tvInputNama.text.toString()

        val requestPostLjk = RequestPostLjk(
            answerFalse,
            answerTrue,
            getCurrentDatetime(),
            getCurrentDatetime(),
            getListAnswer,
            getMatkul,
            getNik,
            getNama,
            scoreResult
        )

        val callApiSubmit = ApiClient().apiService.callPostLjk(requestPostLjk)
        callApiSubmit.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                binding.pbSubmit.visibility = View.GONE
                binding.btnCheck.visibility = View.VISIBLE
                Toast.makeText(this@Results, "Success dikirim", Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

        })
    }

    private fun readFile(file: File): String {
        val text = StringBuilder()
        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                text.append('\n')
            }

//            println("respon Gson Array ${Gson().toJson(jsonArray)}")

            br.close()
        } catch (e: IOException) {
            Toast.makeText(
                applicationContext,
                "No Answer Key Found.\nPlease Set Up an Answer Key First",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("ERROR", e.message.toString())
            finishAffinity()
            startActivity(Intent(this@Results, StartActivity::class.java))
        }
        return text.toString()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_STORAGE_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.btnCheck.callOnClick()
            } else {
                Toast.makeText(this, "storage permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

}
