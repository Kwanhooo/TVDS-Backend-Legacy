package com.ruoyi.common.exception;

/**
 * 错误码
 *
 * @author Kwanho
 */
public enum ErrorCode {

    SUCCESS(0, "OK"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NULL_ERROR(40001, "请求数据为空"),
    NOT_LOGIN(40100, "未登录"),
    NO_AUTH(40101, "无权限"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    SAVE_ERROR(50010, "新增失败"),
    UPDATE_ERROR(50020, "更新失败"),
    DELETE_ERROR(50030, "删除失败"),
    MODEL_RUN_ERROR(60010, "模型运行时错误"),
    MODEL_RESULT_INVALID(60020, "模型结果无效"),
    FILE_UPLOAD_ERROR(60020, "文件上传错误"),
    WORKFLOW_ERROR(60030, "工作流错误");

    private final int code;

    /**
     * 状态码信息
     */
    private final String message;


    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
