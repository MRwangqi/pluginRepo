package com.codelang.plugin.check.manifest

import com.codelang.plugin.check.manifest.base.IManifest
import com.codelang.plugin.config.Config
import groovy.util.Node
import org.jetbrains.kotlin.com.google.gson.Gson
import java.io.File
import java.util.zip.ZipInputStream

/**
 * @author wangqi
 * @since 2022/2/28.
 */
class PermissionManifest : IManifest {
    private val hashMap = HashMap<String, ArrayList<String>>()

    override fun onNode(
        parentNode: Node,
        path: String,
        dependency: String,
        fileName: String,
        fileSize: Long,
        zipInputStream: ZipInputStream
    ) {
        parentNode.children()?.forEach {
            val node = (it as? Node)
            if (node != null && node.name().equals("uses-permission")) {
                var deps = hashMap[dependency]
                if (deps == null) {
                    deps = ArrayList()
                    hashMap[dependency] = deps
                }
                deps.add(node.attribute("android:name")?.toString() ?: "NULL")
            }
        }
    }

    override fun onEnd() {
        println()
        println("==================== 未匹配的权限 ============================")

        var text: String? = null
        try {
            val file = File(Config.permissionFile)
            if (!file.exists()) {
                println("未配置权限检查文件")
            }else{
                text = file.readText()
            }
        } catch (e: Exception) {
            println("未配置权限检查文件,需要进行配置才能检查")
        }


        if (text.isNullOrEmpty()){
            return
        }

        var list = ArrayList<String>()
        try {
            list = Gson().fromJson<ArrayList<String>>(text, ArrayList::class.java)
        } catch (e: Exception) {
        }

        hashMap.forEach { (t, u) ->
            val l = u.filter { !list.contains(it) }.toList()
            if (l.isNotEmpty()) {
                println("$t :")
                l.forEach {
                    println("---> $it")
                }
            }
        }
    }
}