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

import android.view.View
import com.drake.engine.base.EngineFragment
import com.wintmain.wNet.R
import com.wintmain.wNet.databinding.FragmentTimingRequestBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import lib.wintmain.libwNet.Get
import lib.wintmain.libwNet.utils.scopeNetLife
import org.json.JSONObject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class TimingRequestFragment :
    EngineFragment<FragmentTimingRequestBinding>(R.layout.fragment_timing_request) {

    private var scope: CoroutineScope? = null

    override fun initView() {
        binding.v = this
    }

    override fun initData() {
    }

    override fun onClick(v: View) {
        when (v) {
            binding.btnRepeat -> repeatRequest()
            binding.infinityRepeat -> infinityRequest()
            binding.btnCancel -> scope?.cancel()
        }
    }

    /** 重复请求10次 */
    private fun repeatRequest() {
        scope?.cancel()
        scope = scopeNetLife {
            // 每两秒请求一次, 总共执行10次
            repeat(20) {
                delay(1000)
                val data =
                    Get<String>("http://api.k780.com/?app=life.time&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json").await()
                binding.tvContent.text =
                    JSONObject(data).getJSONObject("result").getString("datetime_2")
                // 通过return@repeat可以终止循环
            }
        }
    }

    /** 无限次数请求 */
    private fun infinityRequest() {
        scope?.cancel()
        scope = scopeNetLife {
            // 每两秒请求一次, 总共执行10次
            while (true) {
                delay(1.toDuration(DurationUnit.SECONDS))
                val data =
                    Get<String>("http://api.k780.com/?app=life.time&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json").await()
                binding.tvContent.text =
                    JSONObject(data).getJSONObject("result").getString("datetime_2")
                // 通过break可以终止循环
            }
        }
    }
}