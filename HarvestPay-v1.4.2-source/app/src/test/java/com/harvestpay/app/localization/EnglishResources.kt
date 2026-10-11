package com.harvestpay.app.localization

import com.harvestpay.app.R
import java.io.File
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory

/** JVM fixtures read the real Android resource source, rather than duplicate copy. */
object EnglishResources {
    fun install() {
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(File("src/main/res/values/strings.xml"))
        val nodes = document.getElementsByTagName("string")
        val values = (0 until nodes.length).associate { index ->
            val node = nodes.item(index)
            val name = node.attributes.getNamedItem("name").nodeValue
            R.string::class.java.getField(name).getInt(null) to node.textContent
                .replace("\\'", "'").replace("\\\"", "\"").replace("\\n", "\n")
        }
        AppText.resolver = { id, arguments -> String.format(Locale.ENGLISH, requireNotNull(values[id]), *arguments) }
    }
}
