package com.clubank.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.clubank.common.R;

import java.io.File;

/**
 * Created by fengyq on 2016/11/16.
 */
public class GlideUtil<T> {

    public final int BLUR_VALUE = 20; //模糊
    public final int CORNER_RADIUS = 20; //圆角
    public final float THUMB_SIZE = 0.5f; //0-1之间  10%原图的大小


    private static GlideUtil mInstance;
    private int loadingImage = R.drawable.placeholderimg;

    private GlideUtil() {
    }

    public static GlideUtil getInstance() {
        if (mInstance == null) {
            synchronized (GlideUtil.class) {
                if (mInstance == null) {
                    mInstance = new GlideUtil();
                }
            }
        }
        return mInstance;
    }


    /**
     * 加载图片
     *
     * @param model .load(String string)	string可以为一个文件路径、uri或者url
     *              .load(Uri uri)	uri类型
     *              .load(File file)	文件
     *              .load(Integer resourceId)	资源Id,R.drawable.xxx或者R.mipmap.xxx
     *              .load(byte[] model)	byte[]类型
     *              .load(T model)
     */
    protected void setImage(Context context, ImageView iv, T model, int resLoadingImage, int
            rounded, boolean
                                    isCircle) {
        if (resLoadingImage == 0) {//设置默认图片
            resLoadingImage = loadingImage;
        }
        RequestBuilder<Bitmap> builder = getBitmapBuilder(context, model, resLoadingImage);
//        if (isCircle) {//圆图
//            builder.transform(new GlideCircleTransform(context)).dontAnimate().into(iv);
//        } else if (rounded != 0) {//圆角图片
//            builder.transform(new GlideRoundTransform(context, rounded)).into(iv);
//        } else {
//            builder.centerCrop().into(iv);
//        }
        builder.into(iv);
    }


    /**
     * dontAnimate() 没有任何淡入淡出效果
     * crossFade() 一个淡入淡出动画方法还有另外重载方法 .crossFade(int duration)
     * 。如果你想要去减慢（或加快）动画，随时可以传一个毫秒的时间给这个方法。动画默认的持续时间是 300毫秒。
     */
    public RequestBuilder<Bitmap> getBitmapBuilder(Context context, T model, int resLoadingImage) {

        RequestBuilder<Bitmap> request = null;
//        DrawableTypeRequest request = null;
        if (model instanceof String) {

            String url = (String) model;
            request = Glide.with(context).asBitmap().load(url);

        } else if (model instanceof byte[]) {

            byte[] imageByte = (byte[]) model;
            request = Glide.with(context).asBitmap()
                    .load(imageByte);

        } else if (model instanceof Integer) {

            Integer imageResId = (Integer) model;
            request = Glide.with(context).asBitmap()
                    .load(imageResId);

        } else if (model instanceof Uri) {

            Uri uri = (Uri) model;
            request = Glide.with(context).asBitmap()
                    .load(uri);

        } else if (model instanceof File) {

            File file = (File) model;
            request = Glide.with(context)
                    .asBitmap()
                    .load(file);
        } else {
            request = Glide.with(context)
                    .asBitmap()
                    .load(model);
        }
        RequestOptions options = new RequestOptions()
                .priority(Priority.NORMAL)//下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL)//下载的优先级
                .error(resLoadingImage)
                .placeholder(resLoadingImage);
        request.apply(options);
        return request;
    }


    /**
     * 常规图
     */
    public void setImage(Context context, ImageView iv, T model) {
        setImage(context, iv, model, 0, 0, false);
    }

    public void setImage(Activity a, int ImageViewId, T resValue) {
        View view = a.findViewById(ImageViewId);
        if (view instanceof ImageView) {
            setImage(a, (ImageView) view, resValue);
        }

    }


    /**
     * 圆图
     */
    public void setImage(Context context, ImageView iv, T model, boolean isCircle) {
        setImage(context, iv, model, 0, 0, isCircle);
    }


    public void setImage(Activity a, int ImageViewId, T resValue, boolean isCircle) {
        View view = a.findViewById(ImageViewId);
        if (view instanceof ImageView) {
            setImage(a, (ImageView) view, resValue, isCircle);
        }

    }

    /**
     * 自定义预加载图片
     */
    public void setImage(Context context, ImageView iv, T model, int loadingImage) {
        setImage(context, iv, model, loadingImage, 0, false);
    }


    public void setImage(Activity a, int ImageViewId, T model, int loadingImage) {
        View view = a.findViewById(ImageViewId);
        if (view instanceof ImageView) {
            setImage(a, (ImageView) view, model, loadingImage);
        }

    }


    /**
     * 常规加载图片
     *
     * @param context
     * @param imageView 图片容器
     * @param model     图片地址
     * @param isFade    是否需要动画
     */
    public void loadImage(Context context, ImageView imageView,
                          T model, boolean isFade) {

        RequestBuilder<Bitmap> builder = getBitmapBuilder(context, model, loadingImage);
        if (isFade) {
            builder.into(imageView);
        } else {
            builder.into(imageView);
        }
    }


    /**
     * 加载图片并设置为指定大小
     *
     * @param context
     * @param imageView
     * @param withSize
     * @param heightSize
     */
    public void loadOverrideImage(Context context, ImageView imageView,
                                  T model, int withSize, int heightSize) {
        RequestOptions options = new RequestOptions()
                .override(withSize, heightSize);
        RequestBuilder<Bitmap> builder = getBitmapBuilder(context, model, loadingImage);
        builder.apply(options).into(imageView);
    }

    /**
     * 加载图片并对其进行模糊处理
     *
     * @param context
     * @param imageView
     * @param imgUrl
     */
    public void loadBlurImage(Context context, ImageView imageView, String imgUrl) {
//         .crossFade()
        RequestOptions options = new RequestOptions()
                .priority(Priority.NORMAL)//下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL)//下载的优先级
                .error(R.drawable.placeholderimg);

        Glide.with(context)
                .load(imgUrl)
//                .bitmapTransform(new BlurTransformation(context))
                .into(imageView);
    }

    /**
     * 加载圆图
     *
     * @param context
     * @param imageView
     * @param imgUrl
     */
    public void loadCircleImage(Context context, ImageView imageView, String imgUrl) {
// .crossFade()
        RequestOptions options = new RequestOptions()
                .priority(Priority.NORMAL)//下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL)//下载的优先级
                .error(R.drawable.placeholderimg);
        Glide.with(context)
                .load(imgUrl)
//                .bitmapTransform(new GlideCircleTransform(context))
                .into(imageView);
    }

    /**
     * 加载模糊的圆角的图片
     *
     * @param context
     * @param imageView
     * @param imgUrl
     */
    public void loadBlurCircleImage(Context context, ImageView imageView, String imgUrl) {
//        .crossFade()
        RequestOptions options = new RequestOptions()
                .priority(Priority.NORMAL)//下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL)//下载的优先级
                .error(R.drawable.placeholderimg);
        Glide.with(context)
                .load(imgUrl)
//                .bitmapTransform(
//                        new BlurTransformation(context),
//                        new GlideCircleTransform(context))
                .into(imageView);
    }

    /**
     * 加载圆角图片
     */
    public void loadCornerImage(Context context, ImageView imageView, T imgUrl) {
//         .crossFade()
        RequestOptions options = new RequestOptions()
                .priority(Priority.NORMAL)//下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL)//下载的优先级
                .error(R.drawable.placeholderimg);

        Glide.with(context)
                .load(imgUrl)
//                .bitmapTransform(new GlideRoundTransform(context, CORNER_RADIUS))
                .into(imageView);
    }

    /**
     * 加载模糊的圆角图片
     *
     * @param context
     * @param imageView
     * @param imgUrl
     */
    public void loadBlurCornerImage(Context context, ImageView imageView, String imgUrl) {
//        .crossFade()
        RequestOptions options = new RequestOptions()
                .priority(Priority.NORMAL)//下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL)//下载的优先级
                .error(R.drawable.placeholderimg);
        Glide.with(context)
                .load(imgUrl)
//                .bitmapTransform(new BlurTransformation(context), new GlideRoundTransform
//                        (context, CORNER_RADIUS))
                .into(imageView);
    }

    /**
     * 同步加载图片
     *
     * @param context
     * @param imgUrl
     * @param target
     */
    public void loadBitmapSync(Context context, String imgUrl, SimpleTarget<Bitmap> target) {
        RequestOptions options = new RequestOptions()
                .priority(Priority.NORMAL)//下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL)//下载的优先级
                .error(R.drawable.placeholderimg);
        Glide.with(context)
                .asBitmap()
                .load(imgUrl)
                .apply(options)
                .into(target);
    }

    /**
     * 加载gif
     *
     * @param context
     * @param imageView
     * @param imgUrl
     */
    public void loadGifImage(Context context, ImageView imageView, String imgUrl) {
//        .crossFade()
        RequestOptions options = new RequestOptions()
                .priority(Priority.NORMAL)//下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL)//下载的优先级
                .error(R.drawable.placeholderimg);
        Glide.with(context)
                .asGif()
                .apply(options)
                .load(imgUrl)
                .into(imageView);
    }

    /**
     * 加载gif的缩略图
     *
     * @param context
     * @param imageView
     * @param imgUrl
     */
    public void loadGifThumbnailImage(Context context, ImageView imageView, String imgUrl) {

//        .crossFade()
        RequestOptions options = new RequestOptions()
                .priority(Priority.NORMAL)//下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL)//下载的优先级
                .error(R.drawable.placeholderimg);
        Glide.with(context)
                .asGif()
                .apply(options)
                .load(imgUrl)
                .thumbnail(THUMB_SIZE)
                .into(imageView);
    }

    /**
     * 恢复请求，一般在停止滚动的时候
     *
     * @param context
     */
    public void resumeRequests(Context context) {
        Glide.with(context).resumeRequests();
    }

    /**
     * 暂停请求 正在滚动的时候
     *
     * @param context
     */
    public void pauseRequests(Context context) {
        Glide.with(context).pauseRequests();
    }

    /**
     * 清除磁盘缓存
     *
     * @param context
     */
    public void clearDiskCache(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();//清理磁盘缓存 需要在子线程中执行
            }
        }).start();
    }

    /**
     * 清除内存缓存
     *
     * @param context
     */
    public void clearMemory(Context context) {
        Glide.get(context).clearMemory();//清理内存缓存  可以在UI主线程中进行
    }

}