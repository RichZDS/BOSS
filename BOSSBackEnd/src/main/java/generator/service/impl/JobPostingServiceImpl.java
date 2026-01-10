package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.JobPosting;
import generator.service.JobPostingService;
import generator.mapper.JobPostingMapper;
import org.springframework.stereotype.Service;

/**
* @author 33882
* @description 针对表【job_posting(岗位表)】的数据库操作Service实现
* @createDate 2026-01-01 00:02:54
*/
@Service
public class JobPostingServiceImpl extends ServiceImpl<JobPostingMapper, JobPosting>
    implements JobPostingService{

}




