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

enum class Rules(val ruleName: String) {
    AVOID_BRANCHING_STATEMENT_AS_LAST_IN_LOOP("avoid branching statement as last in loop"),
    AVOID_DEFAULT_ARGUMENTS_ON_VIRTUAL_METHODS("avoid default arguments on virtual methods"),
    AVOID_PRIVATE_STATIC_MEMBERS("avoid private static members"),
    BASE_CLASS_DESTRUCTOR_SHOULD_BE_VIRTUAL_OR_PROTECTED("base class destructor should be virtual or protected"),
    BITWISE_OPERATOR_IN_CONDITIONAL("bitwise operator in conditional"),
    BROKEN_NIL_CHECK("broken nil check"),
    BROKEN_NULL_CHECK("broken null check"),
    BROKEN_ODDNESS_CHECK("broken oddness check"),
    CALLING_PROHIBITED_METHOD("calling prohibited method"),
    CALLING_PROTECTED_METHOD("calling protected method"),
    COLLAPSIBLE_IF_STATEMENTS("collapsible if statements"),
    CONSTANT_CONDITIONAL_OPERATOR("constant conditional operator"),
    CONSTANT_IF_EXPRESSION("constant if expression"),
    DEAD_CODE("dead code"),
    DEEP_NESTED_BLOCK("deep nested block"),
    DESTRUCTOR_OF_VIRTUAL_CLASS("destructor of virtual class"),
    DOUBLE_NEGATIVE("double negative"),
    EMPTY_CATCH_STATEMENT("empty catch statement"),
    EMPTY_DO_WHILE_STATEMENT("empty do/while statement"),
    EMPTY_ELSE_BLOCK("empty else block"),
    EMPTY_FINALLY_STATEMENT("empty finally statement"),
    EMPTY_FOR_STATEMENT("empty for statement"),
    EMPTY_IF_STATEMENT("empty if statement"),
    EMPTY_SWITCH_STATEMENT("empty switch statement"),
    EMPTY_TRY_STATEMENT("empty try statement"),
    EMPTY_WHILE_STATEMENT("empty while statement"),
    FOR_LOOP_SHOULD_BE_WHILE_LOOP("for loop should be while loop"),
    GOTO_STATEMENT("goto statement"),
    HIGH_CYCLOMATIC_COMPLEXITY("high cyclomatic complexity"),
    HIGH_NCSS_METHOD("high ncss method"),
    HIGH_NPATH_COMPLEXITY("high npath complexity"),
    ILL_PLACED_DEFAULT_LABEL_IN_SWITCH_STATEMENT("ill-placed default label in switch statement"),
    INVERTED_LOGIC("inverted logic"),
    IVAR_ASSIGNMENT_OUTSIDE_ACCESSORS_OR_INIT("ivar assignment outside accessors or init"),
    JUMBLED_INCREMENTER("jumbled incrementer"),
    LONG_CLASS("long class"),
    LONG_LINE("long line"),
    LONG_METHOD("long method"),
    LONG_VARIABLE_NAME("long variable name"),
    MISPLACED_NIL_CHECK("misplaced nil check"),
    MISPLACED_NULL_CHECK("misplaced null check"),
    MISSING_ABSTRACT_METHOD_IMPLEMENTATION("missing abstract method implementation"),
    MISSING_BREAK_IN_SWITCH_STATEMENT("missing break in switch statement"),
    MISSING_CALL_TO_BASE_METHOD("missing call to base method"),
    MISSING_DEFAULT_IN_SWITCH_STATEMENTS("missing default in switch statements"),
    MISSING_HASH_METHOD("missing hash method"),
    MULTIPLE_UNARY_OPERATOR("multiple unary operator"),
    NON_CASE_LABEL_IN_SWITCH_STATEMENT("non case label in switch statement"),
    PARAMETER_REASSIGNMENT("parameter reassignment"),
    PREFER_EARLY_EXITS_AND_CONTINUE("prefer early exits and continue"),
    REDUNDANT_CONDITIONAL_OPERATOR("redundant conditional operator"),
    REDUNDANT_IF_STATEMENT("redundant if statement"),
    REDUNDANT_LOCAL_VARIABLE("redundant local variable"),
    REDUNDANT_NIL_CHECK("redundant nil check"),
    RETURN_FROM_FINALLY_BLOCK("return from finally block"),
    SHORT_VARIABLE_NAME("short variable name"),
    THROW_EXCEPTION_FROM_FINALLY_BLOCK("throw exception from finally block"),
    TOO_FEW_BRANCHES_IN_SWITCH_STATEMENT("too few branches in switch statement"),
    TOO_MANY_FIELDS("too many fields"),
    TOO_MANY_METHODS("too many methods"),
    TOO_MANY_PARAMETERS("too many parameters"),
    UNNECESSARY_DEFAULT_STATEMENT_IN_COVERED_SWITCH_STATEMENT("unnecessary default statement in covered switch statement"),
    UNNECESSARY_ELSE_STATEMENT("unnecessary else statement"),
    UNNECESSARY_NULL_CHECK_FOR_DEALLOC("unnecessary null check for dealloc"),
    UNUSED_LOCAL_VARIABLE("unused local variable"),
    UNUSED_METHOD_PARAMETER("unused method parameter"),
    USE_BOXED_EXPRESSION("use boxed expression"),
    USE_CONTAINER_LITERAL("use container literal"),
    USE_NUMBER_LITERAL("use number literal"),
    USE_OBJECT_SUBSCRIPTING("use object subscripting"),
    USELESS_PARENTHESES("useless parentheses")
}
