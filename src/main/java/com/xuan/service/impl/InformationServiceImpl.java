package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xuan.domain.dto.StudyMaterialAddDTO;
import com.xuan.domain.dto.StudyMaterialUpdateDTO;
import com.xuan.domain.entity.Information;
import com.xuan.domain.entity.Users;
import com.xuan.domain.vo.StudyMaterialVO;
import com.xuan.mapper.InformationMapper;
import com.xuan.mapper.UsersMapper;
import com.xuan.result.Result;
import com.xuan.service.IInformationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2024-12-30
 */
@Service
public class InformationServiceImpl extends ServiceImpl<InformationMapper, Information> implements IInformationService {

    @Autowired
    private InformationMapper informationMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Value("classpath:prompts/summarize.txt")
    private Resource summarizePrompt;

    @Autowired
    private OpenAiChatModel chatModel;

    @Override
    public Result<List<StudyMaterialVO>> getStudyMaterialsByUserId(Integer userId) {
        // 1. 检查用户是否存在
        Users user = usersMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 2. 查询该用户的所有学习资料
        List<Information> informationList = informationMapper.selectList(
                new LambdaQueryWrapper<Information>()
                        .eq(Information::getCreatorId, userId));

        // 3. 转换为 StudyMaterialVO 列表
        List<StudyMaterialVO> studyMaterialVOList = informationList.stream().map(information -> {
            StudyMaterialVO vo = new StudyMaterialVO();
            BeanUtils.copyProperties(information, vo);
            return vo;
        }).collect(Collectors.toList());

        return Result.success(studyMaterialVOList);
    }

    @Override
    public Result<String> getStudyMaterialContent(Integer materialId) {
        // 1. 检查学习资料是否存在
        Information information = informationMapper.selectById(materialId);
        if (information == null) {
            return Result.error("学习资料不存在");
        }

        // 2. 返回学习资料内容
        return Result.success(information.getContent());
    }

    @Override
    public Result<String> addStudyMaterial(StudyMaterialAddDTO studyMaterialAddDTO) {
        // 1. 检查用户是否存在
        Users user = usersMapper.selectById(studyMaterialAddDTO.getUserId());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 2. 创建 Information 对象并保存
        Information information = new Information();
        information.setCreatorId(studyMaterialAddDTO.getUserId());
        information.setTitle(studyMaterialAddDTO.getTitle());
        information.setContent(studyMaterialAddDTO.getContent());
        informationMapper.insert(information);

        return Result.success("学习资料创建成功");
    }

    @Override
    public Result<String> updateStudyMaterial(StudyMaterialUpdateDTO studyMaterialUpdateDTO) {
        // 1. 检查参数
        if (studyMaterialUpdateDTO.getMaterialId() == null) {
            return Result.error("学习资料ID不能为空");
        }
        if (studyMaterialUpdateDTO.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }

        // 2. 检查学习资料是否存在
        Information information = informationMapper.selectById(studyMaterialUpdateDTO.getMaterialId());
        if (information == null) {
            return Result.error("学习资料不存在");
        }

        // 3. 检查当前用户是否有权限更新该学习资料
        if (!information.getCreatorId().equals(studyMaterialUpdateDTO.getUserId())) {
            return Result.error("无权更新该学习资料");
        }

        // 4. 更新学习资料
        LambdaUpdateWrapper<Information> updateWrapper = new LambdaUpdateWrapper<Information>()
                .eq(Information::getId, studyMaterialUpdateDTO.getMaterialId());
        if (StringUtils.hasText(studyMaterialUpdateDTO.getTitle())) {
            updateWrapper.set(Information::getTitle, studyMaterialUpdateDTO.getTitle());
        }
        if (StringUtils.hasText(studyMaterialUpdateDTO.getContent())) {
            updateWrapper.set(Information::getContent, studyMaterialUpdateDTO.getContent());
        }
        informationMapper.update(null, updateWrapper);

        return Result.success("学习资料更新成功");
    }

    @Override
    public Result<String> deleteStudyMaterial(Integer userId, Integer materialId) {
        // 1. 检查学习资料是否存在以及是否属于该用户
        Information information = informationMapper.selectById(materialId);
        if (information == null) {
            return Result.error("学习资料不存在");
        }
        if (!information.getCreatorId().equals(userId)) {
            return Result.error("无权删除该学习资料");
        }

        // 2. 删除学习资料
        informationMapper.deleteById(materialId);

        return Result.success("学习资料删除成功");
    }

    @Override
    public Result<String> summarizeStudyMaterial(Integer materialId) {
        // 1. 检查学习资料是否存在
        Information information = informationMapper.selectById(materialId);
        if (information == null) {
            return Result.error("学习资料不存在");
        }

        // 2. 读取 prompt 模板
        String promptTemplate;
        System.out.println("summarizePrompt = " + summarizePrompt);
        try {
            promptTemplate = new String(summarizePrompt.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Result.error("读取 prompt 模板失败");
        }


        // 3. 替换模板中的变量
        String prompt = promptTemplate
                .replace("{title}", information.getTitle())
                .replace("{content}", information.getContent());

        // 4. 调用 AI 大模型并获取响应
        String aiResponse = chatModel.call(prompt);

        // 5. 返回总结内容
        return Result.success(aiResponse);
    }

}
