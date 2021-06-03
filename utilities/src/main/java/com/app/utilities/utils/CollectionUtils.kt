package com.app.utilities.utils

object CollectionUtils {
    fun isEmpty(coll: Collection<*>?): Boolean {
        return coll == null || coll.isEmpty()
    }

    fun isNotEmpty(coll: Collection<*>?): Boolean {
        return !isEmpty(coll)
    }
}
