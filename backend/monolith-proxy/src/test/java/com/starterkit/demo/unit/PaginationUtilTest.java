package com.starterkit.demo.unit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;

import com.starterkit.demo.util.PaginationUtil;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class PaginationUtilTest {

    @Test
     void testGeneratePaginationHttpHeadersFirstPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<String> page = new PageImpl<>(Collections.emptyList(), pageable, 100);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/test");

        assertEquals("100", headers.getFirst("X-Total-Count"));

        String linkHeader = headers.getFirst(HttpHeaders.LINK);
        assertTrue(linkHeader.contains("</api/test?page=1&size=10>; rel=\"next\""));
        assertTrue(linkHeader.contains("</api/test?page=9&size=10>; rel=\"last\""));
        assertTrue(linkHeader.contains("</api/test?page=0&size=10>; rel=\"first\""));
        assertTrue(!linkHeader.contains("rel=\"prev\""));
    }

    @Test
     void testGeneratePaginationHttpHeadersMiddlePage() {
        Pageable pageable = PageRequest.of(5, 10);
        Page<String> page = new PageImpl<>(Collections.emptyList(), pageable, 100);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/test");

        assertEquals("100", headers.getFirst("X-Total-Count"));

        String linkHeader = headers.getFirst(HttpHeaders.LINK);
        assertTrue(linkHeader.contains("</api/test?page=6&size=10>; rel=\"next\""));
        assertTrue(linkHeader.contains("</api/test?page=4&size=10>; rel=\"prev\""));
        assertTrue(linkHeader.contains("</api/test?page=9&size=10>; rel=\"last\""));
        assertTrue(linkHeader.contains("</api/test?page=0&size=10>; rel=\"first\""));
    }

    @Test
     void testGeneratePaginationHttpHeadersLastPage() {
        Pageable pageable = PageRequest.of(9, 10);
        Page<String> page = new PageImpl<>(Collections.emptyList(), pageable, 100);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/test");

        assertEquals("100", headers.getFirst("X-Total-Count"));

        String linkHeader = headers.getFirst(HttpHeaders.LINK);
        assertTrue(!linkHeader.contains("rel=\"next\""));
        assertTrue(linkHeader.contains("</api/test?page=8&size=10>; rel=\"prev\""));
        assertTrue(linkHeader.contains("</api/test?page=9&size=10>; rel=\"last\""));
        assertTrue(linkHeader.contains("</api/test?page=0&size=10>; rel=\"first\""));
    }

    @Test
     void testGeneratePaginationHttpHeadersSinglePage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<String> page = new PageImpl<>(Collections.emptyList(), pageable, 10);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/test");

        assertEquals("10", headers.getFirst("X-Total-Count"));

        String linkHeader = headers.getFirst(HttpHeaders.LINK);
        assertTrue(!linkHeader.contains("rel=\"next\""));
        assertTrue(!linkHeader.contains("rel=\"prev\""));
        assertTrue(linkHeader.contains("</api/test?page=0&size=10>; rel=\"last\""));
        assertTrue(linkHeader.contains("</api/test?page=0&size=10>; rel=\"first\""));
    }
}
