package com.zsqw123.demo.hotfix

import android.app.Application
import android.content.Context
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexClassLoader
import java.io.File
import java.lang.Exception
import java.lang.reflect.Array

lateinit var app: App

class App : Application() {
    init {
        app = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        val allApk = File(filesDir, "hotfix.apk")
        val patchDex = File(filesDir, "patch.dex")
        try {
            if (patchDex.exists()) hotfixProgessive(patchDex)
            else if (allApk.exists()) hotfix(allApk)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hotfix(apkFile: File) {
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

    private fun hotfixProgessive(hotfixFile: File) {
        val newDexLoader = DexClassLoader(hotfixFile.path, cacheDir.path, null, null)
        // 只要实现 oldDexLoader.pathList.dexElements += newDexLoader.pathList.dexElements 即可完成替换

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
        val oldDexElements = dexElementsField.get(oldPathList)
        val newDexElements = dexElementsField.get(newPathList) // newDexLoader.pathList.dexElements
        if (oldDexElements == null || newDexElements == null) return
        val oldLen = Array.getLength(oldDexElements)
        val addLen = Array.getLength(newDexElements)
        val pathArray = Array.newInstance(oldDexElements::class.java.componentType!!, oldLen + addLen)
        for (i in 0 until oldLen) {
            Array.set(pathArray, i + addLen, Array.get(oldDexElements, i))
        }
        for (i in 0 until addLen) {
            Array.set(pathArray, i, Array.get(newDexElements, i))
        }
        dexElementsField.set(oldPathList, pathArray)
    }
}