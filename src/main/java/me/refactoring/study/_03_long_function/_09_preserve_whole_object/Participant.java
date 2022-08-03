package me.refactoring.study._03_long_function._09_preserve_whole_object;

public record Participant(String username, Map<Integer, Boolean> homework) {
    public Participant(String username) {
        this(username, new HashMap<>());
    }

    public void setHomeworkDone(int index) {
        this.homework.put(index, true);
    }

    double getRate(int studyDashboard) {
        long count = participant.homework().values().stream()
        .filter(v -> v == true)
        .count();
        return (double) (count * 100 / studyDashboard);
        }

}
