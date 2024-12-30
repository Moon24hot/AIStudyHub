package com.xuan.service.impl;

import com.xuan.domain.entity.Questions;
import com.xuan.mapper.QuestionsMapper;
import com.xuan.service.IQuestionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2024-12-30
 */
@Service
public class QuestionsServiceImpl extends ServiceImpl<QuestionsMapper, Questions> implements IQuestionsService {

}
