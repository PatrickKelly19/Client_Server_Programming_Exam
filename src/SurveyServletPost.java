// Fig. 9.27: SurveyServlet.java
// A Web-based survey that uses JDBC from a servlet.

import java.io.*;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "SurveyServletPost", urlPatterns = {"/html_form_send.php"})

public class SurveyServletPost extends HttpServlet {
    private Connection connection;
    private PreparedStatement results, booksid;
    private final static Logger LOGGER = Logger.getLogger(SurveyServletPost.class.getName());

    // set up database connection and prepare SQL statements
    public void init( ServletConfig config )
            throws ServletException
    {
        // attempt database connection and create PreparedStatements
        try {
            LOGGER.info("Attempting database connection POST\n");
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/books" ,"root","root");

            // PreparedStatement to obtain survey option table's data
            booksid = connection.prepareStatement("SELECT MAX(id) FROM book"
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
    protected void doPost( HttpServletRequest request,
                           HttpServletResponse response )
            throws ServletException, IOException
    {

        LOGGER.info("Gone into doPost\n");
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

        // read current survey response
        String first_name = request.getParameter( "first_name" );
        String last_name = request.getParameter( "last_name" );
        String ISBN = request.getParameter( "ISBN" );
        String Title = request.getParameter( "Title" );
        String Copyright = request.getParameter( "Copyright" );
        String comments = request.getParameter( "comments" );

        // if copyright is empty, leave as default
        if (Objects.equals(Copyright, "")) {
            Copyright = " ";
            LOGGER.info("Copyright is empty\n");
        }

        // error checking survey form entries
        boolean entry = (Objects.equals(first_name, "")) || (Objects.equals(last_name, "")) || (Objects.equals(ISBN, "")) || (Objects.equals(Title, "")) || (Objects.equals(comments, ""));

        //if none of the survey entries are empty
        if (!entry) try {

            int UserID = 0;
            results = connection.prepareStatement("Insert Into book Values (?,?,?,?,?,?,?)");

            // get total of all survey responses
            ResultSet totalRS = booksid.executeQuery();
            if (totalRS.next()) {
                UserID = totalRS.getInt(1) + 1;
                results.setInt(1, UserID);
                results.setString(2, first_name);
                results.setString(3, last_name);
                results.setString(4, ISBN);
                results.setString(5, Title);
                results.setString(6, Copyright);
                results.setString(7, comments);
            }
            // get results
            results.executeUpdate();
            LOGGER.info("Results saved to database successfully!\n");
            out.println("<title>Thank you!</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<br>User ID Number = " + UserID);
            out.println("</br>Results successfully stored in database");
            out.println("<br><form>\n" + "<input class=\"MyButton\" type=\"button\" value=\"Check database results\" onclick=\"window.location.href='http://localhost:8080/Get'\" />\n" + "</form></br>");
            out.println("<p>Thank you for participating.<br>");
            out.println("<button onclick=\"goBack()\">Back Button</button><script>function goBack() {\n" + "window.history.back();\n" + "}</script></br>");
            // end XHTML document
            out.println("</pre></body></html>");
        }

        // if database exception occurs, return error page
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            LOGGER.info("!Entry Database exception\n");
            out.println("<title>Error</title>");
            out.println("</head>");
            out.println("<body><p>Database error occurred. ");
            out.println("<br>");
            out.println("<button onclick=\"goBack()\">Back Button</button><script>function goBack() {\n" + "window.history.back();\n" + "}</script>");
            out.println("</br>");
            out.println("Try again later.</p></body></html>");
            out.close();
        }
        else
        {
            try {
                LOGGER.info("Paramters with '*' not filled in properly\n");
                out.println("<title>Sorry!</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("Fill in each parameter with '*' beside it's name");
                out.println("<br><button onclick=\"goBack()\">Back Button</button><script>function goBack() {\n" + "window.history.back();\n" + "}</script></br>");
                // end XHTML document
                out.println("</pre></body></html>");
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info("Error\n");
                out.println("<title>Error</title>");
                out.println("</head>");
                out.println("<br><button onclick=\"goBack()\">Back Button</button><script>function goBack() {\n" + "window.history.back();\n" + "}</script>");
                out.println("Try again later.</p></body></html></br>");
                out.close();
            }
        }
    } // end of doPost method

    // close SQL statements and database when servlet terminates
    public void destroy()
    {
        try {
            // attempt to close statements and database connection
            results.close();
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
