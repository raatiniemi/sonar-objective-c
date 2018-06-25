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

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

private const val pathToRules = "sonar-objective-c-plugin/src/main/resources/org/sonar/plugins/oclint/rules.txt"
private const val pathToProfile = "sonar-objective-c-plugin/src/main/resources/org/sonar/plugins/oclint/profile-oclint.xml"

private const val baseUrl = "http://docs.oclint.org/en/stable/rules"
private val availableRuleCategoriesWithSeverity = mapOf(
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

    listRuleCategoriesWithMissingSeverity(nameForAvailableRuleCategories())

    val rules = availableRuleCategoriesWithSeverity
            .map { RuleCategory(name = it.key, severity = it.value) }
            .flatMap { fetchRulesFor(it) }
            .sortedBy { it.name }

    println("Found ${rules.count()} rules.")

    println("Writing available rules to rules.txt")
    writeRulesToFile(rules)

    println("Writing available rules to profile-oclint.xml")
    writeProfileToFile(buildProfile(rules))
}

private fun writeRulesToFile(rules: List<Rule>) {
    File(pathToRules).printWriter()
            .use { out ->
                out.println(headerTemplate())

                rules.forEach {
                    out.println(ruleTemplate(it))
                }
            }
}

private fun buildProfile(rules: List<Rule>): Profile {
    val profileRules = rules.map { ProfileRule(key = it.key) }
            .toList()

    return Profile(rule = profileRules)
}

private fun writeProfileToFile(profile: Profile) {
    val module = JacksonXmlModule()
    module.setDefaultUseWrapper(false)

    val mapper = XmlMapper(module)
    mapper.enable(SerializationFeature.INDENT_OUTPUT)

    File(pathToProfile).printWriter()
            .use { out ->
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>")
                out.println(mapper.writeValueAsString(profile))
            }
}

private fun listRuleCategoriesWithMissingSeverity(availableRuleCategories: List<String>) {
    (availableRuleCategories - availableRuleCategoriesWithSeverity.keys)
            .forEach { println("Rule \"$it\" is missing severity") }
}

private fun nameForAvailableRuleCategories(): List<String> {
    return fetch("index.html")
            .select(".toctree-l1 > a")
            .map { it.text() }
}

private fun fetchRulesFor(category: RuleCategory): List<Rule> {
    return fetch(basenamePath(category))
            .select(".section > .section")
            .map { Rule.from(category, html = it) }
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
