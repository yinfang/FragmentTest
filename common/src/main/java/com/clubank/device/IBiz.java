package com.clubank.device;

import android.content.Context;

import com.clubank.domain.Result;
import com.clubank.util.MyDBHelper;

/**
 * 应用要实现的一些通用方法，参见demo里的 MyBiz
 *
 * @author chenyh
 *
 */
public interface IBiz {

	/**
	 * 检查是否登陆，如果没登陆则跳出登陆框。如果登陆成功则会回调logined()方法
	 *
	 * @param loginType
	 *            标识，以便如果有几个地方调用此方法时，回调 logined 区分
	 */
	void checkLogin(int loginType);

	/**
	 * 到主界面
	 */
	void goMain();

	/**
	 * 打开登陆界面
	 */
	void openLoginActivity();

	/**
	 * 登陆成功要执行的通用代码
	 *
	 * @param result
	 *            调用远程登录方法后的结果数据
	 */
	void loginSuccess(Result result);

	/**
	 * 设置服务端类型
	 *
	 * @param serverType
	 *            一般1 代表 远程 2 代表测试服务端
	 */
	void setServerType(int serverType);

	/**
	 * 恢复一些全局变量
	 */
	void restoreVars();

	/**
	 * 一些通用的异步方法结果处理，例如登陆或检查新版本等
	 *
	 * @param op
	 * @param result
	 */
	void processAsyncResult(Class<?> op, Result result);

	/**
	 * 返回检查登陆类别标识，就是checkLogin带入的loginType
	 *
	 * @return
	 */
	int getLoginType();

	/**
	 * 检查新版本。
	 *
	 * @param isManual
	 *            是否是手动检查
	 */
	void checkVersion(boolean isManual);

	/**
	 * 数据库初始化。
	 *
	 */
	MyDBHelper initDBHelper(Context context);

	/**
	 * 上传头像
	 * 
	 * 
	 */
	void UploadFile(Context context, String filePath);


	void openMainActive();

}
