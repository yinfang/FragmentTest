package com.clubank.domain;

/**
 * 
 */
public class RT {
	public static final int OPERATION_FAILED = 10001; // 操作失败
	public static final int SOCKET_TIMEOUT = 10010;// 网络超时
	public static final int SOCKET_ERROR = 10011;// 网络错误
	public static final int SERVER_ERROR = 1111111; // 服务端异常 tang
	public static final int INTERFACE_ERROR = 10013; // 接口错误
	public static final int CLUB_OFFLINE = 10014;// 球会端不在线(特殊，暂时放common)
	public static final int CLUB_MIDWARE_OBSOLETE = 10015;// 球会端应用过时(特殊，暂时放common)
	public static final int ILLEGAL_ACCESS = 10098;// 未授权的访问


	public static final int SUCCESS =BRT.SUCCESS.getCode();// 操作成功
	public static final int UNKNOWN_ERROR = -999;// 未知错误

}
