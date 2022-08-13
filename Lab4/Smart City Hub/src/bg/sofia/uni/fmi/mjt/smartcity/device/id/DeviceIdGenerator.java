package bg.sofia.uni.fmi.mjt.smartcity.device.id;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.util.EnumMap;
import java.util.Map;

public class DeviceIdGenerator {

    private static final Map<DeviceType, Integer> ID_NUMBERS_PER_DEVICE_TYPE = new EnumMap<>(DeviceType.class);
    // final -> We cannot reassign the map i.e. idNumbersPerDeviceType = new EnumMap<>(DeviceType.class);
    // static -> shared field; If not static, for every created smart device the idNumbersPerDeviceType will be created again

    private static final String SEPARATOR = "-";

    public static String generateId(DeviceType type, String name) {
        if (!ID_NUMBERS_PER_DEVICE_TYPE.containsKey(type)) {
            ID_NUMBERS_PER_DEVICE_TYPE.put(type, 0);
        }

        Integer currentIdNumber = ID_NUMBERS_PER_DEVICE_TYPE.get(type);
        ID_NUMBERS_PER_DEVICE_TYPE.put(type, currentIdNumber + 1);

        // deviceId = <short name of device type>-<device name>-<unique number per device type starts from 0>
        return type.getShortName() + SEPARATOR + name + SEPARATOR + currentIdNumber;
    }

}
