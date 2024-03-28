$(function() {
	let currentUser = $("#currentUser").val()
	console.log(currentUser);
	init();
   
	// 댓글 읽기 시작
	let totalCount = 0;
	/** 초기화 함수 */
	function init(){
		axios.post('/commentsTotalCount', {
			'boardIdx': $("#boardIdx").val(),
		})
		.then(function (res) {
			totalCount = res.data;
			updatePage(totalCount, 1);
			updateTable(1);
		})
		.catch(function (e){
			console.log(e);
		});
	}
	/** 댓글을 채우는 함수 */
	function updateTable(currentPage){
		$("#commentList").empty();
		axios.post('/comments', {
			'boardIdx': $("#boardIdx").val(),
			'currentPage': currentPage,
		})
		.then(function (res) {
			const data = res.data;
			console.log(data);
			content=``;
			data.forEach(comment=>{
				const date = new Date(comment.regDate);
				const formattedDate = new Intl.DateTimeFormat('ko-KR', {
				    year: 'numeric',
				    month: '2-digit',
				    day: '2-digit'
				}).format(date);
				content += `
					<li>
						<span class="nickName">${comment.member.nickName}</span><span class="material-symbols-outlined" style="font-size: 15px; margin-right: 5px;">schedule</span><span class="regDate">${formattedDate}</span>
						<p class="reply">${comment.reply}</p>
						`
				if(currentUser == comment.userRef){
					content += `
						<div style="text-align: right">	
							<span class="replyBtn"style="cursor:pointer;" onclick="commentUpdateFn(this, ${comment.idx}, '${comment.reply}')">수정</span> <span class="commentDeleteBtn" style="cursor:pointer;" onclick="commentDeleteFn(${comment.idx})">삭제</span>					
						</div>
					`
				}
				content += `</li>`
			})
			$("#commentList").html(content);
		})
		.catch(function (error) {
			console.log(error);
		});
	}
	
	/** 페이지버튼을 바꿔주는 함수 */
	function updatePage(totalCount, currentPage) {
		const sizeOfPage = 5;
		const sizeOfBlock = 5;
		page = ``;
		if(totalCount>0){
			let totalPage = Math.floor((totalCount - 1) / sizeOfPage) + 1;
			if (currentPage > totalPage) currentPage = 1;
			let startPage = Math.floor((currentPage - 1) / sizeOfBlock) * sizeOfBlock + 1;
			let endPage = startPage + sizeOfBlock - 1;
			if (endPage > totalPage) endPage = totalPage;
			
			page = `<ul class='uk-pagination' uk-margin>`
			if(startPage>1){
				page += `<li><span uk-pagination-previous onclick='updatePage(${totalCount},${startPage - 1})'></span></li>`
			}
			for(let i = startPage; i<= endPage; i++){
				if(i==currentPage){
					page += `<li><span class="active">${i}</span></li>`
				} else {
					page += `<li><span onclick='updatePage(${totalCount},${i})'>${i}</span></li>`
				}
			}
			if(endPage < totalPage) {
				page += `<li><span uk-pagination-next onclick='updatePage(${totalCount},${endPage+1})'></span></li>`
			}
		}
		$("#page").html(page);
		updateTable(currentPage);
	}
	// 댓글 읽기 종료
	
	$("#commentSubmit").submit(function(){
		value=$("#comment").val();
			console.log(value);
		if(value.trim().length==0){
			alert("댓글을 입력해주세요");
			return false;
		  }
	})
})

function commentDeleteFn(commentIdx) {
	console.log(commentIdx);
	
    axios.delete(`/comment/${commentIdx}`)
    .then(res => {
        let data = res.data;
        console.log(res);
        if(data == 1) {
            alert('댓글이 성공적으로 삭제되었습니다.');
            window.location.reload();               
        } else {
            alert('게시글 숨김취소 중 오류가 발생했습니다. 다시 시도해주세요.');
        }
    })
    .catch(e => {
        console.error('게시글 숨김취소 중 오류가 발생했습니다:', e);
        alert('게시글 숨김취소 중 오류가 발생했습니다. 다시 시도해주세요.');
    }); 
	
}

function commentUpdateFn(obj, commentIdx, commentreply) {
	if (obj.classList.contains('active')) {
        return;
    }
    $(".subCommentForm").remove();
	$(".replyBtn").removeClass('active');
    let div = obj.parentNode;
    while (div && div.tagName !== 'DIV') { // 부모노드찾기
    	div = div.parentNode;
    }
    content = `
    	<form action="/commentUpdate" method="post" class="subCommentForm" onsubmit = "subCommentForm(this, event)">
			<input type="hidden" class="commentIdx" name="idx" value="${commentIdx}">
			<div style="display: flex; justify-content: end; align-item:center">
				<span style="margin-right:5px;">답글</span>
				<textarea class="subreply" name="reply" maxlength="30" class="uk-input" style="background: #252422; color: #fff; width: 400px;height:36px; border: none; border-bottom: 2px solid black;" placeholder="답글 추가...">${commentreply}</textarea>				
			</div>
			<div style="text-align: right;" id="commentBtn">
				<input type="button" class="subCancleBtn" value="취소" onclick="removeSubCommentForm(this)">
				<input type="submit" value="댓글">
			</div>				
		</form>
    `
    obj.classList.add('active');
	div.innerHTML += content;
}



function subCommentForm(obj ,event){
	event.preventDefault(); // 폼의 기본 동작(전송)을 취소합니다.
	let formData = new FormData();
	console.log( $(obj).find(".commentIdx").val() );
	console.log( $(obj).find(".subreply").val() );
	const reply = $(obj).find(".subreply").val();
	if(reply.trim().length == 0){
		alert('댓글내용은 필수입니다.')
		return false;
	}
	formData.append('idx', $(obj).find(".commentIdx").val());
    formData.append('reply', $(obj).find(".subreply").val().trim());
	axios.post('/commentUpdate', formData)
    .then(res => {
		if(res.data == 1){
			alert("댓글이 수정되었습니다.");
			window.location.reload();
		} else {
			alert('댓글수정 중 오류가 발생했습니다. 다시 시도해주세요.');
		}
    })
    .catch(err => {
        console.error(err);
    });
	return false;
}
function mainCommentForm(obj ,event){
	event.preventDefault(); // 폼의 기본 동작(전송)을 취소합니다.
	let formData = new FormData();
	console.log( $(obj).find(".commentIdx").val() );
	const reply = $(obj).find("#reply").val();
	if(reply.trim().length == 0){
		alert('댓글내용은 필수입니다.')
		return false;
	}
	formData.append('boardRef', $("#boardIdx").val());
    formData.append('reply', $(obj).find("#reply").val());
	axios.post('/commentupload', formData)
    .then(res => {
		if(res.data == 1){
			alert("댓글이 저장되었습니다.");
			window.location.reload();
		} else {
			alert('댓글저장 중 오류가 발생했습니다. 다시 시도해주세요.');
		}
    })
    .catch(err => {
        console.error(err);
    });
	return false;
}
