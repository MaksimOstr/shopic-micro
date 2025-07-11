package com.reviewservice.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Setter
@DiscriminatorValue("COMMENT")
public class ReviewCommentReport extends Report {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private ReviewComment comment;
}
