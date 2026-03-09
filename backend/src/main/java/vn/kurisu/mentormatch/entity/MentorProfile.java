package vn.kurisu.mentormatch.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mentor_profiles")
public class MentorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "wallet_balance", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal walletBalance = BigDecimal.ZERO;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "mentor_skills",
            joinColumns = @JoinColumn(name = "mentor_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();

    @OneToMany(mappedBy = "mentorProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Certificate> certificates = new HashSet<>();
}
