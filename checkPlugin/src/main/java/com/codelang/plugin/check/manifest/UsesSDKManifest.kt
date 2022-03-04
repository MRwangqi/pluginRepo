package com.codelang.plugin.check.manifest

import com.codelang.plugin.check.manifest.base.IManifest
import com.codelang.plugin.ext.toFileSize
import groovy.util.Node
import java.util.zip.ZipInputStream

/**
 * @author wangqi
 * @since 2022/2/23.
 */
class UsesSDKManifest : IManifest {

   private val hashMap = HashMap<String, Pair<String, String>>()

    override fun onNode(parentNode: Node, path: String, dependency: String, fileName: String, fileSize: Long, zipInputStream: ZipInputStream) {
        parentNode.children()?.forEach {
            val node = (it as? Node)
            //todo uses-sdk 解析,解析这个的目的：
            //todo 1、依赖库可能适配了 android12(targetSDK 为 31)，使用了 android12 相关的特性， 但项目的 targetSDK 却为 27，导致不可预期的适配问题
            //
            if (node!=null){
                if (node.name().equals("uses-sdk")) {
                    hashMap[dependency] = Pair(node.attribute("android:minSdkVersion")?.toString() ?: "",
                        node.attribute("android:targetSdkVersion")?.toString() ?: "NULL")
                }
            }

        }
    }

    override fun onEnd() {
        println()
        println("==================== uses-sdk 检查 ============================")
        hashMap.forEach { (t, u) ->
            println("依赖 = $t ---> minSdkVersion=${u.first} targetSdkVersion=${u.second}")
        }
    }
}