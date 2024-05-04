package com.ngxqt.mdm.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CoroutineDispatcherProvider @Inject constructor() {
    val Main: CoroutineDispatcher by lazy { Dispatchers.Main }
    val IO: CoroutineDispatcher by lazy { Dispatchers.IO }
    val Default: CoroutineDispatcher by lazy { Dispatchers.Default }
    val Unconfined: CoroutineDispatcher by lazy { Dispatchers.Unconfined }
}