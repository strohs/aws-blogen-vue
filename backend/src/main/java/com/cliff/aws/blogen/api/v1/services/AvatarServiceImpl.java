package com.cliff.aws.blogen.api.v1.services;

import com.cliff.aws.blogen.domain.Blogen;
import com.cliff.aws.blogen.domain.BlogenPrimaryKey;
import com.cliff.aws.blogen.repositories.BlogenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Services for working with Avatar image files
 *
 * @author Cliff
 */
@Slf4j
@Service
public class AvatarServiceImpl implements AvatarService {


    private BlogenRepository blogenRepository;

    @Autowired
    public AvatarServiceImpl( BlogenRepository blogenRepository ) {
        this.blogenRepository = blogenRepository;
    }


    @Override
    public List<String> getAllAvatarImageNames() {
        return new ArrayList<>( blogenRepository.findAllAvatarFileNames() );
    }

    @Override
    public Optional<Blogen> findAvatarByFileName( String imageFileName ) {
        return blogenRepository.findByPrimaryHashAndPrimaryRange( imageFileName, Blogen.RANGE_AVATAR);
    }

    @Override
    public Blogen saveAvatarFileName( String fileName ) {
        BlogenPrimaryKey pk = BlogenPrimaryKey.builder()
                .primaryHash( fileName )
                .primaryRange( Blogen.RANGE_AVATAR)
                .build();
        Blogen blogen = Blogen.builder().blogenPrimaryKey( pk ).build();
        return blogenRepository.save(blogen);
    }

}
