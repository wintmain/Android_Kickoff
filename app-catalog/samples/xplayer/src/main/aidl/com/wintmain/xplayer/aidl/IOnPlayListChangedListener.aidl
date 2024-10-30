// IOnSongChange.aidl
package com.wintmain.xplayer.aidl;
import com.wintmain.xplayer.aidl.Song;

// Declare any non-default types here with import statements

interface IOnPlayListChangedListener {

    void onPlayListChange(in Song current,int index,int id);
}
