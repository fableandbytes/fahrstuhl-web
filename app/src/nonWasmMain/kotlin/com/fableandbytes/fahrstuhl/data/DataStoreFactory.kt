package com.fableandbytes.fahrstuhl.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun createDataStore(): DataStore<Preferences>

const val DATASTORE_FILE_NAME = "fahrstuhl.preferences_pb"
