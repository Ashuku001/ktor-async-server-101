package com.example.plugins.util

import io.ktor.http.content.*
import java.io.File
import java.util.UUID

fun PartData.FileItem.saveFile(folderPath: String): String {
    val filename = "${UUID.randomUUID().toString()}.${File(originalFileName as String).extension}"

    val fileBytes = streamProvider().readBytes()

    val folder = File(folderPath)
    folder.mkdirs()
    File("$folder/$filename").writeBytes(fileBytes)

    return filename
}