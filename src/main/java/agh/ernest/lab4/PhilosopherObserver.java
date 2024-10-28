package agh.ernest.lab4;

import java.util.*;

public class PhilosopherObserver {
    private final Map<Integer, List<Long>> thinkingTimes = new HashMap<>();
    private final Map<Integer, List<Long>> eatingTimes = new HashMap<>();
    private final Map<Integer, List<Long>> hungryTimes = new HashMap<>();
    private final Map<Integer, Long> lastStateChange = new HashMap<>();

    public void recordStateChange(int philosopherNumber, State newState) {
        long currentTime = System.currentTimeMillis();

        if (lastStateChange.containsKey(philosopherNumber)) {
            long elapsedTime = currentTime - lastStateChange.get(philosopherNumber);

            switch (newState) {
                case thinking -> thinkingTimes.computeIfAbsent(philosopherNumber, k -> new ArrayList<>()).add(elapsedTime);
                case eating -> eatingTimes.computeIfAbsent(philosopherNumber, k -> new ArrayList<>()).add(elapsedTime);
                case hungry -> hungryTimes.computeIfAbsent(philosopherNumber, k -> new ArrayList<>()).add(elapsedTime);
            }
        }
        lastStateChange.put(philosopherNumber, currentTime);
    }

    private double calculateMean(List<Long> times) {
        return times.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }

    private double calculateMedian(List<Long> times) {
        List<Long> sorted = times.stream().sorted().toList();
        int size = sorted.size();
        if (size == 0) return 0.0;
        if (size % 2 == 1) return sorted.get(size / 2);
        return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
    }

    public OptionalDouble getMeanThinkingTime(int philosopherNumber) {
        return thinkingTimes.containsKey(philosopherNumber) ?
                OptionalDouble.of(calculateMean(thinkingTimes.get(philosopherNumber))) : OptionalDouble.empty();
    }

    public OptionalDouble getMedianThinkingTime(int philosopherNumber) {
        return thinkingTimes.containsKey(philosopherNumber) ?
                OptionalDouble.of(calculateMedian(thinkingTimes.get(philosopherNumber))) : OptionalDouble.empty();
    }

    public OptionalDouble getMeanHungryTime(int philosopherNumber) {
        return hungryTimes.containsKey(philosopherNumber) ?
                OptionalDouble.of(calculateMean(hungryTimes.get(philosopherNumber))) : OptionalDouble.empty();
    }

    public OptionalDouble getMedianHungryTime(int philosopherNumber) {
        return hungryTimes.containsKey(philosopherNumber) ?
                OptionalDouble.of(calculateMedian(hungryTimes.get(philosopherNumber))) : OptionalDouble.empty();
    }

    public OptionalDouble getMeanEatingTime(int philosopherNumber) {
        return eatingTimes.containsKey(philosopherNumber) ?
                OptionalDouble.of(calculateMean(eatingTimes.get(philosopherNumber))) : OptionalDouble.empty();
    }

    public OptionalDouble getMedianEatingTime(int philosopherNumber) {
        return eatingTimes.containsKey(philosopherNumber) ?
                OptionalDouble.of(calculateMedian(eatingTimes.get(philosopherNumber))) : OptionalDouble.empty();
    }

    // Generate report for all philosophers
    public void generateReport() {
        System.out.println("\n--- Philosopher Activity Report ---\n");
        for (Integer philosopherNumber : thinkingTimes.keySet()) {
            System.out.println("Philosopher " + philosopherNumber + ":");
            getMeanThinkingTime(philosopherNumber).ifPresent(mean ->
                    System.out.println("  Mean thinking time: " + mean + " ms"));
            getMedianThinkingTime(philosopherNumber).ifPresent(median ->
                    System.out.println("  Median thinking time: " + median + " ms"));

            getMeanHungryTime(philosopherNumber).ifPresent(mean ->
                    System.out.println("  Mean hungry time: " + mean + " ms"));
            getMedianHungryTime(philosopherNumber).ifPresent(median ->
                    System.out.println("  Median hungry time: " + median + " ms"));

            getMeanEatingTime(philosopherNumber).ifPresent(mean ->
                    System.out.println("  Mean eating time: " + mean + " ms"));
            getMedianEatingTime(philosopherNumber).ifPresent(median ->
                    System.out.println("  Median eating time: " + median + " ms"));
            System.out.println();
        }
    }
}

