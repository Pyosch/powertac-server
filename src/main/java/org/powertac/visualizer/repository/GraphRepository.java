package org.powertac.visualizer.repository;

import org.powertac.visualizer.domain.Graph;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Graph entity.
 */
public interface GraphRepository extends JpaRepository<Graph,Long> {

    @Query("select graph from Graph graph where graph.owner.login = ?#{principal.username}")
    List<Graph> findByOwnerIsCurrentUser();

}
