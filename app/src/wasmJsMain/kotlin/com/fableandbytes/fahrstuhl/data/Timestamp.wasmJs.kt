package com.fableandbytes.fahrstuhl.data

import kotlinx.datetime.Clock

actual fun getCurrentTimestamp(): Long = Clock.System.now().toEpochMilliseconds()
