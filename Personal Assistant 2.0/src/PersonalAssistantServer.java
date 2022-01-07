import org.jsoup.Connection ;
import org.jsoup.Jsoup ;
import org.jsoup.nodes.Document ;

import java.awt.Desktop ;
import java.io.BufferedReader ;
import java.io.DataInputStream ;
import java.io.DataOutputStream ;
import java.io.File ;
import java.io.FileNotFoundException ;
import java.io.IOException ;
import java.io.InputStream ;
import java.io.InputStreamReader ;
import java.io.OutputStream ;
import java.net.ServerSocket ;
import java.net.Socket ;
import java.net.URI ;
import java.net.URL ;
import java.util.Scanner ;

/**
 *
 */

/**
 * @author nguyent68
 * @version 1.0.0 2020-11-16 Initial implementation
 */
public class PersonalAssistantServer implements Runnable
    {

    private static int port = 9823 ;

    private EmailPortalWindow variableToSendAndReceiveInformationFromForEmail ;


    @Override
    public void run()
        {
        try
            {
            final ServerSocket socketOfServer = new ServerSocket( port ) ;
            final Socket socketToConnectTo = socketOfServer.accept() ;

            final DataOutputStream serverSendToClient =
                                            new DataOutputStream( socketToConnectTo.getOutputStream() ) ;
            final DataInputStream serverReceivesFromClient =
                                            new DataInputStream( socketToConnectTo.getInputStream() ) ;
            final String clientName = nameOfClient() ;
            serverSendToClient.writeUTF( "Hello sir " + clientName ) ;
            String messageReceived = "" ;
            serverSendToClient.writeUTF( "Do you need something, sir " + clientName +
                                         "?" ) ;

            // Loops as long as the message sent isn't "bye sebastian".
            while ( !messageReceived.toLowerCase().contains( "bye sebastian" ) )
                {
                messageReceived = serverReceivesFromClient.readUTF() ;

                // Gets information about the weather from a website.
                if ( messageReceived.toLowerCase().contains( "weather" ) )
                    {
                    serverSendToClient.writeUTF( weatherData() ) ;
                    }

                // Opens up the browser, goes to Google, and searches up whatever the
                // user wants.
                else if ( messageReceived.toLowerCase().contains( "search up" ) )
                    {
                    messageReceived = messageReceived.replace( "search up ", "" )
                                                     .replace( "Search up ", "" ) ;
                    serverSendToClient.writeUTF( "Yes sir. I will search up " +
                                                 messageReceived ) ;
                    messageReceived = messageReceived.replace( " ", "+" ) ;
                    usingBrowserToGoToWebsiteSearchSomethingUpOrOpeningANewTab( "google.com/search?q=" +
                                                                                messageReceived ) ;
                    }

                // Goes to a specific website the user tells Sebastian to go to.
                else if ( messageReceived.toLowerCase().contains( "go to " ) )
                    {
                    serverSendToClient.writeUTF( "I will go to the website as you ask, sir." ) ;
                    usingBrowserToGoToWebsiteSearchSomethingUpOrOpeningANewTab( messageReceived +
                                                                                ".com" ) ;
                    }

                // Another simple reply.
                else if ( messageReceived.toLowerCase().contains( "bored" ) )
                    {
                    serverSendToClient.writeUTF( "I do not mean to be rude sir, but perhaps you can code to cure your boredom." ) ;
                    }

                // Gathers information from Wikipedia.
                else if ( messageReceived.toLowerCase()
                                         .contains( "look up information about " ) )
                    {
                    final String infoFromWikipedia = searchUpOnWikipediaAboutSomething( messageReceived.replaceAll( "look up information about ",
                                                                                                                    "+" ) ) ;
                    serverSendToClient.writeUTF( infoFromWikipedia ) ;
                    }
                else if ( messageReceived.toLowerCase().contains( "new tab" ) )
                    {
                    serverSendToClient.writeUTF( "Opening a new tab." ) ;
                    usingBrowserToGoToWebsiteSearchSomethingUpOrOpeningANewTab( "google.com" ) ;
                    }

                // Opens an application specified by the user (sensitive to case and
                // others).
                else if ( messageReceived.toLowerCase().contains( "open " ) )
                    {
                    serverSendToClient.writeUTF( "Yes sir, I will open the specified application." ) ;
                    openApplication( messageReceived ) ;
                    }

                // Get system information and gives to user.
                else if ( messageReceived.toLowerCase()
                                         .contains( "system information" ) )
                    {
                    serverSendToClient.writeUTF( getSystemInformation() ) ;
                    }
                // Allows users to send email via the Gmail server.
                else if ( messageReceived.toLowerCase().contains( "email" ) )
                    {
                    final String emailResult = sendEmail() ;
                    serverSendToClient.writeUTF( emailResult ) ;
                    }
                else if ( messageReceived.toLowerCase().contains( "brightness" ) )
                    {
                    final int adjustingToUserRequestedLevel =
                                                    numberInString( messageReceived ) ;
                    serverSendToClient.writeUTF( "Will adjust brightness to " +
                                                 adjustingToUserRequestedLevel ) ;
                    adjustingBrightness( adjustingToUserRequestedLevel ) ;
                    }
                // A simpler way of conversing with Sebastian without too much code.
                else
                    {
                    serverSendToClient.writeUTF( possibleConversations( messageReceived.toLowerCase() ) ) ;
                    }
                }
            serverSendToClient.close() ;
            serverReceivesFromClient.close() ;
            socketToConnectTo.close() ;
            socketOfServer.close() ;
            return ;
            }
        catch ( final Exception e )
            {}
        }


    // All this method does is read the name in the file "NameOfClient.txt".
    private static String nameOfClient() throws IOException
        {
        Process powerShellExecution = Runtime.getRuntime().exec( "powershell.exe" ) ;
        powerShellExecution = Runtime.getRuntime().exec( "cmd.exe" ) ;
        powerShellExecution = Runtime.getRuntime().exec( "whoami" ) ;
        final BufferedReader reader = new BufferedReader( new InputStreamReader( powerShellExecution.getInputStream() ) ) ;
        String userName = reader.readLine() ;
        userName = userName.replace( userName.substring( 0,
                                                         userName.indexOf( "\\" ) +
                                                            1 ),
                                     "" ) ;
        return userName ;
        }


    private static String weatherData() throws Exception
        {
        // Sets up a connection with the weather website server.
        final String weatherWebsite = "https://weather.com/weather/today/l/bf217d537cc1c8074ec195ce07fb74de3c1593caa6033b7c3be4645ccc5b01de" ;

        final Connection connect = Jsoup.connect( weatherWebsite ) ;
        // Get the documentation from the server.
        final Document documentToGet = connect.get() ;
        final String result = documentToGet.body().text() ;
        // We only care about a specific part, all other part of the documentation is
        // unnecessary.
        final String otherResult = result.substring( result.indexOf( "Boston, MA Weather" ),
                                                     result.indexOf( "Today's Forecast for Boston" ) )
                                         .replaceAll( "Boston, MA Weather", "" ) ;

        final String realForecast = result.substring( result.indexOf( "Today's Forecast for Boston" ),
                                                      result.indexOf( "Next Hours" ) )
                                          .replaceAll( "%", "% chance of rain" )
                                          .replaceAll( "Chance of Rain", "" ) +
                                    "\nBut " + otherResult ;
        return realForecast ;
        }


    // Exactly the same process as "searchSomethingUp" method except that it goes to
    // a different website.
    private static void usingBrowserToGoToWebsiteSearchSomethingUpOrOpeningANewTab(
                                                                                    String websiteURL )
        throws Exception
        {
        websiteURL = websiteURL.toLowerCase()
                               .replace( "go to ", "" )
                               .replace( "search up ", "" ) ;
        if ( Desktop.isDesktopSupported() &&
             Desktop.getDesktop().isSupported( Desktop.Action.BROWSE ) )
            {
            Desktop.getDesktop().browse( new URI( "https://" + websiteURL ) ) ;
            }
        }


    // Get information from Wikipedia and returns it to the user.
    private static String searchUpOnWikipediaAboutSomething(
                                                             final String searchUpSomethingOnWiki )
        throws Exception
        {
        final URL wikiAccess = new URL( "https://en.wikipedia.org/w/index.php?action=raw&title=" +
                                        searchUpSomethingOnWiki.replaceAll( " ",
                                                                            "_" ) ) ;
        String textResultFromWikipedia = "" ;
        // Gets input from the website.
        try ( BufferedReader br = new BufferedReader( new InputStreamReader( wikiAccess.openConnection()
                                                                                       .getInputStream() ) ) )
            {
            String lineFromWikipediaText = null ;
            // Makes sure that the line does not start with one of the following (we
            // don't want any of these lines).
            while ( null != ( lineFromWikipediaText = br.readLine() ) )
                {
                lineFromWikipediaText = lineFromWikipediaText.trim() ;
                if ( !lineFromWikipediaText.startsWith( "|" ) &&
                     !lineFromWikipediaText.startsWith( "{" ) &&
                     !lineFromWikipediaText.startsWith( "}" ) &&
                     !lineFromWikipediaText.startsWith( "<center>" ) &&
                     !lineFromWikipediaText.startsWith( "---" ) &&
                     !lineFromWikipediaText.startsWith( ">" ) &&
                     !lineFromWikipediaText.startsWith( "<" ) &&
                     !lineFromWikipediaText.startsWith( "==" ) )
                    {
                    // Append line that does not contain a |, }, etc.
                    textResultFromWikipedia += lineFromWikipediaText ;
                    }
                if ( textResultFromWikipedia.length() > 3000 )
                    {
                    break ;
                    }
                }
            }
        return textResultFromWikipedia ;
        }


    // Opens up an application the user specified on the system. This is case
    // sensitive and will not work if the user does not type correctly.
    private static void openApplication( String applicationName ) throws IOException
        {
        applicationName = applicationName.replace( "open ", "" )
                                         .replaceAll( " ", "" )
                                         .replace( ".", "" ) ;
        Runtime.getRuntime()
               .exec( "rundll32 SHELL32.DLL,ShellExec_RunDLL " + applicationName +
                      ".exe" ) ;
        }


// Adjusts the brightness of the computer.
    private static void adjustingBrightness( final int brightnessLevel )
        throws IOException
        {
        final String commandToAdjustBrightness = String.format( "$brightness = %d;",
                                                                brightnessLevel ) +
                                                 "$delay = 0;" +
                                                 "$myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods;" +
                                                 "$myMonitor.wmisetbrightness($delay, $brightness)" ;
        final String commandToInputIntoPowerShell = "powershell.exe  " +
                                                    commandToAdjustBrightness ;
        // Executing the command
        final Process powerShellProcess =
                                        Runtime.getRuntime()
                                               .exec( commandToInputIntoPowerShell ) ;

        powerShellProcess.getOutputStream().close() ;
        }


// Very simple. All it does it return the information of the system it is currently
// on.
    private static String getSystemInformation()
        {
        final String systemInformation = "Available Processors: " +
                                         Runtime.getRuntime().availableProcessors() +
                                         "\nFree Memory (byte): " +
                                         Runtime.getRuntime().freeMemory() +
                                         "\nMax Memory (byte): " +
                                         Runtime.getRuntime().maxMemory() +
                                         "\nTotalMemory (byte): " +
                                         Runtime.getRuntime().totalMemory() ;
        return systemInformation ;
        }


    // Reads the "Conversations.txt" file and tries to match the user input with all
    // possible user inputs in the file.
    // If matched, the response Sebastian should say (based on the file), will be
    // said.
    private String possibleConversations( final String messageFromUser )
        throws FileNotFoundException
        {
        final Scanner readingConversationsPossibility =
                                        new Scanner( new InputStreamReader(PersonalAssistantServer.class.getResourceAsStream( "Conversations" )) ) ;
        while ( readingConversationsPossibility.hasNext() )
            {
            final String userPossibility = readingConversationsPossibility.nextLine()
                                                                          .replaceFirst( "User:",
                                                                                         "" ) ;
            if ( userPossibility.contains( messageFromUser ) ||
                 messageFromUser.contains( userPossibility ) )
                {
                readingConversationsPossibility.close() ;
                return userPossibility.replaceAll( messageFromUser, "" )
                                      .replaceAll( ";Sebastian:", "" ) ;
                }
            }
        return "Sorry, I could not understand that." ;
        }


// Allows user to send email.
    private String sendEmail() throws Exception
        {

// final Scanner input = new Scanner( System.in ) ;
        final Socket emailSocket = new Socket( "gmail-smtp-in.l.google.com", 25 ) ;
        final InputStream stream = emailSocket.getInputStream() ;
        final InputStreamReader reader = new InputStreamReader( stream ) ;
        final BufferedReader inputReader = new BufferedReader( reader ) ;
        String serverResponse = inputReader.readLine() ;
        final OutputStream sendToServer = emailSocket.getOutputStream() ;

        if ( !serverResponse.contains( "220" ) )
            {
            sendToServer.close() ;
            emailSocket.close() ;
            inputReader.close() ;
            return "It seems we cannot connect to the server." ;
            }

        String commandCode = "HELO x\r\n" ;
        sendToServer.write( commandCode.getBytes( "US-ASCII" ) ) ;

        serverResponse = inputReader.readLine() ;
        this.variableToSendAndReceiveInformationFromForEmail.displayInstructions( serverResponse ) ;
        Thread.sleep( 2000 ) ;
        if ( !serverResponse.contains( "250" ) )
            {
            sendToServer.close() ;
            emailSocket.close() ;
            inputReader.close() ;
            this.variableToSendAndReceiveInformationFromForEmail.closeWindowForEmail() ;
            return "I could not send response to the email server." ;
            }

        this.variableToSendAndReceiveInformationFromForEmail.displayInstructions( "From what email are you sending this message from?" ) ;
        final String sourceEmail = this.variableToSendAndReceiveInformationFromForEmail.getTextFromTextBox() ;
        commandCode = "MAIL FROM: <" + sourceEmail + ">\r\n" ;
        sendToServer.write( commandCode.getBytes( "US-ASCII" ) ) ;
        serverResponse = inputReader.readLine() ;
        this.variableToSendAndReceiveInformationFromForEmail.displayInstructions( serverResponse ) ;
        Thread.sleep( 2000 ) ;

        if ( !serverResponse.contains( "250" ) )
            {
            sendToServer.close() ;
            emailSocket.close() ;
            inputReader.close() ;
            this.variableToSendAndReceiveInformationFromForEmail.closeWindowForEmail() ;
            return "I could not send response to the email server." ;
            }
        
        this.variableToSendAndReceiveInformationFromForEmail.displayInstructions( "What is the email destination?" ) ;
        final String destinationEmail = this.variableToSendAndReceiveInformationFromForEmail.getTextFromTextBox() ;
        commandCode = "RCPT TO: <" + destinationEmail + ">\r\n" ;
        sendToServer.write( commandCode.getBytes( "US-ASCII" ) ) ;
        serverResponse = inputReader.readLine() ;
        this.variableToSendAndReceiveInformationFromForEmail.displayInstructions( serverResponse ) ;

        if ( !serverResponse.contains( "250" ) )
            {
            sendToServer.close() ;
            emailSocket.close() ;
            inputReader.close() ;
            this.variableToSendAndReceiveInformationFromForEmail.closeWindowForEmail() ;
            return "I could not send response to the email server." ;
            }
        
        commandCode = "DATA\r\n" ;
        sendToServer.write( commandCode.getBytes( "US-ASCII" ) ) ;
        serverResponse = inputReader.readLine() ;
        this.variableToSendAndReceiveInformationFromForEmail.displayInstructions( serverResponse ) ;
        Thread.sleep( 2000 ) ;

        commandCode = "From: <" + sourceEmail + ">\r\n" ;
        sendToServer.write( commandCode.getBytes( "US-ASCII" ) ) ;

        commandCode = "To: <" + destinationEmail + ">\r\n" ;
        sendToServer.write( commandCode.getBytes( "US-ASCII" ) ) ;

        this.variableToSendAndReceiveInformationFromForEmail.displayInstructions( "What is the subject of the email?" ) ;
        final String subjectOfEmail = this.variableToSendAndReceiveInformationFromForEmail.getTextFromTextBox() ;
        commandCode = "Subject: " + subjectOfEmail + "\r\n" ;
        sendToServer.write( commandCode.getBytes( "US-ASCII" ) ) ;

        commandCode = "" ;
        String characterInput = "" ;
        this.variableToSendAndReceiveInformationFromForEmail.displayInstructions( "Now for the body of the email." ) ;
        Thread.sleep( 3000 ) ;
        this.variableToSendAndReceiveInformationFromForEmail.displayInstructions( "Once you are done typing the email" ) ;
        Thread.sleep( 3000 ) ;
        this.variableToSendAndReceiveInformationFromForEmail.displayInstructions( "Press enter and type 'done' and press enter again!" ) ;
        Thread.sleep( 4000 ) ;
        this.variableToSendAndReceiveInformationFromForEmail.displayInstructions( "Type in the contents of your email: " ) ;
        while ( !characterInput.toLowerCase().equals( "done" ) )
            {
            commandCode = commandCode + "\n" + characterInput ;
            characterInput = this.variableToSendAndReceiveInformationFromForEmail.getTextFromTextBox() ;
            }
        System.out.println( commandCode ) ;
        sendToServer.write( commandCode.getBytes( "US-ASCII" ) ) ;

        // AFTER THIS POINT IT DOESN'T WORK. FIGURE OUT WHAT WENT WRONG.
        commandCode = ".\r\n" ;
        sendToServer.write( commandCode.getBytes( "US-ASCII" ) ) ;
        serverResponse = inputReader.readLine() ;
        this.variableToSendAndReceiveInformationFromForEmail.displayInstructions( serverResponse ) ;
        System.out.println( characterInput ) ;
        Thread.sleep( 3000 ) ;
        if ( serverResponse.contains( "Our system has detected" ) )
            {
            sendToServer.close() ;
            emailSocket.close() ;
            inputReader.close() ;
            return "The server has refused to connect with us. We may need to try again." ;
            }
        commandCode = "QUIT\r\n" ;
        System.out.print( commandCode ) ;
        sendToServer.write( commandCode.getBytes( "US-ASCII" ) ) ;
        serverResponse = inputReader.readLine() ;
        System.out.println( serverResponse ) ;
        this.variableToSendAndReceiveInformationFromForEmail.displayInstructions( serverResponse ) ;
        Thread.sleep( 3000 ) ;
        sendToServer.close() ;
        emailSocket.close() ;
        inputReader.close() ;
        return "Email sent!" ;
        }


    private int numberInString( final String stringToSearchNumberFor )
        {
        int integerResult = 0 ;
        for ( int i = 0 ; i < stringToSearchNumberFor.length() ; i++ )
            {
            final char currentCharacter = stringToSearchNumberFor.charAt( i ) ;
            if ( Character.isDigit( currentCharacter ) )
                {
                integerResult = ( integerResult * 10 ) + ( currentCharacter - '0' ) ;
                }
            }
        return integerResult ;
        }


    public void setEmailWindowVariable( final EmailPortalWindow emailPortalWindow )
        {
        this.variableToSendAndReceiveInformationFromForEmail = emailPortalWindow ;
        }

    }
// end class personalAssistantServer