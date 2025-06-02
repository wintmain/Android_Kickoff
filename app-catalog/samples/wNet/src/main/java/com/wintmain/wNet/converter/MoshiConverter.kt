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

package com.wintmain.wNet.converter

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import lib.wintmain.libwNet.convert.JSONConvert
import org.json.JSONObject
import java.lang.reflect.Type

class MoshiConverter : JSONConvert(code = "errorCode", message = "errorMsg", success = "0") {

    companion object {
        private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    override fun <R> String.parseBody(succeed: Type): R? {
        val string = try {
            JSONObject(this).getString("data")
        } catch (e: Exception) {
            this
        }
        return moshi.adapter<R>(succeed).fromJson(string)
    }
}