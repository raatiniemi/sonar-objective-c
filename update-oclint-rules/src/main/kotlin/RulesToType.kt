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

internal val rulesToTypes = mapOf(
        // Basic
        Rules.BITWISE_OPERATOR_IN_CONDITIONAL to Types.CODE_SMELL,
        Rules.BROKEN_NULL_CHECK to Types.VULNERABILITY,
        Rules.BROKEN_NIL_CHECK to Types.VULNERABILITY,
        Rules.BROKEN_ODDNESS_CHECK to Types.BUG,
        Rules.COLLAPSIBLE_IF_STATEMENTS to Types.CODE_SMELL,
        Rules.CONSTANT_CONDITIONAL_OPERATOR to Types.BUG,
        Rules.CONSTANT_IF_EXPRESSION to Types.CODE_SMELL,
        Rules.DEAD_CODE to Types.CODE_SMELL,
        Rules.DOUBLE_NEGATIVE to Types.BUG,
        Rules.FOR_LOOP_SHOULD_BE_WHILE_LOOP to Types.CODE_SMELL,
        Rules.GOTO_STATEMENT to Types.CODE_SMELL,
        Rules.JUMBLED_INCREMENTER to Types.BUG,
        Rules.MISPLACED_NULL_CHECK to Types.VULNERABILITY,
        Rules.MISPLACED_NIL_CHECK to Types.VULNERABILITY,
        Rules.MULTIPLE_UNARY_OPERATOR to Types.CODE_SMELL,
        Rules.RETURN_FROM_FINALLY_BLOCK to Types.VULNERABILITY,
        Rules.THROW_EXCEPTION_FROM_FINALLY_BLOCK to Types.VULNERABILITY,

        // Cocoa
        Rules.MISSING_HASH_METHOD to Types.BUG,
        Rules.MISSING_CALL_TO_BASE_METHOD to Types.BUG,
        Rules.CALLING_PROHIBITED_METHOD to Types.CODE_SMELL,
        Rules.CALLING_PROTECTED_METHOD to Types.CODE_SMELL,
        Rules.MISSING_ABSTRACT_METHOD_IMPLEMENTATION to Types.CODE_SMELL,

        // Convention
        Rules.AVOID_BRANCHING_STATEMENT_AS_LAST_IN_LOOP to Types.CODE_SMELL,
        Rules.BASE_CLASS_DESTRUCTOR_SHOULD_BE_VIRTUAL_OR_PROTECTED to Types.BUG,
        Rules.UNNECESSARY_DEFAULT_STATEMENT_IN_COVERED_SWITCH_STATEMENT to Types.CODE_SMELL,
        Rules.ILL_PLACED_DEFAULT_LABEL_IN_SWITCH_STATEMENT to Types.BUG,
        Rules.DESTRUCTOR_OF_VIRTUAL_CLASS to Types.CODE_SMELL,
        Rules.INVERTED_LOGIC to Types.CODE_SMELL,
        Rules.MISSING_BREAK_IN_SWITCH_STATEMENT to Types.BUG,
        Rules.NON_CASE_LABEL_IN_SWITCH_STATEMENT to Types.CODE_SMELL,
        Rules.IVAR_ASSIGNMENT_OUTSIDE_ACCESSORS_OR_INIT to Types.CODE_SMELL,
        Rules.PARAMETER_REASSIGNMENT to Types.CODE_SMELL,
        Rules.PREFER_EARLY_EXITS_AND_CONTINUE to Types.CODE_SMELL,
        Rules.MISSING_DEFAULT_IN_SWITCH_STATEMENTS to Types.CODE_SMELL,
        Rules.TOO_FEW_BRANCHES_IN_SWITCH_STATEMENT to Types.CODE_SMELL,

        // Design
        Rules.AVOID_DEFAULT_ARGUMENTS_ON_VIRTUAL_METHODS to Types.CODE_SMELL,
        Rules.AVOID_PRIVATE_STATIC_MEMBERS to Types.CODE_SMELL,

        // Empty
        Rules.EMPTY_CATCH_STATEMENT to Types.CODE_SMELL,
        Rules.EMPTY_DO_WHILE_STATEMENT to Types.CODE_SMELL,
        Rules.EMPTY_ELSE_BLOCK to Types.CODE_SMELL,
        Rules.EMPTY_FINALLY_STATEMENT to Types.CODE_SMELL,
        Rules.EMPTY_FOR_STATEMENT to Types.CODE_SMELL,
        Rules.EMPTY_IF_STATEMENT to Types.CODE_SMELL,
        Rules.EMPTY_SWITCH_STATEMENT to Types.CODE_SMELL,
        Rules.EMPTY_TRY_STATEMENT to Types.CODE_SMELL,
        Rules.EMPTY_WHILE_STATEMENT to Types.CODE_SMELL,

        // Migration
        Rules.USE_BOXED_EXPRESSION to Types.CODE_SMELL,
        Rules.USE_CONTAINER_LITERAL to Types.CODE_SMELL,
        Rules.USE_NUMBER_LITERAL to Types.CODE_SMELL,
        Rules.USE_OBJECT_SUBSCRIPTING to Types.CODE_SMELL,

        // Naming
        Rules.LONG_VARIABLE_NAME to Types.CODE_SMELL,
        Rules.SHORT_VARIABLE_NAME to Types.CODE_SMELL,

        // Redundant
        Rules.REDUNDANT_CONDITIONAL_OPERATOR to Types.CODE_SMELL,
        Rules.REDUNDANT_IF_STATEMENT to Types.CODE_SMELL,
        Rules.REDUNDANT_LOCAL_VARIABLE to Types.CODE_SMELL,
        Rules.REDUNDANT_NIL_CHECK to Types.CODE_SMELL,
        Rules.UNNECESSARY_ELSE_STATEMENT to Types.CODE_SMELL,
        Rules.UNNECESSARY_NULL_CHECK_FOR_DEALLOC to Types.CODE_SMELL,
        Rules.USELESS_PARENTHESES to Types.CODE_SMELL,

        // Size
        Rules.HIGH_CYCLOMATIC_COMPLEXITY to Types.CODE_SMELL,
        Rules.LONG_CLASS to Types.CODE_SMELL,
        Rules.LONG_LINE to Types.CODE_SMELL,
        Rules.LONG_METHOD to Types.CODE_SMELL,
        Rules.HIGH_NCSS_METHOD to Types.CODE_SMELL,
        Rules.DEEP_NESTED_BLOCK to Types.CODE_SMELL,
        Rules.HIGH_NPATH_COMPLEXITY to Types.CODE_SMELL,
        Rules.TOO_MANY_FIELDS to Types.CODE_SMELL,
        Rules.TOO_MANY_METHODS to Types.CODE_SMELL,
        Rules.TOO_MANY_PARAMETERS to Types.CODE_SMELL,

        // Unused
        Rules.UNUSED_LOCAL_VARIABLE to Types.CODE_SMELL,
        Rules.UNUSED_METHOD_PARAMETER to Types.CODE_SMELL
)
