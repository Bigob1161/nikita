package models;

public class BulkyLaundry extends LaundryRequest {
    private final int extraUnits;

    public BulkyLaundry(int id, int customerId, int weightUnits, int mode, int priority, int extraUnits) {
        super(id, customerId, weightUnits, mode, priority);
        if (extraUnits < 1 || extraUnits > 3) {
            throw new IllegalArgumentException("extraUnits должен быть от 1 до 3");
        }
        this.extraUnits = extraUnits;
    }

    public int getExtraUnits() {
        return extraUnits;
    }

    @Override
    public int getLoadUnits() {
        return getWeightUnits() + extraUnits;
    }

    @Override
    public String getDetails() {
        return "Объёмные вещи: id=" + getId()
                + ", клиент=" + getCustomerId()
                + ", режим=" + getMode()
                + ", загрузка=" + getLoadUnits()
                + ", статус=" + (isWashed() ? "выстирана" : "ожидает");
    }
}
