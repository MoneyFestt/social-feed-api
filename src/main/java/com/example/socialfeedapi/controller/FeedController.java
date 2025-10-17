package com.example.socialfeedapi.controller;

import com.example.socialfeedapi.entity.Comment;
import com.example.socialfeedapi.entity.Post;
import com.example.socialfeedapi.entity.User;
import com.example.socialfeedapi.repository.CommentRepository;
import com.example.socialfeedapi.repository.LikeRepository;
import com.example.socialfeedapi.repository.PostRepository;
import com.example.socialfeedapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.socialfeedapi.service.DataSeederService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;



    @GetMapping
    public List<PostResponse> getFeed(@RequestParam Long userId) {
        // Перевіримо чи користувач існує
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Беремо останні 20 постів
        List<Post> posts = postRepository.findTop20ByOrderByCreatedAtDesc();

        return posts.stream().map(post -> {
            List<Comment> comments = commentRepository.findByPostId(post.getId());

            boolean likedByUser = likeRepository.existsByUserIdAndPostId(userId, post.getId());

            return new PostResponse(
                    post.getId(),
                    new UserInfo(post.getUser()),
                    post.getContent(),
                    post.getLikesCount(),
                    post.getCommentsCount(),
                    post.getCreatedAt().toString(),
                    comments.stream()
                            .map(c -> new CommentInfo(c.getId(), c.getContent(), new UserInfo(c.getUser())))
                            .collect(Collectors.toList()),
                    likedByUser
            );

        }).collect(Collectors.toList());
    }

    // DTO для поста
    public record PostResponse(
            Long id,
            UserInfo user,
            String content,
            int likesCount,
            int commentsCount,
            String createdAt,
            List<CommentInfo> comments,
            boolean likedByUser
    ) {}

    // DTO для користувача
    public record UserInfo(Long id, String username, int followersCount, String createdAt) {
        public UserInfo(User user) {
            this(user.getId(), user.getUsername(), user.getFollowersCount(), user.getCreatedAt().toString());
        }
    }

    // DTO для коментаря
    public record CommentInfo(Long id, String content, UserInfo user) {}
}
