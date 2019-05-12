package com.tonyodev.dispatch.thread

import com.tonyodev.dispatch.ThreadType

/**
 * The default ThreadHandler Factory used by the library.
 * */
class DefaultThreadHandlerFactory: ThreadHandlerFactory {

    @Volatile
    private var newThreadCount = 0
    private val mainThread = Thread.currentThread()

    override fun create(threadType: ThreadType): ThreadHandler {
        return when(threadType) {
            ThreadType.BACKGROUND -> DefaultThreadHandler("dispatchBackground")
            ThreadType.BACKGROUND_SECONDARY -> DefaultThreadHandler("dispatchBackgroundSecondary")
            ThreadType.NETWORK -> DefaultThreadHandler("dispatchNetwork")
            ThreadType.IO -> DefaultThreadHandler("dispatchIO")
            ThreadType.NEW -> getNewDispatchHandler()
            ThreadType.MAIN -> DefaultThreadHandler("dispatchMain", mainThread)
            ThreadType.TEST -> TestThreadHandler("dispatchTest")
        }
    }

    override fun create(threadName: String?): ThreadHandler {
        return getNewDispatchHandler(threadName)
    }

    private fun getNewDispatchHandler(name: String? = null): ThreadHandler {
        val threadName = if (name == null || name.isEmpty()) {
            "dispatch${++newThreadCount}"
        } else {
            name
        }
        return DefaultThreadHandler(threadName)
    }

}