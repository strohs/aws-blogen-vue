package com.cliff.aws.blogen.api.v1.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.cliff.aws.blogen.api.v1.mappers.CategoryMapper;
import com.cliff.aws.blogen.api.v1.model.CategoryDTO;
import com.cliff.aws.blogen.api.v1.model.CategoryListDTO;
import com.cliff.aws.blogen.domain.Blogen;
import com.cliff.aws.blogen.exceptions.BadRequestException;
import com.cliff.aws.blogen.repositories.BlogenRepository;
import com.cliff.aws.blogen.services.utils.PageRequestBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Category CRUD Service for REST API
 * For Categories:
 *   we can get a list of all categories
 *   get a specific category
 *   create a new category
 *   change the name of a category
 *
 * Only Blogen Admins can perform CRUD ops on categories
 *
 * NOTE: Deleting categories is not supported in this API...for now
 *
 * @author Cliff
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private BlogenRepository blogenRepository;
    private CategoryMapper categoryMapper;
    private PageRequestBuilder pageRequestBuilder;

    @Autowired
    public CategoryServiceImpl( BlogenRepository blogenRepository, CategoryMapper categoryMapper,
                                PageRequestBuilder pageRequestBuilder ) {
        this.blogenRepository = blogenRepository;
        this.categoryMapper = categoryMapper;
        this.pageRequestBuilder = pageRequestBuilder;
    }

    @Override
    public CategoryListDTO getCategories( int pageNum, int pageSize ) {
        PageRequest pageRequest = pageRequestBuilder.buildPageRequest( pageNum, pageSize );
        //retrieve the posts
        Page<Blogen> page = blogenRepository.findByPrimaryRange( Blogen.RANGE_CATEGORY, pageRequest );

        List<CategoryDTO> catDTOS = new ArrayList<>();
        page.forEach( blogen -> {
            CategoryDTO dto = categoryMapper.blogenToCategoryDto( blogen );
            catDTOS.add( dto );
        } );
        return new CategoryListDTO( catDTOS, PageRequestBuilder.buildPageInfoResponse( page ) );
    }

    @Override
    public CategoryDTO getCategory( String categoryName ) {
        Blogen category = blogenRepository
                .findByPrimaryHashAndPrimaryRange( categoryName, Blogen.RANGE_CATEGORY)
                .orElseThrow( () -> new BadRequestException( "category with id: " + categoryName + " does not exist" ) );
        CategoryDTO categoryDTO = categoryMapper.blogenToCategoryDto( category );
        return categoryDTO;
    }

    @PreAuthorize( "hasRole('ADMIN')" )
    @Override
    public CategoryDTO createNewCategory( CategoryDTO categoryDTO ) {
        CategoryDTO savedDTO = null;
        try {
            //mapper should set primaryHash, primaryRange, categoryName
            Blogen categoryToSave = categoryMapper.categoryDtoToBlogen( categoryDTO );
            Blogen savedCategory = blogenRepository.save( categoryToSave );
            savedDTO = categoryMapper.blogenToCategoryDto( savedCategory );
            //todo see what gets thrown if dynamodb can not save
        } catch ( Exception e ) {
            String message = String.format( "Error saving category with name %s", categoryDTO.getName() );
            throw new DataIntegrityViolationException( message );
        }
        return savedDTO;
    }

    @PreAuthorize( "hasRole('ADMIN')" )
    @Override
    public CategoryDTO updateCategory( String oldCatName, CategoryDTO categoryDTO ) {
        Blogen category = blogenRepository.findByPrimaryHashAndPrimaryRange( oldCatName, Blogen.RANGE_CATEGORY)
                .orElseThrow( () -> new BadRequestException( "category does not exist with name:" + oldCatName ) );
        //dynamo requires us to delete category first, then save the updated one
        blogenRepository.delete( category );
        // create new category from values in the DTO
        Blogen categoryToSave = categoryMapper.categoryDtoToBlogen( categoryDTO );
        Blogen savedCategory = blogenRepository.save( categoryToSave );
        // update all the old category names in existing posts with the new category name
        List<Blogen> existingPosts = blogenRepository.fetchAllPostsByCategoryName( oldCatName );
        existingPosts.forEach(blogen -> blogen.setCategoryName( categoryDTO.getName() ));
        blogenRepository.updateItems( existingPosts, DynamoDBMapperConfig.SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES);
        
        return categoryMapper.blogenToCategoryDto( savedCategory );
    }

    @Override
    public boolean categoryNameAvailable( String name ) {
        boolean isReserved = CategoryService.isReservedCategoryName( name );
        Optional<Blogen> category = blogenRepository.findByPrimaryHashAndPrimaryRange( name, Blogen.RANGE_CATEGORY);
        if (!isReserved && !category.isPresent() ) {
            return true;
        } else if (isReserved) {
            throw new BadRequestException( "category name is reserved: " + name);
        } else {
            throw new BadRequestException( "category name already exists: " + name );
        }
    }

    @Override
    public boolean categoryNameExists( String categoryName ) {
        return blogenRepository.findByPrimaryHashAndPrimaryRange( categoryName, Blogen.RANGE_CATEGORY).isPresent();
    }
}
