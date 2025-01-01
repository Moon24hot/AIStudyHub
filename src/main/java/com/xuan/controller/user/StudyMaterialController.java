package com.xuan.controller.user;

import com.xuan.domain.dto.StudyMaterialAddDTO;
import com.xuan.domain.dto.StudyMaterialUpdateDTO;
import com.xuan.domain.vo.StudyMaterialVO;
import com.xuan.result.Result;
import com.xuan.service.IInformationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "学习资料相关接口", description = "用户端 - 学习资料相关接口")
@RequestMapping("/api/user/material")
@RestController
public class StudyMaterialController {

    @Autowired
    private IInformationService informationService;

    @GetMapping("/list")
    @Operation(summary = "获取学习资料列表")
    public Result<List<StudyMaterialVO>> getStudyMaterials(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        return informationService.getStudyMaterialsByUserId(userId);
    }

    @GetMapping("/view")
    @Operation(summary = "查看学习资料内容")
    public Result<String> viewStudyMaterialContent(
            @Parameter(description = "学习资料ID", required = true) @RequestParam Integer materialId) {
        if (materialId == null) {
            return Result.error("学习资料ID不能为空");
        }
        return informationService.getStudyMaterialContent(materialId);
    }

    @PostMapping("/add")
    @Operation(summary = "新增学习资料")
    public Result<String> addStudyMaterial(@RequestBody StudyMaterialAddDTO studyMaterialAddDTO) {
        if (studyMaterialAddDTO.getUserId() == null)
            return Result.error("用户id不能为空");
        if (studyMaterialAddDTO.getTitle() == null)
            return Result.error("标题不能为空");
        if (studyMaterialAddDTO.getContent() == null)
            return Result.error("内容不能为空");
        return informationService.addStudyMaterial(studyMaterialAddDTO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新学习资料")
    public Result<String> updateStudyMaterial(@RequestBody StudyMaterialUpdateDTO studyMaterialUpdateDTO) {
        return informationService.updateStudyMaterial(studyMaterialUpdateDTO);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除学习资料")
    public Result<String> deleteStudyMaterial(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId,
            @Parameter(description = "学习资料ID", required = true) @RequestParam Integer materialId) {
        return informationService.deleteStudyMaterial(userId, materialId);
    }

}