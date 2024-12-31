package com.xuan.mapper;

import com.xuan.domain.vo.QuestionVO;
import org.apache.ibatis.annotations.Mapper;
import com.xuan.domain.entity.Questions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2024-12-30
 */
@Mapper
public interface QuestionsMapper extends BaseMapper<Questions> {

}
