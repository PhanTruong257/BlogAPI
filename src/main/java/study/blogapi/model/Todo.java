package study.blogapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import study.blogapi.model.audit.UserDateAudit;
import study.blogapi.model.user.User;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "todos", uniqueConstraints = { @UniqueConstraint(columnNames = { "title" }) })
public class Todo extends UserDateAudit {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "title")
    private String title;

    @Column(name = "completed")
    private Boolean completed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    public User getUser() {
        return user;
    }
}
