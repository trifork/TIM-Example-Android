package com.trifork.timandroid.util

import android.view.View

fun viewVisibility(visible: Boolean): Int = if (visible) {
    View.VISIBLE
} else {
    View.GONE
}