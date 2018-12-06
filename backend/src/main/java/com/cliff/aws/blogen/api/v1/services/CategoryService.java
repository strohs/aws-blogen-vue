package com.cliff.aws.blogen.api.v1.services;

import com.cliff.aws.blogen.api.v1.controllers.CategoryController;
import com.cliff.aws.blogen.api.v1.model.CategoryDTO;
import com.cliff.aws.blogen.api.v1.model.CategoryListDTO;
import com.cliff.aws.blogen.domain.Blogen;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * CategoryService for REST API
 *
 * @author Cliff
 */
public interface CategoryService {


    /**
     * retrieves a 'page' worth of categories
     * @param pageNum the page of categories to retrieve using 0-based indices
     * @param pageSize the max number of categories to retrieve per page
     * @return
     */
    CategoryListDTO getCategories( int pageNum, int pageSize );

    /**
     * Get a specific Category
     *
     * @param categoryName the Category name to search for
     * @return a CategoryDTO representing the Blogen category
     */
    CategoryDTO getCategory( String categoryName );

    /**
     * Create a new Blogen Category
     * @param categoryDTO containes the details of the Category to create
     * @return a CategoryDTO representing the newly created Category
     */
    CategoryDTO createNewCategory( CategoryDTO categoryDTO );

    /**
     * update a specific category
     * @param categoryName the category name to update
     * @param categoryDTO DTO containing the fields to update
     * @return a categoryDTO containing the details of the updated category
     */
    CategoryDTO updateCategory( String categoryName, CategoryDTO categoryDTO );

    /**
     * return true if the specified does not exist in the database AND it is not a reserved word
     * @param name
     * @return
     */
    boolean categoryNameAvailable( String name );

    /**
     * returns true if the category name exists in the database, else false
     * @param categoryName
     * @return
     */
    boolean categoryNameExists( String categoryName );

    String CATEGORY_ALL = "ALL";

    Set<String> reservedCategoryNames = new HashSet<String>( Arrays.asList(CATEGORY_ALL) );

    static boolean isReservedCategoryName( String name ) {
        return reservedCategoryNames.contains( name.toUpperCase() );
    }

    static String buildCategoryUrl( Blogen blogen ) {
        return CategoryController.BASE_URL + "/" + blogen.getCategoryName();
    }
}
