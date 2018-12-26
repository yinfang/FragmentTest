package com.clubank.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.clubank.common.R;
import com.clubank.device.BaseActivity;

/**
 * Created by fengyq on 2015/12/9.
 */
public class MyGraphic {

    private String[] names;
    private double[] numbers;
    private double[] numbers1;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);/* 参数抗锯齿 */
    private Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);/* 参数抗锯齿 */
    private Bitmap bmp;
    private BaseActivity ba;
    private String v_scale_desc = "";
    private String h_scale_desc = "";
    private int factor;
    private int left = 100;
    private int top = 50;
    private int h_pace_pixel;
    private int v_pace_pixel;
    private float v_pace_actual;
    private int nv = 5;// 纵向格子数
    private float v_min;

    /**
     *
     * @param ba
     * @param names   横向日期数据
     * @param numbers  点上的数据
     *         numbers1  第二组数据
     * @param v_scale_desc
     *            纵向刻度说明
     * @param h_scale_desc
     *            横向刻度说明
     */
    public  MyGraphic(BaseActivity ba, String[] names, double[] numbers,
                      double[] numbers1, String v_scale_desc, String h_scale_desc,
                      int factor) {
        this.ba = ba;
        this.names = names;
        this.numbers = numbers;
        this.numbers1 = numbers1;
        this.v_scale_desc = v_scale_desc;
        this.h_scale_desc = h_scale_desc;
        this.factor = factor;
    }

    // 创建bmp位图对象
    //绘制图表图片。color曲线的颜色linecolor线
        @SuppressLint("NewApi")
    public void draw(int color,int linecolor) {

        int max = (int) Math.round(U.max(numbers));
        int min = (int) Math.round(U.min(numbers));
        if (numbers1 != null) {// 如果有第2组数据（同比）
            double[] merged = new double[numbers.length + numbers1.length];// 合并数组
            System.arraycopy(numbers, 0, merged, 0, numbers.length);
            System.arraycopy(numbers1, 0, merged, numbers.length,
                    numbers1.length);
            max = (int) Math.round(U.max(merged));// 用合并后的数组重新计算纵向刻度（重要）
            min = (int) Math.round(U.min(merged));
        }
        int diff = max - min;
        if (diff < 10 && diff > nv) {
            nv = diff;// 如果数字太小就直接以1做刻度
        }

        v_pace_actual = (float) Math.ceil(diff * 1.00f / nv);// 纵向刻度步长，左边数字的间距
        v_pace_actual = v_pace_actual < 1 ? 1 : v_pace_actual;
        v_min = min;

		/* 计算取整的纵向刻度，以便看起来更符合阅读习惯 */
        if (diff > 10000) {//
            double n = Math.pow(10, Math.ceil(Math.log10(diff)) - 3);
            v_pace_actual = (float) (Math.ceil(diff / nv / n) * n);
            v_min = (float) Math.floor(min / 1000f) * 1000f;
        } else if (diff > 1000) {
            v_pace_actual = (float) Math.ceil(diff / nv / 100f) * 100f;
            v_min = (float) Math.floor(min / 100f) * 100f;
        } else if (diff > 100) {
            v_pace_actual = (float) Math.ceil(diff / nv / 10f) * 10f;
            v_min = (float) Math.floor(min / 10f) * 10f;
        }
        paint.setARGB(255, 160, 160, 160);
        paint.setStyle(Paint.Style.STROKE);
        v_pace_pixel = (int) (720 * 1.00 / nv) - 20;// 纵向格子屏幕像素

        h_pace_pixel = v_pace_pixel;// 横向格子屏幕像素
        paint.setTextSize(25);
        paint1.setTextSize(25);

        bmp = Bitmap.createBitmap(left * 2 + h_pace_pixel * names.length,
                v_pace_pixel * nv + top * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        canvas.drawText(v_scale_desc, left - 65, 50, paint);// 纵向刻度说明 y
        for (int i = 0; i <= nv; i++) {
            canvas.drawLine(left, top + i * v_pace_pixel, left
                            + (names.length - 1) * h_pace_pixel,
                    top + i * v_pace_pixel, paint);// 横线
            if (names.length > 0) {
                float s = (v_min + v_pace_actual * (nv - i)) / factor;
                String ss = "" + s;
                if (factor == 1 || factor > 1 && s > factor * 10) {
                    ss = "" + (int) s;
                }
                canvas.drawText("" + ss, left - 95, top + v_pace_pixel * i,
                        paint);// 纵向刻度（左边的数字）
            }
        }

        canvas.drawText(h_scale_desc, left + h_pace_pixel * (names.length - 1)
                + 20, top + nv * h_pace_pixel, paint);// 横向刻度说明
        for (int i = 0; i < names.length; i++) {
            canvas.drawLine(left + i * h_pace_pixel, top, left + i
                    * h_pace_pixel, top + h_pace_pixel * nv, paint);// 竖线
            if (i < names.length) {
                canvas.drawText(names[i], left + h_pace_pixel * i - 5, top + nv
                        * h_pace_pixel + 35, paint);// 横向刻度
            }
        }
       /* int color = ba.getResources().getColor(R.color.analyze_bight_bule);
        int color1 = ba.getResources().getColor(R.color.Firebrick1);*/
        paint.setColor(color);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint1.setColor(linecolor);
        paint1.setStrokeWidth(3);
        paint1.setStyle(Paint.Style.FILL_AND_STROKE);
        if (names.length <= 1) {// 以下for开始画曲线
            return;
        }
        if (numbers1 != null) {// 需要画第二组数据
            drawLine(canvas, paint1, numbers1);
        }
        drawLine(canvas, paint, numbers);

        if (numbers1 != null) {
            drawPoint(canvas, paint1, numbers1, R.drawable.ball_orange, linecolor);
        }
        drawPoint(canvas, paint, numbers, R.drawable.ball_blue, color);
    }

    private void drawLine(Canvas canvas, Paint paint, double[] numbers) {
        for (int i = 0; i < names.length - 1; i++) {
            // 如果自己和下一个点都为0，就不画此线。
            if (i < names.length - 1 && numbers[i] == 0 && numbers[i + 1] == 0) {
                continue;
            }
            float x1 = left + h_pace_pixel * i;
            float x2 = left + h_pace_pixel * (i + 1);
            float y1 = top
                    + (float) (nv * v_pace_pixel * (1 - (numbers[i] - v_min)
                    / (v_pace_actual * nv)));
            float y2 = top
                    + (float) (nv * v_pace_pixel * (1 - (numbers[i + 1] - v_min)
                    / (v_pace_actual * nv)));
            canvas.drawLine(x1, y1, x2, y2, paint);
        }

    }

    private void drawPoint(Canvas canvas, Paint paint, double[] numbers,
                           int resBall, int color) {
        for (int i = 0; i < names.length; i++) {
            // 如果自己和两边都为0，就不画此点。
            if (numbers[i] == 0
                    && (i == 0 || i > 0 && numbers[i - 1] == 0)
                    && (i == names.length - 1 || i < names.length - 1
                    && numbers[i + 1] == 0)) {
                continue;
            }
            float y = top
                    + (float) (nv * v_pace_pixel * (1 - (numbers[i] - v_min)
                    / (v_pace_actual * nv)));
            Bitmap ball = BitmapFactory.decodeResource(ba.getResources(),
                    resBall);
            float offset_h = ball.getScaledWidth(canvas) / 2;
            float offset_v = ball.getScaledHeight(canvas) / 2;

            paint.setStrokeWidth(1);
            String n = "" + numbers[i];
            if (n.endsWith(".0")) {
                n = n.replace(".0", "");
            }
            canvas.drawText(n, left + h_pace_pixel * i - offset_h, y - offset_v
                    - 10, paint);
            paint.setColor(color);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawBitmap(ball, left + h_pace_pixel * i - offset_h, y
                    - offset_v, paint);
        }

    }

    public Bitmap getBitmap() {
        return bmp;
    }
}
