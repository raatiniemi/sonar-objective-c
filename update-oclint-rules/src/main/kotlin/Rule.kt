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

data class Rule(
        val name: String,
        val category: String,
        val severity: Int
) {
    companion object {
        fun from(category: RuleCategory, html: Element): Rule {
            val elements = skipExample(skipVersion(paragraphs(html)))

            return Rule(
                    name = readName(elements),
                    category = category.name,
                    severity = category.severity
            )
        }

        private fun paragraphs(html: Element): Elements = html.select("p")

        private fun skipVersion(elements: List<Element>) = elements.drop(1)

        private fun skipExample(elements: List<Element>) = elements.dropLast(1)

        private fun readName(elements: List<Element>): String {
            return elements.first()
                    .select("p > strong")
                    .text()
                    .removePrefix("Name: ")
                    .capitalize()
        }
    }
}
