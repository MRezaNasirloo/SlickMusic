package com.mrezanasirloo.slickmusic.presentation.ui.main

import com.mrezanasirloo.domain.implementation.usecase.UseCasePermissionReadExternalStorageImpl
import com.mrezanasirloo.slick.uni.PartialViewState
import com.mrezanasirloo.slick.uni.SlickPresenterUni
import com.vanniktech.rxpermission.Permission
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.Function
import javax.inject.Inject
import javax.inject.Named

class PresenterMain @Inject constructor(
        private val readPermission: UseCasePermissionReadExternalStorageImpl,
        @Named("main") main: Scheduler?,
        @Named("io") io: Scheduler?
) : SlickPresenterUni<ViewMain, StateMain>(main, io) {
    override fun start(view: ViewMain) {
        @Suppress("RedundantSamConstructor")
        val permission: Observable<PartialViewState<StateMain>> = command(ViewMain::commandPermission)
                .startWith(1)
                .flatMap { readPermission.execute().toObservable() }
                .map(Function<Permission, PartialViewState<StateMain>> { PartialStatePermission(it.state()) })
                .onErrorReturn { PartialStateError(it) }

        subscribe(StateMain(), permission)
    }

    override fun render(state: StateMain, view: ViewMain) {
        println("state = [${state}], view = [${view}]")
        view.apply {
            if (state.permissionRequested) {
                when (state.permissionState) {
                    Permission.State.GRANTED -> showPages()
                    Permission.State.DENIED -> showRational()
                    Permission.State.DENIED_NOT_SHOWN -> showRationalSettingPage()
                    Permission.State.REVOKED_BY_POLICY -> showError(Throwable("REVOKED_BY_POLICY"))
                }
            }
            state.error?.let {
                //TODO Handle errors here
                showError(it)
            }
        }

    }

}

data class StateMain(
        val permissionRequested: Boolean = false,
        val permissionState: Permission.State = Permission.State.DENIED,
        val error: Throwable? = null
)

class PartialStatePermission(private val permissionState: Permission.State) : PartialViewState<StateMain> {
    override fun reduce(state: StateMain?): StateMain {
        return state!!.copy(permissionState = permissionState, permissionRequested = true)
    }
}

class PartialStateError(private val error: Throwable) : PartialViewState<StateMain> {
    override fun reduce(state: StateMain?): StateMain {
        return state!!.copy(error = error)
    }
}
