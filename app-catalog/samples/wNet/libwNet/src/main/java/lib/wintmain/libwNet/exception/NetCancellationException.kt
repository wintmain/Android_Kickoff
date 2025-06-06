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

package lib.wintmain.libwNet.exception

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import lib.wintmain.libwNet.Net
import java.util.concurrent.CancellationException

/**
 * 取消网络任务的异常
 */
class NetCancellationException(
    coroutineScope: CoroutineScope,
    message: String? = null,
) : CancellationException(message) {
    init {
        Net.cancelGroup(coroutineScope.coroutineContext[CoroutineExceptionHandler])
    }
}

/**
 * 在作用域中抛出该异常将取消其作用域内所有的网络请求(如果存在的话)
 */
@Suppress("FunctionName")
fun CoroutineScope.NetCancellationException(message: String? = null): NetCancellationException {
    return NetCancellationException(this, message)
}