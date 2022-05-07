import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        House withMandatoryAtributes = new House(
                1,
                LocalDateTime.of(2000,1,20,0,0),
                new HouseAddress("5th Avenue", 22),
                Set.of(1,2,3,4,5,6,7,8,9,10)
        );
        withMandatoryAtributes.addApartment(16);
        withMandatoryAtributes.removeApartment(16);

        House withOptionalAtributes = new House(
                2,
                "IT House",
                LocalDateTime.of(2002,9,24,0,0),
                new HouseAddress("5th Avenue", 22),
                Set.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14)
        );
        House
                .getHouseInUseSince(LocalDateTime.of(1999,1,1,1,1))
                .forEach(System.out::println);
        System.out.println("House is in exploitation for " + withOptionalAtributes.getOperatingTime() + " years");

        String housesPath = "./houses.ser";

        System.out.println("Size before saving : " + House.getHouses().size());
        if (House.save(housesPath)){
            System.out.println("Houses were saved");
        }
        House.load(housesPath);
        System.out.println("Size after loading : " + House.getHouses().size());
        System.out.println("Number of houses before setting minimum apartment number: " + House.getHouses().size());
        House.setMinimumApartmentsNumber(11);
        System.out.println("Number of houses after setting minimum apartment number: " + House.getHouses().size());
    }
}
