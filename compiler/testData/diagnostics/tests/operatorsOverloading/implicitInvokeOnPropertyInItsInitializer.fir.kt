// ISSUE: KT-65576

fun foo(): Int = 0

object Implicit {
    operator fun Any.invoke(): String = "Fail"

    val foo = <!RECURSION_IN_IMPLICIT_TYPES!>foo<!>()
}

object Explicit {
    operator fun Any.invoke(): String = "Fail"

    val foo: String = foo()
}

class Inv<T>(val value: T)

object ImplicitWrapped {
    operator fun Inv<*>.invoke(): Inv<String> = Inv("Fail")

    val foo = Inv(<!TYPECHECKER_HAS_RUN_INTO_RECURSIVE_PROBLEM!>foo<!>)()
}

object ImplicitIndirect {
    operator fun Any.invoke(): String = "Fail"

    val foo get() = bar()
    val bar get() = baz()
    val baz get() = <!RECURSION_IN_IMPLICIT_TYPES!>foo<!>()
}

fun takeInt(x: Int) {}

fun test() {
    takeInt(<!ARGUMENT_TYPE_MISMATCH!>Implicit.foo<!>) // should be an error
    takeInt(<!ARGUMENT_TYPE_MISMATCH!>Explicit.foo<!>) // should be an error
    takeInt(<!ARGUMENT_TYPE_MISMATCH!>ImplicitWrapped.foo<!>) // should be an error
    takeInt(<!ARGUMENT_TYPE_MISMATCH!>ImplicitIndirect.foo<!>) // should be an error
    takeInt(<!ARGUMENT_TYPE_MISMATCH!>ImplicitIndirect.bar<!>) // should be an error
    takeInt(<!ARGUMENT_TYPE_MISMATCH!>ImplicitIndirect.baz<!>) // should be an error
}
