package bg.sofia.uni.fmi.mjt.smartcity.hub;

//The Exception class is the superclass of checked exceptions
public class DeviceNotFoundException extends Exception {
    public DeviceNotFoundException(String message) {
        super(message);
    }
}
