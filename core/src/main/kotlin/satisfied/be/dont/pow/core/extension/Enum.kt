package satisfied.be.dont.pow.core.extension

import satisfied.be.dont.pow.core.ValuableEnum

// reified 키워드는 인라인 함수와 함께 사용될 때 제네릭 타입 매개변수를 런타임에 사용할 수 있도록 해줌.
// where는 Kotlin의 제네릭 타입 제약 조건을 지정하는것.
inline fun <reified T> findEnumEntryByValue(value: String): T? where T: Enum<T>, T: ValuableEnum {
    return enumValues<T>().find { it.value == value }
}


inline fun <reified T : Enum<T>> safeValueOf(type: String): T? {
    return try {
        java.lang.Enum.valueOf(T::class.java, type)
    } catch (e: IllegalArgumentException) {
        null
    }
}