/*
 * Copyright 2023-2025 wintmain
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lib.wintmain.libwNet

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import lib.wintmain.libwNet.scope.AndroidScope
import lib.wintmain.libwNet.scope.NetCoroutineScope
import lib.wintmain.libwNet.time.Interval

/**
 * 在[ViewModel]被销毁时取消协程作用域
 */
fun ViewModel.scopeLife(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
): AndroidScope {
    val scope = AndroidScope(dispatcher = dispatcher).launch(block)
    addCloseable(scope)
    return scope
}

/**
 * 在[ViewModel]被销毁时取消协程作用域以及其中的网络请求
 * 具备网络错误全局处理功能, 其内部的网络请求会跟随其作用域的生命周期
 */
fun ViewModel.scopeNetLife(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
): NetCoroutineScope {
    val scope = NetCoroutineScope(dispatcher = dispatcher).launch(block)
    addCloseable(scope)
    return scope
}

/** 轮询器根据ViewModel生命周期自动取消 */
fun Interval.life(viewModel: ViewModel) = apply {
    viewModel.addCloseable(this)
}