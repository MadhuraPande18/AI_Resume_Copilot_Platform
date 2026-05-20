package com.interviewcopilot.backend.repository;

import com.interviewcopilot.backend.model.AtsScore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtsScoreRepository extends MongoRepository<AtsScore, String> {
    List<AtsScore> findByUserId(String userId);
    Optional<AtsScore> findTopByUserIdOrderByAnalyzedAtDesc(String userId);
}