package org.compose.webcompat.ui

expect interface Modifier {
    fun <R> foldIn(initial: R, operation: (R, ModifierElement) -> R): R
    fun <R> foldOut(initial: R, operation: (ModifierElement, R) -> R): R
    open infix fun then(other: Modifier): Modifier
    companion object : Modifier
}

expect interface ModifierElement