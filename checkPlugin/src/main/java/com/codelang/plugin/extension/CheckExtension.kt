package com.codelang.plugin.extension

/**
 * @author wangqi
 * @since 2022/2/27.
 */
open class CheckExtension {
    var buildType: String = ""
    var flavors: String = ""
    var permissionFile: String = ""

    override fun toString(): String {
        return "CheckExtension(buildType='$buildType', flavors='$flavors', permissionFile='$permissionFile')"
    }


}