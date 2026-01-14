package com.zds.boss.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zds.boss.model.dto.application.ApplicationQueryRequest;
import com.zds.boss.model.entity.Application;
import com.zds.boss.model.vo.ApplicationVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ApplicationMapper extends BaseMapper<Application> {

    @Select("""
            <script>
            select
                a.id,
                a.user_id as userId,
                u.username as userName,
                a.resume_id as resumeId,
                a.job_id as jobId,
                a.boss_id as bossId,
                a.status,
                a.applied_at as appliedAt,
                a.updated_at as updatedAt
            from application a
            left join user u on u.id = a.user_id
            <where>
                <if test="query != null and query.id != null">
                    and a.id = #{query.id}
                </if>
                <if test="query != null and query.userId != null">
                    and a.user_id = #{query.userId}
                </if>
                <if test="query != null and query.resumeId != null">
                    and a.resume_id = #{query.resumeId}
                </if>
                <if test="query != null and query.jobId != null">
                    and a.job_id = #{query.jobId}
                </if>
                <if test="query != null and query.bossId != null">
                    and a.boss_id = #{query.bossId}
                </if>
                <if test="query != null and query.status != null">
                    and a.status = #{query.status}
                </if>
            </where>
            <choose>
                <when test="query != null and query.sortField != null and (query.sortField == 'id' or query.sortField == 'a.id')">
                    order by a.id
                </when>
                <when test="query != null and query.sortField != null and (query.sortField == 'userId' or query.sortField == 'user_id' or query.sortField == 'a.user_id')">
                    order by a.user_id
                </when>
                <when test="query != null and query.sortField != null and (query.sortField == 'resumeId' or query.sortField == 'resume_id' or query.sortField == 'a.resume_id')">
                    order by a.resume_id
                </when>
                <when test="query != null and query.sortField != null and (query.sortField == 'jobId' or query.sortField == 'job_id' or query.sortField == 'a.job_id')">
                    order by a.job_id
                </when>
                <when test="query != null and query.sortField != null and (query.sortField == 'bossId' or query.sortField == 'boss_id' or query.sortField == 'a.boss_id')">
                    order by a.boss_id
                </when>
                <when test="query != null and query.sortField != null and (query.sortField == 'status' or query.sortField == 'a.status')">
                    order by a.status
                </when>
                <when test="query != null and query.sortField != null and (query.sortField == 'appliedAt' or query.sortField == 'applied_at' or query.sortField == 'a.applied_at')">
                    order by a.applied_at
                </when>
                <when test="query != null and query.sortField != null and (query.sortField == 'updatedAt' or query.sortField == 'updated_at' or query.sortField == 'a.updated_at')">
                    order by a.updated_at
                </when>
                <otherwise>
                    order by a.applied_at
                </otherwise>
            </choose>
            <choose>
                <when test="query != null and query.sortOrder != null and query.sortOrder == 'ascend'">
                    asc
                </when>
                <otherwise>
                    desc
                </otherwise>
            </choose>
            </script>
            """)
    IPage<ApplicationVO> selectApplicationVOByPage(Page<ApplicationVO> page, @Param("query") ApplicationQueryRequest query);
}

