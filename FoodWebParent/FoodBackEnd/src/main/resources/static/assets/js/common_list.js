function showDeleteModal(link, nameForm) {
	let entityID = link.attr("entityId");
	$("#btnYes").attr("href", link.attr("href"));
	$("#msgDeleteContent").text("Bạn có chắc muốn xóa " + nameForm + " có ID là " + entityID + " không?");
	$("#deleteModal").modal();
}