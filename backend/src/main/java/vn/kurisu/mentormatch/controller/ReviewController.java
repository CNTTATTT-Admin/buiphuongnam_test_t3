package vn.kurisu.mentormatch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vn.kurisu.mentormatch.dto.request.ReviewRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.ReviewResponse;
import vn.kurisu.mentormatch.entity.*;
import vn.kurisu.mentormatch.repository.BookingRepository;
import vn.kurisu.mentormatch.repository.ReviewRepository;
import vn.kurisu.mentormatch.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ApiResponse<ReviewResponse> createReview(@RequestBody ReviewRequest request) {
        User currentUser = getCurrentUser();

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getMentee().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only review your own bookings");
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("You can only review completed bookings");
        }

        if (reviewRepository.existsByBookingId(booking.getId())) {
            throw new RuntimeException("You have already reviewed this booking");
        }

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        User mentor = booking.getTimeSlot().getMentor();

        Review review = Review.builder()
                .booking(booking)
                .mentee(currentUser)
                .mentor(mentor)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);

        return ApiResponse.<ReviewResponse>builder()
                .result(mapToResponse(review))
                .message("Review submitted successfully")
                .build();
    }

    @GetMapping("/mentor/{mentorId}")
    public ApiResponse<List<ReviewResponse>> getMentorReviews(@PathVariable Integer mentorId) {
        List<ReviewResponse> reviews = reviewRepository.findByMentorIdOrderByCreatedAtDesc(mentorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviews)
                .build();
    }

    @GetMapping("/booking/{bookingId}")
    public ApiResponse<ReviewResponse> getReviewByBooking(@PathVariable Integer bookingId) {
        Review review = reviewRepository.findByBookingId(bookingId).orElse(null);
        return ApiResponse.<ReviewResponse>builder()
                .result(review != null ? mapToResponse(review) : null)
                .build();
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .bookingId(review.getBooking().getId())
                .menteeId(review.getMentee().getId())
                .menteeName(review.getMentee().getFullName())
                .menteeAvatar(review.getMentee().getAvatarUrl())
                .mentorId(review.getMentor().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
