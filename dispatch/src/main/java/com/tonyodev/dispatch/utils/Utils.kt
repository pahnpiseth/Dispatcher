package com.tonyodev.dispatch.utils

import com.tonyodev.dispatch.ThreadType
import com.tonyodev.dispatch.thread.ThreadHandler
import java.lang.IllegalArgumentException
import java.util.*

internal fun getNewQueueId(): Int {
    return UUID.randomUUID().hashCode()
}

internal fun getNewDispatchId(): String {
    return UUID.randomUUID().toString()
}

internal fun throwIfUsesMainThreadForBackgroundWork(handler: ThreadHandler?) {
    if (handler != null && handler.threadName == Threader.uiHandler.threadName) {
        throw IllegalArgumentException("Dispatch handler cannot use the main thread to perform background work." +
                "Pass in a handler that uses a different thread.")
    }
}

internal fun throwIfUsesMainThreadForBackgroundWork(threadType: ThreadType) {
    if (threadType == ThreadType.MAIN) {
        throw IllegalArgumentException("Dispatch handler cannot use the main thread to perform background work." +
                "Pass in a handler that uses a different thread.")
    }
}