package models;

import interfaces.Loadable;

public abstract class LaundryRequest implements Loadable {
    private final int id;
    private final int customerId;
    private final int weightUnits;
    private final int mode;
    private final int priority;
    private boolean washed;

    public LaundryRequest(int id, int customerId, int weightUnits, int mode, int priority) {
        if (id <= 0) {
            throw new IllegalArgumentException("id должен быть положительным");
        }
        if (customerId <= 0) {
            throw new IllegalArgumentException("customerId должен быть положительным");
        }
        if (weightUnits < 1 || weightUnits > 12) {
            throw new IllegalArgumentException("weightUnits должен быть от 1 до 12");
        }
        if (mode != 1 && mode != 2) {
            throw new IllegalArgumentException("mode должен быть 1 или 2");
        }
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("priority должен быть от 1 до 5");
        }
        this.id = id;
        this.customerId = customerId;
        this.weightUnits = weightUnits;
        this.mode = mode;
        this.priority = priority;
        this.washed = false;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getWeightUnits() {
        return weightUnits;
    }

    public int getMode() {
        return mode;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isWashed() {
        return washed;
    }

    public void markWashed() {
        washed = true;
    }

    public abstract String getDetails();
}
