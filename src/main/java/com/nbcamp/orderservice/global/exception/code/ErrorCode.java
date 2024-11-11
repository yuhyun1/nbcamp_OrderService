package com.nbcamp.orderservice.global.exception.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	/**
	 * Server
	 */
	VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "유효성 검사 실패"),
	CONSTRAINT_VIOLATION(HttpStatus.CONFLICT, "제약 조건 위반"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생하였습니다."),
	S3_UPLOADER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드 중 오류가 발생하였습니다."),
	UNSUPPORTED_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "지원되지 않는 Content-Type입니다."),

	/**
	 * Common
	 */
	NOT_FOUND_ENUM_CONSTANT(HttpStatus.BAD_REQUEST, "열거형 상수값을 찾을 수 없습니다."),
	IS_NULL(HttpStatus.BAD_REQUEST, "NULL 값이 들어왔습니다."),
	COMMON_INVALID_PARAM(HttpStatus.BAD_REQUEST, "요청한 값이 올바르지 않습니다."),
	INVALID_AUTHENTICATION(HttpStatus.BAD_REQUEST, "인증이 올바르지 않습니다."),
	NO_SUCH_METHOD(HttpStatus.BAD_REQUEST, "메소드를 찾을 수 없습니다."),

	/**
	 * Json Web Token
	 */
	EXPIRED_JWT_EXCEPTION(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
	UNSUPPORTED_JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "지원되지 않는 토큰입니다."),
	MALFORMED_JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 형식의 토큰입니다."),
	SIGNATURE_EXCEPTION(HttpStatus.BAD_REQUEST, "토큰 서명이 올바르지 않습니다."),
	ILLEGAL_ARGUMENT_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 인자가 전달되었습니다."),

	/**
	 * Authentication
	 */
	INTERNAL_AUTHENTICATION_SERVICE(HttpStatus.BAD_REQUEST, "인증 서비스가 존재하지 않습니다."),
	NON_EXPIRED_ACCOUNT(HttpStatus.BAD_REQUEST, "사용자 계정이 탈퇴되었습니다."),
	NON_LOCKED_ACCOUNT(HttpStatus.BAD_REQUEST, "사용자 계정이 정지되었습니다."),
	DISABLE_ACCOUNT(HttpStatus.BAD_REQUEST, "사용자 계정은 비활성화 상태입니다."),
	EXPIRED_CREDENTIAL(HttpStatus.BAD_REQUEST, "사용자 인증 정보가 만료되었습니다."),
	INSUFFICIENT_PERMISSION(HttpStatus.FORBIDDEN, "사용자에게 접근 권한이 없습니다."),


	/**
	 * Admin, Member
	 */
	WITHDRAWN_MEMBER(HttpStatus.BAD_REQUEST, "탈퇴한 회원입니다."),
	NOT_FOUND_ADMIN(HttpStatus.BAD_REQUEST, "어드민 정보를 찾을 수 없습니다."),
	NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "회원 정보를 찾을 수 없습니다."),
	EXIST_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),
	EXIST_PHONE(HttpStatus.BAD_REQUEST, "중복된 전화번호입니다."),
	EXIST_NICKNAME(HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
	FAIL_LOGIN(HttpStatus.BAD_REQUEST, "이메일 또는 비밀번호가 잘못되었습니다."),
	NOT_MATCH_CONFIRM(HttpStatus.BAD_REQUEST, "입력 값과 확인 값이 일치하지 않습니다."),
	MATCH_PASSWORD_AND_NEW_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호와 변경할 비밀번호가 일치합니다."),
	NOT_FOUND_BLOCK(HttpStatus.BAD_REQUEST, "회원 차단 정보를 찾을 수 없습니다."),
	CAN_NOT_BLOCK(HttpStatus.BAD_REQUEST, "해당 계정은 차단할 수 없습니다."),
	ALREADY_BLOCK(HttpStatus.BAD_REQUEST, "해당 계정은 이미 차단되어 있습니다."),
	ALREADY_UNBLOCK(HttpStatus.BAD_REQUEST, "해당 계정은 차단되어 있지 않습니다."),
	INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN, "해당 요청은 권한이 부족하여 수행할 수 없습니다."),
	INVALID_UUID_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 UUID 형식입니다."),

	/**
	 * Category
	 */
	NOT_FOUND_CATEGORY(HttpStatus.BAD_REQUEST, "카테고리를 찾을 수 없습니다."),
	EXIST_CATEGORY(HttpStatus.BAD_REQUEST, "중복된 카테고리입니다."),

	/**
	 * STORE
	 */
	NOT_FOUND_STORE(HttpStatus.BAD_REQUEST, "매장을 찾을 수 없습니다."),

	/**
	 * PRODUCT
	 */
	NOT_FOUND_PRODUCT(HttpStatus.BAD_REQUEST, "상품을 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;

}
