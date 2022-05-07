import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class House implements Serializable {
    private long id;
    private String name; // nullable
    private LocalDateTime dateOfSale;
    private HouseAddress houseAddress;  // atribut złożony
    private Set<Integer> apartments; // powtażalny
    private static long minimumApartmentsNumber = 10;// klasowy
    private static Set<House> houses = new HashSet<>();//ekstensja

    //overload
    public House(long id, LocalDateTime dateOfSale, HouseAddress houseAddress, Set<Integer> apartments) {
        setId(id);
        setDateOfSale(dateOfSale);
        setHouseAddress(houseAddress);
        setApartments(apartments);
        addHouse(this);
    }

    public House(long id, String name, LocalDateTime dateOfSale, HouseAddress houseAddress, Set<Integer> apartments) {
        setId(id);
        setName(name);
        setDateOfSale(dateOfSale);
        setHouseAddress(houseAddress);
        setApartments(apartments);
        addHouse(this);
    }

    private static void addHouse(House house) throws IllegalArgumentException {
        if (house == null) {
            throw new IllegalArgumentException("House cannot be null");
        }
        boolean alreadyAdded = House.houses.stream().anyMatch(h -> h.getId() == house.getId());
        if (alreadyAdded) {
            throw new IllegalArgumentException("House with id " + house.getId() + " has already been added");
        }
        House.houses.add(house);
    }

    private static void removeHouse(House house) throws IllegalArgumentException {
        if (house == null) {
            throw new IllegalArgumentException("House cannot be null");
        }
        boolean alreadyDeleted = House.houses.stream().allMatch(h -> h.getId() != house.getId());
        if (alreadyDeleted) {
            throw new IllegalArgumentException("House with id " + house.getId() + " has already been deleted");
        }
        House.houses.remove(house);
    }


    //metoda klasowa
    public static List<House> getHouseInUseSince(LocalDateTime date) throws IllegalArgumentException {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (date.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot define date in future");
        }
        return House.houses
                .stream()
                .filter(p -> p.getDateOfSale().isAfter(date))
                .collect(Collectors.toList());
    }

// ekstensja trwala save & load
    public static boolean save(String path) {//TODO: function must take a Stream
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path))) {
            os.writeObject(houses);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void load(String path) {//TODO: function must take a Stream
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(path))) {
            Set<House> loadedHouses = (Set<House>) is.readObject();
            setHouses(loadedHouses);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void addApartment(int apNumber) throws IllegalArgumentException {
        if (apNumber <= 0) {
            throw new IllegalArgumentException("Apartment number cannot be negative or 0");
        }
        boolean alreadyExists = this.apartments.stream().anyMatch(a -> a == apNumber);
        if (alreadyExists) {
            throw new IllegalArgumentException("Apartment number you entered already exists.");
        }
        this.apartments.add(apNumber);
    }

    public void removeApartment(int apNumber) throws IllegalArgumentException {
        if (apNumber <= 0) {
            throw new IllegalArgumentException("Apartment number cannot be negative or 0");
        }
        if (this.apartments.size() <= minimumApartmentsNumber) {
            throw new IllegalArgumentException("Number of apartment must be greater than " + minimumApartmentsNumber);
        }
        boolean removed = this.apartments.remove(apNumber);
        if (!removed) {
            throw new IllegalArgumentException("Cannot remove apartment which was not added to the set");
        }
    }


    //Getters
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDateOfSale() {
        return dateOfSale;
    }

    public HouseAddress getHouseAddress() {
        return houseAddress;
    }

    public Set<Integer> getApartments() {
        return Collections.unmodifiableSet(apartments);
    }

    public static long getMinimumApartmentsNumber() {
        return minimumApartmentsNumber;
    }

    public static Set<House> getHouses() {
        return Collections.unmodifiableSet(houses);
    }

    //atribute pochodny
    public long getOperatingTime() {
        return ChronoUnit.YEARS.between(LocalDateTime.now(), this.dateOfSale);
    }

    //Setters

    public void setId(long id) throws IllegalArgumentException {
        if (id < 0) {
            throw new IllegalArgumentException("Id can not be negative value.");
        }
        boolean alreadyExists = houses.stream().anyMatch(house -> house.getId() == id);
        if (alreadyExists) {
            throw new IllegalArgumentException("Id you entered already exists.");
        }
        this.id = id;
    }

    public void setName(String name) throws IllegalArgumentException {
        if (name != null && name.trim().equals("")) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name;
    }

    public void setDateOfSale(LocalDateTime dateOfSale) throws IllegalArgumentException {
        if (dateOfSale == null) {
            throw new IllegalArgumentException("Sale date cannot be null");
        }
        if (dateOfSale.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Date of sale cannot be set to date in future");
        }
        this.dateOfSale = dateOfSale;
    }

    public void setHouseAddress(HouseAddress houseAddress) throws IllegalArgumentException {
        if (houseAddress == null) {
            throw new IllegalArgumentException("House address cannot be null.");
        }
        this.houseAddress = houseAddress;
    }

    public void setApartments(Set<Integer> apartments) throws IllegalArgumentException {
        if (apartments == null || apartments.size() <= 0) {
            throw new IllegalArgumentException("Apartments set cannot be null");
        }
        if (apartments.size() < minimumApartmentsNumber) {
            throw new IllegalArgumentException("In House must be more than " + minimumApartmentsNumber + " apartments.");
        }
        boolean isNegative = apartments.stream().anyMatch(a -> a <= 0);
        if (isNegative) {
            throw new IllegalArgumentException("Apartments set cannot have non positive values");
        }
        this.apartments = new HashSet<>(apartments);
    }

    public static void setHouses(Set<House> houses) throws IllegalArgumentException {
        if (houses == null || houses.size() <= 0) {
            throw new IllegalArgumentException("Houses list cannot be null (or empty).");
        }

        House.houses = new HashSet<>(houses);
    }

    public static void setMinimumApartmentsNumber(long minimumApartmentsNumber) {
        if (minimumApartmentsNumber <= 0) {
            throw new IllegalArgumentException("Minimum apartments number must be greater than 0");
        }
        Set<House> toDelete = House.houses
                .stream()
                .filter(house -> house.getApartments().size() < minimumApartmentsNumber)
                .collect(Collectors.toSet());

        for (House house: toDelete) {
            House.removeHouse(house);
        }
        House.minimumApartmentsNumber = minimumApartmentsNumber;
    }

    @Override
    public String toString() {
        return "House{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateOfSale=" + dateOfSale.toLocalDate().toString() +
                ", houseAddress=" + houseAddress +
                ", apartments=" + apartments +
                '}';
    }
}
