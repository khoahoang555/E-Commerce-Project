package com.poly.admin.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.admin.export.CategoryCsvExporter;
import com.poly.admin.model.CategoryFormModel;
import com.poly.admin.model.CategoryPageInfoModel;
import com.poly.admin.service.CategoryService;
import com.poly.admin.utils.FileUploadUtil;
import com.poly.admin.validate.CategoryFormValidate;
import com.poly.common.Constants;
import com.poly.common.entity.Category;

@Controller
@RequestMapping("/admin")
public class CategoryController {
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private CategoryFormValidate categoryFormValidate;
	
	@InitBinder
	private void initBinder(WebDataBinder binder) {
		Object target = binder.getTarget();
		if (target == null) {
			return;
		} 
		if (target.getClass() == CategoryFormModel.class) {
			binder.setValidator(categoryFormValidate);
		}
	}
	
	@GetMapping("/category/form/new")
	public String viewPageCategoryForm(Model model) {
		CategoryFormModel cateFormModel = new CategoryFormModel();
		model.addAttribute("categoryForm", cateFormModel);
		model.addAttribute("urlPath", "/admin/category/form/new");
		return "categories/form_category";
	}
	
	@PostMapping("/category/form/new")
	public String handlerNewCategoryForm(Model model, @ModelAttribute("categoryForm") @Validated CategoryFormModel categoryForm,
			BindingResult result, RedirectAttributes redirectAttributes, Integer id) throws IOException {
		
		if (result.hasErrors()) {
			return "categories/form_category";
		} else {
			if (!categoryForm.getCheckImage().isEmpty()) {
				String fileName = StringUtils.cleanPath(categoryForm.getCheckImage().getOriginalFilename());
				categoryForm.setImage(fileName);
				Category category = categoryService.saveFromCategoryModelToCategory(categoryForm, id);
				String uploadDir = "../category-images/" + category.getId();
				FileUploadUtil.cleanDir(uploadDir);
				FileUploadUtil.saveFile(uploadDir, fileName, categoryForm.getCheckImage());
			} else {
				categoryService.saveFromCategoryModelToCategory(categoryForm, id);
			}	
		}
		
		if (id == null) {			
			redirectAttributes.addFlashAttribute("message", "Bạn đã thêm mới thành công 1 danh mục!");
		} else {
			redirectAttributes.addFlashAttribute("message", "Bạn đã cập nhật thành công thông tin danh mục có id là " + id + "!");
		}
		
		redirectAttributes.addFlashAttribute("content", Constants.TITLE_ALERT_SUCCESS);
		redirectAttributes.addFlashAttribute("status", Constants.STATUS_ALERT_SUCCESS);
		
		return this.getRedirectURLToAffectedCategory(categoryForm);
	}
	
	private String getRedirectURLToAffectedCategory(CategoryFormModel categoryFormModel) {
		return "redirect:/admin/category/list/page/1?sortField=id&sortDir=asc&keyword=" + categoryFormModel.getName();
	}
	
	@GetMapping("/category/form/edit/{id}")
	public String editCategory(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			CategoryFormModel categoryForm = categoryService.loadFromCategoryToCategoryModel(id);
			model.addAttribute("categoryForm", categoryForm);
			model.addAttribute("urlPath", "/admin/category/form/edit/" + id);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("content", Constants.TITLE_ALERT_WARNING);
			redirectAttributes.addFlashAttribute("status", Constants.STATUS_ALERT_WARNING);
			redirectAttributes.addFlashAttribute("message", "Category có ID " + id + " không tồn tại!");
			return "redirect:/admin/category/list";
		}	
		
		return "categories/form_category";
	}
	
	@PostMapping("/category/form/edit/{id}")
	public String handlerEditUser(@PathVariable("id") Integer id, Model model,
			@ModelAttribute("categoryForm") @Validated CategoryFormModel cateFormModel,
			BindingResult result, RedirectAttributes redirectAttributes) throws IOException{
		return handlerNewCategoryForm(model, cateFormModel, result, redirectAttributes, id);
	}
	
	@GetMapping("/category/list")
	public String categoryList(Model model) {
		return listByPage(1, "id", "asc", null, model);
	}
	
	@GetMapping("/category/list/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum, 
			@Param("sortField") String sortField, @Param("sortDir") String sortDir,
			@Param("keyword") String keyword, Model model) {
		
		CategoryPageInfoModel pageInfoModel = new CategoryPageInfoModel();
		List<Category> listCategories = categoryService.listByPage(pageInfoModel, pageNum, sortField, sortDir, keyword);
		
		int startCount = 1 + (pageNum - 1) * Constants.NUMBER_CATEGORY_PER_PAGE;
		long endCount = startCount + (Constants.NUMBER_CATEGORY_PER_PAGE - 1);
		if (endCount > pageInfoModel.getTotalElement()) {
			endCount = pageInfoModel.getTotalElement();
		}
		
		String reserveSort = sortDir.equals("asc")?"desc":"asc";
		
		model.addAttribute("totalElement", pageInfoModel.getTotalElement());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPage", pageInfoModel.getTotalPage());
		model.addAttribute("reserveSort", reserveSort);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		
		return "categories/list_category";
	}
	
	
	@GetMapping("/category/list/enabled/{id}")
	public String categoryEnabled(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Category category = categoryService.updateEnabled(id);
			String enabled = category.isEnabled()?"kích hoạt" : "vô hiệu hóa";
			redirectAttributes.addFlashAttribute("content", Constants.TITLE_ALERT_SUCCESS);
			redirectAttributes.addFlashAttribute("status", Constants.STATUS_ALERT_SUCCESS);
			redirectAttributes.addFlashAttribute("message", "Category " + category.getId() + " đã được " + enabled + " thành công!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("content", Constants.TITLE_ALERT_WARNING);
			redirectAttributes.addFlashAttribute("status", Constants.STATUS_ALERT_WARNING);
			redirectAttributes.addFlashAttribute("message", "Category có ID " + id + " không tồn tại!");
		}
			
		return "redirect:/admin/category/list";
	}
	
	@GetMapping("/category/list/delete/{id}")
	public String deleteCategory(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Category category = categoryService.checkCategoryBeforeDelete(id);
			if (category == null) {
				redirectAttributes.addFlashAttribute("content", Constants.TITLE_ALERT_WARNING);
				redirectAttributes.addFlashAttribute("status", Constants.STATUS_ALERT_WARNING);
				redirectAttributes.addFlashAttribute("message", "Category có ID " + id + " không được phép xóa!");
			} else {
				categoryService.delete(id);
				String categoryDir = "../category-images/" + id;
				FileUploadUtil.removeDir(categoryDir);
				
				redirectAttributes.addFlashAttribute("message", "Bạn đã xóa thành công danh mục có ID là " + id +"!");
				redirectAttributes.addFlashAttribute("content", Constants.TITLE_ALERT_SUCCESS);
				redirectAttributes.addFlashAttribute("status", Constants.STATUS_ALERT_SUCCESS);
			}
	
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("content", Constants.TITLE_ALERT_WARNING);
			redirectAttributes.addFlashAttribute("status", Constants.STATUS_ALERT_WARNING);
			redirectAttributes.addFlashAttribute("message", "Category có ID " + id + " không tồn tại!");
		}
		
		
		return "redirect:/admin/category/list";
	}
	
	@GetMapping("/category/list/export/csv")
	public void exportToCsv(HttpServletResponse resp) throws IOException {
		List<Category> listCategories = categoryService.findAll();
		CategoryCsvExporter exporter = new CategoryCsvExporter();
		exporter.export(listCategories, resp);
	}
	
	@ModelAttribute("listCategoriesForm")
	public List<Category> listCategoriesForm() {
		List<Category> list = categoryService.listCategoriesUsedInForm();
		return list;
	}
}
