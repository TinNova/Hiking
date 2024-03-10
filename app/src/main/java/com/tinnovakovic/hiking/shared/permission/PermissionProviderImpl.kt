package com.tinnovakovic.hiking.shared.permission

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.tinnovakovic.hiking.shared.ContextProvider
import javax.inject.Inject

class PermissionProviderImpl @Inject constructor(
    private val contextProvider: ContextProvider,
): PermissionProvider {

    override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            contextProvider.getContext().applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    contextProvider.getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

}
