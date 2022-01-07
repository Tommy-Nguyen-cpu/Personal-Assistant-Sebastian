import java.io.DataInputStream ;
import java.io.DataOutputStream ;
import java.net.Socket ;
import java.util.Locale ;
import java.util.Scanner ;

import javax.speech.Central ;
import javax.speech.synthesis.Synthesizer ;
import javax.speech.synthesis.SynthesizerModeDesc ;

import javafx.application.Platform ;
import javafx.scene.control.TextField ;
import javafx.scene.input.KeyCode ;
import javafx.scene.shape.Circle ;

/**
 *
 */

/**
 * @author nguyent68
 * @version 1.0.0 2020-11-16 Initial implementation
 */

public class PersonalAssistantClient implements Runnable
    {

    private static int serverPort = 9823 ;

    private static Circle circle ;

    private final TextField textBox = new TextField() ;
    String sendMessage = "" ;
    String receivedMessage = "" ;
    private boolean isDoneGettingUserInput = false ;

    private EmailPortalWindow variableToDisplayEmailPortalWindow ;


    @Override
    public void run()
        {
        try
            {
            final Scanner clientInput = new Scanner( System.in ) ;
            final Socket clientSocket = new Socket( "localhost", serverPort ) ;

            final DataInputStream clientGetFromServer =
                                            new DataInputStream( clientSocket.getInputStream() ) ;
            final DataOutputStream clientSendsToServer =
                                            new DataOutputStream( clientSocket.getOutputStream() ) ;

            replyToQuestion( new String( clientGetFromServer.readUTF() ) ) ;
            replyToQuestion( new String( clientGetFromServer.readUTF() ) ) ;

            while ( !this.sendMessage.toLowerCase().contains( "bye sebastian" ) )
                {
                userInput() ;
                if ( this.isDoneGettingUserInput )
                    {
                    clientSendsToServer.writeUTF( this.sendMessage ) ;
                    final String receivedMessage = new String( clientGetFromServer.readUTF() ) ;
                    replyToQuestion( receivedMessage ) ;
                    this.isDoneGettingUserInput = false ;
                    }
                }
            replyToQuestion( "Have a good day sir." ) ;
            clientSocket.close() ;
            clientGetFromServer.close() ;
            clientSendsToServer.close() ;
            clientInput.close() ;
            Platform.exit() ;
            }
        catch ( final Exception e )
            {
            replyToQuestion( "There was an error trying to activate me. The sockets were not closed before activating me." ) ;
            }
        }


    // Creates a voice for Sebastian.
    public static void replyToQuestion( final String clientRequest )
        {
        try
            {
            System.setProperty( "freetts.voices",
                                "com.sun.speech.freetts.en.us" +
                                                  ".cmu_us_kal.KevinVoiceDirectory" ) ;

            Central.registerEngineCentral( "com.sun.speech.freetts" +
                                           ".jsapi.FreeTTSEngineCentral" ) ;

            final Synthesizer synthesizer = Central.createSynthesizer( new SynthesizerModeDesc( Locale.US ) ) ;

            synthesizer.allocate() ;

            synthesizer.resume() ;

            setCircleColor( true ) ;
            synthesizer.speakPlainText( clientRequest, null ) ;
            synthesizer.waitEngineState( Synthesizer.QUEUE_EMPTY ) ;

            setCircleColor( false ) ;
            }

        catch ( final Exception e )
            {
            e.printStackTrace() ;
            }
        }


    // Creates a variable that holds a reference to the circle object created in
    // ProgramExecution class.
    public void setCircle( final Circle circleReference )
        {
        PersonalAssistantClient.circle = circleReference ;
        }


    private void userInput()
        {
        this.textBox.setOnKeyPressed( e ->
            {
            if ( e.getCode() == KeyCode.ENTER )
                {
                this.sendMessage = this.textBox.getText() ;
                this.textBox.clear() ;
                this.isDoneGettingUserInput = true ;
                if ( this.sendMessage.toLowerCase().contains( "email" ) )
                    {
                    this.variableToDisplayEmailPortalWindow.showWindow() ;
                    }
                }
            } ) ;
        }


    // Sets the color of Sebastians eye to a different color if Sebastian is
    // speaking.
    public static void setCircleColor( final boolean isOn )
        {
        if ( isOn )
            {
            circle.setFill( javafx.scene.paint.Color.RED ) ;
            }
        else
            {
            circle.setFill( javafx.scene.paint.Color.DARKRED ) ;
            }
        }

    public TextField giveTextField()
        {
        return this.textBox ;
        }


    public void setEmailPortalWindowVariable( final EmailPortalWindow emailPortal )
        {
        this.variableToDisplayEmailPortalWindow = emailPortal ;
        }

    }
// end class personalAssistantClient