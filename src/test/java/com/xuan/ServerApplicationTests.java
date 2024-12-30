package com.xuan;

import com.xuan.domain.entity.QuestionBanks;
import com.xuan.enums.BanksStatus;
import com.xuan.service.IQuestionBanksService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class ServerApplicationTests {

    @Autowired
    private IQuestionBanksService questionBanksService;

    /**
     * 测试得到枚举字段
     */
    @Test
    public void testGetStatus() {
        List<QuestionBanks> list = questionBanksService.list();
        list.forEach(System.out::println);
    }

    /**
     * 测试插入枚举字段
     */
    @Test
    public void testInsertStatus() {
        boolean save = questionBanksService.save(new QuestionBanks()
				.setStatus(BanksStatus.NOT_SHARED)
				.setCreateTime(LocalDateTime.now())
				.setCreatorId(4)
				.setTitle("行策")
				.setDescription("23年行策题库")
		);
        System.out.println("save = " + save);
	}
}
