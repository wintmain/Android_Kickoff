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

package com.wintmain.wNet.ui.fragment

import com.drake.engine.base.EngineFragment
import com.wintmain.wNet.R
import com.wintmain.wNet.databinding.FragmentReadCacheBinding
import lib.wintmain.libwNet.Post
import lib.wintmain.libwNet.cache.CacheMode
import lib.wintmain.libwNet.utils.scopeNetLife

/**
 * 默认支持Http标准缓存协议
 * 如果需要自定义缓存模式来强制读写缓存，可以使用[CacheMode], 这会覆盖默认的Http标准缓存协议.
 * 可以缓存任何数据, 包括文件. 并且遵守LRU缓存策略限制最大缓存空间
 */
class ReadCacheFragment : EngineFragment<FragmentReadCacheBinding>(R.layout.fragment_read_cache) {

    override fun initView() {
        scopeNetLife {
            binding.tvFragment.text =
                Post<String>(com.wintmain.wNet.constants.Api.TEXT) {
                    setCacheMode(CacheMode.REQUEST_THEN_READ) // 请求网络失败会读取缓存, 请断网测试
                    // setCacheKey("自定义缓存KEY")
                }.await()
        }
    }

    override fun initData() {
    }
}
