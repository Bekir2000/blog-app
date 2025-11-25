package org.example.blogbackend.post.repository;

import org.example.blogbackend.category.model.entity.Category;
import org.example.blogbackend.post.model.PostStatus;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.tag.model.entity.Tag;
import org.example.blogbackend.user.model.entity.User;
import org.springframework.data.domain.Page; // <--- NEW IMPORT
import org.springframework.data.domain.Pageable; // <--- NEW IMPORT
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    Page<Post> findAllByStatusAndCategoryAndTagsContaining(
            PostStatus status,
            Category category,
            Tag tag,
            Pageable pageable
    );

    Page<Post> findAllByStatusAndCategory(
            PostStatus status,
            Category category,
            Pageable pageable
    );

    Page<Post> findAllByStatusAndTagsContaining(
            PostStatus status,
            Tag tag,
            Pageable pageable
    );

    Page<Post> findAllByStatus(
            PostStatus status,
            Pageable pageable
    );

    // This remains List<Post> as drafts are usually displayed all at once.
    List<Post> findAllByAuthorAndStatus(User author, PostStatus status);
}
