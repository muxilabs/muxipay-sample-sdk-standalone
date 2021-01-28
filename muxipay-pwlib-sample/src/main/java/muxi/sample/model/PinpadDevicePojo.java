package muxi.sample.model;

public class PinpadDevicePojo {
    private String deviceName;
    private String deviceAdress;

    public PinpadDevicePojo(String deviceName, String deviceAdress) {
        this.deviceName = deviceName;
        this.deviceAdress = deviceAdress;
    }

    public String getDeviceName() {
        return deviceName;
    }


    public String getDeviceAdress() {
        return deviceAdress;
    }
}
