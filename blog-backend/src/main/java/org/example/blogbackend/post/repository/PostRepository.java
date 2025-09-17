package org.example.blogbackend.post.repository;

import org.example.blogbackend.category.model.entity.Category;
import org.example.blogbackend.post.model.PostStatus;
import org.example.blogbackend.post.model.entity.Post;
import org.example.blogbackend.tag.model.entity.Tag;
import org.example.blogbackend.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findAllByStatusAndCategoryAndTagsContaining(PostStatus status, Category category, Tag tag);
    List<Post> findAllByStatusAndCategory(PostStatus status, Category category);
    List<Post> findAllByStatusAndTagsContaining(PostStatus status, Tag tag);
    List<Post> findAllByStatus(PostStatus status);
    List<Post> findAllByAuthorAndStatus(User author, PostStatus status);
}
