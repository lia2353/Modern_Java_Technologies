package bg.sofia.uni.fmi.mjt.smartcity.hub;

import bg.sofia.uni.fmi.mjt.smartcity.device.SmartDevice;
import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class SmartCityHub {
    private final Map<String, SmartDevice> deviceRegistry;
    private final Map<DeviceType, Integer> quantityPerDeviceType;

    public SmartCityHub() {
        this.deviceRegistry = new LinkedHashMap<>();
        this.quantityPerDeviceType = new EnumMap<>(DeviceType.class);
    }

    /**
     * Adds a @device to the SmartCityHub.
     *
     * @throws IllegalArgumentException         in case @device is null.
     * @throws DeviceAlreadyRegisteredException in case the @device is already registered.
     */
    public void register(SmartDevice device) throws DeviceAlreadyRegisteredException {
        if (device == null) {
            throw new IllegalArgumentException("Device is null"); //run-time exception
        }
        if (deviceRegistry.containsKey(device.getId())) {
            throw new DeviceAlreadyRegisteredException("Device is already registered");
        }

        deviceRegistry.put(device.getId(), device);
        incrementQuantity(device.getType());
    }

    /**
     * Removes the @device from the SmartCityHub.
     *
     * @throws IllegalArgumentException in case null is passed.
     * @throws DeviceNotFoundException  in case the @device is not found.
     */
    public void unregister(SmartDevice device) throws DeviceNotFoundException {
        if (device == null) {
            throw new IllegalArgumentException("Device is null"); //run-time exception
        }
        if (!deviceRegistry.containsKey(device.getId())) {
            throw new DeviceNotFoundException("Device is not found in the registered devices");
        }

        deviceRegistry.remove(device.getId());
        decrementQuantity(device.getType());
    }

    /**
     * Returns a SmartDevice with an ID @id.
     *
     * @throws IllegalArgumentException in case @id is null.
     * @throws DeviceNotFoundException  in case device with ID @id is not found.
     */
    public SmartDevice getDeviceById(String id) throws DeviceNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }
        if (!deviceRegistry.containsKey(id)) {
            throw new DeviceNotFoundException("Device with given id is not found in the registered devices");
        }

        return deviceRegistry.get(id);
    }

    /**
     * Returns the total number of devices with type @type registered in SmartCityHub.
     *
     * @throws IllegalArgumentException in case @type is null.
     */
    public int getDeviceQuantityPerType(DeviceType type) {
        if (type == null) {
            throw new IllegalArgumentException("Type is null");
        }

        return getQuantity(type);
    }

    /**
     * Returns a collection of IDs of the top @n devices which consumed
     * the most power from the time of their installation until now.
     * <p>
     * The total power consumption of a device is calculated by the hours elapsed
     * between the two LocalDateTime-s: the installation time and the current time (now)
     * multiplied by the stated nominal hourly power consumption of the device.
     * <p>
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<String> getTopNDevicesByPowerConsumption(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative number of devices");
        }
        if (n > deviceRegistry.size()) {
            n = deviceRegistry.size();
        }

        List<String> list = new ArrayList<>(deviceRegistry.keySet());
        Collections.sort(list, new Comparator<String>() {
            public int compare(String idFirst, String idSecond) {
                return compareDevicePowerConsumption(deviceRegistry.get(idFirst), deviceRegistry.get(idSecond));
            }
        });
        return new ArrayList<>(list).subList(0, n);
    }

    private int compareDevicePowerConsumption(SmartDevice first, SmartDevice second) {
        LocalDateTime currentTime = LocalDateTime.now();

        double hoursOfWorkFirst = Duration.between(first.getInstallationDateTime(), currentTime).toHours();
        double hoursOfWorkSecond = Duration.between(second.getInstallationDateTime(), currentTime).toHours();

        double totalPowerConsumptionFirst = hoursOfWorkFirst * first.getPowerConsumption();
        double totalPowerConsumptionSecond = hoursOfWorkSecond * second.getPowerConsumption();

        return Double.compare(totalPowerConsumptionFirst, totalPowerConsumptionSecond);
    }

    /**
     * Returns a collection of the first @n registered devices, i.e the first @n that were added
     * in the SmartCityHub (registration != installation).
     * <p>
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<SmartDevice> getFirstNDevicesByRegistration(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative number of devices");
        }
        if (n >= deviceRegistry.size()) {
            return deviceRegistry.values();
        }

        return new ArrayList<>(deviceRegistry.values()).subList(0, n);
    }

    private void incrementQuantity(DeviceType deviceType) {
        int currentQuantity = 0;
        if (quantityPerDeviceType.containsKey(deviceType)) {
            currentQuantity = quantityPerDeviceType.get(deviceType);
        }
        quantityPerDeviceType.put(deviceType, currentQuantity + 1);
    }

    private void decrementQuantity(DeviceType deviceType) {
        int currentQuantity = quantityPerDeviceType.get(deviceType);
        quantityPerDeviceType.put(deviceType, currentQuantity - 1);
    }

    private int getQuantity(DeviceType deviceType) {
        int currentQuantity = 0;
        if (quantityPerDeviceType.containsKey(deviceType)) {
            currentQuantity = quantityPerDeviceType.get(deviceType);
        }

        return currentQuantity;
    }
}
