package com.starterkit.demo.unit;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.starterkit.demo.util.CookieUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CookieUtilsUnitTest {

	private CookieUtils cookieUtils;
	private HttpServletRequest request;
	private HttpServletResponse response;

	@BeforeEach
	public void setup() {
		cookieUtils = CookieUtils.getInstance();
		request = Mockito.mock(HttpServletRequest.class);
		response = Mockito.mock(HttpServletResponse.class);
	}

	@Test
	void getCookie_CookieExists_ReturnsCookie() {
		Cookie cookie = new Cookie("testCookie", "testValue");
		Cookie[] cookies = {cookie};
		when(request.getCookies()).thenReturn(cookies);

		Optional<Cookie> result = cookieUtils.getCookie(request, "testCookie");

		assertThat(result).isPresent();
		assertThat(result.get().getValue()).isEqualTo("testValue");
	}

	@Test
	void getCookie_CookieDoesNotExist_ReturnsEmpty() {
		Cookie[] cookies = {new Cookie("anotherCookie", "value")};
		when(request.getCookies()).thenReturn(cookies);

		Optional<Cookie> result = cookieUtils.getCookie(request, "testCookie");

		assertThat(result).isNotPresent();
	}

	@Test
	void getCookie_NoCookies_ReturnsEmpty() {
		when(request.getCookies()).thenReturn(null);

		Optional<Cookie> result = cookieUtils.getCookie(request, "testCookie");

		assertThat(result).isNotPresent();
	}

	@Test
	void clearCookie_AddsExpiredCookie() {
		cookieUtils.clearCookie(response, "testCookie");

		verify(response, times(1)).addCookie(argThat(cookie ->
				cookie.getName().equals("testCookie") &&
				cookie.getValue() == null &&
				cookie.getPath().equals("/") &&
				cookie.isHttpOnly() &&
				cookie.getSecure() &&
				cookie.getMaxAge() == 0
		));
	}
}
