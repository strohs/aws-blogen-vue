package com.cliff.aws.blogen.repositories;


import com.cliff.aws.blogen.domain.Blogen;
import com.cliff.aws.blogen.domain.BlogenPrimaryKey;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring-data-dynamodb supports both findBy and queryBy repository methods in the same way:
 *    It tries to build a DynamoDB QueryRequest, but is only able to do so if all properties in the method name
 *    are one of the following:
 *
 *     Hash Key
 *     Range Key
 *     GSI Hash Key
 *     GSI Range Key
 *
 * It will fall back on a Scan if the properties used do not fall within the list above. For scan to work you
 * need to annotate either the class or method with the @EnableScan annotation
 *
 * Author: Cliff
 */
@EnableScan
@EnableScanCount
public interface BlogenRepository extends PagingAndSortingRepository<Blogen, BlogenPrimaryKey>, BlogenCustomRepository {

    List<Blogen> findByPrimaryHash( String id );

    List<Blogen> findByPrimaryRange( String range );

    Page<Blogen> findByPrimaryRange( String range, Pageable pageable );

    Optional<Blogen> findByPrimaryHashAndPrimaryRange( String hash, String range );
    
}
