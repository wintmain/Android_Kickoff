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

package lib.wintmain.toaster.toast;

import android.app.Application;

/**
 * @Description 不需要通知栏权限的 Toast
 * @Author wintmain
 * @mailto wosintmain@gmail.com>
 * @Date 2022-11-27 16:58:05
 */
final class SupportToast extends BaseToast {

    // 吐司弹窗显示辅助类
    private ToastHelper mToastHelper;

    SupportToast(Application application) {
        super(application);
        mToastHelper = new ToastHelper(this, application);
    }

    @Override
    public void show() {
        // 移除之前显示吐司的任务
        getHandler().removeCallbacks(this);
        // 添加一个显示吐司的任务
        getHandler().postDelayed(this, SHOW_DELAY_MILLIS);
    }

    /** {@link Runnable} */
    @Override
    public void run() {
        // 设置吐司文本
        getMessageView().setText(getText());
        // 显示吐司
        mToastHelper.show();
    }

    @Override
    public void cancel() {
        // 移除之前显示吐司的任务
        getHandler().removeCallbacks(this);
        // 取消显示
        mToastHelper.cancel();
    }
}
