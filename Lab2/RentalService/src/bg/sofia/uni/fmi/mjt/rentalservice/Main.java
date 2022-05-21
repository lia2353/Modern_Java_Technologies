package bg.sofia.uni.fmi.mjt.rentalservice;

import bg.sofia.uni.fmi.mjt.rentalservice.location.Location;
import bg.sofia.uni.fmi.mjt.rentalservice.service.RentalService;
import bg.sofia.uni.fmi.mjt.rentalservice.service.RentalServiceAPI;
import bg.sofia.uni.fmi.mjt.rentalservice.vehicle.Bicycle;
import bg.sofia.uni.fmi.mjt.rentalservice.vehicle.Car;
import bg.sofia.uni.fmi.mjt.rentalservice.vehicle.Scooter;
import bg.sofia.uni.fmi.mjt.rentalservice.vehicle.Vehicle;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Vehicle v1 = new Car("red-car", new Location(-2, -3));
        Vehicle v2 = new Car("blue-car", new Location(1, 1));
        Vehicle v3 = new Bicycle("red-bicycle", new Location(0, 2));
        Vehicle v4 = new Scooter("yellow-scooter", new Location(4, 3));
        Vehicle v5 = new Car("white-car", new Location(-1, 1));

        System.out.println("Vehicles:");
        Vehicle[] vehicles = {v1, v2, v3, v4, v5};
        for (Vehicle vehicle : vehicles) {
            System.out.println(vehicle.getId() + " " + vehicle.getType());
        }

        RentalServiceAPI rentalService = new RentalService(vehicles);


        System.out.println("Rent vehicle " + v1.getId() + ". Price: " + rentalService.rentUntil(v1, LocalDateTime.now().plusMinutes(56)));
        System.out.println("Rent vehicle " + v1.getId() + ". Price: " + rentalService.rentUntil(v1, LocalDateTime.now().plusMinutes(30)));

        System.out.println("Rent null. Price: " + rentalService.rentUntil(null, LocalDateTime.now().plusMinutes(30)));

        System.out.println("Rent vehicle " + v4.getId() + ". Price: " + rentalService.rentUntil(v4, LocalDateTime.now().plusSeconds(4)));
        //Pause for 4 seconds
        Thread.sleep(4000);
        System.out.println("Rent vehicle " + v4.getId() + ". Price: " + rentalService.rentUntil(v4, LocalDateTime.now().plusSeconds(678)));

        Vehicle vehicle = rentalService.findNearestAvailableVehicleInRadius("CAR", new Location(0, 0), 5);
        System.out.println("Nearest : " + (vehicle != null ? vehicle.getId() : "No such vehicle available"));
        System.out.println("Rent vehicle " + vehicle.getId() + ". Price: " + rentalService.rentUntil(vehicle, LocalDateTime.now().plusMinutes(45)));
        vehicle = rentalService.findNearestAvailableVehicleInRadius("CAR", new Location(0, 0), 5);
        System.out.println("Nearest : " + (vehicle != null ? vehicle.getId() : "No such vehicle available"));

        vehicle = rentalService.findNearestAvailableVehicleInRadius("BICYCLE", new Location(0, 0), 1);
        System.out.println("Nearest : " + (vehicle != null ? vehicle.getId() : "No such vehicle available"));
        vehicle = rentalService.findNearestAvailableVehicleInRadius("BICYCLE", new Location(0, 0), 2);
        System.out.println("Nearest : " + (vehicle != null ? vehicle.getId() : "No such vehicle available"));
    }

}
