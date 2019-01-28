package kristof.ccm.ccm2018goooglemap;

public class User {
    private String name;
    private String phoneNumber;

    public User(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public User() {
        this ("no name", "0000000000");
    }

    public String getName() { return name; }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
