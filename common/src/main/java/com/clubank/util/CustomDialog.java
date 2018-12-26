package com.clubank.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubank.common.R;
import com.clubank.domain.C;

public class CustomDialog {

    private Context context;
    private OKProcessor okProcessor;
    private OKProcessorDialog okProcessorDialog;
    private CancelProcessor cancelProcessor;
    private CancelProcessorDialog cancelProcessorDialog;
    private Initializer initializer;
    private View view;
    private boolean setCancelable = true;

    public CustomDialog(Context context) {
        this.context = context;
        if (C.dialogTheme != -1) {
            context.setTheme(C.dialogTheme);
        }
    }

    public CustomDialog(Context context, int customLayout) {
        this.context = context;
        if (C.dialogTheme != -1) {
            context.setTheme(C.dialogTheme);
        }
        view = LayoutInflater.from(context).inflate(customLayout, null);
    }

    public CustomDialog(Context context, View customView) {
        this.context = context;
        if (C.dialogTheme != -1) {
            context.setTheme(C.dialogTheme);
        }
        view = customView;
    }

    public void setOKProcessor(OKProcessor processor) {
        this.okProcessor = processor;
    }

    public void setOKProcessorDialog(OKProcessorDialog processor) {
        this.okProcessorDialog = processor;
    }

    public void setCancelProcessor(CancelProcessor processor) {
        this.cancelProcessor = processor;
    }

    public void setCancelProcessorDialog(CancelProcessorDialog processor) {
        this.cancelProcessorDialog = processor;
    }

    public void setInitializer(Initializer initializer) {
        this.initializer = initializer;
    }

    public interface OKProcessor {
        void process(Builder alertDialog, View view);
    }

    public interface OKProcessorDialog {
        /**
         * @param dialog
         * @return true Close the dialog,false don't close dialog
         */
        void process(Dialog dialog);
    }

    public interface CancelProcessor {
        void process(Builder alertDialog, View view);
    }

    public interface CancelProcessorDialog {
        /**
         * @param dialog
         * @return true Close the dialog,false don't close dialog
         */
        void process(Dialog dialog);
    }

    public interface Initializer {
        void init(Builder alertDialog, View view);
    }

    public Builder show(CharSequence title, boolean showCancel) {
        final Builder dialog = new Builder(context);
        dialog.setTitle(title);
        dialog.setPositiveButton(context.getString(R.string.ok),
                new OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (okProcessor != null) {
                            okProcessor.process(dialog, view);
                        }
                    }
                });
        if (showCancel) {
            dialog.setNegativeButton(context.getString(R.string.cancel),
                    new OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            if (cancelProcessor != null) {
                                cancelProcessor.process(dialog, view);

                            }
                        }
                    });
        }
        if (initializer != null) {
            initializer.init(dialog, view);
        }
        if (view != null) {
            dialog.setView(view);
        }
        AlertDialog ad = dialog.show();
        ad.setCancelable(setCancelable);
        ad.setCanceledOnTouchOutside(false);
        TextView textView = (TextView) ad.findViewById(android.R.id.message);
        if (textView != null) {
            textView.setTextSize(17);
        }
        return dialog;
    }

    public void setCancelable(boolean setCancelable) {
        this.setCancelable = setCancelable;
    }

    /**
     * 可以在项目中替换dialog布局改变样式
     *
     * @param title
     * @param showCancel
     * @param
     * @return
     */
    public AlertDialog show(CharSequence title, CharSequence msg, boolean showCancel, int background) {
        final AlertDialog ad = new Builder(context).create();
        ad.show();
        Window window = ad.getWindow();
        window.setContentView(R.layout.custom_dialog);
        //	d.setTitle(title);
        TextView tv = (TextView) ad.findViewById(R.id.title);
        tv.setText(title);
        TextView tmsg = (TextView) ad.findViewById(R.id.body_content);
        tmsg.setGravity(Gravity.CENTER);
        tmsg.setText(msg);

        Button btn = (Button) ad.findViewById(R.id.btn_ok);
        Button btn2 = (Button) ad.findViewById(R.id.btn_cancel);
        if (background != 0) {
            btn.setBackgroundResource(background);
            btn2.setBackgroundResource(background);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (okProcessorDialog != null) {
                    okProcessorDialog.process(ad);

                }
            }
        });


        if (!showCancel) {
            btn2.setVisibility(View.GONE);
        } else {
            btn2.setVisibility(View.VISIBLE);
        }

        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ad.dismiss();
            }
        });


        return ad;
    }

    /**
     * 自定义确定取消按钮文字,取消按钮点击事件
     *
     * @param title
     * @param msg
     * @param sure
     * @param cancel
     * @param showCancel
     * @param background
     * @return
     */
    public AlertDialog show(CharSequence title, CharSequence msg, CharSequence sure, CharSequence cancel, boolean showCancel, int background) {
        final AlertDialog ad = new Builder(context).create();
        ad.show();
        ad.setCancelable(false);
        ad.setCanceledOnTouchOutside(false);
        Window window = ad.getWindow();
        window.setContentView(R.layout.custom_dialog);
        //	d.setTitle(title);
        TextView tv = (TextView) ad.findViewById(R.id.title);
        tv.setText(title);
        TextView tmsg = (TextView) ad.findViewById(R.id.body_content);
        tmsg.setGravity(Gravity.CENTER);
        tmsg.setText(msg);

        Button btn = (Button) ad.findViewById(R.id.btn_ok);
        btn.setText(sure);
        Button btn2 = (Button) ad.findViewById(R.id.btn_cancel);
        btn2.setText(cancel);
        if (background != 0) {
            btn.setBackgroundResource(background);
            btn2.setBackgroundResource(background);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (okProcessorDialog != null) {
                    okProcessorDialog.process(ad);

                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelProcessorDialog != null) {
                    cancelProcessorDialog.process(ad);

                }
            }
        });

        if (!showCancel) {
            btn2.setVisibility(View.GONE);
        } else {
            btn2.setVisibility(View.VISIBLE);
        }

        return ad;
    }

    public AlertDialog showImage(Bitmap bitmap) {
        final AlertDialog ad = new Builder(context).create();
        ad.show();
        Window window = ad.getWindow();
        window.setContentView(R.layout.image_dialog);
        ImageView image = (ImageView) ad.findViewById(R.id.image);
        image.setImageBitmap(bitmap);

        return ad;
    }
}
