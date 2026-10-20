package logic;

import models.LaundryRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LaundryQueue<T extends LaundryRequest> {
    private List<T> requests;
    private int capacity;

    public LaundryQueue(int capacity) {
        if (capacity < 1 || capacity > 20) {
            throw new IllegalArgumentException("capacity должна быть от 1 до 20");
        }
        this.requests = new ArrayList<>();
        this.capacity = capacity;
    }

    public boolean add(T request) {
        if (request == null || request.isWashed()) {
            return false;
        }
        boolean exists = requests.stream().anyMatch(r -> r.getId() == request.getId());
        if (exists) {
            return false;
        }
        requests.add(request);
        return true;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean setCapacity(int capacity) {
        if (capacity < 1 || capacity > 20) {
            return false;
        }
        this.capacity = capacity;
        return true;
    }

    public Stream<T> getAnalyticsStream() {
        return new ArrayList<>(requests).stream();
    }

    public List<T> washBatch(Predicate<T> condition, Comparator<T> order, Consumer<T> action) {
        List<T> pending = requests.stream()
                .filter(r -> !r.isWashed())
                .sorted(order)
                .collect(Collectors.toList());
        List<T> accepted = new ArrayList<>();
        int load = 0;
        int mode = 0;
        for (T request : pending) {
            boolean fits = load + request.getLoadUnits() <= capacity;
            boolean compatible = mode == 0 || request.getMode() == mode;
            if (fits && compatible && condition.test(request)) {
                load += request.getLoadUnits();
                if (mode == 0) {
                    mode = request.getMode();
                }
                action.accept(request);
                accepted.add(request);
            }
        }
        return accepted;
    }
}
