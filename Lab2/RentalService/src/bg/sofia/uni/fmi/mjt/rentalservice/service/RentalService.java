package bg.sofia.uni.fmi.mjt.rentalservice.service;

import bg.sofia.uni.fmi.mjt.rentalservice.location.Location;
import bg.sofia.uni.fmi.mjt.rentalservice.vehicle.Vehicle;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

public class RentalService implements RentalServiceAPI {

    private static final String BICYCLE_TYPE = "BICYCLE";
    private static final String CAR_TYPE = "CAR";
    private static final String SCOOTER_TYPE = "SCOOTER";
    private final Vehicle[] vehicles;

    public RentalService(Vehicle[] vehicles) {
        this.vehicles = Arrays.copyOf(vehicles, vehicles.length);
    }

    @Override
    public double rentUntil(Vehicle vehicle, LocalDateTime until) {
        if (vehicle == null || until == null || until.isBefore(LocalDateTime.now())
                || vehicle.getEndOfReservationPeriod().isAfter(LocalDateTime.now())) {
            return -1.0;
        }
        if (!isRegisteredVehicle(vehicle)) {
            return -1.0;
        }
        vehicle.setEndOfReservationPeriod(until);
        return getTripTime(until) * vehicle.getPricePerMinute();
    }

    @Override
    public Vehicle findNearestAvailableVehicleInRadius(String type, Location location, double maxDistance) {
        if (!isValidType(type) || location == null || maxDistance < 0.0) {
            return null;
        }

        double minDistance = maxDistance;
        Vehicle nearestVehicle = null;
        for (Vehicle vehicle : vehicles) {
            if (!vehicle.getType().equals(type) || vehicle.getEndOfReservationPeriod().isAfter(LocalDateTime.now())) {
                continue;
            }
            double distance = getDistanceBetween(location, vehicle.getLocation());
            if (distance <= minDistance) {
                minDistance = distance;
                nearestVehicle = vehicle;
            }
        }
        return nearestVehicle;
    }

    private boolean isRegisteredVehicle(Vehicle vehicle) {
        for (Vehicle value : vehicles) {
            if (value.equals(vehicle)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidType(String type) {
        if (type == null) {
            return false;
        }
        return type.equals(BICYCLE_TYPE) || type.equals(CAR_TYPE) || type.equals(SCOOTER_TYPE);
    }

    private double getTripTime(LocalDateTime until) {
        Duration tripDuration = Duration.between(LocalDateTime.now(), until);
        return tripDuration.toSecondsPart() > 0 ? tripDuration.toMinutesPart() + 1 : tripDuration.toMinutesPart();
    }

    private double getDistanceBetween(Location from, Location to) {
        double x = from.getX() - to.getX();
        double y = from.getY() - to.getY();
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

}
