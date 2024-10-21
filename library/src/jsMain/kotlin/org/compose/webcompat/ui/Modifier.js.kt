package org.compose.webcompat.ui

import com.varabyte.kobweb.compose.ui.Modifier as KobModifier

actual typealias Modifier = KobModifierExtended

interface KobModifierExtended : KobModifier {
    fun <R> foldIn(initial: R, operation: (R, ModifierElement) -> R): R
    fun <R> foldOut(initial: R, operation: (ModifierElement, R) -> R): R
    infix fun then(other: KobModifierExtended): KobModifierExtended =
        if (other === KobModifierExtended) {
            this
        } else {
            CombinedModifier(this, other)
        }

    interface Element : KobModifier.Element, KobModifierExtended {
        override fun <R> foldIn(initial: R, operation: (R, ModifierElement) -> R): R =
            operation(initial, this)

        override fun <R> foldOut(initial: R, operation: (ModifierElement, R) -> R): R =
            operation(this, initial)

        override fun <R> fold(
            initial: R,
            operation: (R, com.varabyte.kobweb.compose.ui.Modifier.Element) -> R
        ): R =
            operation(initial, this)
    }

    companion object : KobModifierExtended {

        override fun <R> foldIn(initial: R, operation: (R, ModifierElement) -> R): R = initial

        override fun <R> foldOut(initial: R, operation: (ModifierElement, R) -> R): R = initial

        override fun then(other: Modifier): Modifier = other

        override fun <R> fold(
            initial: R,
            operation: (R, com.varabyte.kobweb.compose.ui.Modifier.Element) -> R
        ): R = initial
    }
}

actual typealias ModifierElement = KobModifierExtended.Element

private class CombinedModifier(
    val outer: KobModifierExtended,
    val inner: KobModifierExtended
) : KobModifierExtended, KobModifier.Element {
    override fun <R> foldIn(initial: R, operation: (R, ModifierElement) -> R): R =
        inner.foldIn(outer.foldIn(initial, operation), operation)

    override fun <R> foldOut(initial: R, operation: (ModifierElement, R) -> R): R =
        outer.foldOut(inner.foldOut(initial, operation), operation)

    override fun equals(other: Any?): Boolean =
        other is CombinedModifier && outer == other.outer && inner == other.inner

    override fun <R> fold(
        initial: R,
        operation: (R, com.varabyte.kobweb.compose.ui.Modifier.Element) -> R
    ): R = operation(initial, this)

    override fun hashCode(): Int = outer.hashCode() + 31 * inner.hashCode()

    override fun toString() = "[" + foldIn("") { acc, element ->
        if (acc.isEmpty()) element.toString() else "$acc, $element"
    } + "]"
}