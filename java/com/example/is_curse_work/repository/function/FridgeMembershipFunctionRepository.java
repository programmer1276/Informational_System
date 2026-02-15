package com.example.is_curse_work.repository.function;

public interface FridgeMembershipFunctionRepository {
    void addMember(Long fridgeId, Long userId, boolean moderator);
    void removeMember(Long fridgeId, Long userId);
}
