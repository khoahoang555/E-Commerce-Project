package com.poly.admin.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.admin.export.UserCsvExporter;
import com.poly.admin.export.UserExcelExporter;
import com.poly.admin.export.UserPdfExporter;
import com.poly.admin.model.UserFormModel;
import com.poly.admin.service.RoleService;
import com.poly.admin.service.UserService;
import com.poly.admin.utils.FileUploadUtil;
import com.poly.admin.validate.UserFormValidate;
import com.poly.common.Constants;
import com.poly.common.entity.Role;
import com.poly.common.entity.User;

@Controller
@RequestMapping("/admin")
public class UserController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private UserFormValidate userFormValidate;
	
	/**
	 * Ham dung de rang buoc form tuong ung voi man hinh
	 * 
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		Object target = binder.getTarget();
		if (target == null) {
			return;
		}
		if(target.getClass() == UserFormModel.class) {
			// Rang buoc form bat loi voi controller tuong ung
			binder.setValidator(userFormValidate);
		}	
	}
	
	/**
	 * Hien thi man hinh tao moi user
	 * 
	 * @param model
	 * @return duong dan den man hinh thong tin user
	 */
	@GetMapping("/user/form/new")
	public String formNew(Model model) {
		UserFormModel userModel = new UserFormModel();
		
		// Gan cac attribute cua model tuong ung vao cac field cua form 
		model.addAttribute("userForm", userModel);
		model.addAttribute("urlPath", "/admin/user/form/new");
		return "users/form_user";
	}
	
	/**
	 * Dung de handler trang thai user
	 * 
	 * @param id
	 * @param redirectAttributes
	 * @return hien thi lai man hinh danh sach user
	 */
	@GetMapping("/user/list/enabled/{id}")
	public String userEnabled(@PathVariable("id") Integer id,
			RedirectAttributes redirectAttributes) {
		try {
			User user = userService.updateEnabled(id);
			String enabled = user.getEnabled()?"kích hoạt" : "vô hiệu hóa";
			redirectAttributes.addFlashAttribute("content", Constants.TITLE_ALERT_SUCCESS);
			redirectAttributes.addFlashAttribute("status", Constants.STATUS_ALERT_SUCCESS);
			redirectAttributes.addFlashAttribute("message", "User " + user.getId() + " đã được " + enabled + " thành công!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("content", Constants.TITLE_ALERT_WARNING);
			redirectAttributes.addFlashAttribute("status", Constants.STATUS_ALERT_WARNING);
			redirectAttributes.addFlashAttribute("message", "User có ID " + id + " không tồn tại!");
		}
			
		return "redirect:/admin/user/list";
	}
	
	/**
	 * Handler them moi mot user
	 * 
	 * @param model
	 * @param userModel
	 * @param result
	 * @param redirectAttributes
	 * @param id
	 * @param multipartFile
	 * @return man hinh danh sach user
	 * @throws IOException
	 */
	@PostMapping("/user/form/new")
	public String handlerNewUser(Model model, @ModelAttribute("userForm") @Validated UserFormModel userModel, 
			BindingResult result, RedirectAttributes redirectAttributes, Integer id,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		// Neu co loi thi hien thi lai form
		if (result.hasErrors()) {
			return "users/form_user";
		} 
		// Neu khong co loi thi tien hanh luu data vao bang user va hien thi list user
		else {
			
			if (!multipartFile.isEmpty()) {
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				userModel.setPhoto(fileName);
				User user = userService.saveFromUserModelToUser(userModel, id);
				String uploadDir = "user-photos/" + user.getId();
				FileUploadUtil.cleanDir(uploadDir);
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			} else {			
				userService.saveFromUserModelToUser(userModel, id);
			}
			
			if (id == null) {			
				redirectAttributes.addFlashAttribute("message", "Bạn đã thêm mới thành công 1 người dùng!");
			} else {
				redirectAttributes.addFlashAttribute("message", "Bạn đã cập nhật thành công thông tin người dùng có id là " + id + "!");
			}
			
			redirectAttributes.addFlashAttribute("content", Constants.TITLE_ALERT_SUCCESS);
			redirectAttributes.addFlashAttribute("status", Constants.STATUS_ALERT_SUCCESS);
			
			return this.getRedirectURLToAffectedUser(userModel);
		}
	}
	
	/**
	 * Hien thi thong tin user vua duoc luu
	 * 
	 * @param userModel
	 * @return man hinh danh sach user
	 */
	private String getRedirectURLToAffectedUser(UserFormModel userModel) {
		String firstPartOfEmail = userModel.getEmail().split("@")[0];
		return "redirect:/admin/user/list/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
	}
	
	/**
	 * Hien thi thong tin user theo id len man hinh thong tin user
	 * 
	 * @param id
	 * @param model
	 * @param redirectAttributes
	 * @return duong dan den man hinh thong tin user
	 */
	@GetMapping("/user/form/edit/{id}")
	public String editUser(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			UserFormModel userForm = userService.loadFromUserToUserModel(id);
			model.addAttribute("userForm", userForm);
			model.addAttribute("urlPath", "/admin/user/form/edit/" + id);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("content", Constants.TITLE_ALERT_WARNING);
			redirectAttributes.addFlashAttribute("status", Constants.STATUS_ALERT_WARNING);
			redirectAttributes.addFlashAttribute("message", "User có ID " + id + " không tồn tại!");
			return "redirect:/admin/user/list";
		}	
		
		return "users/form_user";
	}
	
	/**
	 * Xu ly cap nhat thong tin user
	 * 
	 * @param model
	 * @param userModel
	 * @param result
	 * @param redirectAttributes
	 * @param id
	 * @param multipartFile
	 * @return man hinh danh sach user
	 * @throws IOException
	 */
	@PostMapping("/user/form/edit/{id}")
	public String handlerEditUser(Model model, @ModelAttribute("userForm") @Validated UserFormModel userModel, 
			BindingResult result, RedirectAttributes redirectAttributes,
			@PathVariable("id") Integer id,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		return this.handlerNewUser(model, userModel, result, redirectAttributes, id, multipartFile);
	}
	
	/**
	 * Hien thi man hinh danh sach user
	 * 
	 * @param model
	 * @return duong dan den man hinh danh sach user
	 */
	@GetMapping("/user/list")
	public String userList(Model model) {		
		return this.listByPage(1, "id", "asc", null, model);
	}
	
	/**
	 * Hien thi danh sach user theo so page
	 * 
	 * @param pageNum
	 * @param sortField
	 * @param sortDir
	 * @param keyword
	 * @param model
	 * @return duong dan den man hinh danh sach user
	 */
	@GetMapping("/user/list/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum, 
			@Param("sortField") String sortField, @Param("sortDir") String sortDir,
			@Param("keyword") String keyword, Model model) {
		Page<User> pageUser = userService.listByPage(pageNum, sortField, sortDir, keyword);
		List<User> listUsers = pageUser.getContent();
		
		int startCount = 1 + (pageNum - 1) * Constants.NUMBER_USER_PER_PAGE;
		long endCount = startCount + (Constants.NUMBER_USER_PER_PAGE - 1);
		if (endCount > pageUser.getTotalElements()) {
			endCount = pageUser.getTotalElements();
		}
		
		String reserveSort = sortDir.equals("asc")?"desc":"asc";
		
		model.addAttribute("totalElement", pageUser.getTotalElements());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPage", pageUser.getTotalPages());
		model.addAttribute("reserveSort", reserveSort);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		
		return "users/list_user";
	}
	
	/**
	 * Ham de xoa 1 user
	 * 
	 * @param id
	 * @param redirectAttributes
	 * @return hien thi lai man hinh danh sach user
	 */
	@GetMapping("/user/list/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			userService.delete(id);			
			
			String userDir = "user-photos/" + id;
			FileUploadUtil.removeDir(userDir);
			
			redirectAttributes.addFlashAttribute("message", "Bạn đã xóa thành công người dùng có ID là " + id +"!");
			redirectAttributes.addFlashAttribute("content", Constants.TITLE_ALERT_SUCCESS);
			redirectAttributes.addFlashAttribute("status", Constants.STATUS_ALERT_SUCCESS);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("content", Constants.TITLE_ALERT_WARNING);
			redirectAttributes.addFlashAttribute("status", Constants.STATUS_ALERT_WARNING);
			redirectAttributes.addFlashAttribute("message", "User có ID " + id + " không tồn tại!");
		}
	
		return "redirect:/admin/user/list";
	}
	
	/**
	 * Xuat file csv
	 * 
	 * @param resp
	 * @throws IOException
	 */
	@GetMapping("/user/list/export/csv")
	public void exportToCSV(HttpServletResponse resp) throws IOException {
		List<User> listUsers = userService.findAll();
		UserCsvExporter exporter = new UserCsvExporter();
		
		exporter.export(listUsers, resp);
	}
	
	/**
	 * Xuat file pdf
	 *  
	 * @param resp
	 * @throws IOException
	 */
	@GetMapping("/user/list/export/pdf")
	public void exportToPdf(HttpServletResponse resp) throws IOException {
		List<User> listUsers = userService.findAll();
		UserPdfExporter exporter = new UserPdfExporter();
		
		exporter.export(listUsers, resp);
	}
	
	/**
	 * Xuat file excel
	 * 
	 * @param resp
	 * @throws IOException
	 */
	@GetMapping("/user/list/export/excel")
	public void exportToExcel(HttpServletResponse resp) throws IOException {
		List<User> listUsers = userService.findAll();
		UserExcelExporter exporter = new UserExcelExporter();
		
		exporter.export(listUsers, resp);
	}
	
	/**
	 * Hien thi thong tin tat ca cac quyen trong dtb
	 * 
	 * @param mode
	 * @return danh sach thong tin role
	 */
	@ModelAttribute("listRoles")
	public List<Role> listRoles(Model mode) {
		List<Role> listRole = roleService.findAll();
		return listRole;
	}
}
