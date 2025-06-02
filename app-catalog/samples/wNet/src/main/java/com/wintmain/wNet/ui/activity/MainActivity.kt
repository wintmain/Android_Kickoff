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

package com.wintmain.wNet.ui.activity

import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.drake.engine.base.EngineActivity
import com.drake.statusbar.immersive
import com.wintmain.wNet.R
import com.wintmain.wNet.databinding.ActivityMainBinding

/**
 * 以下代码设置导航, 和框架本身无关无需关心, 请查看[com.wintmain.wNet.ui.fragment]内的Fragment
 */
class MainActivity : EngineActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun initView() {
        immersive(binding.toolbar, true)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav)

        Glide.with(this)
            .load("https://avatars.githubusercontent.com/u/21078112?v=4")
            .circleCrop()
            .into(binding.drawerNav.getHeaderView(0).findViewById(R.id.iv))

        binding.toolbar.setupWithNavController(
            navController,
            AppBarConfiguration(binding.drawerNav.menu, binding.drawer)
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.subtitle =
                (destination as FragmentNavigator.Destination).className.substringAfterLast('.')
        }
        binding.drawerNav.setupWithNavController(navController)
    }

    override fun initData() {
    }

    override fun onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) binding.drawer.closeDrawers() else super.onBackPressed()
    }
}
