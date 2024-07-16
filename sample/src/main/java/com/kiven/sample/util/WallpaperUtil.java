package com.kiven.sample.util;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import com.kiven.kutils.tools.KGranting;
import com.kiven.sample.service.LiveWallpaper;

/**
 * Created by wangk on 2018/4/9.
 */

public class WallpaperUtil {
    /**
     * 跳转到系统设置壁纸界面
     *
     * @param paramActivity
     */
    public static void setLiveWallpaper(final Activity paramActivity, final int requestCode) {
        KGranting.requestPermissions(paramActivity, new String[]{Manifest.permission.SET_WALLPAPER, Manifest.permission.BIND_WALLPAPER}, new String[]{"设置壁纸", "绑定壁纸"}, new KGranting.GrantingCallBack() {
            @Override
            public void onGrantSuccess(boolean isSuccess) {
                try {
                    Intent localIntent = new Intent();
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {//ICE_CREAM_SANDWICH_MR1  15
                        localIntent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);//android.service.wallpaper.CHANGE_LIVE_WALLPAPER
                        //android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT
                        localIntent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT
                                , new ComponentName(paramActivity, LiveWallpaper.class));
                    } else {
                        localIntent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);//android.service.wallpaper.LIVE_WALLPAPER_CHOOSER
                    }
                    paramActivity.startActivityForResult(localIntent, requestCode);
                } catch (Exception localException) {
                    localException.printStackTrace();
                }
            }
        });
    }

    /**
     * 判断是否是使用我们的壁纸
     *
     * @param paramContext
     * @return
     */
    public static boolean wallpaperIsUsed(Context paramContext) {
        WallpaperInfo localWallpaperInfo = WallpaperManager.getInstance(paramContext).getWallpaperInfo();
        return ((localWallpaperInfo != null) && (localWallpaperInfo.getPackageName().equals(paramContext.getPackageName())) &&
                (localWallpaperInfo.getServiceName().equals(LiveWallpaper.class.getCanonicalName())));
    }

    public static Bitmap getDefaultWallpaper(Context paramContext) {
        Bitmap localBitmap;
        if (isLivingWallpaper(paramContext))
            return null;

        localBitmap = ((BitmapDrawable) WallpaperManager.getInstance(paramContext).getDrawable()).getBitmap();
        return localBitmap;
    }

    public static boolean isLivingWallpaper(Context paramContext) {
        return (WallpaperManager.getInstance(paramContext).getWallpaperInfo() != null);
    }
}
