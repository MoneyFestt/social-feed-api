package com.example.socialfeedapi.controller;

import com.example.socialfeedapi.entity.Post;
import com.example.socialfeedapi.entity.User;
import com.example.socialfeedapi.repository.PostRepository;
import com.example.socialfeedapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.socialfeedapi.entity.Like;
import com.example.socialfeedapi.entity.LikeId;
import com.example.socialfeedapi.entity.Comment;
import com.example.socialfeedapi.repository.LikeRepository;
import com.example.socialfeedapi.repository.CommentRepository;


import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    // Створити пост
    @PostMapping
    public Post createPost(@RequestParam Long userId, @RequestBody Post post) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        post.setUser(user);
        return postRepository.save(post);
    }

    // Отримати пост за id
    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    // Отримати всі пости користувача
    @GetMapping("/user/{userId}")
    public List<Post> getPostsByUser(@PathVariable Long userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Оновити пост
    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Long id, @RequestBody Post updatedPost) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setContent(updatedPost.getContent());
        return postRepository.save(post);
    }

    // Видалити пост
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
    }

    // Додати лайк/анлайк
    @PostMapping("/{id}/like")
    public String likePost(@PathVariable Long id, @RequestParam Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LikeId likeId = new LikeId(user.getId(), post.getId());
        boolean exists = likeRepository.existsById(likeId);

        if (exists) {
            likeRepository.deleteById(likeId);
            post.setLikesCount(post.getLikesCount() - 1);
            postRepository.save(post);
            return "Unliked";
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
            post.setLikesCount(post.getLikesCount() + 1);
            postRepository.save(post);
            return "Liked";
        }
    }

    // Додати коментар
    @PostMapping("/{id}/comment")
    public Comment addComment(@PathVariable Long id, @RequestParam Long userId, @RequestBody Comment comment) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        comment.setPost(post);
        comment.setUser(user);
        Comment saved = commentRepository.save(comment);

        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return saved;
    }

    // Отримати коментарі поста
    @GetMapping("/{id}/comments")
    public List<Comment> getComments(@PathVariable Long id) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(id);
    }

}
