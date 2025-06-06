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

package lib.wintmain.wBasis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import lib.wintmain.wBasis.action.*;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/** desc : Dialog 技术基类 */
public class BaseDialog extends AppCompatDialog
        implements LifecycleOwner,
        ActivityAction,
        ResourcesAction,
        HandlerAction,
        ClickAction,
        AnimAction,
        KeyboardAction,
        DialogInterface.OnShowListener,
        DialogInterface.OnCancelListener,
        DialogInterface.OnDismissListener {

    private final ListenersWrapper<BaseDialog> mListeners = new ListenersWrapper<>(this);
    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

    @Nullable
    private List<BaseDialog.OnShowListener> mShowListeners;
    @Nullable
    private List<BaseDialog.OnCancelListener> mCancelListeners;
    @Nullable
    private List<BaseDialog.OnDismissListener> mDismissListeners;

    public BaseDialog(Context context) {
        this(context, R.style.BaseDialogTheme);
    }

    public BaseDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    /** 获取 Dialog 的根布局 */
    public View getContentView() {
        View contentView = findViewById(Window.ID_ANDROID_CONTENT);
        if (contentView instanceof ViewGroup && ((ViewGroup) contentView).getChildCount() == 1) {
            return ((ViewGroup) contentView).getChildAt(0);
        }
        return contentView;
    }

    /** 设置 Dialog 宽度 */
    public void setWidth(int width) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        window.setAttributes(params);
    }

    /** 设置 Dialog 高度 */
    public void setHeight(int height) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = height;
        window.setAttributes(params);
    }

    /** 设置水平偏移 */
    public void setXOffset(int offset) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = offset;
        window.setAttributes(params);
    }

    /** 设置垂直偏移 */
    public void setYOffset(int offset) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.y = offset;
        window.setAttributes(params);
    }

    /** 获取 Dialog 重心 */
    public int getGravity() {
        Window window = getWindow();
        if (window == null) {
            return Gravity.NO_GRAVITY;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        return params.gravity;
    }

    /** 设置 Dialog 重心 */
    public void setGravity(int gravity) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setGravity(gravity);
    }

    /** 获取 Dialog 的动画 */
    public int getWindowAnimations() {
        Window window = getWindow();
        if (window == null) {
            return BaseDialog.ANIM_DEFAULT;
        }
        return window.getAttributes().windowAnimations;
    }

    /** 设置 Dialog 的动画 */
    public void setWindowAnimations(@StyleRes int id) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setWindowAnimations(id);
    }

    /** 设置背景遮盖层开关 */
    public void setBackgroundDimEnabled(boolean enabled) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        if (enabled) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

    /** 设置背景遮盖层的透明度（前提条件是背景遮盖层开关必须是为开启状态） */
    public void setBackgroundDimAmount(@FloatRange(from = 0.0, to = 1.0) float dimAmount) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setDimAmount(dimAmount);
    }

    @Override
    public void dismiss() {
        removeCallbacks();
        View focusView = getCurrentFocus();
        if (focusView != null) {
            getSystemService(InputMethodManager.class)
                    .hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
        super.dismiss();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }

    /**
     * 设置一个显示监听器
     *
     * @param listener 显示监听器对象
     * @deprecated 请使用 {@link #addOnShowListener(BaseDialog.OnShowListener)}}
     */
    @Deprecated
    @Override
    public void setOnShowListener(@Nullable DialogInterface.OnShowListener listener) {
        if (listener == null) {
            return;
        }
        addOnShowListener(new ShowListenerWrapper(listener));
    }

    /**
     * 设置一个取消监听器
     *
     * @param listener 取消监听器对象
     * @deprecated 请使用 {@link #addOnCancelListener(BaseDialog.OnCancelListener)}
     */
    @Deprecated
    @Override
    public void setOnCancelListener(@Nullable DialogInterface.OnCancelListener listener) {
        if (listener == null) {
            return;
        }
        addOnCancelListener(new CancelListenerWrapper(listener));
    }

    /**
     * 设置一个销毁监听器
     *
     * @param listener 销毁监听器对象
     * @deprecated 请使用 {@link #addOnDismissListener(BaseDialog.OnDismissListener)}
     */
    @Deprecated
    @Override
    public void setOnDismissListener(@Nullable DialogInterface.OnDismissListener listener) {
        if (listener == null) {
            return;
        }
        addOnDismissListener(new DismissListenerWrapper(listener));
    }

    /**
     * 设置一个按键监听器
     *
     * @param listener 按键监听器对象
     * @deprecated 请使用 {@link #setOnKeyListener(BaseDialog.OnKeyListener)}
     */
    @Deprecated
    @Override
    public void setOnKeyListener(@Nullable DialogInterface.OnKeyListener listener) {
        super.setOnKeyListener(listener);
    }

    public void setOnKeyListener(@Nullable BaseDialog.OnKeyListener listener) {
        super.setOnKeyListener(new KeyListenerWrapper(listener));
    }

    /**
     * 添加一个显示监听器
     *
     * @param listener 监听器对象
     */
    public void addOnShowListener(@Nullable BaseDialog.OnShowListener listener) {
        if (mShowListeners == null) {
            mShowListeners = new ArrayList<>();
            super.setOnShowListener(mListeners);
        }
        mShowListeners.add(listener);
    }

    /**
     * 添加一个取消监听器
     *
     * @param listener 监听器对象
     */
    public void addOnCancelListener(@Nullable BaseDialog.OnCancelListener listener) {
        if (mCancelListeners == null) {
            mCancelListeners = new ArrayList<>();
            super.setOnCancelListener(mListeners);
        }
        mCancelListeners.add(listener);
    }

    /**
     * 添加一个销毁监听器
     *
     * @param listener 监听器对象
     */
    public void addOnDismissListener(@Nullable BaseDialog.OnDismissListener listener) {
        if (mDismissListeners == null) {
            mDismissListeners = new ArrayList<>();
            super.setOnDismissListener(mListeners);
        }
        mDismissListeners.add(listener);
    }

    /**
     * 移除一个显示监听器
     *
     * @param listener 监听器对象
     */
    public void removeOnShowListener(@Nullable BaseDialog.OnShowListener listener) {
        if (mShowListeners == null) {
            return;
        }
        mShowListeners.remove(listener);
    }

    /**
     * 移除一个取消监听器
     *
     * @param listener 监听器对象
     */
    public void removeOnCancelListener(@Nullable BaseDialog.OnCancelListener listener) {
        if (mCancelListeners == null) {
            return;
        }
        mCancelListeners.remove(listener);
    }

    /**
     * 移除一个销毁监听器
     *
     * @param listener 监听器对象
     */
    public void removeOnDismissListener(@Nullable BaseDialog.OnDismissListener listener) {
        if (mDismissListeners == null) {
            return;
        }
        mDismissListeners.remove(listener);
    }

    /** 设置显示监听器集合 */
    private void setOnShowListeners(@Nullable List<BaseDialog.OnShowListener> listeners) {
        super.setOnShowListener(mListeners);
        mShowListeners = listeners;
    }

    /** 设置取消监听器集合 */
    private void setOnCancelListeners(@Nullable List<BaseDialog.OnCancelListener> listeners) {
        super.setOnCancelListener(mListeners);
        mCancelListeners = listeners;
    }

    /** 设置销毁监听器集合 */
    private void setOnDismissListeners(@Nullable List<BaseDialog.OnDismissListener> listeners) {
        super.setOnDismissListener(mListeners);
        mDismissListeners = listeners;
    }

    /** {@link DialogInterface.OnShowListener} */
    @Override
    public void onShow(DialogInterface dialog) {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);

        if (mShowListeners == null) {
            return;
        }

        for (int i = 0; i < mShowListeners.size(); i++) {
            mShowListeners.get(i).onShow(this);
        }
    }

    /** {@link DialogInterface.OnCancelListener} */
    @Override
    public void onCancel(DialogInterface dialog) {
        if (mCancelListeners == null) {
            return;
        }

        for (int i = 0; i < mCancelListeners.size(); i++) {
            mCancelListeners.get(i).onCancel(this);
        }
    }

    /** {@link DialogInterface.OnDismissListener} */
    @Override
    public void onDismiss(DialogInterface dialog) {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);

        if (mDismissListeners == null) {
            return;
        }

        for (int i = 0; i < mDismissListeners.size(); i++) {
            mDismissListeners.get(i).onDismiss(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    /** 点击监听器 */
    public interface OnClickListener<V extends View> {

        /** 点击事件触发了 */
        void onClick(BaseDialog dialog, V view);
    }

    /** 创建监听器 */
    public interface OnCreateListener {

        /** Dialog 创建了 */
        void onCreate(BaseDialog dialog);
    }

    /** 显示监听器 */
    public interface OnShowListener {

        /** Dialog 显示了 */
        void onShow(BaseDialog dialog);
    }

    /** 取消监听器 */
    public interface OnCancelListener {

        /** Dialog 取消了 */
        void onCancel(BaseDialog dialog);
    }

    /** 销毁监听器 */
    public interface OnDismissListener {

        /** Dialog 销毁了 */
        void onDismiss(BaseDialog dialog);
    }

    /** 按键监听器 */
    public interface OnKeyListener {

        /** 触发了按键 */
        boolean onKey(BaseDialog dialog, KeyEvent event);
    }

    @SuppressWarnings("unchecked")
    public static class Builder<B extends BaseDialog.Builder<?>>
            implements ActivityAction, ResourcesAction, ClickAction, KeyboardAction {

        /** Activity 对象 */
        private final Activity mActivity;

        /** Context 对象 */
        private final Context mContext;

        /** Dialog 显示监听 */
        private final List<BaseDialog.OnShowListener> mShowListeners = new ArrayList<>();

        /** Dialog 取消监听 */
        private final List<BaseDialog.OnCancelListener> mCancelListeners = new ArrayList<>();

        /** Dialog 销毁监听 */
        private final List<BaseDialog.OnDismissListener> mDismissListeners = new ArrayList<>();

        /** Dialog 对象 */
        private BaseDialog mDialog;

        /** Dialog 布局 */
        private View mContentView;

        /** 主题样式 */
        private int mThemeId = R.style.BaseDialogTheme;

        /** 动画样式 */
        private int mAnimStyle = BaseDialog.ANIM_DEFAULT;

        /** 宽度和高度 */
        private int mWidth = WindowManager.LayoutParams.WRAP_CONTENT;

        private int mHeight = WindowManager.LayoutParams.WRAP_CONTENT;

        /** 重心位置 */
        private int mGravity = Gravity.NO_GRAVITY;

        /** 水平偏移 */
        private int mXOffset;

        /** 垂直偏移 */
        private int mYOffset;

        /** 是否能够被取消 */
        private boolean mCancelable = true;

        /** 点击空白是否能够取消 前提是这个对话框可以被取消 */
        private boolean mCanceledOnTouchOutside = true;

        /** 背景遮盖层开关 */
        private boolean mBackgroundDimEnabled = true;

        /** 背景遮盖层透明度 */
        private float mBackgroundDimAmount = 0.5f;

        /** Dialog 创建监听 */
        private BaseDialog.OnCreateListener mCreateListener;

        /** Dialog 按键监听 */
        private BaseDialog.OnKeyListener mKeyListener;

        /** 点击事件集合 */
        private SparseArray<BaseDialog.OnClickListener<?>> mClickArray;

        public Builder(Activity activity) {
            this((Context) activity);
        }

        public Builder(Context context) {
            mContext = context;
            mActivity = getActivity();
        }

        /** 设置主题 id */
        public B setThemeStyle(@StyleRes int id) {
            mThemeId = id;
            if (isCreated()) {
                // Dialog 创建之后不能再设置主题 id
                throw new IllegalStateException("are you ok?");
            }
            return (B) this;
        }

        /** 设置动画，已经封装好几种样式，具体可见{@link AnimAction}类 */
        public B setAnimStyle(@StyleRes int id) {
            mAnimStyle = id;
            if (isCreated()) {
                mDialog.setWindowAnimations(id);
            }
            return (B) this;
        }

        /** 设置宽度 */
        public B setWidth(int width) {
            mWidth = width;
            if (isCreated()) {
                mDialog.setWidth(width);
                return (B) this;
            }

            // 这里解释一下为什么要重新设置 LayoutParams
            // 因为如果不这样设置的话，第一次显示的时候会按照 Dialog 宽高显示
            // 但是 Layout 内容变更之后就不会按照之前的设置宽高来显示
            // 所以这里我们需要对 View 的 LayoutParams 也进行设置
            ViewGroup.LayoutParams params =
                    mContentView != null ? mContentView.getLayoutParams() : null;
            if (params != null) {
                params.width = width;
                mContentView.setLayoutParams(params);
            }
            return (B) this;
        }

        /** 设置高度 */
        public B setHeight(int height) {
            mHeight = height;
            if (isCreated()) {
                mDialog.setHeight(height);
                return (B) this;
            }

            // 这里解释一下为什么要重新设置 LayoutParams
            // 因为如果不这样设置的话，第一次显示的时候会按照 Dialog 宽高显示
            // 但是 Layout 内容变更之后就不会按照之前的设置宽高来显示
            // 所以这里我们需要对 View 的 LayoutParams 也进行设置
            ViewGroup.LayoutParams params =
                    mContentView != null ? mContentView.getLayoutParams() : null;
            if (params != null) {
                params.height = height;
                mContentView.setLayoutParams(params);
            }
            return (B) this;
        }

        /** 设置重心位置 */
        public B setGravity(int gravity) {
            // 适配布局反方向
            mGravity =
                    Gravity.getAbsoluteGravity(
                            gravity, getResources().getConfiguration().getLayoutDirection());
            if (isCreated()) {
                mDialog.setGravity(gravity);
            }
            return (B) this;
        }

        /** 设置水平偏移 */
        public B setXOffset(int offset) {
            mXOffset = offset;
            if (isCreated()) {
                mDialog.setXOffset(offset);
            }
            return (B) this;
        }

        /** 设置垂直偏移 */
        public B setYOffset(int offset) {
            mYOffset = offset;
            if (isCreated()) {
                mDialog.setYOffset(offset);
            }
            return (B) this;
        }

        /** 是否可以取消 */
        public B setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            if (isCreated()) {
                mDialog.setCancelable(cancelable);
            }
            return (B) this;
        }

        /** 是否可以通过点击空白区域取消 */
        public B setCanceledOnTouchOutside(boolean cancel) {
            mCanceledOnTouchOutside = cancel;
            if (isCreated() && mCancelable) {
                mDialog.setCanceledOnTouchOutside(cancel);
            }
            return (B) this;
        }

        /** 设置背景遮盖层开关 */
        public B setBackgroundDimEnabled(boolean enabled) {
            mBackgroundDimEnabled = enabled;
            if (isCreated()) {
                mDialog.setBackgroundDimEnabled(enabled);
            }
            return (B) this;
        }

        /** 设置背景遮盖层的透明度（前提条件是背景遮盖层开关必须是为开启状态） */
        public B setBackgroundDimAmount(@FloatRange(from = 0.0, to = 1.0) float dimAmount) {
            mBackgroundDimAmount = dimAmount;
            if (isCreated()) {
                mDialog.setBackgroundDimAmount(dimAmount);
            }
            return (B) this;
        }

        /** 设置创建监听 */
        public B setOnCreateListener(@NonNull BaseDialog.OnCreateListener listener) {
            mCreateListener = listener;
            return (B) this;
        }

        /** 添加显示监听 */
        public B addOnShowListener(@NonNull BaseDialog.OnShowListener listener) {
            mShowListeners.add(listener);
            return (B) this;
        }

        /** 添加取消监听 */
        public B addOnCancelListener(@NonNull BaseDialog.OnCancelListener listener) {
            mCancelListeners.add(listener);
            return (B) this;
        }

        /** 添加销毁监听 */
        public B addOnDismissListener(@NonNull BaseDialog.OnDismissListener listener) {
            mDismissListeners.add(listener);
            return (B) this;
        }

        /** 设置按键监听 */
        public B setOnKeyListener(@NonNull BaseDialog.OnKeyListener listener) {
            mKeyListener = listener;
            if (isCreated()) {
                mDialog.setOnKeyListener(listener);
            }
            return (B) this;
        }

        /** 设置文本 */
        public B setText(@IdRes int viewId, @StringRes int stringId) {
            return setText(viewId, getString(stringId));
        }

        public B setText(@IdRes int id, CharSequence text) {
            ((TextView) findViewById(id)).setText(text);
            return (B) this;
        }

        /** 设置文本颜色 */
        public B setTextColor(@IdRes int id, @ColorInt int color) {
            ((TextView) findViewById(id)).setTextColor(color);
            return (B) this;
        }

        /** 设置提示 */
        public B setHint(@IdRes int viewId, @StringRes int stringId) {
            return setHint(viewId, getString(stringId));
        }

        public B setHint(@IdRes int id, CharSequence text) {
            ((TextView) findViewById(id)).setHint(text);
            return (B) this;
        }

        /** 设置可见状态 */
        public B setVisibility(@IdRes int id, int visibility) {
            findViewById(id).setVisibility(visibility);
            return (B) this;
        }

        /** 设置背景 */
        public B setBackground(@IdRes int viewId, @DrawableRes int drawableId) {
            return setBackground(viewId, ContextCompat.getDrawable(mContext, drawableId));
        }

        public B setBackground(@IdRes int id, Drawable drawable) {
            findViewById(id).setBackground(drawable);
            return (B) this;
        }

        /** 设置图片 */
        public B setImageDrawable(@IdRes int viewId, @DrawableRes int drawableId) {
            return setBackground(viewId, ContextCompat.getDrawable(mContext, drawableId));
        }

        public B setImageDrawable(@IdRes int id, Drawable drawable) {
            ((ImageView) findViewById(id)).setImageDrawable(drawable);
            return (B) this;
        }

        /** 设置点击事件 */
        public B setOnClickListener(
                @IdRes int id, @NonNull BaseDialog.OnClickListener<?> listener) {
            if (mClickArray == null) {
                mClickArray = new SparseArray<>();
            }
            mClickArray.put(id, listener);

            if (isCreated()) {
                View view = mDialog.findViewById(id);
                if (view != null) {
                    view.setOnClickListener(new ViewClickWrapper(mDialog, listener));
                }
            }
            return (B) this;
        }

        /** 创建 */
        @SuppressLint("RtlHardcoded")
        public BaseDialog create() {
            // 判断布局是否为空
            if (mContentView == null) {
                throw new IllegalArgumentException("are you ok?");
            }

            // 如果当前正在显示
            if (isShowing()) {
                dismiss();
            }

            // 如果当前没有设置重心，就设置一个默认的重心
            if (mGravity == Gravity.NO_GRAVITY) {
                mGravity = Gravity.CENTER;
            }

            // 如果当前没有设置动画效果，就设置一个默认的动画效果
            if (mAnimStyle == BaseDialog.ANIM_DEFAULT) {
                switch (mGravity) {
                    case Gravity.TOP:
                        mAnimStyle = BaseDialog.ANIM_TOP;
                        break;
                    case Gravity.BOTTOM:
                        mAnimStyle = BaseDialog.ANIM_BOTTOM;
                        break;
                    case Gravity.LEFT:
                        mAnimStyle = BaseDialog.ANIM_LEFT;
                        break;
                    case Gravity.RIGHT:
                        mAnimStyle = BaseDialog.ANIM_RIGHT;
                        break;
                    default:
                        mAnimStyle = BaseDialog.ANIM_DEFAULT;
                        break;
                }
            }

            // 创建新的 Dialog 对象
            mDialog = createDialog(mContext, mThemeId);
            mDialog.setContentView(mContentView);
            mDialog.setCancelable(mCancelable);
            if (mCancelable) {
                mDialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
            }
            mDialog.setOnShowListeners(mShowListeners);
            mDialog.setOnCancelListeners(mCancelListeners);
            mDialog.setOnDismissListeners(mDismissListeners);
            mDialog.setOnKeyListener(mKeyListener);

            Window window = mDialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = mWidth;
                params.height = mHeight;
                params.gravity = mGravity;
                params.x = mXOffset;
                params.y = mYOffset;
                params.windowAnimations = mAnimStyle;
                if (mBackgroundDimEnabled) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    window.setDimAmount(mBackgroundDimAmount);
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                }
                window.setAttributes(params);
            }

            for (int i = 0; mClickArray != null && i < mClickArray.size(); i++) {
                View view = mContentView.findViewById(mClickArray.keyAt(i));
                if (view != null) {
                    view.setOnClickListener(new ViewClickWrapper(mDialog, mClickArray.valueAt(i)));
                }
            }

            // 将 Dialog 的生命周期和 Activity 绑定在一起
            if (mActivity != null) {
                DialogLifecycle.with(mActivity, mDialog);
            }

            if (mCreateListener != null) {
                mCreateListener.onCreate(mDialog);
            }

            return mDialog;
        }

        /** 显示 */
        public void show() {
            if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
                return;
            }

            if (!isCreated()) {
                create();
            }

            if (isShowing()) {
                return;
            }

            mDialog.show();
        }

        /** 销毁当前 Dialog */
        public void dismiss() {
            if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
                return;
            }

            if (mDialog == null) {
                return;
            }

            mDialog.dismiss();
        }

        @Override
        public Context getContext() {
            return mContext;
        }

        /** 当前 Dialog 是否创建了 */
        public boolean isCreated() {
            return mDialog != null;
        }

        /** 当前 Dialog 是否显示了 */
        public boolean isShowing() {
            return isCreated() && mDialog.isShowing();
        }

        /** 创建 Dialog 对象（子类可以重写此方法来改变 Dialog 类型） */
        @NonNull
        protected BaseDialog createDialog(Context context, @StyleRes int themeId) {
            return new BaseDialog(context, themeId);
        }

        /** 延迟执行 */
        public final void post(Runnable runnable) {
            if (isShowing()) {
                mDialog.post(runnable);
            } else {
                addOnShowListener(new ShowPostWrapper(runnable));
            }
        }

        /** 延迟一段时间执行 */
        public final void postDelayed(Runnable runnable, long delayMillis) {
            if (isShowing()) {
                mDialog.postDelayed(runnable, delayMillis);
            } else {
                addOnShowListener(new ShowPostDelayedWrapper(runnable, delayMillis));
            }
        }

        /** 在指定的时间执行 */
        public final void postAtTime(Runnable runnable, long uptimeMillis) {
            if (isShowing()) {
                mDialog.postAtTime(runnable, uptimeMillis);
            } else {
                addOnShowListener(new ShowPostAtTimeWrapper(runnable, uptimeMillis));
            }
        }

        /** 获取 Dialog 的根布局 */
        public View getContentView() {
            return mContentView;
        }

        /** 设置布局 */
        public B setContentView(@LayoutRes int id) {
            // 这里解释一下，为什么要传 new FrameLayout，因为如果不传的话，XML 的根布局获取到的 LayoutParams 对象会为空，也就会导致宽高参数解析不出来
            return setContentView(
                    LayoutInflater.from(mContext).inflate(id, new FrameLayout(mContext), false));
        }

        public B setContentView(View view) {
            // 请不要传入空的布局
            if (view == null) {
                throw new IllegalArgumentException("are you ok?");
            }

            mContentView = view;
            if (isCreated()) {
                mDialog.setContentView(view);
                return (B) this;
            }

            ViewGroup.LayoutParams layoutParams = mContentView.getLayoutParams();
            if (layoutParams != null
                    && mWidth == ViewGroup.LayoutParams.WRAP_CONTENT
                    && mHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                // 如果当前 Dialog 的宽高设置了自适应，就以布局中设置的宽高为主
                setWidth(layoutParams.width);
                setHeight(layoutParams.height);
            }

            // 如果当前没有设置重心，就自动获取布局重心
            if (mGravity == Gravity.NO_GRAVITY) {
                if (layoutParams instanceof FrameLayout.LayoutParams) {
                    int gravity = ((FrameLayout.LayoutParams) layoutParams).gravity;
                    if (gravity != FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY) {
                        setGravity(gravity);
                    }
                } else if (layoutParams instanceof LinearLayout.LayoutParams) {
                    int gravity = ((LinearLayout.LayoutParams) layoutParams).gravity;
                    if (gravity != FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY) {
                        setGravity(gravity);
                    }
                }

                if (mGravity == Gravity.NO_GRAVITY) {
                    // 默认重心是居中
                    setGravity(Gravity.CENTER);
                }
            }
            return (B) this;
        }

        /** 根据 id 查找 View */
        @Override
        public <V extends View> V findViewById(@IdRes int id) {
            if (mContentView == null) {
                // 没有 setContentView 就想 findViewById ?
                throw new IllegalStateException("are you ok?");
            }
            return mContentView.findViewById(id);
        }

        /** 获取当前 Dialog 对象 */
        public BaseDialog getDialog() {
            return mDialog;
        }
    }

    /** Dialog 生命周期绑定 */
    private static final class DialogLifecycle
            implements Application.ActivityLifecycleCallbacks,
            BaseDialog.OnShowListener,
            BaseDialog.OnDismissListener {

        private BaseDialog mDialog;
        private Activity mActivity;

        /** Dialog 动画样式（避免 Dialog 从后台返回到前台后再次触发动画效果） */
        private int mDialogAnim;

        private DialogLifecycle(Activity activity, BaseDialog dialog) {
            mActivity = activity;
            dialog.addOnShowListener(this);
            dialog.addOnDismissListener(this);
        }

        private static void with(Activity activity, BaseDialog dialog) {
            new DialogLifecycle(activity, dialog);
        }

        @Override
        public void onActivityCreated(
                @NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            if (mActivity != activity) {
                return;
            }

            if (mDialog == null || !mDialog.isShowing()) {
                return;
            }

            // 还原 Dialog 动画样式（这里必须要使用延迟设置，否则还是有一定几率会出现）
            mDialog.postDelayed(
                    () -> {
                        if (mDialog == null || !mDialog.isShowing()) {
                            return;
                        }
                        mDialog.setWindowAnimations(mDialogAnim);
                    },
                    100);
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            if (mActivity != activity) {
                return;
            }

            if (mDialog == null || !mDialog.isShowing()) {
                return;
            }

            // 获取 Dialog 动画样式
            mDialogAnim = mDialog.getWindowAnimations();
            // 设置 Dialog 无动画效果
            mDialog.setWindowAnimations(BaseDialog.ANIM_EMPTY);
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(
                @NonNull Activity activity, @NonNull Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            if (mActivity != activity) {
                return;
            }

            unregisterActivityLifecycleCallbacks();
            mActivity = null;

            if (mDialog == null) {
                return;
            }
            mDialog.removeOnShowListener(this);
            mDialog.removeOnDismissListener(this);
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            mDialog = null;
        }

        @Override
        public void onShow(BaseDialog dialog) {
            mDialog = dialog;
            registerActivityLifecycleCallbacks();
        }

        @Override
        public void onDismiss(BaseDialog dialog) {
            mDialog = null;
            unregisterActivityLifecycleCallbacks();
        }

        /** 注册 Activity 生命周期监听 */
        private void registerActivityLifecycleCallbacks() {
            if (mActivity == null) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mActivity.registerActivityLifecycleCallbacks(this);
            } else {
                mActivity.getApplication().registerActivityLifecycleCallbacks(this);
            }
        }

        /** 反注册 Activity 生命周期监听 */
        private void unregisterActivityLifecycleCallbacks() {
            if (mActivity == null) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mActivity.unregisterActivityLifecycleCallbacks(this);
            } else {
                mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
            }
        }
    }

    /** Dialog 监听包装类（修复原生 Dialog 监听器对象导致的内存泄漏） */
    private static final class ListenersWrapper<
            T extends
                    DialogInterface.OnShowListener & DialogInterface.OnCancelListener
                    & DialogInterface.OnDismissListener>
            extends SoftReference<T>
            implements DialogInterface.OnShowListener,
            DialogInterface.OnCancelListener,
            DialogInterface.OnDismissListener {

        private ListenersWrapper(T referent) {
            super(referent);
        }

        @Override
        public void onShow(DialogInterface dialog) {
            if (get() == null) {
                return;
            }
            get().onShow(dialog);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            if (get() == null) {
                return;
            }
            get().onCancel(dialog);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (get() == null) {
                return;
            }
            get().onDismiss(dialog);
        }
    }

    /** 点击事件包装类 */
    @SuppressWarnings("rawtypes")
    private static final class ViewClickWrapper implements View.OnClickListener {

        private final BaseDialog mDialog;
        @Nullable
        private final BaseDialog.OnClickListener mListener;

        private ViewClickWrapper(BaseDialog dialog, @Nullable BaseDialog.OnClickListener listener) {
            mDialog = dialog;
            mListener = listener;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final void onClick(View view) {
            if (mListener == null) {
                return;
            }
            mListener.onClick(mDialog, view);
        }
    }

    /** 显示监听包装类 */
    private static final class ShowListenerWrapper
            extends SoftReference<DialogInterface.OnShowListener>
            implements BaseDialog.OnShowListener {

        private ShowListenerWrapper(DialogInterface.OnShowListener referent) {
            super(referent);
        }

        @Override
        public void onShow(BaseDialog dialog) {
            // 在横竖屏切换后监听对象会为空
            if (get() == null) {
                return;
            }
            get().onShow(dialog);
        }
    }

    /** 取消监听包装类 */
    private static final class CancelListenerWrapper
            extends SoftReference<DialogInterface.OnCancelListener>
            implements BaseDialog.OnCancelListener {

        private CancelListenerWrapper(DialogInterface.OnCancelListener referent) {
            super(referent);
        }

        @Override
        public void onCancel(BaseDialog dialog) {
            // 在横竖屏切换后监听对象会为空
            if (get() == null) {
                return;
            }
            get().onCancel(dialog);
        }
    }

    /** 销毁监听包装类 */
    private static final class DismissListenerWrapper
            extends SoftReference<DialogInterface.OnDismissListener>
            implements BaseDialog.OnDismissListener {

        private DismissListenerWrapper(DialogInterface.OnDismissListener referent) {
            super(referent);
        }

        @Override
        public void onDismiss(BaseDialog dialog) {
            // 在横竖屏切换后监听对象会为空
            if (get() == null) {
                return;
            }
            get().onDismiss(dialog);
        }
    }

    /** 按键监听包装类 */
    private static final class KeyListenerWrapper implements DialogInterface.OnKeyListener {

        private final BaseDialog.OnKeyListener mListener;

        private KeyListenerWrapper(BaseDialog.OnKeyListener listener) {
            mListener = listener;
        }

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            // 在横竖屏切换后监听对象会为空
            if (mListener == null || !(dialog instanceof BaseDialog)) {
                return false;
            }
            return mListener.onKey((BaseDialog) dialog, event);
        }
    }

    /** post 任务包装类 */
    private static final class ShowPostWrapper implements OnShowListener {

        private final Runnable mRunnable;

        private ShowPostWrapper(Runnable runnable) {
            mRunnable = runnable;
        }

        @Override
        public void onShow(BaseDialog dialog) {
            if (mRunnable == null) {
                return;
            }
            dialog.removeOnShowListener(this);
            dialog.post(mRunnable);
        }
    }

    /** postDelayed 任务包装类 */
    private static final class ShowPostDelayedWrapper implements OnShowListener {

        private final Runnable mRunnable;
        private final long mDelayMillis;

        private ShowPostDelayedWrapper(Runnable r, long delayMillis) {
            mRunnable = r;
            mDelayMillis = delayMillis;
        }

        @Override
        public void onShow(BaseDialog dialog) {
            if (mRunnable == null) {
                return;
            }
            dialog.removeOnShowListener(this);
            dialog.postDelayed(mRunnable, mDelayMillis);
        }
    }

    /** postAtTime 任务包装类 */
    private static final class ShowPostAtTimeWrapper implements OnShowListener {

        private final Runnable mRunnable;
        private final long mUptimeMillis;

        private ShowPostAtTimeWrapper(Runnable r, long uptimeMillis) {
            mRunnable = r;
            mUptimeMillis = uptimeMillis;
        }

        @Override
        public void onShow(BaseDialog dialog) {
            if (mRunnable == null) {
                return;
            }
            dialog.removeOnShowListener(this);
            dialog.postAtTime(mRunnable, mUptimeMillis);
        }
    }
}
