package com.poly.admin.model;

public class CategoryPageInfoModel {
	private Long totalElement;
	private Integer totalPage;
	
	public CategoryPageInfoModel() {}

	public Long getTotalElement() {
		return totalElement;
	}

	public void setTotalElement(Long totalElement) {
		this.totalElement = totalElement;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
	
}
