package com.codelang.plugin.check.manifest.base

/**
 * @author wangqi
 * @since 2022/2/27.
 */
import com.codelang.plugin.check.base.BaseFileCheck
import com.codelang.plugin.check.manifest.ExportedManifest
import com.codelang.plugin.check.manifest.PermissionManifest
import com.codelang.plugin.check.manifest.UsesSDKManifest
import groovy.util.XmlParser
import org.xml.sax.InputSource
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.util.zip.ZipInputStream

class ManifestCheck : BaseFileCheck {

    private val list = arrayListOf<IManifest>().apply {
        add(ExportedManifest())
        add(PermissionManifest())
        add(UsesSDKManifest())
    }

    override fun onIteratorFile(path: String, dependency: String, fileName: String, fileSize: Long, zipInputStream: ZipInputStream) {
        if (!fileName.endsWith("AndroidManifest.xml")) return

        // xml 解析无法直接使用 zipInputStream，会报 close 异常
        val text = BufferedReader(InputStreamReader(zipInputStream)).readText()

        // 解析的文本必须按如下进行包装成 input，直接给 text 进行 xml 解析会报 MalformedURLException 异常
        val ins = InputSource(ByteArrayInputStream(text.toByteArray()))


        val parentNode = XmlParser(false, false)
                .parse(ins)

        list.forEach {
            it.onNode(parentNode, path, dependency, fileName, fileSize, zipInputStream)
        }

    }

    override fun onEnd() {
        list.forEach {
            it.onEnd()
        }
    }
}
