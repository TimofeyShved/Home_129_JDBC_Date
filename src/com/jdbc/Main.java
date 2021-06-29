package com.jdbc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main( String args[] ) {
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            c.setAutoCommit(false);
            System.out.println("Открытие бд, успех!");

            try {
                stmt = c.createStatement();
                String sql = "CREATE TABLE Book " +
                        "(id INT NOT NULL," +
                        " NAME CHAR(50) NOT NULL, " +
                        " dt DATE," +
                        " PRIMARY KEY (id))";
                stmt.execute("drop table IF EXISTS Book");
                stmt.executeUpdate(sql);
                //c.commit();
            }catch (Exception e){};

            String myDate = "2014/10/29 18:10:45";
            LocalDateTime localDateTime = LocalDateTime.parse(myDate,
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss") );

            long millis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            System.out.println(millis);


            PreparedStatement preparedStatement = null; //= c.prepareStatement("insert into Book (id, name, dt) values (1, ?, ?)");
            /* preparedStatement.setString(1, "inferno");
            preparedStatement.setDate(2, new Date(millis));
            preparedStatement.execute();


             */
            // или
            preparedStatement = c.prepareStatement("insert into Book (id, name, dt) values (1, 'tom', '2017-02-12')");
            preparedStatement.execute();

            preparedStatement= c.prepareStatement("SELECT * FROM Book");
            ResultSet rs = preparedStatement.executeQuery();

            while ( rs.next() ) {
                Blob blob1 = rs.getBlob("img");
                BufferedImage image1 = ImageIO.read(blob1.getBinaryStream());
                File outFile = new File("save.jpg");
                ImageIO.write(image1, "jpg", outFile);
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() ); 
            System.exit(0);
        }
        System.out.println("Выборка данных, успех!");
    }
}