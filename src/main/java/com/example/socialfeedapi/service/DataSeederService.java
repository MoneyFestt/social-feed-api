package com.example.socialfeedapi.service;

import com.example.socialfeedapi.entity.*;
import com.example.socialfeedapi.repository.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DataSeederService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    private static final int BATCH_SIZE = 1000;

    public DataSeederService(UserRepository userRepository,
                             PostRepository postRepository,
                             CommentRepository commentRepository,
                             LikeRepository likeRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }

    /**
     * Основний метод, який запускає генерацію по черзі.
     * Виконується асинхронно (у фоні).
     */
    @Async
    public void seedData() {
        int targetUsers = 10_000;
        int targetPosts = 100_000;
        int targetComments = 500_000;
        int targetLikes = 1_000_000;

        long existingUsers = userRepository.count();
        long existingPosts = postRepository.count();
        long existingComments = commentRepository.count();
        long existingLikes = likeRepository.count();

        System.out.printf("🔹 Existing: users=%d, posts=%d, comments=%d, likes=%d%n",
                existingUsers, existingPosts, existingComments, existingLikes);

        if (existingUsers < targetUsers) generateUsers(targetUsers - (int) existingUsers);
        if (existingPosts < targetPosts) generatePosts(targetPosts - (int) existingPosts);
        if (existingComments < targetComments) generateComments(targetComments - (int) existingComments);
        if (existingLikes < targetLikes) generateLikes(targetLikes - (int) existingLikes);

        System.out.println("Data seeding completed successfully!");
    }

    // ---------------- USERS ----------------
    @Transactional
    public void generateUsers(int count) {
        System.out.println("Generating users...");
        List<User> batch = new ArrayList<>();
        long start = userRepository.count();

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setUsername("user" + (start + i));
            batch.add(user);

            if (batch.size() >= BATCH_SIZE) {
                userRepository.saveAllAndFlush(batch);
                batch.clear();
                printProgress("Users", start + i, count);
            }
        }
        if (!batch.isEmpty()) userRepository.saveAllAndFlush(batch);
        System.out.println("Users generated: " + count);
    }

    // ---------------- POSTS ----------------
    @Transactional
    public void generatePosts(int count) {
        System.out.println("Generating posts...");
        List<User> users = userRepository.findAll();
        List<Post> batch = new ArrayList<>();
        long start = postRepository.count();

        for (int i = 0; i < count; i++) {
            Post post = new Post();
            post.setUser(users.get(randomIndex(users.size())));
            post.setContent("Пост #" + (start + i));
            batch.add(post);

            if (batch.size() >= BATCH_SIZE) {
                postRepository.saveAllAndFlush(batch);
                batch.clear();
                printProgress("Posts", start + i, count);
            }
        }
        if (!batch.isEmpty()) postRepository.saveAllAndFlush(batch);
        System.out.println("Posts generated: " + count);
    }

    // ---------------- COMMENTS ----------------
    @Transactional
    public void generateComments(int count) {
        System.out.println("Generating comments...");
        List<User> users = userRepository.findAll();
        List<Post> posts = postRepository.findAll();
        List<Comment> batch = new ArrayList<>();
        long start = commentRepository.count();

        for (int i = 0; i < count; i++) {
            Comment c = new Comment();
            c.setUser(users.get(randomIndex(users.size())));
            c.setPost(posts.get(randomIndex(posts.size())));
            c.setContent("Коментар #" + (start + i));
            batch.add(c);

            if (batch.size() >= BATCH_SIZE) {
                commentRepository.saveAllAndFlush(batch);
                batch.clear();
                printProgress("Comments", start + i, count);
            }
        }
        if (!batch.isEmpty()) commentRepository.saveAllAndFlush(batch);
        System.out.println("Comments generated: " + count);
    }

    // ---------------- LIKES ----------------
    @Transactional
    public void generateLikes(int count) {
        System.out.println("Generating likes...");
        List<User> users = userRepository.findAll();
        List<Post> posts = postRepository.findAll();
        List<Like> batch = new ArrayList<>();
        Set<String> existingPairs = new HashSet<>();
        long start = likeRepository.count();

        while (existingPairs.size() < count) {
            User user = users.get(randomIndex(users.size()));
            Post post = posts.get(randomIndex(posts.size()));
            String key = user.getId() + ":" + post.getId();

            if (existingPairs.add(key)) {
                Like l = new Like();
                l.setUser(user);
                l.setPost(post);
                batch.add(l);

                if (batch.size() >= BATCH_SIZE) {
                    likeRepository.saveAllAndFlush(batch);
                    batch.clear();
                    printProgress("Likes", start + existingPairs.size(), count);
                }
            }
        }

        if (!batch.isEmpty()) likeRepository.saveAllAndFlush(batch);
        System.out.println("Likes generated: " + count);
    }

    // ---------------- HELPERS ----------------
    private int randomIndex(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

    private void printProgress(String entity, long done, long total) {
        double percent = (done * 100.0) / total;
        System.out.printf("%s progress: %.2f%%%n", entity, percent);
    }
}
