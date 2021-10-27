package application;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class App {

    public static void main( String[] args ) throws SQLException {

        Statement statement=null;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection connection = getConnection()){

                System.out.println("Connection to Store DB succesfull!");

                statement = connection.createStatement();

                String serialNumb="";
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter the serial number of the aircraft");
                serialNumb=scanner.nextLine();

                PreparedStatement preparedStatement = connection.prepareStatement("SELECT airport.airplane.name, " +
                        "airport.airplane.serialNumber, airport.route.departure, " +
                        "airport.route.arrival, airport.crew.name, airport.employee.name, " +
                        "airport.employee.surname\n" +
                        "from airport.airplane, airport.airplaneroute, airport.route, " +
                        "airport.airplanecrew, airport.crew, airport.employee, airport.crewemployee\n" +
                        "where airport.airplane.serialNumber = ? and " +
                        "airport.airplane.airplaneId = airport.airplaneroute.airplaneId and " +
                        "airport.route.routeId = airport.airplaneroute.routeId and " +
                        "airport.airplane.airplaneId = airport.airplanecrew.airplaneId and " +
                        "airport.crew.crewId = airport.airplanecrew.crewId and " +
                        "airport.crew.crewId=airport.crewemployee.crewId and " +
                        "airport.employee.employeeId=airport.crewemployee.employeeId");

                preparedStatement.setString(1, serialNumb);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()){
                    System.out.println(
                            resultSet.getString("airplane.name")
                                    + " "+ resultSet.getString("airplane.serialNumber")
                                    + " " + resultSet.getString("route.departure")
                                    + " " + resultSet.getString("route.arrival")
                                    + " " + resultSet.getString("crew.name")
                                    + " " + resultSet.getString("employee.name")
                                    + " " + resultSet.getString("employee.surname")
                                    );

                }}
        }
        catch(Exception ex){
            System.out.println("Connection failed...");

            System.out.println(ex);
        }

        finally {
            statement.close();
        }



    }

    public static Connection getConnection() throws SQLException, IOException {

        Properties props = new Properties();
        try(InputStream in = Files.newInputStream(Paths.get("src\\main\\java\\properties\\database.properties"))){
            props.load(in);
        }
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        return DriverManager.getConnection(url, username, password);
    }
}
