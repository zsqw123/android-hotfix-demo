package com.zsqw123.demo.hotfix

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
            Executors.newCachedThreadPool().execute {
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
                        binding.btHotfix.text = "hotFixedAll"
                        toast("全量热更新成功")
                    }
                } else Log.d("-------------", "hotFix Failed")
            }
        }
        binding.btHotfixPatch.setOnClickListener {
            binding.btHotfixPatch.isClickable = false
            Executors.newCachedThreadPool().execute {
                val url = "https://cdn.jsdelivr.net/gh/zsqw123/cdn@master/prj/demoHotfix/patch.dex"
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                if (connection.responseCode == 200) {
                    val hotFixFile = File(filesDir, "patch.dex")
                    if (!hotFixFile.exists()) hotFixFile.parentFile?.mkdirs()
                    connection.inputStream.copyTo(FileOutputStream(hotFixFile))
                    Log.d("-------------", "hotFixPatched")
                    runOnUiThread {
                        binding.btHotfixPatch.isClickable = true
                        binding.btHotfixPatch.text = "hotFixedPatch"
                        toast("增量热更新成功")
                    }
                } else Log.d("-------------", "hotFixPatch Failed")
            }

        }
        binding.tvHotfix.text = HotFixMethod().getContent()
    }
}

fun toast(msg: String) = Toast.makeText(app, msg, Toast.LENGTH_SHORT).show()