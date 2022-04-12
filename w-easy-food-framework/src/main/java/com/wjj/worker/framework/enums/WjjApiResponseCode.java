package com.wjj.worker.framework.enums;

/**
 * 自定义请求code返回类
 *
 * @author BeerGod
 */
public enum WjjApiResponseCode implements WjjApiEnumsInterFace<Integer> {
    /**
     * 参数无效
     */
    METHOD_ARGUMENT_NOT_VALID(40300, "参数提交无效"),
    /**
     * 必须实现 job 接口，且注册于 ioc 中
     */
    CLASS_NOT_FOUND(50000, "指定class路径不存在，请确保此类存在并实现了Job接口"),
    /**
     * 当前分组任务已存在
     */
    JOB_IS_EXIST(50001, "该任务分组下已存在此任务"),
    /**
     * 当前job不存在
     */
    JOB_NOT_EXIST(50002, "此job不存在"),
    /**
     * 添加job任务失败
     */
    ADD_JOB_ERROR(50003, "添加job失败"),
    /**
     * 更新job服务异常
     */
    UPDATE_JOB_ERROR(50004, "更新job失败"),
    /**
     * 服务异常
     */
    SERVER_ERROR(50005, "服务异常"),
    /**
     * json 反序列化失败
     */
    JSON_READER_ERROR(50005, "读取数据序列化发生错误"),
    ;


    WjjApiResponseCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 返回响应code
     */
    private Integer code;
    /**
     * 具体描述
     */
    private String msg;

    @Override
    public Integer code() {
        return code;
    }

    @Override
    public String msg() {
        return msg;
    }
}
