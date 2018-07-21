/*
 * Copyright (C) 2018 Tobias Raatiniemi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RuleTest {
    private val ruleCategory = RuleCategory("category name", 2)

    @Test
    fun from_simpleRuleDescription() {
        val expected = Rule(
                "Bitwise operator in conditional",
                """
                <p>Checks for bitwise operations in conditionals. Although being written on purpose in some rare cases, bitwise operations are considered to be too “smart”. Smart code is not easy to understand.</p>
                <p>This rule is defined by the following class: <a class="reference external" href="https://github.com/oclint/oclint/blob/master/oclint-rules/rules/basic/BitwiseOperatorInConditionalRule.cpp">oclint-rules/rules/basic/BitwiseOperatorInConditionalRule.cpp</a></p>
                <p><strong>Example:</strong></p>
                <pre>void example(int a, int b)
                {
                    if (a | b)
                    {
                    }
                    if (a & b)
                    {
                    }
                }</pre>
                """.trimIndent(),
                ruleCategory.name,
                ruleCategory.severity
        )
        val ruleDescription = """
            <div class="section" id="bitwiseoperatorinconditional">
            <h2>BitwiseOperatorInConditional<a class="headerlink" href="#bitwiseoperatorinconditional" title="Permalink to this headline">¶</a></h2>
            <p><strong>Since: 0.6</strong></p>
            <p><strong>Name: bitwise operator in conditional</strong></p>
            <p>Checks for bitwise operations in conditionals. Although being written on purpose in some rare cases, bitwise operations are considered to be too “smart”. Smart code is not easy to understand.</p>
            <p>This rule is defined by the following class: <a class="reference external" href="https://github.com/oclint/oclint/blob/master/oclint-rules/rules/basic/BitwiseOperatorInConditionalRule.cpp">oclint-rules/rules/basic/BitwiseOperatorInConditionalRule.cpp</a></p>
            <p><strong>Example:</strong></p>
            <div class="highlight-cpp"><div class="highlight"><pre><span></span><span class="kt">void</span> <span class="nf">example</span><span class="p">(</span><span class="kt">int</span> <span class="n">a</span><span class="p">,</span> <span class="kt">int</span> <span class="n">b</span><span class="p">)</span>
            <span class="p">{</span>
                <span class="k">if</span> <span class="p">(</span><span class="n">a</span> <span class="o">|</span> <span class="n">b</span><span class="p">)</span>
                <span class="p">{</span>
                <span class="p">}</span>
                <span class="k">if</span> <span class="p">(</span><span class="n">a</span> <span class="o">&amp;</span> <span class="n">b</span><span class="p">)</span>
                <span class="p">{</span>
                <span class="p">}</span>
            <span class="p">}</span>
            </pre></div>
            </div>
            </div>
        """.trimIndent()

        val actual = Rule.from(ruleCategory, Jsoup.parse(ruleDescription))

        assertEquals(expected, actual)
    }

    @Test
    fun from_advancedRuleDescription() {
        val expected = Rule(
                "High npath complexity",
                """
                <p>NPath complexity is determined by the number of execution paths through that method. Compared to cyclomatic complexity, NPath complexity has two outstanding characteristics: first, it distinguishes between different kinds of control flow structures; second, it takes the various type of acyclic paths in a flow graph into consideration.</p>
                <p>Based on studies done by the original author in AT&amp;T Bell Lab, an NPath threshold value of 200 has been established for a method.</p>
                <p>This rule is defined by the following class: <a class="reference external" href="https://github.com/oclint/oclint/blob/master/oclint-rules/rules/size/NPathComplexityRule.cpp">oclint-rules/rules/size/NPathComplexityRule.cpp</a></p>
                <p><strong>Example:</strong></p>
                <pre>void example()
                {
                    // complicated code that is hard to understand
                }</pre>
                <p><strong>Thresholds:</strong></p>
                <dl><dt> NPATH_COMPLEXITY</dt> <dd> The NPath complexity reporting threshold, default value is 200.</dd></dl>
                <p><strong>Suppress:</strong></p>
                <pre>__attribute__((annotate("oclint:suppress[high npath complexity]")))</pre>
                <p><strong>References:</strong></p>
                <p>Brian A. Nejmeh (1988). <a class="reference external" href="http://dl.acm.org/citation.cfm?id=42379">“NPATH: a measure of execution path complexity and its applications”</a>. <em>Communications of the ACM 31 (2) p. 188-200</em></p>
                """.trimIndent(),
                ruleCategory.name,
                ruleCategory.severity
        )
        val ruleDescription = """
            <div class="section" id="highnpathcomplexity">
            <h2>HighNPathComplexity<a class="headerlink" href="#highnpathcomplexity" title="Permalink to this headline">¶</a></h2>
            <p><strong>Since: 0.4</strong></p>
            <p><strong>Name: high npath complexity</strong></p>
            <p>NPath complexity is determined by the number of execution paths through that method.
            Compared to cyclomatic complexity, NPath complexity has two outstanding characteristics:
            first, it distinguishes between different kinds of control flow structures;
            second, it takes the various type of acyclic paths in a flow graph into consideration.</p>
            <p>Based on studies done by the original author in AT&amp;T Bell Lab,
            an NPath threshold value of 200 has been established for a method.</p>
            <p>This rule is defined by the following class: <a class="reference external" href="https://github.com/oclint/oclint/blob/master/oclint-rules/rules/size/NPathComplexityRule.cpp">oclint-rules/rules/size/NPathComplexityRule.cpp</a></p>
            <p><strong>Example:</strong></p>
            <div class="highlight-cpp"><div class="highlight"><pre><span></span><span class="kt">void</span> <span class="nf">example</span><span class="p">()</span>
            <span class="p">{</span>
                <span class="c1">// complicated code that is hard to understand</span>
            <span class="p">}</span>
            </pre></div>
            </div>
            <p><strong>Thresholds:</strong></p>
            <dl class="docutils">
            <dt>NPATH_COMPLEXITY</dt>
            <dd>The NPath complexity reporting threshold, default value is 200.</dd>
            </dl>
            <p><strong>Suppress:</strong></p>
            <div class="highlight-cpp"><div class="highlight"><pre><span></span><span class="n">__attribute__</span><span class="p">((</span><span class="n">annotate</span><span class="p">(</span><span class="s">"oclint:suppress[high npath complexity]"</span><span class="p">)))</span>
            </pre></div>
            </div>
            <p><strong>References:</strong></p>
            <p>Brian A. Nejmeh  (1988). <a class="reference external" href="http://dl.acm.org/citation.cfm?id=42379">“NPATH: a measure of execution path complexity and its applications”</a>. <em>Communications of the ACM 31 (2) p. 188-200</em></p>
            </div>
        """.trimIndent()

        val actual = Rule.from(ruleCategory, Jsoup.parse(ruleDescription))

        assertEquals(expected, actual)
    }
}
