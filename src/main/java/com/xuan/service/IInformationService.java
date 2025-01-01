package com.xuan.service;

import com.xuan.domain.dto.StudyMaterialAddDTO;
import com.xuan.domain.dto.StudyMaterialUpdateDTO;
import com.xuan.domain.entity.Information;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.domain.vo.StudyMaterialVO;
import com.xuan.result.Result;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2024-12-30
 */
public interface IInformationService extends IService<Information> {

    /**
     * 根据用户ID获取学习资料列表
     * @param userId 用户ID
     * @return 学习资料列表
     */
    Result<List<StudyMaterialVO>> getStudyMaterialsByUserId(Integer userId);

    /**
     * 查看学习资料内容
     * @param materialId 学习资料ID
     * @return 学习资料内容
     */
    Result<String> getStudyMaterialContent(Integer materialId);

    /**
     * 新增学习资料
     * @param studyMaterialAddDTO 学习资料新增DTO
     * @return 结果
     */
    Result<String> addStudyMaterial(StudyMaterialAddDTO studyMaterialAddDTO);

    /**
     * 更新学习资料
     * @param studyMaterialUpdateDTO 学习资料更新DTO
     * @return 结果
     */
    Result<String> updateStudyMaterial(StudyMaterialUpdateDTO studyMaterialUpdateDTO);

    /**
     * 删除学习资料
     * @param userId 用户ID
     * @param materialId 学习资料ID
     * @return 结果
     */
    Result<String> deleteStudyMaterial(Integer userId, Integer materialId);
}
