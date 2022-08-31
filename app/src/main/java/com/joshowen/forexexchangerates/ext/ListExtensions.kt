package com.joshowen.forexexchangerates.ext

fun <T> List<T>.getSelectedItems(selectedIndexes: List<Long>): List<T> {
    return selectedIndexes.map {
        this[it.toInt()]
    }
}