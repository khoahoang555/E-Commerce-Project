package com.poly.admin.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.poly.admin.model.CategoryFormModel;
import com.poly.admin.service.CategoryService;
import com.poly.common.entity.Category;

@Component
public class CategoryFormValidate implements Validator{
	@Autowired
	private CategoryService categoryService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz == CategoryFormModel.class;
	}

	@Override
	public void validate(Object target, Errors errors) {
		CategoryFormModel categoryFormModel = (CategoryFormModel) target;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotBlank.categoryForm.name");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "alias", "NotBlank.categoryForm.alias");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "enabled", "NotBlank.categoryForm.enabled");
		
		if (categoryFormModel.getId() == null) {
			if (categoryFormModel.getCheckImage().isEmpty()) {
				errors.rejectValue("checkImage", "NotBlank.categoryForm.image");
			}
		}
		
		if (!errors.hasFieldErrors("name")) {			
			if (categoryFormModel.getName().length() > 128) {
				errors.rejectValue("name", "Max.categoryForm.name");
			} else {
				if (categoryFormModel.getId() == null) {
					Category category = categoryService.findByName(categoryFormModel.getName());
					if (category != null) {
						errors.rejectValue("name", "Duplicate.categoryForm.name");
					}
				} 
			}
		}
		
		if (!errors.hasFieldErrors("alias")) {		
			if (categoryFormModel.getName().length() > 64) {
				errors.rejectValue("alias", "Max.categoryForm.alias");
			} else {
				if (categoryFormModel.getId() == null) {
					Category category = categoryService.findByAlias(categoryFormModel.getAlias());
					if (category != null) {
						errors.rejectValue("alias", "Duplicate.categoryForm.alias");
					}
				}		
			}		
		}	
	}

}
