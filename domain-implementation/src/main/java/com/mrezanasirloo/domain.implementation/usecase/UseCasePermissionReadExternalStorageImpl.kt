package com.mrezanasirloo.domain.implementation.usecase

import android.Manifest
import android.app.Application
import android.content.Context
import com.mrezanasirloo.domain.usecase.UseCasePermissionReadExternalStorage
import com.vanniktech.rxpermission.Permission
import com.vanniktech.rxpermission.RealRxPermission
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
class UseCasePermissionReadExternalStorageImpl @Inject constructor(private val context: Context)
    : UseCasePermissionReadExternalStorage<Unit, Observable<Permission>>() {

    override fun execute(parameter: Unit): Observable<Permission> {
        return RealRxPermission.getInstance(context.applicationContext as Application)
                .requestEach(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}