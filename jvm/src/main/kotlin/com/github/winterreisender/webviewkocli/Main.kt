/*
 * Copyright (C) 2022. Winterreisender
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX short identifier: Apache-2.0
 */

/**
 * The Commandline interface for Kotlin/Native
 */

package com.github.winterreisender.webviewkocli

import kotlinx.cli.*
import com.github.winterreisender.webviewko.WebviewKo
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

@Serializable
data class ExecCommandArgument(
    val command :List<String>,
    val timeout :Int? = null,
    val stdin :String? = null
)

@Serializable
data class ExecCommandResult(
    val info :String,
    val stdOut :String,
    val stdErr: String,
    val exitCode :Int,
    val isTimeout: Boolean = false
)



fun main(args: Array<String>) {
    with(ArgParser("webviewko")) {
        val urlArg by argument(ArgType.String, fullName = "url",   description = "URI/URL to navigate").optional()
        val titleArg by option(ArgType.String, fullName = "title", description = "Window title").default("webviewko")
        val widthArg by option(ArgType.Int,    fullName = "width", description = "Window width in px").default(800)
        val heightArg by option(ArgType.Int,   fullName = "height",description = "Window height in px").default(600)
        val hintArg by option(ArgType.Choice<WebviewKo.WindowHint>(), fullName = "hint", description = "Window hint").default(WebviewKo.WindowHint.None)
        val initArg by option(ArgType.String,  fullName = "init",  description = "JS to run on page loading").default("")
        val debugArg by option(ArgType.Boolean,fullName = "debug", description = "Debug mode").default(false)
        parse(args)

        try {
            WebviewKo(if (debugArg) 1 else 0).run {
                title(titleArg)
                size(widthArg, heightArg, hintArg)
                if (initArg.isNotEmpty()) {
                    init(initArg)
                }

                bind("webviewko_os_execCommand") {
                    val arg = Json.decodeFromString<List<ExecCommandArgument>>(it)[0]
                    val process = ProcessBuilder().command(arg.command)
                        //.redirectInput(ProcessBuilder.Redirect.INHERIT)
                        //.redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        //.redirectError(ProcessBuilder.Redirect.INHERIT)
                        .start()

                    if(arg.stdin != null)
                        process.outputStream.buffered().run {
                            write(arg.stdin.toByteArray())
                            flush()
                            close()
                        }

                    var isTimeout = false
                    if(arg.timeout != null && arg.timeout != 0)
                        if(!process.waitFor(arg.timeout.toLong(), TimeUnit.SECONDS)) {
                            isTimeout = true
                            process.destroy()
                        }
                    else
                        process.waitFor()

                    Json.encodeToString(ExecCommandResult(
                        info = process.toString(), // No way to get pid in Java 8
                        stdOut = process.inputStream.readBytes().toString(Charset.defaultCharset()),
                        stdErr = process.errorStream.readBytes().toString(Charset.defaultCharset()),
                        exitCode = process.exitValue(),
                        isTimeout = isTimeout
                    ))
                }

                navigate(urlArg ?: "https://github.com/Winterreisender/webviewkoCLI/wiki/Webviewko-CLI-Help")
                show()
            }
        } catch(e :Throwable) {
            println(e.message)
            if(debugArg) e.printStackTrace()
        }
    }
}

/*
* await webviewko_os_execCommand({
    command: ["fet"],
    timeout: 5,
    stdin: "abc\r\n\n"
});
* */