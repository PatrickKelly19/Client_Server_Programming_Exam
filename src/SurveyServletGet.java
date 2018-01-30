// Fig. 9.27: SurveyServlet.java
// A Web-based survey that uses JDBC from a servlet.

import java.io.*;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "SurveyServletGet", urlPatterns = {"/Get"})

public class SurveyServletGet extends HttpServlet {
    private Connection connection;
    private PreparedStatement get;
    int UserID = 0;
    String firstName, lastName, isbn, title, cpNum, comments;
    private final static Logger LOGGER = Logger.getLogger(SurveyServletGet.class.getName());

    // set up database connection and prepare SQL statements
    public void init( ServletConfig config )
            throws ServletException
    {
        // attempt database connection and create PreparedStatements
        try {
            LOGGER.info("Attempting database connection GET\n");
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/books" ,"root","root");

            // PreparedStatement to get data from database
            get = connection.prepareStatement("SELECT * FROM book"
            );
            LOGGER.info("Creating Prepared Statements\n");

        }

        // for any exception throw an UnavailableException to
        // indicate that the servlet is not currently available
        catch ( Exception exception ) {
            exception.printStackTrace();
            throw new UnavailableException( exception.getMessage() );
        }
    } // end of init method

    // process survey response
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response )
            throws ServletException, IOException
    {

        LOGGER.info("Gone into doGet\n");
        // set up response to client
        response.setContentType( "text/html" );
        PrintWriter out = response.getWriter();

        // start XHTML document
        out.println( "<?xml version = \"1.0\"?>" );

        out.println( "<!DOCTYPE html PUBLIC \"-//W3C//DTD " +
                "XHTML 1.0 Strict//EN\" \"http://www.w3.org" +
                "/TR/xhtml1/DTD/xhtml1-strict.dtd\">" );

        out.println(
                "<html xmlns = \"http://www.w3.org/1999/xhtml\">" );

        // head section of document
        out.println( "<head>" );

        try {

            // get total of all survey responses
            ResultSet totalRS = get.executeQuery();

            // checks if database is empty
            if (!totalRS.isBeforeFirst() ) {
                LOGGER.info("Database error occurred. Database empty\n");
                out.println("<title>Sorry!</title>");
                out.println("</head>");
                out.println("<body><p>Database error occurred. Database empty");
            }

            while (totalRS.next())
            {
                UserID = totalRS.getInt(1);
                firstName = totalRS.getString(2);
                lastName = totalRS.getString(3);
                isbn = totalRS.getString(4);
                title = totalRS.getString(5);
                cpNum = totalRS.getString(6);
                comments = totalRS.getString(7);

                out.println("<title>Thank you!</title>");
                out.println("</head>");
                out.println("<body>");

                out.println("<br>UserID: " + UserID);
                out.println("<br>First Name: " + firstName);
                out.println("<br>Last Name: " + lastName);
                out.println("<br>ISBN: " + isbn);
                out.println("<br>Title: " + title);
                out.println("<br>Copyright Number: " + cpNum);
                out.println("<br>Comments: " + comments + "<br>");
                LOGGER.info("Results being displayed on webpage\n");
            }
            out.println("<button onclick=\"goBack()\">Back Button</button><script>function goBack() {\n" + "window.history.back();\n" + "}</script></br>");
            out.println("</pre></body></html>");
            out.close();
        }
        // if database exception occurs, return error page
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            LOGGER.info("Database exception error\n");
            out.println("<title>Error</title>");
            out.println("</head>");
            out.println("<body><p>Database error occurred. ");
            out.println("<br><button onclick=\"goBack()\">Back Button</button><script>function goBack() {\n" + "window.history.back();\n" + "}</script>");
            out.println("Try again later.</p></body></html></br>");
            out.close();
        }
    } // end of doGet method

    // close SQL statements and database when servlet terminates
    public void destroy()
    {
        try {
            // attempt to close statements and database connection
            connection.close();
            LOGGER.info("Database connection closed\n");
        }

        // handle database exceptions by returning error to client
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();
            LOGGER.info("Database exceptions\n");
        }
    } // end of destroy method
}

/***************************************************************
 * (C) Copyright 2002 by Deitel & Associates, Inc. and         *
 * Prentice Hall. All Rights Reserved.                         *
 *                                                             *
 * DISCLAIMER: The authors and publisher of this book have     *
 * used their best efforts in preparing the book. These        *
 * efforts include the development, research, and testing of   *
 * the theories and programs to determine their effectiveness. *
 * The authors and publisher make no warranty of any kind,     *
 * expressed or implied, with regard to these programs or to   *
 * the documentation contained in these books. The authors     *
 * and publisher shall not be liable in any event for          *
 * incidental or consequential damages in connection with, or  *
 * arising out of, the furnishing, performance, or use of      *
 * these programs.                                             *
 ***************************************************************/