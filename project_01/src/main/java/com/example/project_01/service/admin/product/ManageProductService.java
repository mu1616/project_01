package com.example.project_01.service.admin.product;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.project_01.model.pagination.dto.PageDTO;
import com.example.project_01.model.product.dao.ProductDAO;
import com.example.project_01.model.product.dto.ProductDTO;
import com.example.project_01.model.product.dto.ProductEntity;
@Service
public class ManageProductService {
	@Value("${file.upload.directory}")
	String filePath;
	@Autowired
	ProductDAO dao;
	public String makeFileName(String filename) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String today = formatter.format(new java.util.Date());
		filename = today + UUID.randomUUID().toString() + filename.substring(filename.lastIndexOf("."));
		return filename;
	}
	
	@Transactional
	public void register(ProductEntity productEntity, MultipartFile files, int []mainDisplay) {
		String filename = files.getOriginalFilename();
		String file_ext = filename.substring(filename.lastIndexOf(".") + 1);
		file_ext = file_ext.toLowerCase();
		String[] allow_file = { "jpg", "png", "bmp", "gif" };
		try {
			int cnt = 0;
			for (int i = 0; i < allow_file.length; i++) {
				if (file_ext.equals(allow_file[i])) {
					cnt++;
				}
			}
			if (cnt == 0) {
				// 에러
			} else {
				filename = makeFileName(filename);
				files.transferTo(new File("C://shoesfactory/img/" + filename));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		productEntity.setProduct_image(filePath + filename);
		dao.productRegister(productEntity);
		System.out.println(productEntity);
		System.out.println(mainDisplay);
		
		//display 테이블에 추가
		if(mainDisplay !=null) {
			for(int i=0; i<mainDisplay.length ; i++) {
				System.out.println("maindisplay에 저장"+ productEntity.getProduct_idx()+" "+mainDisplay[i]);
				dao.displayRegister(productEntity.getProduct_idx(), mainDisplay[i]);
			}
		}
	}
	//페이지당 상품개수 = 10
	public PageDTO calPage(int currentPage) {
		int countRecord;
		int startPage;
		int endPage;
		int totalPage;
		PageDTO pageDto = new PageDTO();
		pageDto.setCurrentPage(currentPage);
		countRecord = dao.countProduct();
		pageDto.setCountRecord(countRecord);
		totalPage = (int)Math.ceil(countRecord/(double)10);
		pageDto.setTotalPage(totalPage);
		startPage = (currentPage-1)/10*10+1;
		pageDto.setStartPage(startPage);
		endPage = (startPage+9>totalPage)?totalPage:startPage+9;
		pageDto.setEndPage(endPage);
		
		return pageDto;
	}
	
	public List<ProductDTO> selectProduct(int currentPage) {
		int start = (currentPage-1) * 10;
		int length = 10;
		List<ProductDTO> productList = dao.selectProduct(start, length);
		return productList;
	}
}
