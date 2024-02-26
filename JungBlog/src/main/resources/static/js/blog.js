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
})