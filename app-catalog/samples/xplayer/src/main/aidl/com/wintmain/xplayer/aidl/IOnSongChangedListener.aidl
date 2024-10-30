// IOnSongChange.aidl
package com.wintmain.xplayer.aidl;
import com.wintmain.xplayer.aidl.Song;

// Declare any non-default types here with import statements

interface IOnSongChangedListener {

    //该方法运行在线程池中（非 UI 线程）
    void onSongChange(in Song which,int index,boolean isNext);
}
