package com.wjj.worker.persistence.dao;

import com.wjj.worker.persistence.dto.QuartzDto;
import com.wjj.worker.persistence.dto.QuartzInfoDto;
import com.wjj.worker.framework.request.WjjApiPage;
import com.wjj.worker.framework.request.WjjApiParameter;
import org.springframework.stereotype.Repository;

/**
 * @author BeerGod
 * job相关
 */
@Repository
public interface WJobDao {
    /**
     * 查询job列表
     *
     * @param page 分页对象实例
     * @return
     */
    WjjApiPage<QuartzInfoDto> queryJobList(WjjApiPage<WjjApiParameter> page);

    /**
     * 添加job日志
     */
    int addJobLog(WjjApiParameter parameter);

    /**
     * 添加
     */
    int addJob(QuartzDto quartzDTO);

    /**
     * 更新job
     */
    int updateJob(QuartzDto quartzDTO);
}
