package com.mrezanasirloo.domain.usecase

/**
 * @author : M.Reza.Nasirloo@gmail.com
 * Created on: 2018-06-09
 */
abstract class UseCase<out T> {
    abstract fun execute(): T
}
