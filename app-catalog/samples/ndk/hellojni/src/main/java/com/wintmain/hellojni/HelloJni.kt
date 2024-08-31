/*
 * Copyright (C) 2016 The Android Open Source Project
 * Copyright 2023-2024 wintmain
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
package com.wintmain.hellojni

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.catalog.framework.annotations.Sample
import com.wintmain.hellojni.databinding.ActivityHelloJniBinding

@Sample(name = "hellojni", description = "JNI第一个demo示例",
    documentation = "", tags = ["android-samples", "NDK"])
class HelloJni : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
         * Retrieve our TextView and set its content.
         * the text is retrieved by calling a native
         * function.
         */
        val binding = ActivityHelloJniBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.helloTextview.text = stringFromJNI()
        binding.unimplementedButton.setOnClickListener {
            try {
                unimplementedStringFromJNI()
            } catch (ex: Exception) {
                println("ex: $ex")
            }
        }
    }

    /*
     * A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    private external fun stringFromJNI(): String?

    /*
     * This is another native method declaration that is *not*
     * implemented by 'hello-jni'. This is simply to show that
     * you can declare as many native methods in your Java code
     * as you want, their implementation is searched in the
     * currently loaded native libraries only the first time
     * you call them.
     *
     * Trying to call this function will result in a
     * java.lang.UnsatisfiedLinkError exception !
     */
    external fun unimplementedStringFromJNI(): String?

    companion object {
        /*
         * This is used to load the 'hello-jni' library on application startup.
         * The library has already been unpacked into xxx/libhello-jni.so
         * at the installation time by the package manager.
         */
        init {
            System.loadLibrary("hello-jni")
        }
    }
}
