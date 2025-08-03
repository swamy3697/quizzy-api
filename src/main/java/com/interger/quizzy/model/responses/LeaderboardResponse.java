package com.interger.quizzy.model.responses;

import com.interger.quizzy.model.LeaderboardUser;

import java.util.List;

public class LeaderboardResponse {
    int myRank; // send his rank (position)
    LeaderboardUser mine; // his details
    List<LeaderboardUser> leaderboard; // other users
    int totalCount; //  // total user count
}
