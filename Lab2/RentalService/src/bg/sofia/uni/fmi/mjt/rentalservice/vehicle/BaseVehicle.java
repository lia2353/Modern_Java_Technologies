package bg.sofia.uni.fmi.mjt.rentalservice.vehicle;

import bg.sofia.uni.fmi.mjt.rentalservice.location.Location;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class BaseVehicle implements Vehicle {
    private String id;
    private Location location;
    private LocalDateTime endOfReservationPeriod;

    public BaseVehicle(String id, Location location) {
        this.id = id;
        this.location = location == null ? new Location(0,0) : location;;
        this.endOfReservationPeriod = LocalDateTime.now();
    }

    public abstract double getPricePerMinute();

    public abstract String getType();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public LocalDateTime getEndOfReservationPeriod() {
        if(endOfReservationPeriod != null && endOfReservationPeriod.isAfter(LocalDateTime.now())) {
            return endOfReservationPeriod;
        }
        return LocalDateTime.now();
    }

    @Override
    public void setEndOfReservationPeriod(LocalDateTime until) {
        if(until != null) {
            this.endOfReservationPeriod = until;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseVehicle that = (BaseVehicle) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
