package com.example.headup2_atheer_alharabi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.res.Configuration
import android.os.CountDownTimer
import android.view.Surface
import android.widget.*
import androidx.core.view.isVisible
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var hrTop: LinearLayout
    private lateinit var verMain: LinearLayout
    private lateinit var verCel: LinearLayout
    private lateinit var tvT: TextView
    private lateinit var tvN: TextView
    private lateinit var tvT1: TextView
    private lateinit var tvT2: TextView
    private lateinit var tvT3: TextView
    private lateinit var tvMain: TextView
    private lateinit var bStart: Button
    private var gameActive = false
    var someNumber=0
    private lateinit var celebrities: ArrayList<JSONObject>

    private var celeb = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
  hrTop = findViewById(R.id.hrTime)
                verMain = findViewById(R.id.verMain)
                verCel = findViewById(R.id.verCel)

                tvT= findViewById(R.id.tvT)

                tvN = findViewById(R.id.tvN)
                tvT1 = findViewById(R.id.tvT1)
                tvT2 = findViewById(R.id.tvT2)
                tvT3 = findViewById(R.id.tvT3)

                tvMain = findViewById(R.id.tvHU)
                bStart = findViewById(R.id.bStart)
                bStart.setOnClickListener { requestAPI() }

                celebrities = arrayListOf()
            }

    override fun onConfigurationChanged(newConfig: Configuration) {
                super.onConfigurationChanged(newConfig)
                val rotation = windowManager.defaultDisplay.rotation
                if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180){
                    if(gameActive){
                        celeb++
                        newCelebrity(celeb)
                        updateStatus(false)
                    }else{
                        updateStatus(false)
                    }
                }else{
                    if(gameActive){
                        updateStatus(true)
                    }else{
                        updateStatus(false)
                    }
                }
            }

            private fun newTimer(){
                if(!gameActive){
                    gameActive = true
                    tvMain.text = "Please Rotate Device"
                    bStart.isVisible = false
                    val rotation = windowManager.defaultDisplay.rotation
                    if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180){
                        updateStatus(false)
                    }else{
                        updateStatus(true)
                    }

                    object : CountDownTimer(60000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            tvT.text = "Time: ${millisUntilFinished / 1000}"
                        }


                        override fun onFinish() {
                            gameActive = false
                            tvT.text = "Time: --"
                            tvMain.text = "Heads Up!"
                            bStart.isVisible = true
                            updateStatus(false)
                        }
                    }.start()
                }
            }

            private fun newCelebrity(id: Int){
                if(id < celebrities.size){
                    tvN.text = celebrities[id].getString("name")
                    tvT1.text = celebrities[id].getString("taboo1")
                    tvT2.text = celebrities[id].getString("taboo2")
                    tvT3.text = celebrities[id].getString("taboo3")
                }
            }

            private fun requestAPI(){
                CoroutineScope(Dispatchers.IO).launch {
                    val data = async {
                        getCelebrities()
                    }.await()
                    if(data.isNotEmpty()){
                        withContext(Main){
                            parseJSON(data)
                            celebrities.shuffle()
                            newCelebrity(0)
                            newTimer()
                        }
                    }else{

                    }
                }
            }

            private suspend fun parseJSON(result: String){
                withContext(Dispatchers.Main){
                    celebrities.clear()
                    val jsonArray = JSONArray(result)
                    for(i in 0 until jsonArray.length()){
                        celebrities.add(jsonArray.getJSONObject(i))
                    }
                }
            }

            private fun getCelebrities(): String{
                var response = ""
                try {
                    response = URL("https://dojo-recipes.herokuapp.com/celebrities/")
                        .readText(Charsets.UTF_8)
                }catch (e: Exception){
                    println("Error: $e")
                }
                return response
            }

            private fun updateStatus(showCelebrity: Boolean){
                if(showCelebrity){
                    verCel.isVisible = true
                    verMain.isVisible = false
                }else{
                    verCel.isVisible = false
                    verMain.isVisible = true
                }
            }
        }

