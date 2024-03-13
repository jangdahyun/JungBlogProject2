package kr.ezen.jung.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

/**
 * 로그인과 db에 저장하기 위한 객체
 * @version v.1.00
 */
@Data
public class JungMemberVO implements UserDetails{
	private static final long serialVersionUID = 1L;

	private int idx;						// 키필드
	
	private String username;				// id
	private String password;				// password
	private String email;				// 이메일
	private String role;					// 권한
	
	private String name;					// 실제 이름 
	private String nickName;				// 닉네임
	
	private String phone;					// 전화 번호 (sms를 하진 않겠지만 카카오로그인을 구현 할 경우 필요)
	
	// address
	private String stAddress;						// 도로명주소	(이름 추천)
	private String dtAddress;						// 상세주소		(이름 추천)
	
	private Date birthDate;					// 생일
	private int gender;						// (db에서 boolean저장불가 임시값 1 남 0 여
	
	
	// 가입일로 할 수 있는 짓 마지막 접속 일자를 기록해 마지막 접속일자와 현재 날짜의 차이가 몇 이상이면 휴면으로 만드는?
	// private LocalDateTime joinDate;		// 가입일
	
	// 휴먼상태 체크 이것은 스케줄러로 관리해야할듯
	// private int dormanted;				// ??
	
	// ==========================================================================================
	// *****************************   DB에 저장된 내용 끝      *********************************
	// ==========================================================================================
	
	
	
	// ******************************************************************************************
	// 여기부터는 하나의 db에서 가져오는게 아니라 추가로 유저에 삽입해줘야 할 내용 (즉, 다른 table에서 정보를 넣어줘야한다.)
	// ******************************************************************************************
	
	// 추가사항?? 
	// 1. 프로필사진을 저장할 객체
	// private UserProfile userProfile;		// kr.ezen.jung.vo.UserProfile 유저프로필 사진만 따로 저장해 놓은 db설계후 입력
	
	private int boardCount;
	
	
	// 시큐리티 설정
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
	}
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		return true;
	}
}
