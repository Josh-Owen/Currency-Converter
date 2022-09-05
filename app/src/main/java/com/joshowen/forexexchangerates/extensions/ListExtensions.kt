package com.joshowen.forexexchangerates.extensions

internal fun <T> List<T>.getSelectedItems(selectedIndexes: List<Long>): List<T> {
    return selectedIndexes.map {
        this[it.toInt()]
    }
}