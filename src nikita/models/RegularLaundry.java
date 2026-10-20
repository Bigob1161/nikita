package models;

public class RegularLaundry extends LaundryRequest {

    public RegularLaundry(int id, int customerId, int weightUnits, int mode, int priority) {
        super(id, customerId, weightUnits, mode, priority);
    }

    @Override
    public int getLoadUnits() {
        return getWeightUnits();
    }

    @Override
    public String getDetails() {
        return "Обычные вещи: id=" + getId()
                + ", клиент=" + getCustomerId()
                + ", режим=" + getMode()
                + ", загрузка=" + getLoadUnits()
                + ", статус=" + (isWashed() ? "выстирана" : "ожидает");
    }
}
