package com.wjj.worker.framework.request;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * 自定义分页组件，用户获取分页信息等
 * <pre>
 *     使用方面dao层直接将此参数放入第一位，
 *     如想取参数可以访问 wjjParams.属性 获取
 *
 *    在 controller 层 可通过 WjjApiValidRequest.getPageParameter() 进行获取当前分页实例
 * </pre>
 *
 * @param <T>
 * @author BeerGod
 */
@Data
public class WjjApiPage<T> extends Page<T> {

    private WjjApiParameter wjjParams;

    public WjjApiPage() {
        super();
    }

    public WjjApiPage(long current, long size) {
        super(current, size, 0L);
    }

    public WjjApiPage(long current, long size, long total) {
        super(current, size, total, true);
    }

    public WjjApiPage(long current, long size, boolean searchCount) {
        super(current, size, 0L, searchCount);
    }

    public WjjApiPage(long current, long size, long total, boolean searchCount) {
        super(current, size, total, searchCount);
    }
}
