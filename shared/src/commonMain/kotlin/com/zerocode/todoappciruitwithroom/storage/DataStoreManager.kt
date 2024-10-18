package com.zerocode.todoappciruitwithroom.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        val IS_GRID_VIEW_LISTING_TYPE_KEY = booleanPreferencesKey("key_is_grid_view_listing_type")
    }

    val isGridViewListingType: Flow<Boolean> = dataStore.data.map {
        it[IS_GRID_VIEW_LISTING_TYPE_KEY] ?: false
    }

    suspend fun setIsGridViewListingType(value: Boolean) {
        dataStore.edit {
            it[IS_GRID_VIEW_LISTING_TYPE_KEY] = value
        }
    }
}