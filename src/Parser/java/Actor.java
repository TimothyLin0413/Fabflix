package Parser.java;

public class Actor {

    private final String name;

    private final int birthYear;

    public String getName() {
        return name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public Actor(String name, int birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    public String toString() {
        return "Name:" + getName() + ", " +
                "Year:" + getBirthYear() + ".";
    }
}
