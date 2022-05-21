package bg.sofia.uni.fmi.mjt.rentalservice.vehicle;

import bg.sofia.uni.fmi.mjt.rentalservice.location.Location;

public class Scooter extends BaseVehicle {
    private static final double PRICE_PER_MINUTE = 0.30;
    private static final String TYPE = "SCOOTER";

    public Scooter(String id, Location location) {
        super(id, location);
    }

    @Override
    public double getPricePerMinute() {
        return PRICE_PER_MINUTE;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
