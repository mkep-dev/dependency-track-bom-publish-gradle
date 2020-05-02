/*
 * Copyright (c) 2020.
 * Markus Keppeler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.mkepDev.dependencyTrackBomPublish

import org.gradle.api.logging.Logging
import java.io.File
import javax.inject.Inject

/**
 * The gradle plugin extension to set the properties
 */
open class DependencyTrackBomPublishExtension @Inject constructor(buildDir: File) {

    var host: String = ""
    var realm = "api/v1/bom"
    var apiKey: String = ""
    var projectUuid: String = ""
    var bomFile: File = File(buildDir, "reports/bom.xml")
//    var useInternalCycloneDx: Boolean = true

    /**
     * Checks whether all properties ar set correctly.
     */
    fun isValid(): Boolean {
        var result = true
        if (host.isEmpty()) {
            logger.error("'host' is not set. Set the hostname of the dependency-track server. Syntax: 'host[:port]' e.g. localhost:8080. Port 80 is default")
            result = false
        }
        if (apiKey.isEmpty()) {
            logger.error("'apiKey' is not set. Set the 'apiKey' to access the dependency-track server.")
            result = false
        }
        if (realm.isEmpty()) {
            logger.error("Wrong value for the 'realm' property.")
            result = false
        }
        if (projectUuid.isEmpty()) {
            logger.error("'projectUuid' is not set. This is the uuid of the associated dependency-track project. \n" +
                    "This can be found in the address bar of your browser if you have open the project.")
            result = false
        }
        if (!bomFile.exists()) {
            logger.error("Bom file '${bomFile.absolutePath}' not found.")
            result = false
        }
        return result
    }

    override fun toString(): String {
        return "DependencyTrackBomPublishExtension(host='$host',\n" +
                " realm='$realm',\n" +
                " apiKey='$apiKey',\n" +
                " projectUuid='$projectUuid',\n" +
                " bomFile=$bomFile,\n" +
//                " useInternalCycloneDx=$useInternalCycloneDx" +
                ")"
    }


    companion object {
        private val logger = Logging.getLogger(DependencyTrackBomPublishExtension::class.java)
        const val EXTENSION_IDENTIFIER = "dtrackPublishBom"
    }
}