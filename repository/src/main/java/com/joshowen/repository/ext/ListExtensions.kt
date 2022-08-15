package com.joshowen.repository.ext

fun <T> List<T>.getSelectedItems(selectedIndexes: List<Long>): List<T> {
    return selectedIndexes.map {
        this[it.toInt()]
    }
}