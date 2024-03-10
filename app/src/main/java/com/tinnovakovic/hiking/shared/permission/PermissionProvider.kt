package com.tinnovakovic.hiking.shared.permission

interface PermissionProvider {

    fun hasLocationPermission(): Boolean

}