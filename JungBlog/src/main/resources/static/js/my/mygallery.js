$(function() {
   let time = $("#time").html();
   moment.locale('en');
   time = moment(time).format('ll');
   $("#time").html(time);
   
   $("#heart2").click(function(){
	   alert("로그인 후 이용가능합니다.")
   })
   $("#heart").click(function(e){
      e.preventDefault();
      
      let heartCk = $("#heartCk").val();
      
      if(heartCk==0){
         axios.post('/heartUpload', {
             'boardRef': $("#boardIdx").val()
           })
           .then(function (res) {
             if(res.data=='1'){
             $("#heartCk").val("1")
             $("#ch").html(Number($("#ch").html())+1)
                  $("#heart").addClass("active")
               alert('조아요 성공')
            }
           })
           .catch(function (error) {
             console.log(error);
           });
      } else{
         axios.post('/heartDelete', {
             'boardRef': $("#boardIdx").val()
           })
           .then(function (res) {
             if(res.data=='1'){
             $("#heartCk").val("0")
             $("#ch").html(Number($("#ch").html())-1)
               $("#heart").removeClass("active")
               alert('조아요 취소성공')
            }
           })
           .catch(function (error) {
             console.log(error);
           });
      }

   })

	$("#showButton").click(function(event) {
    // 기본 동작(페이지 새로고침)을 막습니다.
    event.preventDefault();

    // 게시글의 고유 번호 (idx)를 가져옵니다.
    var boardIdx = document.getElementById("boardIdx").value;
    axios.put(`/show/${boardIdx}`)
    .then(res => {
        let data = res.data;
        console.log(res);
        if(data == 1) {
            alert('게시글이 성공적으로 숨김취소되었습니다.');
            window.location.href = "/member/myblog";                
        } else {
            alert('게시글 숨김취소 중 오류가 발생했습니다. 다시 시도해주세요.');
        }
    })
    .catch(e => {
        console.error('게시글 숨김취소 중 오류가 발생했습니다:', e);
        alert('게시글 숨김취소 중 오류가 발생했습니다. 다시 시도해주세요.');
    });
});


    
    // form 요소가 제출될 때
    document.getElementById("deleteButton").addEventListener("click", function(event) {
        // 기본 동작(페이지 새로고침)을 막습니다.
        event.preventDefault();

        // 게시글의 고유 번호 (idx)를 가져옵니다.
        var boardIdx = document.getElementById("boardIdx").value;
		axios.delete(`/delete/${boardIdx}`)
		.then(res => {
			let data = res.data;
			if(data==1){
				alert('게시글이 성공적으로 삭제되었습니다.');
				window.location.href="/member/myblog";				
			} else {
            	alert('게시글 삭제 중 오류가 발생했습니다. 다시 시도해주세요.');
			}
		})
		.catch(e => {
			console.error('게시글 삭제 중 오류가 발생했습니다:', e);
            alert('게시글 삭제 중 오류가 발생했습니다. 다시 시도해주세요.');
		})
    });

    $("#updateBtn").click(function() {
        // 수정할 게시글의 idx 가져오기
        var boardIdx = $("#boardIdx").val();
        // 수정할 게시글의 제목 가져오기
        var editTitle = $("#editTitle").val();
        // 수정할 게시글의 내용 가져오기
        var editContent = $("#editContent").val();

        // 수정할 게시글 정보를 수정 폼으로 전송
        window.location.href = "/gallery/update/" + boardIdx;
	});
	
	$("#hideButton").click(function(event) {
    // 기본 동작(페이지 새로고침)을 막습니다.
    event.preventDefault();

    // 게시글의 고유 번호 (idx)를 가져옵니다.
    var boardIdx = document.getElementById("boardIdx").value;
    axios.delete(`/blog/${boardIdx}`)
    .then(res => {
        let data = res.data;
        if(data == 1) {
            alert('게시글이 성공적으로 숨김되었습니다.');
            window.location.href = "/member/myblog";                
        } else {
            alert('게시글 숨김 중 오류가 발생했습니다. 다시 시도해주세요.');
        }
    })
    .catch(e => {
        console.error('게시글 숨김 중 오류가 발생했습니다:', e);
        alert('게시글 숨김 중 오류가 발생했습니다. 다시 시도해주세요.');
    });
});

})