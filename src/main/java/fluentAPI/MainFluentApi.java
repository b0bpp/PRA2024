package fluentAPI;

import fluentAPI.interfaces.Title;
import fluentAPI.model.Person;
import fluentAPI.model.PersonBuilder;

import java.util.List;
import java.util.function.Function;

public class MainFluentApi {

    public static void main(String[] args) {

        //Package-private constructor is not accessible here, we must use the builder:
        //Person p = new Person("a", Title.PROF);

        // My person will be Adam
        PersonBuilder builder = new PersonBuilder();
        Person adam = builder.withName("Adam").withTitle(Title.PROF).build();
        adam.print();
        // creating friends
        Person john = builder.withName("John").withTitle(Title.PROF).build();
        john.print();
        Person carol = builder.withName("Carol").withTitle(Title.PROF).build();
        carol.print();
        // add friends
        adam.addFriend(carol).addFriend(john);
        // say hello
        adam.sayHelloToFriends();
        // defining processFriends as clearing the list
        adam.processFriends(a -> {
            a.clear();
            return a;
        }).sayHelloToFriends();

        // defining processFriends as clearing the list with no return arg
        adam.addFriend(john).addFriend(carol);
        adam.processFriendsInPlace(friends -> {
            friends.clear();
        }).sayHelloToFriends();

        Function<List<Person>, Person> chooseMyFirstFriendEver = friends -> friends.get(0);
        adam.addFriend(carol).addFriend(john)
                .chooseBestFriend(chooseMyFirstFriendEver)
                .print();
    }
}