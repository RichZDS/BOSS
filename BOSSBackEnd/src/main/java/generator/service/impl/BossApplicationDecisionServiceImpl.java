package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.BossApplicationDecision;
import generator.service.BossApplicationDecisionService;
import generator.mapper.BossApplicationDecisionMapper;
import org.springframework.stereotype.Service;

/**
* @author 33882
* @description 针对表【boss_application_decision(Boss处理投递（接受/拒绝）表)】的数据库操作Service实现
* @createDate 2026-01-01 00:02:27
*/
@Service
public class BossApplicationDecisionServiceImpl extends ServiceImpl<BossApplicationDecisionMapper, BossApplicationDecision>
    implements BossApplicationDecisionService{

}




