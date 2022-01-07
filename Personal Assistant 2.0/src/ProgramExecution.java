
/**
  */

/**
 * @author nguyent68
 * @version 1.0.0 2020-11-16 Initial implementation
 */

import javafx.animation.AnimationTimer ;
import javafx.application.Application ;
import javafx.application.Platform ;
import javafx.scene.Scene ;
import javafx.scene.control.TextField ;
import javafx.scene.layout.Pane ;
import javafx.scene.paint.Color ;
import javafx.scene.shape.Circle ;
import javafx.scene.shape.Rectangle ;
import javafx.scene.text.Font ;
import javafx.scene.text.FontWeight ;
import javafx.scene.text.Text ;
import javafx.stage.Stage ;

public class ProgramExecution extends Application
    {
    public static boolean isItOver =false;
    private Stage window;
    private PersonalAssistantClient personalClient =
                                    new PersonalAssistantClient() ;
    private PersonalAssistantServer personalServer =
                                    new PersonalAssistantServer() ;
    private Thread threadOfClient;
    private Thread threadOfServer;

    public static void main( final String args[] ) throws Exception
        {
        launch( args ) ;
        }

    private void updating() {
    AnimationTimer timer = new AnimationTimer() {

        @Override
        public void handle( long now )
            {
            window.setOnCloseRequest(e->{
            Platform.exit();
            
            });
            
            }
    
    };
    timer.start();
    }
    @Override
    public void start( final Stage primaryStage )
        {
        EmailPortalWindow newMiniWindowForEmail = new EmailPortalWindow();
        TextField textBox = personalClient.giveTextField();
        textBox.setLayoutX( 200 );
        textBox.setLayoutY( 520 );
        personalClient.setEmailPortalWindowVariable( newMiniWindowForEmail );
        personalServer.setEmailWindowVariable( newMiniWindowForEmail );

        // Eye of the personal assistant.
        final Circle circle = new Circle() ;
        circle.setRadius( 100 ) ;
        circle.setFill( Color.DARKRED ) ;
        circle.setLayoutX( 300 ) ;
        circle.setLayoutY( 200 ) ;
        final Pane root = new Pane() ;

        // Outer glow of the personal assistant's eye.
        final Circle outerGlowCircle = new Circle() ;
        outerGlowCircle.setRadius( 120 ) ;
        outerGlowCircle.setFill( Color.ALICEBLUE ) ;
        outerGlowCircle.setLayoutX( 300 ) ;
        outerGlowCircle.setLayoutY( 200 ) ;

        personalClient.setCircle( circle ) ;

        // The edge of the personal assistant, to differentiate between its face and body.
        final Rectangle rectangle = new Rectangle() ;
        rectangle.setWidth( 800 ) ;
        rectangle.setHeight( 10 ) ;
        rectangle.setY( 400 ) ;
        rectangle.setFill( Color.WHITE ) ;

        // A simple text that displays the name of the corporation.
        final Text nameOfCorporation = new Text() ;
        nameOfCorporation.setFill( Color.WHITE ) ;
        nameOfCorporation.setText( "Tommy Corp" ) ;
        nameOfCorporation.setX( 230 ) ;
        nameOfCorporation.setY( 450 ) ;
        nameOfCorporation.setFont( Font.font( "Verdana", FontWeight.BOLD, 20 ) ) ;
        root.setStyle( "-fx-background-color: black;" ) ;
        root.getChildren()
            .addAll( rectangle, outerGlowCircle, circle, nameOfCorporation,textBox ) ;
        final Scene scene = new Scene( root, 600, 800 ) ;
        primaryStage.setScene( scene ) ;
        primaryStage.setTitle( "Sebastian" ) ;
        primaryStage.setResizable( false ) ;
        primaryStage.show() ;
        this.window = primaryStage;
        circle.requestFocus() ;
        threadOfServer = new Thread(personalServer);
        threadOfClient = new Thread(personalClient);
        threadOfServer.setDaemon( true );
        threadOfClient.setDaemon( true );
        threadOfServer.start();
        threadOfClient.start();
        updating();
        }


    }