package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.Application;
import generator.service.ApplicationService;
import generator.mapper.ApplicationMapper;
import org.springframework.stereotype.Service;

/**
* @author 33882
* @description 针对表【application(用户投递记录表)】的数据库操作Service实现
* @createDate 2026-01-01 00:03:28
*/
@Service
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application>
    implements ApplicationService{

}




