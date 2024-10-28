package agh.ernest.lab4;

public enum State {
    thinking,
    eating,
    hungry;

    public State next() {
        return switch (this) {
            case thinking -> hungry;
            case eating -> thinking;
            case hungry -> eating;
        };
    }
}
