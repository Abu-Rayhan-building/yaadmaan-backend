package edu.sharif.math.yaadmaan.repository;

import edu.sharif.math.yaadmaan.domain.Comment;
import edu.sharif.math.yaadmaan.service.dto.CommentDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select comment from Comment comment where comment.memory.id = :id")
    Page<Comment> findAllForMemory(@Param("id") Long id,Pageable pageable);
}
