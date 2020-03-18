package com.example.project_01;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.project_01.model.product.qna.dao.QnaDAO;
import com.example.project_01.model.product.qna.dto.QnaDTO;
import com.example.project_01.model.product.qna.dto.SearchQnaDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class QnaDaoTest {
	
	@Autowired
	QnaDAO qnaDao;
	
	@Test
	public void selectQna() {
		List<QnaDTO> qnaList = qnaDao.selectQna(0, 10, new SearchQnaDTO());
		for(QnaDTO dto : qnaList)
			System.out.println(dto);
	}
}
