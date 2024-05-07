import java.sql.*;
import java.util.*;
import java.util.Random;

class Customer {
    String customerId;

    public Customer(String customerId) {
        this.customerId = customerId;
    }

    public int CancellationPolicy(int cost) {
        int t_price = cost + 3000;
        return t_price;
    }
}

class FlightAvailable {
    String departure;
    String arrival;

    public FlightAvailable(String departure, String arrival) {
        this.departure = departure;
        this.arrival = arrival;
    }
}

class FlightDate extends FlightAvailable {
    String dateOfTravel;

    public FlightDate(String departure, String arrival, String dateOfTravel) {
        super(departure, arrival);
        this.dateOfTravel = dateOfTravel;
    }
}

class FlightBook extends FlightDate {
    char type;
    int seats;

    public FlightBook(String departure, String arrival, String dateOfTravel, char type, int seats) {
        super(departure, arrival, dateOfTravel);
        this.type = type;
        this.seats = seats;
    }
}

class FlightManager {
    Connection connection;
    int cost = 0;
    int fid = 0;
    

    public FlightManager(Connection connection) {
        this.connection = connection;
    }

    public boolean checkFlight(FlightAvailable flight) {
        boolean flag = false;
        try {
            PreparedStatement check_Flight = connection.prepareStatement("Select depature, arrival from flights where depature=? and arrival=?");
            check_Flight.setString(1, flight.departure);
            check_Flight.setString(2, flight.arrival);

            ResultSet flightResultSet1 = check_Flight.executeQuery();

            if (flightResultSet1.next()) {
                System.out.println("Flight available");
                flag = true;
            } else {
                System.out.println("Flight not available");
                flag = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flag;
    }

    public boolean getFlightByDate(FlightDate flightdate) {
        boolean flag = false;
        try {
            PreparedStatement check_Flight1 = connection.prepareStatement("Select date from flights where date=? and depature=? and arrival=?");
            check_Flight1.setString(1, flightdate.dateOfTravel);
            check_Flight1.setString(2, flightdate.departure);
            check_Flight1.setString(3, flightdate.arrival);

            ResultSet flightResultSet2 = check_Flight1.executeQuery();

            if (flightResultSet2.next()) {
                System.out.println("Flight available on this date");
                flag = true;
            } else {
                System.out.println("Flight not available on this date");
                flag = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flag;
    }

    public boolean getFlightBySeats(FlightBook flight1) {
        boolean flag = false;
	int seats1=0;
	char ch7;
	Scanner s = new Scanner(System.in);
        try {
            if (flight1.type == 'e') {
                PreparedStatement selectFlight = connection.prepareStatement("SELECT e_price, f_id FROM flights WHERE date= ? AND e_seats >= ? AND depature=? AND arrival=?");
                selectFlight.setString(1, flight1.dateOfTravel);
                selectFlight.setInt(2, flight1.seats);
                selectFlight.setString(3, flight1.departure);
                selectFlight.setString(4, flight1.arrival);

                ResultSet flightResultSet = selectFlight.executeQuery();

                if (flightResultSet.next()) {
                    flag = true;
                    int price1 = flightResultSet.getInt("e_price");
                    fid = flightResultSet.getInt("f_id");
                    System.out.println("Seats available");
                    System.out.println("The price of one ticket is " + price1);
                    cost = price1 * flight1.seats;
                    System.out.println("The price for " + flight1.seats + " tickets is " + cost);

                } else {
		    System.out.println("Seats not available");
		    PreparedStatement selectFlight1 =  connection.prepareStatement("SELECT e_seats,e_price, f_id from flights WHERE date= ? AND e_seats < ? AND depature=? AND arrival=?");
		    selectFlight1.setString(1, flight1.dateOfTravel);
                    selectFlight1.setInt(2, flight1.seats);
                    selectFlight1.setString(3, flight1.departure);
                    selectFlight1.setString(4, flight1.arrival);

                    ResultSet flightResultSet1 = selectFlight1.executeQuery();
			if(flightResultSet1.next()){
				flag=true;
                    		seats1 = flightResultSet1.getInt("e_seats");
		    		System.out.println("Available vacant seats are:"+seats1);
		    		System.out.println("Do you want to book the"+seats1+"(y or n)");
				ch7=s.next().charAt(0);
				if(ch7=='y'){
					int price1 = flightResultSet1.getInt("e_price");
                    			fid = flightResultSet1.getInt("f_id");
                    			System.out.println("The price of one ticket is " + price1);
					flight1.seats=seats1;
                    			cost = price1 * flight1.seats;
                    			System.out.println("The price for " + flight1.seats + " tickets is " + cost);
				}
		    }
                }
            } else if (flight1.type == 'b') {
                PreparedStatement selectFlight = connection.prepareStatement("SELECT b_price, f_id FROM flights WHERE date= ? AND b_seats >= ? AND depature=? AND arrival=?");
                selectFlight.setString(1, flight1.dateOfTravel);
                selectFlight.setInt(2, flight1.seats);
                selectFlight.setString(3, flight1.departure);
                selectFlight.setString(4, flight1.arrival);

                ResultSet flightResultSet = selectFlight.executeQuery();

                if (flightResultSet.next()) {
                    flag = true;
                    int price1 = flightResultSet.getInt("b_price");
                    fid = flightResultSet.getInt("f_id");
                    System.out.println("Seats available");
                    System.out.println("The price of one ticket is " + price1);
                    cost = price1 * flight1.seats;
                    System.out.println("The price for " + seats1 + " tickets is " + cost);
		    

                } else {
		    System.out.println("Seats not available");
                    PreparedStatement selectFlight1 =  connection.prepareStatement("SELECT b_seats,b_price, f_id from flights WHERE date= ? AND b_seats < ? AND depature=? AND arrival=?");
		    selectFlight1.setString(1, flight1.dateOfTravel);
                    selectFlight1.setInt(2, flight1.seats);
                    selectFlight1.setString(3, flight1.departure);
                    selectFlight1.setString(4, flight1.arrival);

                    ResultSet flightResultSet1 = selectFlight1.executeQuery();

		    if(flightResultSet1.next()){
			
                    	seats1 = flightResultSet1.getInt("b_seats");
		    	System.out.println("Available vacant seats are:"+seats1);
		    	System.out.println("Do you want to book the "+seats1+" seats(y or n)");
			ch7=s.next().charAt(0);
			if(ch7=='y'){
                                        flag=true;
					flight1.seats=seats1;
					int price1 = flightResultSet1.getInt("b_price");
                    			fid = flightResultSet1.getInt("f_id");
                    			System.out.println("The price of one ticket is " + price1);
                    			cost = price1 * flight1.seats;
                    			System.out.println("The price for " + seats1 + " tickets is " + cost);
			}
		    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }
}

class Airline {
    Connection connection;

    public Airline(Connection connection) {
        this.connection = connection;
    }

    public void updateAirlineBank(int amount) {
        try {
            PreparedStatement updateAirlineBank = connection.prepareStatement("UPDATE airline_bank set amt= amt+? where id=1");
            updateAirlineBank.setInt(1, amount);
            updateAirlineBank.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getAirlineBank() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT amt FROM airline_bank WHERE id =1");
            if (rs.next()) {
                return rs.getInt("amt");
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
    Connection connection;
    String cust_name;

    public PremiumCardHolder(Connection connection, String customerId) {
        super(customerId);
        this.connection = connection;
    }

    public boolean isPremium(String customerId) {
        boolean flag = false;

        try {

            PreparedStatement checkPremium = connection.prepareStatement("SELECT name FROM p_card WHERE c_id=?");
            checkPremium.setString(1, customerId);
            ResultSet FlightResultSet2 = checkPremium.executeQuery();
            if (FlightResultSet2.next()) {
                flag = true;
                cust_name = FlightResultSet2.getString("name");
                System.out.println("Welcome " + cust_name);
            } else {
                System.out.println("Not a premium holder");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flag;
    }
}
class VipCardHolder extends Customer{
	Connection connection;
	String v_name;
	public VipCardHolder(Connection connection, String customerId) {
        	super(customerId);
        	this.connection = connection;
    	}
	public boolean isVip(String customerId){
		boolean flag=false;
		try{
			PreparedStatement checkVip = connection.prepareStatement("SELECT name from vip where c_id=?");
			checkVip.setString(1,customerId);
			ResultSet VipResult = checkVip.executeQuery();
			if(VipResult.next()){
				flag=true;
				v_name = VipResult.getString("name");
				System.out.println("Welcome " + v_name);	
			}
			else{
				System.out.println("Not a vip card holder");
			}
		}
		catch (SQLException e) {
            		e.printStackTrace();
        	}

        	return flag;
	}
}

public class AirlineApplication {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String c_name=" ";
        String dp;
        String ar;
        String date;
        char t;
        int s;
        int t_price=0;
        String cancel = "no";
        char ch4;

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java","root","Akanksha28@");
            FlightManager flightManager = new FlightManager(connection);
            Airline airline = new Airline(connection);

            do {
                System.out.println("Hello!!");
                System.out.println("1.Booking 2.cancellation");
                System.out.println("Enter your choice");
                int ch5 = sc.nextInt();

                switch (ch5) {
                    case 1:
                        System.out.println("Enter Departure and Arrival city:");
                        dp = sc.next();
                        ar = sc.next();

                        FlightAvailable flight1 = new FlightAvailable(dp, ar);

                        if (flightManager.checkFlight(flight1) == true) {
                            System.out.println("Enter date of departure");
                            date = sc.next();

                            FlightDate fd = new FlightDate(dp, ar, date);

                            if (flightManager.getFlightByDate(fd) == true) {
                                System.out.println("Enter Economy(e) or Business(b) class");
                                t = sc.next().charAt(0);
                                System.out.println("Enter no of seats");
                                s = sc.nextInt();

                                FlightBook flight2 = new FlightBook(dp, ar, date, t, s);

                                if (flightManager.getFlightBySeats(flight2) == true) {
                                    System.out.println("Do you want to book a flight Yes(y) or No(n)?");
                                    char ch = sc.next().charAt(0);
                                    if (ch == 'y') {
					System.out.println("Are you a vip customer(y or n)?");
					char ch8 = sc.next().charAt(0);
				    	if(ch8 == 'y'){
						System.out.println("Enter your Customer ID");
						String id1 = sc.next();
						VipCardHolder cust = new VipCardHolder(connection, id1);
						if(cust.isVip(id1)==true){
							c_name = cust.v_name;
							t_price = 0;
							cancel = "reserved";
						}
					}
					else{
                                        System.out.println("Are you a premium customer(y or n)?");
                                        char ch1 = sc.next().charAt(0);

                                        if (ch1 == 'y') {
                                            System.out.println("Enter your Customer ID");
                                            String id = sc.next();
                                            PremiumCardHolder customer = new PremiumCardHolder(connection, id);

                                            if (customer.isPremium(id) == true) {
                                                c_name = customer.cust_name;
                                                t_price = flightManager.cost;
                                                cancel = "premium";
                                            } else {
                                                System.out.println("Enter your name");
                                                c_name = sc.next();
                                                System.out.println(c_name);
                                                System.out.println("Do you want to add cancellation policy(y or n)?  An extra amount of 3000 will be added to your total price");
                                                char ch2 = sc.next().charAt(0);
                                                if (ch2 == 'y') {
                                                    t_price = customer.CancellationPolicy(flightManager.cost);
                                                    cancel = "yes";
                                                } else {
                                                    t_price = flightManager.cost;
                                                    cancel = "no";
                                                }
                                            }
                                        } else {
                                            System.out.println("Enter your name");
                                            c_name = sc.next();
                                            System.out.println("You want cancellation policy(y or n)");
                                            char ch2 = sc.next().charAt(0);
                                            if (ch2 == 'y') {
                                                t_price = flightManager.cost + 3000;
                                                cancel = "yes";
                                            } else {
                                                t_price = flightManager.cost;
                                                cancel = "no";
                                            }
                                        }
					}
                                        Statement createTicketTable = connection.createStatement();
                                        int c = 1;
                                        createTicketTable.execute("CREATE table IF NOT EXISTS tickets(ticket_id int UNIQUE AUTO_INCREMENT,name varchar(30),departure varchar(20),arrival varchar(20),date varchar(10),seats int,type varchar(30),cancellation varchar(20),t_price int,f_id int)");

                                        PreparedStatement insertTicket = connection.prepareStatement("INSERT INTO tickets(name,departure,arrival,date,seats,type,cancellation,t_price,f_id) VALUES(?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
                                        
                 
                                        insertTicket.setString(1, c_name);
                                        insertTicket.setString(2, flight2.departure);
                                        insertTicket.setString(3, flight2.arrival);
                                        insertTicket.setString(4, flight2.dateOfTravel);
                                        insertTicket.setInt(5, flight2.seats);
                                        char flightType = flight2.type;
                                        insertTicket.setString(6, String.valueOf(flightType));
                                        insertTicket.setString(7, cancel);
                                        insertTicket.setInt(8, t_price);
                                        insertTicket.setInt(9, flightManager.fid);
                                        insertTicket.executeUpdate();
					
					
					

                                        if (flight2.type == 'e') {
                                            PreparedStatement updateFlightSeats = connection.prepareStatement(
                                                "UPDATE flights SET e_seats = e_seats - ? WHERE f_id=? ");
                                            updateFlightSeats.setInt(1, flight2.seats);
                                            updateFlightSeats.setInt(2, flightManager.fid);
                                            updateFlightSeats.executeUpdate();
                                        } else if (flight2.type == 'b') {
                                            PreparedStatement updateFlightSeats = connection.prepareStatement(
                                                "UPDATE flights SET b_seats = b_seats - ? WHERE f_id=? ");
                                            updateFlightSeats.setInt(1, flight2.seats);
                                            updateFlightSeats.setInt(2, flightManager.fid);
                                            updateFlightSeats.executeUpdate();
                                        }
					
                                        airline.updateAirlineBank(t_price);
					
					ResultSet generatedKeys = insertTicket.getGeneratedKeys();
                			if (generatedKeys.next()) {
                    				int ticketId = generatedKeys.getInt(1);
                    				System.out.println("Your ticket id is " + ticketId);
                                       
                                        	System.out.println("Total ticket price:" + t_price);

                                        	System.out.println("========================================================");
                                        	System.out.println("|             AIRLINE TICKET                           |");
                                        	System.out.println("========================================================");
                                        	System.out.println("| Ticket ID: " + ticketId + "                      |");
                                        	System.out.println("| Customer Name: " + c_name + "                        |");
                                        	System.out.println("| Departure: " + flight2.departure + "                 |");
                                        	System.out.println("| Arrival: " + flight2.arrival + "                     |");
                                        	System.out.println("| Date of Travel: " + flight2.dateOfTravel + "         |");
						System.out.println("| Seats Booked: " + flight2.seats + "                  |");
                                        	System.out.println("| Flight Type: " + flightType + "                      |");
                                        	System.out.println("| Cancellation Policy: " + cancel + "                  |");
                                        	System.out.println("| Total Price: " + t_price + "                         |");
                                        	System.out.println("========================================================");
					}
                                    }
                                }
                            }
                        }
                        break;

                    case 2:
                        System.out.println("Enter the ticketid:");
                        int t_id = sc.nextInt();
                        PreparedStatement check_Ticket = connection.prepareStatement(
                            "Select cancellation,t_price from tickets where ticket_id=?");
                        check_Ticket.setInt(1, t_id);
                        ResultSet finalset = check_Ticket.executeQuery();

                        if (finalset.next()) {
                            String cancellation = finalset.getString("cancellation").trim();
                            int pricecancel = finalset.getInt("t_price");
                            int totalpricecancel;

                            if ("yes".equalsIgnoreCase(cancellation)) {
                                totalpricecancel = pricecancel - 3000;
                            } else if ("premium".equalsIgnoreCase(cancellation)) {
                                totalpricecancel = pricecancel;
                            } else if("reserved".equalsIgnoreCase(cancellation)){
 				totalpricecancel = pricecancel;
			    } else {
                                totalpricecancel = pricecancel * 30 / 100;
                            }

                            if (airline.hasEnoughFunds(totalpricecancel)) {
                                airline.updateAirlineBank(-totalpricecancel);
				if("reserved".equalsIgnoreCase(cancellation)){
 					System.out.println("Ticket cancelled");
			    	}
				else{
                                	System.out.println("Refund Granted");
				}
                                PreparedStatement getDetails = connection.prepareStatement(
                                    "Select seats,type,f_id from tickets where ticket_id = ?");
                                getDetails.setInt(1, t_id);
                                ResultSet rs1 = getDetails.executeQuery();

                                if (rs1.next()) {
                                    int s1 = rs1.getInt("seats");
                                    char type1 = rs1.getString("type").charAt(0);
                                    int id1 = rs1.getInt("f_id");

                                    if (type1 == 'e') {
                                        PreparedStatement updateFlight = connection.prepareStatement(
                                            "UPDATE flights SET e_seats = e_seats + ? WHERE f_id=?");
                                        updateFlight.setInt(1, s1);
                                        updateFlight.setInt(2, id1);
                                        updateFlight.executeUpdate();
                                    } else if (type1 == 'b') {
                                        PreparedStatement updateFlight = connection.prepareStatement(
                                            "UPDATE flights SET b_seats = b_seats + ? WHERE f_id=?");
                                        updateFlight.setInt(1, s1);
                                        updateFlight.setInt(2, id1);
                                        updateFlight.executeUpdate();
                                    }
                                }

                                PreparedStatement deleteTicket = connection.prepareStatement(
                                    "DELETE FROM tickets WHERE ticket_id=?");
                                deleteTicket.setInt(1, t_id);
                                deleteTicket.executeUpdate();
                            } else {
                                System.out.println("Refund will be given shortly");
                            }
                        }
                        break;

                    default:
                        System.out.println("Enter a correct choice");
                        break;
                }

                System.out.println("Do you want to continue(y or n)");
                ch4 = sc.next().charAt(0);
            } while (ch4 == 'y');

            System.out.println("Thank you");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}