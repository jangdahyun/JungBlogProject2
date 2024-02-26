$(function() {
	
	
	$("#selectBox").change(function(){
		alert(1);
	})
	
	$("#writeBtn").submit(function(){
		return true;
	})
	
})
function view(idx) {
	location.href = "/blog/" + idx
}

//back 눌렀을때 리로드하는 함수
window.onpageshow = function(event) {
	if (event.persisted) {
		document.location.reload();
	}
};