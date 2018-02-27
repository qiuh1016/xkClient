package com.cetcme.xkclient.utils;

/**
 * 项目名称：android
 * 类描述：
 * 创建人：ZHANGHANJUN
 * 创建时间：2016/12/5 15:41
 * 修改人：Administrator
 * 修改时间：2016/12/5 15:41
 * 修改备注：
 *
 * @version 邮箱：1178075841@qq.com
 */


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhd.mswcs.R;


/**
 * 公用的弹出框
 *
 * @author lining
 */
public class LoadingDialog {
    private AnimationDrawable animationDrawable;

    private Dialog dialog;
    /**
     * 得到自定义的progressDialog
     *
     * @return
     */
    private LoadingDialog() {}
    private static LoadingDialog singleDialog=null;

    public static LoadingDialog getInstance() {
        if (singleDialog == null) {
            singleDialog = new LoadingDialog();
        }
        return singleDialog;
    }



    public Dialog createLoadingDialog(Context context, String msg) {
        if(context!=null){
            // 首先得到整个View
            View view = LayoutInflater.from(context).inflate(R.layout.loading_dialog_view, null);
            if(view!=null){
                // 获取整个布局
                LinearLayout layout = (LinearLayout) view.findViewById(R.id.dialog_view);
                // 页面中的Img
                ImageView img = (ImageView) view.findViewById(R.id.img);
                // 页面中显示文本
                TextView tipText = (TextView) view.findViewById(R.id.tipTextView);
                // 加载动画，动画用户使img图片不停的旋转
                Animation animation = AnimationUtils.loadAnimation(context,R.anim.dialog_load_animation);
                // 显示动画
                img.startAnimation(animation);
                // 显示文本
                tipText.setText(Html.fromHtml(msg));
                // 创建自定义样式的Dialog
                Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
                // 设置返回键无效
                loadingDialog.setCancelable(false);
                loadingDialog.setCanceledOnTouchOutside(false);
                loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                return loadingDialog;
            }
        }
        return null;
    }

    /**
     * 显示Dialog
     */
    public void showDialog(Context activity, String msg) {
        if (dialog == null) {
            dialog = createLoadingDialog(activity,msg);
            if(dialog!=null){
                dialog.show();
                // 如下对自定义dialog的相关设置一定要放在dialog.show()方法的下面,否则不起作用。
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = ScreenUtils.getScreenWidth(activity)/2; // 宽度
                dialogWindow.setAttributes(lp);
            }

        }
    }

    /**
     * 关闭Dialog
     */
    public void closeDialog() {
        closeAnimation();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    public void openAnimation(AnimationDrawable animationDrawable){
        if(animationDrawable!=null&&!animationDrawable.isRunning()){
            animationDrawable.start();
        }

    }

    public void closeAnimation(){
        if(animationDrawable!=null&&animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }


}
