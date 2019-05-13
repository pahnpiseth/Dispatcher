package com.tonyodev.dispatch.queuecontroller

import com.tonyodev.dispatch.Dispatch

/**
 * A DispatchQueueController manages dispatch queues and is
 * responsible for cancelling a dispatch queue at the appropriate time.
 * Note: DispatchQueueController only holds a strong reference to the root dispatch
 * */
open class DispatchQueueController {

    private val dispatchQueueSet = mutableSetOf<Dispatch<*>>()

    /**
     * Set this dispatch controller to manage the passed in dispatch's queue.
     * It only holds the root dispatch.
     * @param dispatch the dispatch who's queue will be managed.
     * */
    open fun manage(dispatch: Dispatch<*>) {
        synchronized(dispatchQueueSet) {
            dispatchQueueSet.add(dispatch.rootDispatch)
        }
    }

    /**
     * Set this dispatch controller to manage the passed in dispatch's queue.
     * @param dispatchList a list of dispatch who's queue will be managed.
     * */
    open fun manage(dispatchList: List<Dispatch<*>>) {
        for (dispatch in dispatchList) {
            manage(dispatch)
        }
    }

    /**
     * Stop managing the passed in dispatch's queue.
     * @param dispatch the dispatch who's queue to unmanage.
     * */
    open fun unmanage(dispatch: Dispatch<*>) {
        synchronized(dispatchQueueSet) {
            val iterator = dispatchQueueSet.iterator()
            while (iterator.hasNext()) {
                if (iterator.next() == dispatch.rootDispatch) {
                    iterator.remove()
                }
            }
        }
    }

    /**
     * Stop managing the passed in list dispatch's queue.
     * @param dispatchList the list of dispatch who's queue to unmanage.
     * */
    open fun unmanage(dispatchList: List<Dispatch<*>>) {
        synchronized(dispatchQueueSet) {
            val iterator = dispatchQueueSet.iterator()
            var count = 0
            while (iterator.hasNext()) {
                if (dispatchList.contains(iterator.next().rootDispatch)) {
                    iterator.remove()
                    ++count
                    if (count == dispatchList.size) {
                        break
                    }
                }
            }
        }
    }

    /**
     * Cancels all the dispatch queues that are being managed by this
     * instance of DispatchQueueController. All the dispatch queues will then be unmanaged.
     * */
    open fun cancelAllDispatch() {
        synchronized(dispatchQueueSet) {
            val iterator = dispatchQueueSet.iterator()
            var dispatch: Dispatch<*>
            while (iterator.hasNext()) {
                dispatch = iterator.next()
                iterator.remove()
                dispatch.cancel()
            }
        }
    }

    /**
     * Cancels all dispatch queues being managed by this
     * instance of DispatchQueueController if it is in the passed in array.
     * The dispatch queue will no longer be managed.
     * @param arrayOfDispatch dispatch objects.
     * */
    open fun cancelDispatch(vararg arrayOfDispatch: Dispatch<*>) {
        cancelDispatch(arrayOfDispatch.map { it.rootDispatch })
    }

    /**
     * Cancels all dispatch queues being managed by this
     * instance of DispatchQueueController if it is in the passed in collection.
     * The dispatch queue will no longer be managed.
     * @param dispatchCollection dispatch objects.
     * */
    open fun cancelDispatch(dispatchCollection: Collection<Dispatch<*>>) {
        synchronized(dispatchQueueSet) {
            val iterator = dispatchQueueSet.iterator()
            var dispatch: Dispatch<*>
            var count = 0
            while (iterator.hasNext()) {
                dispatch = iterator.next()
                if (dispatchCollection.contains(dispatch)) {
                    iterator.remove()
                    dispatch.cancel()
                    ++count
                    if (count == dispatchCollection.size) {
                        break
                    }
                }
            }
        }
    }

    /**
     * Cancels dispatch queues being managed by this
     * instance of DispatchQueueController if it's queue id is in the passed in list.
     * The dispatch queue will no longer be managed.
     * @param dispatchQueueIds the dispatch queue ids.
     * */
    open fun cancelDispatch(dispatchQueueIds: List<Int>) {
        synchronized(dispatchQueueSet) {
            val iterator = dispatchQueueSet.iterator()
            var dispatch: Dispatch<*>
            var count = 0
            while (iterator.hasNext()) {
                dispatch = iterator.next()
                if (dispatchQueueIds.contains(dispatch.queueId)) {
                    iterator.remove()
                    dispatch.cancel()
                    ++count
                    if (count == dispatchQueueIds.size) {
                        break
                    }
                }
            }
        }
    }

    /**
     * Cancels dispatch queue being managed by this
     * instance of DispatchQueueController if it's queue id is in the passed in array.
     * The dispatch objects will no longer be managed.
     * @param arrayOfDispatchQueueId the dispatch objects queue ids.
     * */
    open fun cancelDispatch(vararg arrayOfDispatchQueueId: Int) {
        cancelDispatch(arrayOfDispatchQueueId.toList())
    }

    /**
     * Gets a copy of the managed queues.
     * @return copy of the manged queues in a set.
     * */
    fun getManagedQueueDispatches(): Set<Dispatch<*>> {
        return synchronized(dispatchQueueSet) { dispatchQueueSet.toSet() }
    }

}