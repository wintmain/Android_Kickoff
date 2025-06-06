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

package lib.wintmain.libwNet.convert

import lib.wintmain.libwNet.exception.ConvertException
import lib.wintmain.libwNet.response.file
import okhttp3.Response
import okio.ByteString
import java.io.File
import java.lang.reflect.GenericArrayType
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
interface NetConverter {

    @Throws(Throwable::class)
    fun <R> onConvert(succeed: Type, response: Response): R?

    companion object DEFAULT : NetConverter {
        /**
         * 返回结果应当等于泛型对象, 可空
         * @param succeed 请求要求返回的泛型类型
         * @param response 请求响应对象
         */
        override fun <R> onConvert(succeed: Type, response: Response): R? {
            return when {
                succeed === String::class.java && response.isSuccessful -> response.body?.string() as R
                succeed === ByteString::class.java && response.isSuccessful -> response.body?.byteString() as R
                succeed is GenericArrayType && succeed.genericComponentType === Byte::class.java && response.isSuccessful -> response.body?.bytes() as R
                succeed === File::class.java && response.isSuccessful -> response.file() as R
                succeed === Response::class.java -> response as R
                else -> throw ConvertException(
                    response,
                    "An exception occurred while converting the NetConverter.DEFAULT"
                )
            }
        }
    }
}