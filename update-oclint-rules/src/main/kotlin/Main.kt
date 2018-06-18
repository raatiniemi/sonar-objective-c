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

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

private const val baseUrl = "http://docs.oclint.org/en/stable/rules"
private val availableRuleSetWithSeverity = mapOf(
        "Basic" to 3,
        "Cocoa" to 1,
        "Convention" to 2,
        "Design" to 2,
        "Empty" to 3,
        "Migration" to 1,
        "Naming" to 2,
        "Redundant" to 1,
        "Size" to 3,
        "Unused" to 0
)

fun main(args: Array<String>) {
    FuelManager.instance.basePath = baseUrl

    listRuleSetsWithMissingSeverity(nameForAvailableRuleSets())
}

private fun listRuleSetsWithMissingSeverity(availableRuleSets: List<String>) {
    (availableRuleSets - availableRuleSetWithSeverity.keys)
            .forEach { println("Rule \"$it\" is missing severity") }
}

private fun nameForAvailableRuleSets(): List<String> {
    return fetch("index.html")
            .select(".toctree-l1 > a")
            .map { it.text() }
}

private fun fetch(path: String): Document {
    val (_, _, result) = "/$path".httpGet().responseString()
    when (result) {
        is Result.Failure -> {
            throw result.error
        }
        is Result.Success -> {
            return Jsoup.parse(result.value)
        }
    }
}
