package com.cliff.aws.blogen.services.utils;

import com.cliff.aws.blogen.api.v1.model.PageInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * Helper class for building Spring-Data page requests.
 *
 * @see  {@link PageRequest}
 *
 * @author Cliff
 */
@Component
public class PageRequestBuilder {

    //number of threads to display on the posts.html page
    @Value( "${blogen.threads.per.page}" )
    private int THREADS_PER_PAGE;

    @Value( "${blogen.categories.per.page}" )
    private int CATEGORIES_PER_PAGE;

    public PageRequest buildPageRequest( int pageNum, int elementsPerPage ) {
        return PageRequest.of( pageNum, elementsPerPage );
    }

    public PageRequest buildPostPageRequest( int pageNum, Sort.Direction sortDir, String property ) {
        return PageRequest.of( pageNum, THREADS_PER_PAGE, sortDir, property );
    }

    public PageRequest buildCategoryPageRequest( int pageNum, Sort.Direction sortDir, String property ) {
        return PageRequest.of( pageNum, CATEGORIES_PER_PAGE, sortDir, property );
    }

    public PageRequest buildPageRequest( int pageNum, int elementsPerPage, Sort.Direction sortDir, String property ) {
        return PageRequest.of( pageNum, elementsPerPage, sortDir, property );
    }

    public static PageInfoResponse buildPageInfoResponse( Page page ) {
        return PageInfoResponse.builder()
                .totalPages( page.getTotalPages() )
                .totalElements( page.getTotalElements() )
                .pageSize( page.getSize() )
                .pageNumber( page.getNumber() )
                .build();
    }
}
