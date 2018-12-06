package com.cliff.aws.blogen.api.v1.validators;

import com.cliff.aws.blogen.api.v1.model.PostRequestDTO;
import com.cliff.aws.blogen.api.v1.services.CategoryService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validate required fields of a {@link com.cliff.aws.blogen.api.v1.model.PostDTO}
 * @author Cliff
 */
@Component
public class PostRequestDtoValidator implements Validator {


    @Override
    public boolean supports( Class<?> clazz ) {
        return PostRequestDTO.class.equals( clazz );
    }

    @Override
    public void validate( Object target, Errors errors ) {
        PostRequestDTO postDTO = (PostRequestDTO) target;

        //these are PostRequestDTO fields which are required
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "title","required.title","title is a required field but was null or empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "text","required.text","text is a required field but was null or empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "categoryName","required.categoryName","categoryName is a required field but was null or empty" );

        // check for reserved category name
        if ( postDTO.getCategoryName() != null && CategoryService.isReservedCategoryName( postDTO.getCategoryName() ) )
            errors.rejectValue(
                    "categoryName",
                    "invalid.categoryName",
                    "categoryName " + postDTO.getCategoryName() + " is reserved and cannot be used" );
    }
}
