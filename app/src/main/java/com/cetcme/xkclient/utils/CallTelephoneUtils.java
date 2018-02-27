package com.cetcme.xkclient.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

public class CallTelephoneUtils {
	private Context context;
	private CharSequence telephone;

	public CallTelephoneUtils(Context context, CharSequence tel) {
		super();
		this.context = context;
		this.telephone = tel;
	}

	public void calltelephone() {
		//调用Android系统的拨号界面，但不发起呼叫，用户按下拨号键才会进行呼叫
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+telephone));
		//直接拨号发起呼叫
		//Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telephone));
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		context.startActivity(intent);
	}
}
