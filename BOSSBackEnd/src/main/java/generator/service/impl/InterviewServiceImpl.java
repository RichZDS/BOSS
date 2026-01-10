package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.Interview;
import generator.service.InterviewService;
import generator.mapper.InterviewMapper;
import org.springframework.stereotype.Service;

/**
* @author 33882
* @description 针对表【interview(面试安排表)】的数据库操作Service实现
* @createDate 2026-01-01 00:03:01
*/
@Service
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, Interview>
    implements InterviewService{

}




