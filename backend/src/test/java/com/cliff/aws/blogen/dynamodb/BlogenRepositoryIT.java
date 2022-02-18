package com.cliff.aws.blogen.dynamodb;

import com.cliff.aws.blogen.bootstrap.Bootstrapper;
import com.cliff.aws.blogen.bootstrap.DynamoDbBootstrapper;
import com.cliff.aws.blogen.domain.Blogen;
import com.cliff.aws.blogen.repositories.BlogenRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * tests that dynamoDB Local starts-up
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles( {"dynamodb-local","dev"})
@Ignore
public class BlogenRepositoryIT {

    @Autowired
    private BlogenRepository repository;

    @Test
    public void repositoryLoads() {
        assertThat( repository, is(notNullValue()) );
    }

    @Test
    public void shouldRetrieveCategoryBusiness() {
        List<Blogen> category = repository.findByPrimaryHash("Business");
        assertThat( category, hasSize( greaterThan(0) ) );
        category.forEach( System.out::println );
    }

    @Test
    public void shouldSaveNewCategory() {
        final Blogen linuxCategory = DynamoDbBootstrapper.buildCategory("Linux");
        final Blogen savedCat = repository.save(linuxCategory);
        assertThat( savedCat, is( notNullValue() ));
        System.out.println( savedCat );
    }
}
