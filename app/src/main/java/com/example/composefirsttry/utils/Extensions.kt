package com.example.composefirsttry.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.composefirsttry.L


fun <T> Fragment.requireActivity() = requireActivity() as T
fun <K> Fragment.getApplication() = requireActivity().application as K


//try to fix sending last data before observing. Helpful link: https://stackoverflow.com/questions/49832787/livedata-prevent-receive-the-last-value-when-start-observing
fun <T> LiveData<T>.observeFreshly(owner: LifecycleOwner, observer: Observer<in T>) {
    // extention fuction to get LiveData's version, will explain in below.
    val sinceVersion = 0
    this.observe(owner, FreshObserver<T>(observer, sinceVersion))
}

class FreshObserver<T>(
    private val delegate: Observer<in T>,
    private val sinceVersion: Int,
    private var version: Int = sinceVersion
) : Observer<T> {
    override fun onChanged(t: T) {
        L.i("FreshObserver.onChanged - version: $version, sinceVersion: $sinceVersion")
        if (version > sinceVersion) {
            delegate.onChanged(t)
        }
        version++
    }
}