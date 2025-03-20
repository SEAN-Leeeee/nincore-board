package com.sean.nlnnong.domain;

public class TeamApplication {

    private User user;
    private Team team;
    private ApplicationStatus status;

    public TeamApplication(User user, Team team) {
        this.user = user;
        this.team = team;
        this.status = ApplicationStatus.PENDING;
    }

    public void approve() {
        this.status = ApplicationStatus.APPROVED;
    }

    public void reject() {
        this.status = ApplicationStatus.REJECTED;
    }
}
