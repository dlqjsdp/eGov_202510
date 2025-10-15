package com.lime.user.service;

import com.lime.user.vo.UserVO;

/**
 * @Class Name  : UserService.java
 * @Description : 회원 관련 비즈니스 로직을 처리하기 위한 서비스 인터페이스
 * @Modification Information
 * 
 *     수정일                       수정자                    수정내용
 *     ----------    ---------    -------------------------------
 *     2025.10.14    노유경                     최초 생성 (회원가입 및 ID 중복검사 기능 정의)
 *     2025.10.15    노유경                     메서드명 수정 (isAvailableUserId → existsUserId)
 */

public interface UserService {
	
	// ID 중복 검사 (해당 ID 존재 여부 확인) -> 반환값이 있으면 true, 없으면 false
	public boolean existsUserId(String userId);
	
	// 회원 등록 (데이터 저장)
	public void register(UserVO vo);

}
