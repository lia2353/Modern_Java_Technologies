package bg.sofia.uni.fmi.mjt.smartcity;

import bg.sofia.uni.fmi.mjt.smartcity.device.*;
import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;
import bg.sofia.uni.fmi.mjt.smartcity.hub.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.smartcity.hub.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.smartcity.hub.SmartCityHub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SmartCityHub myHub = new SmartCityHub();

        SmartDevice camera1 = new SmartCamera("cam1", 2.5, LocalDateTime.of(2010, 8, 12, 15, 30));
        SmartDevice camera2 = new SmartCamera("cam2", 1, LocalDateTime.of(2019, 10, 14, 12, 45));
        SmartDevice lamp = new SmartLamp("lamp1", 5, LocalDateTime.of(2019, 10, 14, 12, 45));
        SmartDevice trLight = new SmartTrafficLight("trlight", 3, LocalDateTime.of(2018, 10, 14, 8, 5));

        try {
            myHub.register(camera1);
            myHub.register(camera2);
            myHub.register(lamp);
            myHub.register(trLight);
            myHub.register(camera2);
        } catch (DeviceAlreadyRegisteredException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("First n registered:");
        List<SmartDevice> top1 = new ArrayList<SmartDevice>(myHub.getFirstNDevicesByRegistration(3));
        for (int i = 0; i < top1.size(); ++i) {
            System.out.print(top1.get(i).getId() + ", ");
        }
        System.out.println(' ');

        try {
            myHub.unregister(lamp);
        } catch (DeviceNotFoundException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("First n by power consumption:");
        List<String> top2 = new ArrayList<String>(myHub.getTopNDevicesByPowerConsumption(3));
        for (int i = 0; i < top2.size(); ++i) {
            System.out.print(top2.get(i) + ", ");
        }
        System.out.println();

        System.out.println(myHub.getDeviceQuantityPerType(DeviceType.CAMERA));
        System.out.println(myHub.getDeviceQuantityPerType(DeviceType.LAMP));
        System.out.println(myHub.getDeviceQuantityPerType(DeviceType.TRAFFIC_LIGHT));
    }

}
