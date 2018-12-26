package com.clubank.domain;

public enum BRT {

    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    PARA_FORMAT_ERROR(202, "参数格式错误"),
    ACCOUNT_ERROR(301, "账号已存在/账号不存在"),
    LOGIN_ERROR(302, "用户名或密码错误"),
    ACCOUNT_FORBIDDEN(303, "账号被禁用"),
    AUTH_ERROR(304, "授权认证失败,非法请求"),
    PAPA_TOO_LONG(401, "参数长度太长"),
    PARA_ERROR(402, "参数错误"),
    MOUNT_LIMIT(403, "数量超过上限"),
    SERVER_ERROR(500, "服务器内部错误"),
    DATA_EXIST(501, "数据已存在"),
    DATA_NOT_EXIST(502, "数据不存在"),
    METHOD_NOT_EXIST(503, "接口不存在"),
    UNKNOWN_ERROR(900, "未知错误");


    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    BRT(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
