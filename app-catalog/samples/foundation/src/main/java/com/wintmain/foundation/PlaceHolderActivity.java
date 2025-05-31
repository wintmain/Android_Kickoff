/*
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

package com.wintmain.foundation;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import com.google.android.catalog.framework.annotations.Sample;
import lib.wintmain.wTitlebar.OnTitleBarListener;
import lib.wintmain.wTitlebar.TitleBarExt;
import lib.wintmain.wTitlebar.style.LightBarStyle;
import lib.wintmain.wToaster.toast.ToastUtils;

@Sample(
        name = "0-Android-Demo",
        description = "一个为了测试API调用的入口",
        tags = "A-Self_demos")
public class PlaceHolderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pft_activity_main);

        // 初始化一些三方库
        initLibs(getApplication());

        TitleBarExt titleBar = findViewById(R.id.tb_main_bar_click);
        titleBar.setOnTitleBarListener(
                new OnTitleBarListener() {

                    @Override
                    public void onLeftClick(TitleBarExt titleBar) {
                        ToastUtils.show("你点击了返回");
                    }

                    @Override
                    public void onTitleClick(TitleBarExt titleBar) {
                        Intent newIntent = new Intent();
                        newIntent.putExtra(AlarmClock.EXTRA_HOUR, 9);
                        newIntent.putExtra(AlarmClock.EXTRA_MINUTES, 30);
                        newIntent.setAction(AlarmClock.ACTION_SET_ALARM);
                        newIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                        newIntent.setComponent(new ComponentName("com.android.deskclock", "com.android.deskclock.HandleApiCalls"));
                        try {
                            startActivity(newIntent);
                            ToastUtils.show("你点击了中间，并且新建了一个9：30的闹钟");
                        } catch (Exception e) {
                            ToastUtils.show("你点击了中间，但是新建闹钟失败");
                        }
                    }

                    @Override
                    public void onRightClick(TitleBarExt titleBar) {
                        ToastUtils.show("你点击了设置");
                    }
                });
    }

    private void initLibs(Application application) {
        // 初始化 TitleBar 默认样式
        TitleBarExt.setDefaultStyle(
                new LightBarStyle() {
                    @Override
                    public TextView newTitleView(Context context) {
                        return new AppCompatTextView(context);
                    }

                    @Override
                    public TextView newLeftView(Context context) {
                        return new AppCompatTextView(context);
                    }

                    @Override
                    public TextView newRightView(Context context) {
                        return new AppCompatTextView(context);
                    }
                });

        // 初始化 Toast
        ToastUtils.init(this.getApplication());
    }
}
