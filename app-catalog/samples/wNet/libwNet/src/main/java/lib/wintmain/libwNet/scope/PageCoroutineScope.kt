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

package lib.wintmain.libwNet.scope

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.drake.brv.PageRefreshLayout
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import lib.wintmain.libwNet.NetConfig

@Suppress("unused", "MemberVisibilityCanBePrivate", "NAME_SHADOWING")
class PageCoroutineScope(
    val page: PageRefreshLayout,
    dispatcher: CoroutineDispatcher = Dispatchers.Main
) : NetCoroutineScope(dispatcher = dispatcher) {

    val index get() = page.index

    init {
        page.findViewTreeLifecycleOwner()?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) cancel()
            }
        })
    }

    override fun start() {
        previewEnabled = !page.loaded
        page.trigger()
    }

    override fun previewFinish(succeed: Boolean) {
        super.previewFinish(succeed)
        if (succeed && previewBreakLoading) {
            page.showContent()
        }
        page.loaded = succeed
    }

    override fun catch(e: Throwable) {
        super.catch(e)
        page.showError(e)
    }

    override fun finally(e: Throwable?) {
        super.finally(e)
        if (e == null || e is CancellationException) {
            page.showContent()
        }
        page.trigger()
    }

    override fun handleError(e: Throwable) {
        if (page.loaded || !page.stateEnabled) {
            NetConfig.errorHandler.onError(e)
        } else {
            NetConfig.errorHandler.onStateError(e, page)
        }
    }
}