package au.com.jayne.dogquiz.common.dagger.viewmodel

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

@Keep
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)