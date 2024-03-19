$(function() {
   
   $("#selectBox").change(function(){
      alert(1);
   })
   
   $("#writeBtn").submit(function(){
      return true;
   }) 
   
})
function viewfile(idx) {
   location.href = "/fileboard/blog/" + idx;
}

//back 눌렀을때 리로드하는 함수
window.onpageshow = function(event) {
   if (event.persisted) {
      document.location.reload();
   }
};
let totalCount = 0;
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
            </li>
         `
      })
      $("#commentList").html(content);
   })
   .catch(function (error) {
      console.log(error);
   });
}

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

$(function() {
   let time = $("#time").html();
   moment.locale('en');
   time = moment(time).format('ll');
   $("#time").html(time);
   
   init();
   
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
   
   
   // form 요소가 제출될 때
    document.getElementById("deleteButton").addEventListener("click", function(event) {
        // 기본 동작(페이지 새로고침)을 막습니다.
        event.preventDefault();

        // 게시글의 고유 번호 (idx)를 가져옵니다.
        var boardIdx = document.getElementById("boardIdx").value;
      axios.delete(`/blog/${boardIdx}`)
      .then(res => {
         let data = res.data;
         if(data==1){
            alert('게시글이 성공적으로 삭제되었습니다.');
            window.location.href="/";            
         } else {
               alert('게시글 삭제 중 오류가 발생했습니다. 다시 시도해주세요.');
         }
      })
      .catch(e => {
         console.error('게시글 삭제 중 오류가 발생했습니다:', e);
            alert('게시글 삭제 중 오류가 발생했습니다. 다시 시도해주세요.');
      })
    });
    
   // 수정 버튼 클릭 이벤트 리스너 등록
$(document).ready(function() {
    // 게시글 수정 버튼 클릭 시
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
});

})