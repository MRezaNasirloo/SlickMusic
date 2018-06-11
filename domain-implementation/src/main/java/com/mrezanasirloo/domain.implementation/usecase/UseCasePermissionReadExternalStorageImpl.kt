package com.mrezanasirloo.domain.implementation.usecase

import android.Manifest
import android.app.Application
import android.content.Context
import com.mrezanasirloo.domain.usecase.UseCasePermissionReadExternalStorage
import com.vanniktech.rxpermission.Permission
import com.vanniktech.rxpermission.RealRxPermission
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
class UseCasePermissionReadExternalStorageImpl @Inject constructor(private val context: Context)
    : UseCasePermissionReadExternalStorage<Unit, Single<Permission>>() {

    override fun execute(parameter: Unit): Single<Permission> {
        return RealRxPermission.getInstance(context.applicationContext as Application)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}