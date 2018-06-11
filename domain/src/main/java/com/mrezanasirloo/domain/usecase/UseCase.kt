package com.mrezanasirloo.domain.usecase

/**
 * @author : M.Reza.Nasirloo@gmail.com
 * Created on: 2018-06-09
 */
abstract class UseCase<in P, out R> {
    abstract fun execute(parameter: P): R
}
