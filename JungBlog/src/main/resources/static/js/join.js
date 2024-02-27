$(function() {
	$("#username").keyup(function() {
		let username = $("#username").val();
		if (username.length >= 4) {
			if (username.indexOf(" ") != -1) {
				$("#message").html("공백은 포함할수 없어요").css('color', 'red');
			} else {
				// Ajax를 호출하여 처리 한다.
				axios.post('/member/userIdCheck', {
					"username": username
				})
					.then(function(response) {
						// 성공 핸들링
						// alert(response.data);
						if (response.data * 1 == 0) {
							$("#message").html("사용가능한 아이디입니다.").css('color', 'blue');
						} else {
							$("#message").html("사용 불가능한 아이디입니다.").css('color', 'red');
						}
					})
					.catch(function(error) {
						// 에러 핸들링
						console.log(error);
					})
					.finally(function() {
						// 항상 실행되는 영역
					});
			}
		} else {
			$("#message").html("").css('color', 'black');
		}
	});

	let checkVal = "";
	$("#sendEmail").click(function() {
		let username = $("#username").val();
		if (username.trim().length == 0) {
			alert("아이디를 입력해주세요")
			$("#username").val("");
			$("#username").focus();
			return;
		}
		if (username.indexOf(" ") != -1) {
			alert("공백은 포함할 수 없어요");
			$("#username").val("");
			$("#username").focus();
			return;
		}
		let email = $("#email").val();
		alert(email)
		if (email == 0) {
			alert("이메일을 선택해주세요");
			$("#message").focus();
			return;
		}
		username = username + '@' + email;
		// Ajax를 호출하여 처리 한다.
		axios.get('/member/send?to=' + username)
			.then(function(response) {
				if (response.data != "") {
					alert("메일 발송 성공")
					$("#emailCheckBox").css('display', 'flex')
					checkVal = response.data;
				} else {
					alert("메일 발송 실패")
				}
			})
			.catch(function(error) {
				console.log(error);
			})
			.finally(function() {
				// 항상 실행되는 영역
			});
	})

	$("#checkMail").click(function() {
		let check = $("#check").val();
		if (check == checkVal) {
			alert("인증 완료");
			$("#dtAddress").attr("disabled",false);
			$("#password").attr("disabled",false);
			$("#confirmPassword").attr("disabled",false);
			$("#name").attr("disabled",false);
			$("#phone").attr("disabled",false);
			$("#nickName").attr("disabled",false);
			$("#bd").attr("disabled",false);
			let gender = document.getElementsByName("gender");
			gender.forEach(e => {
				e.disabled = false;
			})
			$("#dtAddress").attr("disabled",false);
			$("#dtAddress").attr("disabled",false);
		} else {
			alert("인증 실패");
		}
	})

})

// 비밀번호 일치 여부 체크 폼
function checkPasswordMatch() {
	var password = document.getElementById("password").value;
	var confirmPassword = document.getElementById("confirmPassword").value;
	var passwordMatchMessage = document.getElementById("passwordMatchMessage");
	var passwordLengthMessage = document.getElementById("passwordLengthMessage");
	var passwordCharacterMessage = document.getElementById("passwordCharacterMessage");

	// 비밀번호에 공백 포함되어 있는지 체크
	if (password.includes(" ")) {
		passwordCharacterMessage.innerHTML = "비밀번호에는 공백을 포함할 수 없습니다.";
		passwordCharacterMessage.style.color = "red";
		passwordLengthMessage.innerHTML = ""; // 비밀번호 길이 메시지 지우기
		passwordMatchMessage.innerHTML = ""; // 비밀번호 일치 메시지 지우기
		return false;
	} else {
		passwordCharacterMessage.innerHTML = "";
	}

	// 비밀번호 길이 체크
	if (password.length < 8 || password.length > 16) {
		passwordLengthMessage.innerHTML = "비밀번호는 8자리에서 16자리 사이여야 합니다.";
		passwordLengthMessage.style.color = "red";
		passwordCharacterMessage.innerHTML = ""; // 비밀번호에 공백 포함 메시지 지우기
		passwordMatchMessage.innerHTML = ""; // 비밀번호 일치 메시지 지우기
		return false;
	} else {
		passwordLengthMessage.innerHTML = "";
	}

	// 비밀번호 일치 여부 체크
	if (password != confirmPassword) {
		passwordMatchMessage.innerHTML = "비밀번호가 일치하지 않습니다.";
		passwordMatchMessage.style.color = "red";
		return false;
	} else {
		passwordMatchMessage.innerHTML = ""; // 비밀번호가 일치할 때 메시지를 지웁니다.
	}

	return true; // 모든 조건이 충족되면 true 반환
}

// 회원가입 로직
function submitForm() {
	let username = $("#username").val();
	if (username.trim().length === 0) {
		alert("아이디를 입력해주세요.");
		return;
	}

	let email = $("#email").val();
	if (email === "0") {
		alert("이메일을 선택해주세요.");
		return;
	}

	let emailCheckBox = $("#emailCheckBox");
	if (emailCheckBox.css('display') === "none") {
		alert("이메일을 인증해주세요.");
		return;
	}

	let emailVerificationCode = $("#check").val();
	if (emailVerificationCode.trim().length === 0) {
		alert("이메일 인증번호를 입력해주세요.");
		return;
	}

	let password = $("#password").val();
	if (password.trim().length === 0) {
		alert("비밀번호를 입력해주세요.");
		return;
	}

	let confirmPassword = $("#confirmPassword").val();
	if (confirmPassword.trim().length === 0) {
		alert("비밀번호를 다시 한 번 입력해주세요.");
		return;
	}

	if (password.includes(" ")) {
		alert("비밀번호에는 공백을 포함할 수 없습니다.");
		return;
	}

	if (password.length < 8 || password.length > 16) {
		alert("비밀번호는 8자리에서 16자리 사이여야 합니다.");
		return;
	}

	if (password !== confirmPassword) {
		alert("비밀번호가 일치하지 않습니다.");
		return;
	}

	if (!checkPasswordMatch()) {
		return;
	}

	let name = $("input[name='name']").val();
	if (name.trim().length === 0) {
		alert("사용자 이름을 입력해주세요.");
		return;
	}

	let phone = $("input[name='phone']").val();
	if (phone.trim().length === 0) {
		alert("전화번호를 입력해주세요.");
		return;
	}

	let nickName = $("input[name='nickName']").val();
	if (nickName.trim().length === 0) {
		alert("닉네임을 입력해주세요.");
		return;
	}

	let birthdate = $("#bd").val();
	if (!birthdate) {
		alert("생년월일을 입력해주세요.");
		return;
	}

	let gender = $("input[name='gender']:checked").val();
	if (!gender) {
		alert("성별을 선택해주세요.");
		return;
	}

	let userAddress = $("#stAddress").val();
	if (userAddress.trim().length === 0) {
		alert("사용자 주소를 입력해주세요.");
		return;
	}

	let detailedAddress = $("#dtAddress").val();
	if (detailedAddress.trim().length === 0) {
		alert("사용자 상세 주소를 입력해주세요.");
		return;
	}
	const realUserName = username + "@" + email;
	$("#realUserName").val(realUserName);
	if (checkPasswordMatch()) {
		showRegistrationMessage();
		// 로그인 페이지로 이동
		window.location.href = "/login";
	}
}



// 회원가입 완료 메시지 표시 및 로그인 페이지로 이동
function redirectToLogin() {
	var message = "회원가입이 완료되었습니다.";
	alert(message);
	// 로그인 페이지로 이동
	window.location.href = "/login";
}



// 다음 우편번호 API를 이용한 우편번호 검색 함수
function daumPostcode() {
	new daum.Postcode({
		oncomplete: function(data) {
			// 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

			// 각 주소의 노출 규칙에 따라 주소를 조합한다.
			// 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
			var addr = ''; // 주소 변수

			//사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
			if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
				addr = data.roadAddress;
			} else { // 사용자가 지번 주소를 선택했을 경우
				addr = data.jibunAddress;
			}

			// 우편번호를 해당 필드에 넣는다.
			document.getElementById('postcode').value = data.zonecode;
			// 주소 정보(도로명 주소 or 지번 주소)를 해당 필드에 넣는다.
			document.getElementById("stAddress").value = addr;
			// 커서를 상세주소 필드로 이동한다.
			document.getElementById("dtAddress").focus();
		}
	}).open();
}