package vn.kurisu.mentormatch.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mentee_profiles")
public class MenteeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "current_education")
    private String currentEducation;

    @Column(name = "learning_goals", columnDefinition = "TEXT")
    private String learningGoals;

    @Column(name = "interests")
    private String interests;
}
