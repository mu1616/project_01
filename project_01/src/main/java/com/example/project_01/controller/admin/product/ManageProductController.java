package com.example.project_01.controller.admin.product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.project_01.model.pagination.dto.PageDTO;
import com.example.project_01.model.product.dao.ProductDAO;
import com.example.project_01.model.product.dto.ProductDTO;
import com.example.project_01.model.product.dto.ProductEntity;
import com.example.project_01.model.search.dto.SearchDTO;
import com.example.project_01.model.stock.dao.StockDAO;
import com.example.project_01.model.stock.dto.StockDTO;
import com.example.project_01.service.admin.product.ManageProductService;
import com.example.project_01.service.pagination.PageService;

@Controller
public class ManageProductController {
	@Autowired
	ManageProductService productService;
	@Value("${file.upload.directory}")
	String filePath;
	@Value("${smarteditor.upload.directory}")
	String editorPath;
	@Autowired
	ProductDAO productDao;
	@Autowired
	PageService pageService;
	
	@RequestMapping(value = "/admin/product/register", method = RequestMethod.GET)
	public String registerPage() {
		return "admin/admin";
	}

	@RequestMapping(value = "/admin/product/register", method = RequestMethod.POST)
	public String registerProduct(@ModelAttribute ProductEntity productEntity, @RequestPart("profile") MultipartFile files,
			@RequestParam(value = "mainDisplay", required = false) int[] mainDisplay, 
			int size[], int count[]) {
		productService.register(productEntity, files, mainDisplay, size, count);
		return "redirect:/admin/product/list/1";
	}
	
	@RequestMapping("/admin/product/list/{currentPage}")
	public String productList(@PathVariable(value = "currentPage", required = false) int currentPage, 
			@ModelAttribute SearchDTO searchDto, Model model, String searchOption, 
			@RequestParam(value="search", required=false)String searchWord) {			
		if(currentPage <=0) 
			return "redirect:/admin/product/list/1";
		if(searchOption !=null) {
			if(searchOption.equals("상품명")) {
				searchDto.setProduct_name("%"+searchWord+"%");
			}
			if(searchOption.equals("상품번호")) 
				if(searchWord.equals("")) {
					searchDto.setProduct_idx("%");
				} else {
					searchDto.setProduct_idx(searchWord);
				}
		}		
		int totalRecord = productDao.countProduct(searchDto);
		PageDTO pageDto = pageService.calPage(currentPage, 10, totalRecord, 10);
		if(currentPage > pageDto.getTotalPage() && pageDto.getCountRecord()!=0) 
			return "redirect:/admin/product/list/"+pageDto.getTotalPage();		
		model.addAttribute("pageDto", pageDto);
		List<ProductDTO> productList = productService.selectProduct(currentPage, 10, searchDto);
		model.addAttribute("productList", productList);
		searchDto.setProduct_name(searchWord);
		searchDto.setProduct_idx(searchWord);
		model.addAttribute("searchDto",searchDto);
		model.addAttribute("searchOption",searchOption);
		return "/admin/admin_productlist";
	}
	
	@ResponseBody
	@RequestMapping("/admin/product/delete")
	public void productDelete(@RequestParam("product_idx") String[] product_idx) {
		//for(String a : product_idx) System.out.println(a);
		productDao.deleteProduct(product_idx);
	}
	
	@RequestMapping(value = "/admin/product/modify", method = RequestMethod.GET)
	public String modifyPage(int product_idx, Model model) {
		ProductEntity productEntity = productDao.selectOne(product_idx);
		model.addAttribute("productEntity",productEntity);
		return "admin/admin_productmodify";
	}
	
	@RequestMapping(value = "/admin/product/modify", method = RequestMethod.POST)
	public String modify(@ModelAttribute ProductEntity productEntity, int product_idx,
			@RequestPart(value="profile", required=false) MultipartFile files) {
		productService.modifyProduct(productEntity, files, product_idx);
		return "admin/admin_modifyComplete";
	}
	
}
