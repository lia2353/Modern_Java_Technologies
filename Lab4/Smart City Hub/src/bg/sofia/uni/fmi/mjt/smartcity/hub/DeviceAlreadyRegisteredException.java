package bg.sofia.uni.fmi.mjt.smartcity.hub;

//The Exception class is the superclass of checked exceptions
public class DeviceAlreadyRegisteredException extends Exception {
    public DeviceAlreadyRegisteredException(String message) {
        super(message);
    }
}
