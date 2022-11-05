package com.sayan.onesocialbk.repositories;

import java.util.List;
import java.util.Optional;

import com.sayan.onesocialbk.models.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "people", path = "people")
public interface PersonRepository extends MongoRepository<Person, String> {

    List<Person> findByLastName(@Param("name") String name);
    List<Person> findByFirstName(@Param("name") String name);
    Optional<Person> findByPhone(@Param("phone") int phone);
    Optional<Person> findByEmail(@Param("email") String email);
    Optional<Person> findByUsername(@Param("username") String username);
}