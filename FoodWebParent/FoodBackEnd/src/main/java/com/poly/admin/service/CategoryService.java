package com.poly.admin.service;

import java.util.List;

import com.poly.admin.model.CategoryFormModel;
import com.poly.admin.model.CategoryPageInfoModel;
import com.poly.common.entity.Category;

public interface CategoryService {

	Category findByName(String name);

	Category findByAlias(String alias);

	Category saveFromCategoryModelToCategory(CategoryFormModel categoryForm, Integer id);

	List<Category> findAll();

	List<Category> listCategoriesUsedInForm();

	Category updateEnabled(Integer id);

	List<Category> listByPage(CategoryPageInfoModel pageInfoModel, int pageNum, String sortField, String sortDir, String keyword);

	void delete(Integer id);

	Category checkCategoryBeforeDelete(Integer id);

	CategoryFormModel loadFromCategoryToCategoryModel(Integer id);

}
