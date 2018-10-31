/*
 * KOTLIN PSI SPEC TEST (POSITIVE)
 *
 * SECTIONS: constant-literals, boolean-literals
 * PARAGRAPH: 1
 * SENTENCE: [2] These are strong keywords which cannot be used as identifiers unless escaped.
 * NUMBER: 23
 * DESCRIPTION: The use of Boolean literals as the identifier (with backtick) in the atomicExpression.
 * NOTE: this test data is generated by FeatureInteractionTestDataGenerator. DO NOT MODIFY CODE MANUALLY!
 */

val `true` = 10
val `false` = "."

val value_1 = `true` - 100 % `false`
val value_2 = `true`.dec()
val value_3 = "$`false` 10"
val value_4 = "${`true`}"
val value_5 = `false` + " 11..." + `true` + "1"
val value_6 = `false`
