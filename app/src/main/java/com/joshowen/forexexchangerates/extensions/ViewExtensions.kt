package com.joshowen.forexexchangerates.extensions

import android.view.View

internal fun View.display() {
    this.visibility = View.VISIBLE
}

internal fun View.hide() {
    this.visibility = View.INVISIBLE
}