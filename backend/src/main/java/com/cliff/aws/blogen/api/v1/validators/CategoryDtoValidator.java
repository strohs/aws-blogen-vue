package com.cliff.aws.blogen.api.v1.validators;

import com.cliff.aws.blogen.api.v1.model.CategoryDTO;
import com.cliff.aws.blogen.api.v1.services.CategoryService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validator for CategoryDTOs
 *
 * @author Cliff
 */
@Component
public class CategoryDtoValidator implements Validator {

    @Override
    public boolean supports( Class<?> clazz ) {
        return CategoryDTO.class.equals( clazz );
    }

    @Override
    public void validate( Object target, Errors errors ) {

        CategoryDTO categoryDTO = (CategoryDTO) target;

        //these are dto fields which are required
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "name","required.name","category name is a required field but was null or empty" );

        //category id will be ignored...for now
        //if ( categoryDTO.getId() != null )
        //    errors.rejectValue( "id","invalid.id","category id should not be sent as part of the request body" );

        // check for reserved category name
        if (categoryDTO.getName() != null && CategoryService.isReservedCategoryName( categoryDTO.getName() ) )
            errors.rejectValue(
                    "categoryName",
                    "invalid.categoryName",
                    "categoryName " + categoryDTO.getName() + " is reserved and cannot be used" );

        //categoryUrl should not be sent in a request
        if (categoryDTO.getCategoryUrl() != null )
            errors.rejectValue( "categoryUrl","invalid.categoryUrl","categoryUrl should not be sent as part of the request body" );

    }

}
