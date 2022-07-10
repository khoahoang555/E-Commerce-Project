package com.poly.admin.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.poly.admin.model.CategoryFormModel;
import com.poly.admin.model.CategoryPageInfoModel;
import com.poly.admin.repository.CategoryRepository;
import com.poly.admin.service.CategoryService;
import com.poly.common.Constants;
import com.poly.common.entity.Category;

@Service
public class CategoryServiceImpl implements CategoryService {
	@Autowired
	CategoryRepository categoryRepo;
	
	@Override
	public Category findByName(String name) {
		return categoryRepo.findByName(name);
	}

	@Override
	public Category findByAlias(String alias) {
		return categoryRepo.findByAlias(alias);
	}

	@Override
	public Category saveFromCategoryModelToCategory(CategoryFormModel categoryForm, Integer id) {
		Category category = null;
		
		if (id == null) {
			category = new Category();
			category.setImage(categoryForm.getImage());
		} else {
			category = categoryRepo.findById(id).get();
			if (categoryForm.getImage() != null) {
				category.setImage(categoryForm.getImage());
			}
		}
		
		Category parent = categoryForm.getParent();
		boolean enabled = categoryForm.getEnabled()==1?true:false;	
		category.setName(categoryForm.getName());
		category.setAlias(categoryForm.getAlias());
		category.setEnabled(enabled);
		
		category.setParent(parent);
		
		return categoryRepo.save(category);
	}

	@Override
	public List<Category> findAll() {
		return (List<Category>) categoryRepo.findAll();
	}

	@Override
	public List<Category> listCategoriesUsedInForm() {
		Sort sort = Sort.by("name").ascending();
		List<Category> categoriesUsedInForm = new ArrayList<Category>();
		List<Category> rootCategory = categoryRepo.findRootCategories(sort);
		
		for (Category category: rootCategory) {
			categoriesUsedInForm.add(Category.copyIdAndName(category.getId(), category.getName()));
			
			Set<Category> children = sortSubCategories(category.getChildren());
			for (Category subCategory: children) {
				
				String name = "--" + subCategory.getName();
				categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
				listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
			}
		}
		
		return categoriesUsedInForm;
	}
	
	public void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category subCategory, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> listSubCategory = sortSubCategories(subCategory.getChildren());
		for (Category category: listSubCategory) {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name = name + "--";
			}
			name = name + category.getName();
			categoriesUsedInForm.add(Category.copyIdAndName(category.getId(), name));
			listSubCategoriesUsedInForm(categoriesUsedInForm, category, newSubLevel);
		}	
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> children) {
		return sortSubCategories(children, "asc", "name");
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir, String sortField) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

			@Override
			public int compare(Category cate1, Category cate2) {
				int result = 0;
				switch(sortField) {
					case "id":
						if (sortDir.equals("asc")) {
							result = cate1.getId().compareTo(cate2.getId());
						} else {
							result = cate2.getId().compareTo(cate1.getId());
						}
						break;
					case "alias":
						if (sortDir.equals("asc")) {
							result = cate1.getAlias().compareTo(cate2.getAlias());
						} else {
							result = cate2.getAlias().compareTo(cate1.getAlias());
						}
						break;
					default:
						if (sortDir.equals("asc")) {
							result = cate1.getName().compareTo(cate2.getName());
						} else {
							result = cate2.getName().compareTo(cate1.getName());
						}
						break;
				}
								
				return result;
							
			}
		});
		
		sortedChildren.addAll(children);
		return sortedChildren;
	}

	@Override
	public Category updateEnabled(Integer id) {
		Category category = categoryRepo.findById(id).get();
		category.setEnabled(!category.isEnabled());
		return categoryRepo.save(category);
	}

	@Override
	public List<Category> listByPage(CategoryPageInfoModel pageInfoModel ,int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc")?sort.ascending():sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, Constants.NUMBER_CATEGORY_PER_PAGE, sort);
		
		Page<Category> pageCategory = null;
		
		if (keyword == null || keyword.isEmpty()) {
			pageCategory = categoryRepo.findRootCategories(pageable);
		} else {
			pageCategory = categoryRepo.search(keyword, pageable);
		}
		
		pageInfoModel.setTotalElement(pageCategory.getTotalElements());
		pageInfoModel.setTotalPage(pageCategory.getTotalPages());
		
		List<Category> listCategory = listHierarchicalCategories(pageCategory.getContent(), sortDir, sortField);
		
		
		return listCategory;
	}
	
	private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir, String sortField) {
		List<Category> listCategoriesDisplayed = new ArrayList<Category>();
		
		for (Category category: rootCategories) {
			listCategoriesDisplayed.add(category);
			
			Set<Category> children = sortSubCategories(category.getChildren(), sortDir, sortField);
			for (Category subCategory: children) {
				
				String name = "--" + subCategory.getName();
				subCategory.setName(name);
				listCategoriesDisplayed.add(subCategory);
				listSubHierarchicalCategories(listCategoriesDisplayed, subCategory, 1, sortDir, sortField);
			}
		}
		
		return listCategoriesDisplayed;
	}
	
	public void listSubHierarchicalCategories(List<Category> listCategoriesDisplayed, Category subCategory, int subLevel, String sortDir, String sortField) {
		int newSubLevel = subLevel + 1;
		Set<Category> listSubCategory = sortSubCategories(subCategory.getChildren(), sortDir, sortField);
		for (Category category: listSubCategory) {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name = name + "--";
			}
			name = name + category.getName();
			category.setName(name);
			listCategoriesDisplayed.add(category);
			listSubHierarchicalCategories(listCategoriesDisplayed, category, newSubLevel, sortDir, sortField);
		}
	}

	@Override
	public void delete(Integer id) {
		Category category = categoryRepo.findById(id).get();
		categoryRepo.delete(category);
	}

	@Override
	public Category checkCategoryBeforeDelete(Integer id) {
		Category category = categoryRepo.findById(id).get();
		if (!category.hasChildren()) {
			return category;
		}
		return null;
	}

	@Override
	public CategoryFormModel loadFromCategoryToCategoryModel(Integer id) {
		Category category = categoryRepo.findById(id).get();
		CategoryFormModel categoryForm = new CategoryFormModel();
		
		if (category != null) {
			categoryForm.setName(category.getName());
			categoryForm.setAlias(category.getAlias());
			categoryForm.setEnabled(category.isEnabled()?1:0);
			categoryForm.setId(category.getId());
			categoryForm.setImage(category.getImage());
			categoryForm.setParent(category.getParent());
		}
		
		return categoryForm;
	}
}
