package com.tt.ox

import android.app.Application
import com.tt.ox.database.OpponentDatabase

class OXApplication : Application() {
    val database: OpponentDatabase by lazy { OpponentDatabase.getDatabase(this) }
}