package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.device.id.DeviceIdGenerator;
import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class AbstractSmartDevice implements SmartDevice {

    private final String id;
    private final String name;
    private final double powerConsumption;
    private final LocalDateTime installationDateTime;

    // protected -> this constructor is needed only in its children - SmartLamp, SmartTrafficLight and SmartCamera
    protected AbstractSmartDevice(String name, double powerConsumption, LocalDateTime installationDateTime) {
        this.name = name;
        this.powerConsumption = powerConsumption;
        this.installationDateTime = installationDateTime;
        this.id = generateId();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPowerConsumption() {
        return powerConsumption;
    }

    @Override
    public LocalDateTime getInstallationDateTime() {
        return installationDateTime;
    }

    @Override
    public abstract DeviceType getType();

    private String generateId() {
        return DeviceIdGenerator.generateId(getType(), name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractSmartDevice)) return false;
        AbstractSmartDevice that = (AbstractSmartDevice) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
