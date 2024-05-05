package com.apps.arbaelbarca.omrscanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apps.arbaelbarca.omrscanner.databinding.ActivityBottomViewNavigationBinding
import com.apps.arbaelbarca.omrscanner.databinding.ActivityResultsBinding
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.Objects

class Results : AppCompatActivity() {
    var MY_STORAGE_PERMISSION_CODE = 102
    lateinit var binding: ActivityResultsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Objects.requireNonNull(supportActionBar)?.title = "Scanned Results"

        binding.apply {
            answers.text = readFile(MainActivity.file)
            answers.visibility = View.VISIBLE
            score.visibility = View.GONE
            check.setOnClickListener(View.OnClickListener {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_STORAGE_PERMISSION_CODE)
                }
                val path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS + "/OMR/"
                )
                val f = File(path, "key.txt")
                val key = readFile(f).split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val answers = readFile(MainActivity.file).split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
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
                score?.visibility = View.VISIBLE
                score.text = "Score is: " + s / maxscore * 100 + "%"
                check.visibility = View.GONE
                llScore.visibility = View.VISIBLE

                val answerFalse = maxscore - s

                tvItemTotalAnswerTrue.text = s.toString()
                tvItemTotalAnswerFalse.text = answerFalse.toString()

//                Toast.makeText(applicationContext, "The Score is $s/$maxscore", Toast.LENGTH_SHORT).show()
            })
        }

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
                binding.check.callOnClick()
            } else {
                Toast.makeText(this, "storage permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

}
