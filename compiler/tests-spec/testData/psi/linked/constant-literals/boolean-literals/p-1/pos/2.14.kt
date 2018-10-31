/*
 * KOTLIN PSI SPEC TEST (POSITIVE)
 *
 * SECTIONS: constant-literals, boolean-literals
 * PARAGRAPH: 1
 * SENTENCE: [2] These are strong keywords which cannot be used as identifiers unless escaped.
 * NUMBER: 14
 * DESCRIPTION: The use of Boolean literals as the identifier (with backtick) in the typeParameter.
 * NOTE: this test data is generated by FeatureInteractionTestDataGenerator. DO NOT MODIFY CODE MANUALLY!
 */

fun <`true`> f1() {}

fun <reified T : `false`> T.f2() {}

class B<K: L<M<`true`>>> {}

class B<K, T: A<in `false`>> {}

fun <T : org.jetbrains.`true`> T.f3() {}

fun f4(a: List<out `false`>) {}

fun f5(a: List<List<List<`true`?>>>) {}
