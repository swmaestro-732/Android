package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.domain.helper.ResourceHelper
import com.chillsam.courmy.common.domain.helper.StringResource
import java.io.File
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory

/** JVM tests use the shipped resource text rather than duplicating translated strings in fakes. */
internal fun testResources(language: String = "ko"): ResourceHelper {
    val root =
        generateSequence(File(requireNotNull(System.getProperty("user.dir")))) { it.parentFile }
            .first { File(it, "settings.gradle.kts").isFile }
    val directory = if (language == "ko") "values" else "values-$language"
    val strings =
        File(root, "common/presentation/src/main/res/$directory")
            .listFiles { file -> file.extension == "xml" }
            .orEmpty()
            .flatMap { file ->
                val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
                val elements = document.getElementsByTagName("string")
                (0 until elements.length).map { index ->
                    val node = elements.item(index)
                    node.attributes.getNamedItem("name").nodeValue to
                        node.textContent.replace("\\'", "'").replace("\\n", "\n")
                }
            }.toMap()
    return object : ResourceHelper {
        override fun getString(
            resource: StringResource,
            vararg formatArgs: Any,
        ): String =
            String.format(
                Locale.forLanguageTag(language),
                strings.getValue("message_" + resource.name.lowercase(Locale.ROOT)),
                *formatArgs,
            )
    }
}
