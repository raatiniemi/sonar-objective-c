/*
 * Copyright (c) 2018 Tobias Raatiniemi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

internal data class Rule(
        val name: String,
        val description: String,
        val category: String,
        val severity: Int
) {
    val key = name.toLowerCase()
    val type = {
        val rule = Rules.valueOf(sanitizeKey())
        rulesToTypes[rule] ?: Types.CODE_SMELL
    }()

    private fun sanitizeKey() = key.replace("-", "_")
            .replace("/", "_")
            .replace(" ", "_")
            .toUpperCase()


    companion object {
        fun from(category: RuleCategory, html: Element): Rule {
            val elements = skipVersion(elementsFrom(html))

            return Rule(
                    name = readName(elements),
                    description = readDescription(elements),
                    category = category.name,
                    severity = category.severity
            )
        }

        private fun elementsFrom(html: Element): Elements = html.select("p, pre, dl")

        private fun skipVersion(elements: List<Element>) = elements.drop(1)

        private fun readName(elements: List<Element>): String {
            return elements.first()
                    .select("p > strong")
                    .text()
                    .removePrefix("Name: ")
                    .capitalize()
        }

        private fun readDescription(elements: List<Element>): String {
            return elements.drop(1)
                    .joinToString(separator = "\n") { buildElement(it) }
        }

        private fun buildElement(element: Element): String {
            val tagName = element.tagName()// ?: return ""

            return when (tagName) {
                "pre" -> "<pre>${element.text()}</pre>"
                null -> ""
                else -> "<$tagName>${removeNewLines(element)}</$tagName>"
            }
        }

        private fun removeNewLines(element: Element): String {
            return element.html().replace("\n", "")
        }
    }
}
