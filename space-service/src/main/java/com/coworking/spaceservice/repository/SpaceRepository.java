package com.coworking.spaceservice.repository;

import com.coworking.spaceservice.model.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    
    List<Space> findByActiveTrue();
    
    List<Space> findByTypeAndActiveTrue(Space.SpaceType type);
    
    List<Space> findByCapacityGreaterThanEqualAndActiveTrue(Integer capacity);
    
    boolean existsByNameAndActiveTrue(String name);
}

