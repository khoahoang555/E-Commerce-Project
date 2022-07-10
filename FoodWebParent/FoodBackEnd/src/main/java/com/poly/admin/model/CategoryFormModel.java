package com.poly.admin.model;

import org.springframework.web.multipart.MultipartFile;

import com.poly.common.entity.Category;

public class CategoryFormModel {
	private Integer id;
	private String name;
	private String alias;
	private Integer enabled;
	private String image;
	private MultipartFile checkImage;

	private Category parent;
	
	public CategoryFormModel() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public MultipartFile getCheckImage() {
		return checkImage;
	}

	public void setCheckImage(MultipartFile checkImage) {
		this.checkImage = checkImage;
	}

	public String getPathImages() {
		if (this.getImage() == null) {
			return "/assets/img/image-thumbnail.png";
		} else {
			return "/category-images/" + this.getId() + "/" + this.getImage();
		}
	}
}
