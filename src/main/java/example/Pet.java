package example;

//DOC
public abstract class Pet {

    private String name;
    private int age;

    public Pet(String name, int age) {
        this.name = name;
        this.age = age;
    }

    /**
     * Represents a person who's playing with their pet
     * @param person: the person who's playing with the pet
     */
    public abstract void playWithPet(Person person);

    //DOC
    public abstract void sleep();

    //DOC
    public abstract void eat();

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
