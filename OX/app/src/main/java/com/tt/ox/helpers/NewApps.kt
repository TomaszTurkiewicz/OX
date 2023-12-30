package com.tt.ox.helpers

class NewApps {
    var appsInGooglePlay = 0
    var appsSavedInMemory = 0

    fun setAppsInGooglePlayInt(numberOfApps: GooglePlayApps){
        this.appsInGooglePlay = numberOfApps.numberOfApps
    }

    fun setAppsInMemoryInt(numberOfAppsInMemory:Int){
        this.appsSavedInMemory = numberOfAppsInMemory
    }

    fun isNewApp():Boolean{
        return appsInGooglePlay>appsSavedInMemory
    }
}