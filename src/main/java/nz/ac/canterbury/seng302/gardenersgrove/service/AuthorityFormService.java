package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Authority;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.AuthorityFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorityFormService {
    private final AuthorityFormRepository authorityFormRepository;

    @Autowired
    public AuthorityFormService(AuthorityFormRepository authorityFormRepository) {
        this.authorityFormRepository = authorityFormRepository;
    }

    public Optional<Authority> getAuthorityByGardener(Gardener gardener) {
        return authorityFormRepository.findAuthorityByGardener(gardener);
    }

    public void addAuthority(Authority authority) {
        authorityFormRepository.save(authority);
    }


}
