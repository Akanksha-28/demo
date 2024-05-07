import java.sql.*;

class Customer {
    String customerId;
    boolean isPremium;
    boolean hasCancellationPolicy;

    public Customer(String customerId, boolean isPremium) {
        this.customerId = customerId;
        this.isPremium = isPremium;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setHasCancellationPolicy(boolean hasCancellationPolicy) {
        this.hasCancellationPolicy = hasCancellationPolicy;
    }

    public boolean hasCancellationPolicy() {
        return hasCancellationPolicy;
    }
}

class Flight {
    String departure;
    String arrival;
    String dateOfTravel;
    int availableSeats;
    int priceEconomy;
    int priceBusiness;

    public Flight(String departure, String arrival, String dateOfTravel, int availableSeats, int priceEconomy, int priceBusiness) {
        this.departure = departure;
        this.arrival = arrival;
        this.dateOfTravel = dateOfTravel;
        this.availableSeats = availableSeats;
        this.priceEconomy = priceEconomy;
        this.priceBusiness = priceBusiness;
    }

    public String getDateOfTravel() {
        return dateOfTravel;
    }

    public boolean isAvailable() {
        return availableSeats > 0;
    }

    public int getPrice(boolean isBusinessClass) {
        return isBusinessClass ? priceBusiness : priceEconomy;
    }

    public void bookFlight(int numOfSeats) {
        availableSeats -= numOfSeats;
    }

    public void cancelFlight(int numOfSeats) {
        availableSeats += numOfSeats;
    }
}

class FlightManager {
    Connection connection;

    public FlightManager(Connection connection) {
        this.connection = connection;
    }

    public void addFlight(Flight flight) {
        try {
            PreparedStatement insertFlight = connection.prepareStatement(
                    "INSERT INTO flights (departure, arrival, date_of_travel, available_seats, price_economy, price_business) " +
                            "VALUES (?, ?, ?, ?, ?, ?)");
            insertFlight.setString(1, flight.departure);
            insertFlight.setString(2, flight.arrival);
            insertFlight.setString(3, flight.dateOfTravel);
            insertFlight.setInt(4, flight.availableSeats);
            insertFlight.setInt(5, flight.priceEconomy);
            insertFlight.setInt(6, flight.priceBusiness);
            insertFlight.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Flight getFlightByDate(String dateOfTravel) {
        try {
            PreparedStatement selectFlight = connection.prepareStatement(
                    "SELECT * FROM flights WHERE date_of_travel = ? AND available_seats > 0");
            selectFlight.setString(1, dateOfTravel);
            ResultSet flightResultSet = selectFlight.executeQuery();

            if (flightResultSet.next()) {
                String departure = flightResultSet.getString("departure");
                String arrival = flightResultSet.getString("arrival");
                int availableSeats = flightResultSet.getInt("available_seats");
                int priceEconomy = flightResultSet.getInt("price_economy");
                int priceBusiness = flightResultSet.getInt("price_business");

                return new Flight(departure, arrival, dateOfTravel, availableSeats, priceEconomy, priceBusiness);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

class Airline {
   Connection connection;

    public Airline(Connection connection) {
        this.connection = connection;
    }

    public void updateAirlineBank(int amount) {
        try {
            PreparedStatement updateAirlineBank = connection.prepareStatement(
                    "UPDATE airline SET airline_bank = airline_bank + ? WHERE id = 1");
            updateAirlineBank.setInt(1, amount);
            updateAirlineBank.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getAirlineBank() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT airline_bank FROM airline WHERE id = 1");
            if (rs.next()) {
                return rs.getInt("airline_bank");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean hasEnoughFunds(int amount) {
        return getAirlineBank() >= amount;
    }
}

class PremiumCardHolder extends Customer {
    public PremiumCardHolder(String customerId) {
        super(customerId, true);
    }
}

public class AirlineApplication {
    public static void main(String[] args) {
        try {
            // Creating the in-memory H2 database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java","root","Dee@2003");

            // Create the flights table if not exists
            Statement createFlightsTable = connection.createStatement();
            createFlightsTable.execute(
                    "CREATE TABLE IF NOT EXISTS flights (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "departure VARCHAR(100), " +
                            "arrival VARCHAR(100), " +
                            "date_of_travel VARCHAR(10), " +
                            "available_seats INT, " +
                            "price_economy INT, " +
                            "price_business INT)");

            // Create the airline table if not exists
            Statement createAirlineTable = connection.createStatement();
            createAirlineTable.execute(
                    "CREATE TABLE IF NOT EXISTS airline (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "airline_bank INT)");

            // Insert sample flights into the flights table
            FlightManager flightManager = new FlightManager(connection);
            Flight flight1 = new Flight("CityA", "CityB", "2023-07-25", 100, 300, 500);
            Flight flight2 = new Flight("CityC", "CityD", "2023-07-28", 50, 400, 700);
            flightManager.addFlight(flight1);
            flightManager.addFlight(flight2);

            // Insert airline bank amount
            PreparedStatement insertAirlineBank = connection.prepareStatement(
                    "INSERT INTO airline (airline_bank) VALUES (?)");
            insertAirlineBank.setInt(1, 0); // Initial airline bank amount is 0
            insertAirlineBank.executeUpdate();

            // Sample customer and booking details
            Customer customer = new PremiumCardHolder("12345");
            String dateOfTravel = "2023-07-25";
            int numOfSeats = 2;
            boolean isBusinessClass = false;

            // Check if the flight is available
            Flight flight = flightManager.getFlightByDate(dateOfTravel);
            if (flight != null && flight.isAvailable()) {
                // Calculate total ticket price
                int totalTicketPrice = (isBusinessClass ? flight.getPrice(true) : flight.getPrice(false)) * numOfSeats;

                // Apply the cancellation policy if selected
                if (customer.hasCancellationPolicy()) {
                    // Charge the customer for 100% refund
                    int cancellationFee = totalTicketPrice;
                    if (!customer.isPremium()) {
                        Airline airline = new Airline(connection);
                        if (!airline.hasEnoughFunds(cancellationFee)) {
                            System.out.println("Refund will be granted in two weeks.");
                            connection.close();
                            return;
                        }
                        airline.updateAirlineBank(-cancellationFee);
                    }
                } else {
                    // Customer will only get a certain amount as a refund (e.g., 80%)
                    int refundAmount = totalTicketPrice * 80 / 100;
                    if (!customer.isPremium()) {
                        Airline airline = new Airline(connection);
                        if (!airline.hasEnoughFunds(refundAmount)) {
                            System.out.println("Refund will be granted in two weeks.");
                            connection.close();
                            return;
                        }
                        airline.updateAirlineBank(-refundAmount);
                    }
                }

                // Book the flight
                PreparedStatement updateFlightSeats = connection.prepareStatement(
                        "UPDATE flights SET available_seats = available_seats - ? WHERE date_of_travel = ?");
                updateFlightSeats.setInt(1, numOfSeats);
                updateFlightSeats.setString(2, dateOfTravel);
                updateFlightSeats.executeUpdate();

                // Display ticket price
                System.out.println("Total ticket price: $" + totalTicketPrice);

                // Update airline bank with the ticket price
                Airline airline = new Airline(connection);
                airline.updateAirlineBank(totalTicketPrice);
            } else {
                System.out.println("No available flights on the selected date.");
            }

            // Close the connection
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}