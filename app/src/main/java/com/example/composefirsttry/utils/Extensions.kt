package com.example.composefirsttry.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.*


fun <T> Fragment.requireActivity() = requireActivity() as T
fun <K> Fragment.getApplication() = requireActivity().application as K

fun <T> LiveData<T>.toMutableLiveData(): MutableLiveData<T> {
    val mediatorLiveData = MediatorLiveData<T>()
    mediatorLiveData.addSource(this) {
        mediatorLiveData.value = it
    }
    return mediatorLiveData
}

//try to fix sending last data before observing. Helpful link: https://stackoverflow.com/questions/49832787/livedata-prevent-receive-the-last-value-when-start-observing
fun <T> LiveData<T>.observeFreshly(owner: LifecycleOwner, observer: Observer<in T>): Observer<T> {
    val sinceVersion = 0
    val freshObserver = FreshObserver<T>(observer, sinceVersion)
    this.observe(owner, freshObserver)
    return freshObserver
}
fun <T> LiveData<T>.observeForeverFreshly(observer: Observer<in T>): Observer<T> {
    val sinceVersion = 0
    val freshObserver = FreshObserver<T>(observer, sinceVersion)
    this.observeForever(freshObserver)
    return freshObserver
}

class FreshObserver<T>(
    private val delegate: Observer<in T>,
    private val sinceVersion: Int,
    private var version: Int = sinceVersion
) : Observer<T> {
    override fun onChanged(t: T) {
//        L.i("FreshObserver.onChanged - version: $version, sinceVersion: $sinceVersion")
        if (version > sinceVersion) {
            delegate.onChanged(t)
        }
        version++
    }
}