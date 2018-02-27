package com.cetcme.xkclient.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.ImageView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zhd.mswcs.R;
import java.io.File;

/**
 * 对ImageLoader类做了简单的封装
 *
 * Created by Dream on 16/5/30.
 */
public class ImageLoaderUtils {

    private static ImageLoaderUtils imageLoaderUtils;

    private DisplayImageOptions headerDisplayImageOptions;
    private DisplayImageOptions displayImageOptions;

    private ImageLoaderUtils(){

    }

    public static ImageLoaderUtils getInstance(){
        if (imageLoaderUtils == null){
            imageLoaderUtils = new ImageLoaderUtils();
        }
        return imageLoaderUtils;
    }

    private void init(Context context){
        if (!ImageLoader.getInstance().isInited()){
//            DisplayImageOptions options = new DisplayImageOptions.Builder()
//                    .delayBeforeLoading(0)
//                    .showImageOnLoading(R.drawable.pic_default_item_bg)
//                    .showImageForEmptyUri(R.drawable.pic_default_item_bg)
//                    .showImageOnFail(R.drawable.pic_default_item_bg)
//                    .cacheInMemory(true)
//                    .cacheOnDisk(true)
//                    .considerExifParams(true)
//                    .imageScaleType(ImageScaleType.EXACTLY)
//                    .bitmapConfig(Bitmap.Config.ARGB_8888)
//                    .displayer(new SimpleBitmapDisplayer()).build();
//
		/*
		 * 1）.imageScaleType(ImageScaleType imageScaleType) //设置图片的缩放方式
		 * 缩放类型mageScaleType: EXACTLY :图像将完全按比例缩小的目标大小
		 * EXACTLY_STRETCHED:图片会缩放到目标大小完全 IN_SAMPLE_INT:图像将被二次采样的整数倍
		 * IN_SAMPLE_POWER_OF_2:图片将降低2倍，直到下一减少步骤，使图像更小的目标大小 NONE:图片不会调整
		 * 2）.displayer(BitmapDisplayer displayer) //设置图片的显示方式 显示方式displayer：
		 * RoundedBitmapDisplayer（int roundPixels）设置圆角图片
		 * FakeBitmapDisplayer（）这个类什么都没做 FadeInBitmapDisplayer（int
		 * durationMillis）设置图片渐显的时间 SimpleBitmapDisplayer()正常显示一张图片
		 */

            File cacheDir = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "zwjk/Universal-Image-Loader/images/");
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    context)
                    //.memoryCacheExtraOptions(800, 800)
                    //.diskCacheExtraOptions(800,800, null)
                    //.defaultDisplayImageOptions(options)
                    .threadPriority(Thread.NORM_PRIORITY - 4)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .threadPoolSize(3) // Remove for release app
                    .memoryCache(new LruMemoryCache(50 * 1024 * 1024)) // 可以通过自己的内存缓存实现
                    .diskCache(new UnlimitedDiskCache(cacheDir)) // default// 可以自定义缓存路径
                    .diskCacheSize(50 * 1024 * 1024) // 50 Mb sd卡(本地)缓存的最大值
                    .writeDebugLogs() // 打印debug log
                    .build();
            ImageLoader.getInstance().init(config);

//            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        }
    }


    /**
     * 下载头像
     * @param context
     * @param url
     * @param imageView
     */
    public void loadHeaderImage(Context context, String url, ImageView imageView){
        if (headerDisplayImageOptions == null){
            DisplayImageOptions.Builder displayImageOptionsBuilder = new DisplayImageOptions.Builder();
            displayImageOptionsBuilder.cacheInMemory(true)
                    .cacheOnDisk(true)
                    .displayer(new RoundedBitmapDisplayer(360));
            headerDisplayImageOptions = displayImageOptionsBuilder.build();
        }
        displayImage(context,url,imageView,headerDisplayImageOptions);
    }

    /**
     * 下载普通的图片
     * @param context
     * @param url
     * @param imageView
     */
    public void loadImage(Context context, String url, ImageView imageView){
        if (displayImageOptions == null){
            DisplayImageOptions.Builder displayImageOptionsBuilder = new DisplayImageOptions.Builder();
            displayImageOptionsBuilder.cacheInMemory(true)
                    .cacheOnDisk(true)
                    .showImageForEmptyUri(R.drawable.pic_default_item_bg);
            displayImageOptions = displayImageOptionsBuilder.build();
        }
        displayImage(context,url,imageView,displayImageOptions);
    }

    private void displayImage(Context context, String url, ImageView imageView, DisplayImageOptions displayImageOptions){
        init(context);
        ImageLoader.getInstance().displayImage(url,imageView,displayImageOptions);
    }

}
