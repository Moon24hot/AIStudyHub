package com.xuan.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@TableName("tbl_book")
public class TblBook {
    @TableId(value = "id",type = IdType.AUTO)
    @Schema(description ="书籍id")
    private Integer id;
    @Schema(description ="书籍类型")
    private String type;
    @Schema(description ="书籍名称")
    private String name;
    @Schema(description ="书籍介绍")
    private String description;

}