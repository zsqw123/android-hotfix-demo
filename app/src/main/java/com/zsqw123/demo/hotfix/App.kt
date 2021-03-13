package com.zsqw123.demo.hotfix

import android.app.Application
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexClassLoader
import java.io.File

private lateinit var application: Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        application = this
        kotlin.runCatching { hotfix() }
    }

    fun hotfix() {
        val apkFile = File(filesDir, "hotfix.apk")
        if (apkFile.exists()) {
            val newDexLoader = DexClassLoader(apkFile.path, cacheDir.path, null, null)
            // 只要实现 oldDexLoader.pathList.dexElements = newDexLoader.pathList.dexElements 即可完成替换

            // 首先声明 pathList 所在的类和 pathList 字段
            val pathListField = (BaseDexClassLoader::class.java).getDeclaredField("pathList").apply {
                isAccessible = true
            }
            // Field.get(obj) 代表从 obj 中取出 Field 字段代表的值
            // 下方的 get 代表从新 dexLoder 中取出 pathList
            val oldPathList = pathListField.get(classLoader) // oldDexLoader.pathList
            val newPathList = pathListField.get(newDexLoader) // newDexLoader.pathList
            val dexElementsField = (newPathList::class.java).getDeclaredField("dexElements").apply {
                isAccessible = true
            }
            val newDexElements = dexElementsField.get(newPathList) // newDexLoader.pathList.dexElements
            dexElementsField.set(oldPathList, newDexElements) // oldPathList.dexElements = newDexElements 完成替换
        }
    }
}