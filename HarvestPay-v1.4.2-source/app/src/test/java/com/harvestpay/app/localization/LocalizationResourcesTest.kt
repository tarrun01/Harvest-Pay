package com.harvestpay.app.localization

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.*
import org.junit.Test

class LocalizationResourcesTest {
    private fun values(folder: String): Map<String, String> {
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(File("src/main/res/$folder/strings.xml"))
        val nodes = document.getElementsByTagName("string")
        val pairs = (0 until nodes.length).map { i ->
            nodes.item(i).attributes.getNamedItem("name").nodeValue to nodes.item(i).textContent
        }
        assertEquals("Duplicate resource IDs", pairs.size, pairs.map { it.first }.toSet().size)
        return pairs.toMap()
    }
    @Test fun hindiCatalogCoversEveryEnglishStringAndPreservesArguments() {
        val en = values("values"); val hi = values("values-hi")
        assertEquals(en.keys, hi.keys)
        val placeholder = Regex("%[0-9]+\\\$[sdf]")
        en.forEach { (id, value) ->
            assertEquals(id, placeholder.findAll(value).map { it.value }.toList().sorted(),
                placeholder.findAll(requireNotNull(hi[id])).map { it.value }.toList().sorted())
            assertTrue("Empty Hindi text: $id", requireNotNull(hi[id]).isNotBlank())
        }
    }
    @Test fun criticalHindiLabelsAndVersionNotesArePresent() {
        val hi = values("values-hi")
        assertEquals("ग्राहक", hi["text_customers"])
        assertEquals("जीपीएस से खेत नापें", hi["text_measure_by_gps"])
        assertEquals("वर्तमान वर्ज़न", hi["text_current_version"])
        listOf("values", "values-hi").forEach { folder ->
            val xml = File("src/main/res/$folder/strings.xml").readText()
            listOf("release_notes_143", "release_notes_142", "release_notes_141", "release_notes_unknown", "release_notes_122").forEach { assertTrue(xml.contains(it)) }
        }
    }
}
