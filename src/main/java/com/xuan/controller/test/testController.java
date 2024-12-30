package com.xuan.controller.test;

import com.xuan.pojo.entity.TblBook;
import com.xuan.service.testService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "测试")
@RestController
@RequestMapping("/api/test")
public class testController {

    @Autowired
    private testService testService;

    @Operation(summary = "测试查询全部方法")
    @GetMapping("/list")
    public List<TblBook> list() {
        List<TblBook> book = testService.list();
//        book.forEach(item -> System.out.println(item.getDescription()));
        return book;
    }

    @Operation(summary = "测试单个查询方法")
    @GetMapping("/{id}")
    public TblBook getById(@PathVariable int id) {
        TblBook book = testService.getById(id);
        return book;
    }
}