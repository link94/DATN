package com.ngxqt.mdm.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

internal fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

internal fun isUrlValid(url: String): Boolean {
//    val regex = Regex("^(http|https)://[\\w.-]+\\.com$")
    val regex = Regex("^(http|https)://bvdemo\\.ibme\\.edu\\.vn.*$")
    return regex.matches(url)
}

internal fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
    observe(owner, object : Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}