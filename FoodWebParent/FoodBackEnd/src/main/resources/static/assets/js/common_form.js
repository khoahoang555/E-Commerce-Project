$(document).ready(function() {
    $("#fileImage").change(function() {
        let fileSize = this.files[0].size;
        if (fileSize > 102400) {
            this.setCustomValidity("Bạn phải chọn file có kích thước nhỏ hơn 100KB");
            this.reportValidity();
        } else {
            this.setCustomValidity("");
            showInforImageThumbnail(this);
        }
    });
    
    $("#btnRefresh").click(function() {
		window.location = pathURL;
	});
});

function showInforImageThumbnail(fileInput) {
    let file = fileInput.files[0];
    $("#nameImage").text(file.name);
    let reader = new FileReader();
    reader.onload = function(e) {  
        $("#inforImage").attr("src", e.target.result);
    }

    reader.readAsDataURL(file);
}
