package org.example.controllers;

import org.example.models.Review;
import org.example.models.Role;
import org.example.models.User;
import org.example.repo.ReviewRepository;
import org.example.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("name", "World");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Страница про нас!");
        return "about";
    }

    @GetMapping("/reviews")
    public String reviews(Model model) {
        Iterable<Review> reviews = reviewRepository.findAll();
        model.addAttribute("title", "Страница с отзывами");
        model.addAttribute("reviews", reviews);
        return "reviews";
    }

    @PostMapping("/reviews-add")
    public String reviewsAdd(@AuthenticationPrincipal User user, @RequestParam String title, @RequestParam String text, Model model) {
        Review review = new Review(title, text, user);

        reviewRepository.save(review);
        return "redirect:/reviews";
    }

    @GetMapping("/reviews/{id}")
    public String reviewInfo(@PathVariable(value = "id") long reviewId, Model model) {
        Optional<Review> review = reviewRepository.findById(reviewId);

        ArrayList<Review> result = new ArrayList<>();
        review.ifPresent(result::add);

        model.addAttribute("review", result);
        return "/review-info";
    }

    @GetMapping("/reviews/{id}/update")
    public String reviewUpdate(@PathVariable(value = "id") long reviewId, Model model) {
        Optional<Review> review = reviewRepository.findById(reviewId);

        ArrayList<Review> result = new ArrayList<>();
        review.ifPresent(result::add);

        model.addAttribute("review", result);
        return "/review-update";
    }

    @PostMapping("/reviews/{id}/update")
    public String reviewsUpdateForm(@PathVariable(value = "id") long reviewId, @RequestParam String title, @RequestParam String text, Model model) throws ClassNotFoundException {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ClassNotFoundException());

        review.setTitle(title);
        review.setText(text);
        reviewRepository.save(review);

        return "redirect:/reviews/" + reviewId;
    }

    @PostMapping("/reviews/{id}/delete")
    public String reviewDelete(@PathVariable(value = "id") long reviewId, Model model) throws ClassNotFoundException {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ClassNotFoundException());
        reviewRepository.delete(review);

        return "redirect:/reviews";
    }

    @GetMapping("/reg")
    public String reg() {
        return "/reg";
    }

    @PostMapping("/reg")
    public String addUser(User user, Model model) {
        user.setEnabled(true);
        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);
        return "redirect:/login";
    }
}
