package com.tonyodev.dispatch.queuecontroller

import com.tonyodev.dispatch.Dispatch

/**
 * A DispatchQueueController that uses lifecycle events to manage
 * Dispatch queues. It is a class that must be extended
 * and controlled by a lifecycle aware component.
 * */
open class LifecycleDispatchQueueController: DispatchQueueController() {

    private val pausedQueueSet = mutableSetOf<Dispatch<*>>()

    private val stoppedQueueSet = mutableSetOf<Dispatch<*>>()

    private val destroyQueueSet = mutableSetOf<Dispatch<*>>()

    /**
     * Cancels all dispatch queues that are being managed by this
     * DispatchQueueController with a cancel type of CancelType.PAUSED.
     * */
    open fun cancelAllPaused() {
        super.cancelDispatch(pausedQueueSet)
        synchronized(pausedQueueSet) {
            val iterator = pausedQueueSet.iterator()
            while (iterator.hasNext()) {
                iterator.next()
                iterator.remove()
            }
        }
    }

    /**
     * Cancels all dispatch queues that are being managed by this
     * DispatchQueueController with a cancel type of CancelType.STOPPED.
     * */
    open fun cancelAllStopped() {
        super.cancelDispatch(stoppedQueueSet)
        synchronized(stoppedQueueSet) {
            val iterator = stoppedQueueSet.iterator()
            while (iterator.hasNext()) {
                iterator.next()
                iterator.remove()
            }
        }
    }

    /**
     * Cancels all dispatch queues that are being managed by this
     * DispatchQueueController with a cancel type of CancelType.DESTROYED.
     * */
    open fun cancelAllDestroyed() {
        super.cancelAllDispatch()
        cancelAllPaused()
        cancelAllStopped()
        synchronized(destroyQueueSet) {
            val iterator = destroyQueueSet.iterator()
            while (iterator.hasNext()) {
                iterator.next()
                iterator.remove()
            }
        }
    }

    override fun unmanage(dispatch: Dispatch<*>) {
        super.unmanage(dispatch)
        var iterator: MutableIterator<Dispatch<*>>
        synchronized(pausedQueueSet) {
            iterator = pausedQueueSet.iterator()
            while (iterator.hasNext()) {
                if (iterator.next() == dispatch.rootDispatch) {
                    iterator.remove()
                    return
                }
            }
        }
        synchronized(stoppedQueueSet) {
            iterator = stoppedQueueSet.iterator()
            while (iterator.hasNext()) {
                if (iterator.next() == dispatch.rootDispatch) {
                    iterator.remove()
                    return
                }
            }
        }
        synchronized(destroyQueueSet) {
            iterator = destroyQueueSet.iterator()
            while (iterator.hasNext()) {
                if (iterator.next() == dispatch.rootDispatch) {
                    iterator.remove()
                    break
                }
            }
        }
    }

    override fun unmanage(dispatchList: List<Dispatch<*>>) {
        for (dispatch in dispatchList) {
            unmanage(dispatch)
        }
    }

    override fun manage(dispatch: Dispatch<*>) {
        manage(dispatch, CancelType.DESTROYED)
    }

    /**
     * Set this dispatch controller to manage the passed in dispatch's queue with
     * the passed in cancelType.
     * @param dispatch the dispatch who's queue will be managed.
     * @param cancelType the cancel type.
     * */
    fun manage(dispatch: Dispatch<*>, cancelType: CancelType) {
        super.manage(dispatch)
        when(cancelType) {
            CancelType.PAUSED -> synchronized(pausedQueueSet) { pausedQueueSet.add(dispatch.rootDispatch) }
            CancelType.STOPPED -> synchronized(stoppedQueueSet) { stoppedQueueSet.add(dispatch.rootDispatch) }
            CancelType.DESTROYED -> synchronized(destroyQueueSet) { destroyQueueSet.add(dispatch.rootDispatch) }
        }
    }

    override fun manage(dispatchList: List<Dispatch<*>>) {
        for (dispatch in dispatchList) {
            manage(dispatch, CancelType.DESTROYED)
        }
    }

    override fun cancelAllDispatch() {
        cancelAllDestroyed()
    }

    override fun cancelDispatch(vararg arrayOfDispatch: Dispatch<*>) {
        super.cancelDispatch(*arrayOfDispatch)
        for (dispatch in arrayOfDispatch) {
            remove(dispatch.queueId)
        }
    }

    override fun cancelDispatch(dispatchQueueIds: List<Int>) {
        super.cancelDispatch(dispatchQueueIds)
        for (queueId in dispatchQueueIds) {
            remove(queueId)
        }
    }

    override fun cancelDispatch(vararg arrayOfDispatchQueueId: Int) {
        super.cancelDispatch(*arrayOfDispatchQueueId)
        for (queueId in arrayOfDispatchQueueId) {
            remove(queueId)
        }
    }

    override fun cancelDispatch(dispatchCollection: Collection<Dispatch<*>>) {
        for (dispatch in dispatchCollection) {
            super.cancelDispatch(dispatch)
            remove(dispatch.queueId)
        }
    }

    private fun remove(queueId: Int) {
        synchronized(pausedQueueSet) {
            val iterator = pausedQueueSet.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().queueId == queueId) {
                    iterator.remove()
                    return
                }
            }
        }
        synchronized(stoppedQueueSet) {
            val iterator = stoppedQueueSet.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().queueId == queueId) {
                    iterator.remove()
                    return
                }
            }
        }
        synchronized(destroyQueueSet) {
            val iterator = destroyQueueSet.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().queueId == queueId) {
                    iterator.remove()
                    break
                }
            }
        }
    }

    /**
     * Gets a copy of the paused managed queues.
     * @return copy of the paused manged queues in a set.
     * */
    fun getManagedPausedQueueDispatches(): Set<Dispatch<*>> {
        return synchronized(pausedQueueSet) { pausedQueueSet.toSet() }
    }

    /**
     * Gets a copy of the stopped managed queues.
     * @return copy of the stopped manged queues in a set.
     * */
    fun getManagedStoppedQueueDispatches(): Set<Dispatch<*>> {
        return synchronized(stoppedQueueSet) { stoppedQueueSet.toSet() }
    }

    /**
     * Gets a copy of the destroyed managed queues.
     * @return copy of the destroyed manged queues in a set.
     * */
    fun getManagedDestroyedQueueDispatches(): Set<Dispatch<*>> {
        return synchronized(destroyQueueSet) { destroyQueueSet.toSet() }
    }

}