/*
 * Copyright 2023 wintmain
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

package com.wintmain.shadowlayout;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.catalog.framework.annotations.Sample;
import com.wintmain.shadowlayout.databinding.ActivityMainBinding;

@Sample(
        name = "shadowlayout",
        description = "阴影布局",
        documentation = "",
        tags = "A-Self_demos"
)
public class ShadowMainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.ShadowLayoutShadow.setOnClickListener(v -> {
            startActivity(new Intent(ShadowMainActivity.this, ShadowActivity.class));
        });
        binding.ShadowLayoutShape.setOnClickListener(v -> {
            startActivity(new Intent(ShadowMainActivity.this, ShapeActivity.class));
        });
        binding.ShadowLayoutWiki.setOnClickListener(v ->{
            startActivity(new Intent(ShadowMainActivity.this, WikiActivity.class));
        });
    }
}
