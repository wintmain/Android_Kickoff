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

package lib.wintmain.wPermission;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * desc : Android 13 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_13)
class PermissionDelegateImplV33 extends PermissionDelegateImplV31 {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.BODY_SENSORS_BACKGROUND)) {
            // 有后台传感器权限的前提条件是要有前台的传感器权限
            return PermissionUtils.checkSelfPermission(context, Permission.BODY_SENSORS)
                    && PermissionUtils.checkSelfPermission(
                    context, Permission.BODY_SENSORS_BACKGROUND);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS)
                || PermissionUtils.equalsPermission(permission, Permission.NEARBY_WIFI_DEVICES)
                || PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_IMAGES)
                || PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VIDEO)
                || PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_AUDIO)) {
            return PermissionUtils.checkSelfPermission(context, permission);
        }

        if (AndroidVersion.getTargetSdkVersionCode(context) >= AndroidVersion.ANDROID_13) {
            // 亲测当这两个条件满足的时候，在 Android 13 不能申请 WRITE_EXTERNAL_STORAGE，会被系统直接拒绝
            // 不会弹出系统授权对话框，框架为了保证不同 Android 版本的回调结果一致性，这里直接返回 true 给到外层
            if (PermissionUtils.equalsPermission(permission, Permission.WRITE_EXTERNAL_STORAGE)) {
                return true;
            }

            if (PermissionUtils.equalsPermission(permission, Permission.READ_EXTERNAL_STORAGE)) {
                return PermissionUtils.checkSelfPermission(context, Permission.READ_MEDIA_IMAGES)
                        && PermissionUtils.checkSelfPermission(context, Permission.READ_MEDIA_VIDEO)
                        && PermissionUtils.checkSelfPermission(
                        context, Permission.READ_MEDIA_AUDIO);
            }
        }

        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isPermissionPermanentDenied(
            @NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.BODY_SENSORS_BACKGROUND)) {
            if (!PermissionUtils.checkSelfPermission(activity, Permission.BODY_SENSORS)) {
                return !PermissionUtils.shouldShowRequestPermissionRationale(
                        activity, Permission.BODY_SENSORS);
            }
            return !PermissionUtils.checkSelfPermission(activity, permission)
                    && !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS)
                || PermissionUtils.equalsPermission(permission, Permission.NEARBY_WIFI_DEVICES)
                || PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_IMAGES)
                || PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VIDEO)
                || PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_AUDIO)) {
            return !PermissionUtils.checkSelfPermission(activity, permission)
                    && !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }

        if (AndroidVersion.getTargetSdkVersionCode(activity) >= AndroidVersion.ANDROID_13) {

            if (PermissionUtils.equalsPermission(permission, Permission.WRITE_EXTERNAL_STORAGE)) {
                return false;
            }

            if (PermissionUtils.equalsPermission(permission, Permission.READ_EXTERNAL_STORAGE)) {
                return !PermissionUtils.checkSelfPermission(activity, Permission.READ_MEDIA_IMAGES)
                        && !PermissionUtils.shouldShowRequestPermissionRationale(
                        activity, Permission.READ_MEDIA_IMAGES)
                        && !PermissionUtils.checkSelfPermission(
                        activity, Permission.READ_MEDIA_VIDEO)
                        && !PermissionUtils.shouldShowRequestPermissionRationale(
                        activity, Permission.READ_MEDIA_VIDEO)
                        && !PermissionUtils.checkSelfPermission(
                        activity, Permission.READ_MEDIA_AUDIO)
                        && !PermissionUtils.shouldShowRequestPermissionRationale(
                        activity, Permission.READ_MEDIA_AUDIO);
            }
        }

        return super.isPermissionPermanentDenied(activity, permission);
    }
}
