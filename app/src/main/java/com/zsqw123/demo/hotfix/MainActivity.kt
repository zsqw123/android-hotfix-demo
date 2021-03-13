package com.zsqw123.demo.hotfix

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.zsqw123.demo.hotfix.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btHotfix.setOnClickListener {
            binding.btHotfix.isClickable = false
            val executor = Executors.newCachedThreadPool()
            executor.execute {
                val url = "https://cdn.jsdelivr.net/gh/zsqw123/cdn@master/prj/demoHotfix/hotfix.apk"
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                if (connection.responseCode == 200) {
                    val hotFixFile = File(filesDir, "hotfix.apk")
                    if (!hotFixFile.exists()) hotFixFile.parentFile?.mkdirs()
                    connection.inputStream.copyTo(FileOutputStream(hotFixFile))
                    Log.d("-------------", "hotFixed")
                    runOnUiThread {
                        binding.btHotfix.isClickable = true
                        binding.btHotfix.text = "hotFixed"
                        app.hotfix()
                    }
                } else Log.d("-------------", "hotFix Failed")
            }
        }
        binding.tvHotfix.text = "NonFixed"
    }
}