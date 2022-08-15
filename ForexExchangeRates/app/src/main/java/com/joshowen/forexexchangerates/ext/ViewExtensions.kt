package com.joshowen.forexexchangerates.ext

import android.view.View

fun View.display() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}