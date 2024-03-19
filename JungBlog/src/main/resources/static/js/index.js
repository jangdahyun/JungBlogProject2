$(function() {
	
	
	$("#selectBox").change(function(){
		alert(1);
	})
	
	$("#writeBtn").submit(function(){
		return true; 
	})
	
})
function view(idx) {
	location.href = "/blog/" + idx;
}
function viewblog(idx) {
	location.href = "/blog/" + idx;
}
function viewgallery(idx) {
	location.href = "/gallery/" + idx;
}
function viewfile(idx) {
   location.href = "/fileboard/blog/" + idx;
}


//back 눌렀을때 리로드하는 함수
window.onpageshow = function(event) {
	if (event.persisted) {
		document.location.reload();
	}
};
function submitForm() {
	document.getElementById("write").submit();
}
