package com.sayan.onesocialbk.repositories;

import java.util.List;
import java.util.Optional;

import com.sayan.onesocialbk.models.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
// import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// @RepositoryRestResource(collectionResourceRel = "people", path = "people")
public interface PersonRepository extends MongoRepository<Person, String> {

    List<Person> findByLastName(@Param("name") String name);

    List<Person> findByFirstName(@Param("name") String name);

    @Query("{ 'phone' : ?0 }")
    Optional<Person> findByPhone(int phone);

    @Query("{ 'email' : ?0 }")
    Optional<Person> findByEmail(String email);

    @Query("{'username': ?0}")
    Optional<Person> findByUsername(String username);

    @Query("{'username': ?0, 'password': ?1}")
    Optional<Person> findByUsernameAndPassword(String username, String password);
}