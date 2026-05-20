package com.interviewcopilot.backend.repository;

import com.interviewcopilot.backend.model.InterviewQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewQuestionRepository extends MongoRepository<InterviewQuestion, String> {
    List<InterviewQuestion> findByUserId(String userId);
    List<InterviewQuestion> findByUserIdAndJobRole(String userId, String jobRole);
}
