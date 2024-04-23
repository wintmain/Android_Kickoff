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

package com.wintmain.mymusicplayer.aidl;

import android.os.IBinder;

/** 服务端的播放列表改变时回调 1 改变歌单时回调 2 从歌单中移除歌曲时回调 */
public abstract class OnPlayListChangedListener extends IOnPlayListChangedListener.Stub {

    @Override
    public IBinder asBinder() {
        return super.asBinder();
    }

    /**
     * 服务端的播放列表改变时回调
     *
     * @param current 当前曲目
     * @param index   曲目下标
     * @param id      歌单 id
     */
    @Override
    public abstract void onPlayListChange(Song current, int index, int id);
}
