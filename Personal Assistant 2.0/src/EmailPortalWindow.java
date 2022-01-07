import javafx.scene.Scene ;
import javafx.scene.control.TextField ;
import javafx.scene.input.KeyCode ;
import javafx.scene.layout.Pane ;
import javafx.scene.paint.Color ;
import javafx.scene.text.Text ;
import javafx.stage.Stage ;

/**
 *
 */

/**
 * @author nguyent68
 * @version 1.0.0 2020-12-18 Initial implementation
 */
public class EmailPortalWindow extends Stage
    {

    private TextField textBox = new TextField() ;
    private final Pane rootOfStage = new Pane() ;
    private String userInputForEmail = "" ;
    private final Stage stageOfEmailWindow ;
    private final Text instructionOnScreen = new Text() ;
// FIND A WAY TO FIX THIS.


    public void displayInstructions( final String instructions )
        {
        this.instructionOnScreen.setText( instructions ) ;
        this.instructionOnScreen.setY( 100 ) ;
        this.instructionOnScreen.setX( 100 ) ;
        this.instructionOnScreen.setScaleX( 1.2 ) ;
        this.instructionOnScreen.setScaleY( 1.2 ) ;
        this.instructionOnScreen.setFill( Color.BLACK ) ;
        }


    public void closeWindowForEmail()
        {
        this.stageOfEmailWindow.close() ;
        }


    public String getTextFromTextBox()
        {
        this.userInputForEmail = "" ;
        while ( this.userInputForEmail.equals( "" ) )
            {
            this.textBox.setOnKeyPressed( e ->
                {
                if ( e.getCode() == KeyCode.ENTER )
                    {
                    this.userInputForEmail = this.textBox.getText() ;
                    this.textBox.clear() ;
                    }
                } ) ;
            }
        return this.userInputForEmail ;
        }


    public void showWindow()
        {
        this.stageOfEmailWindow.show() ;
        }


    public EmailPortalWindow()

        {
        this.stageOfEmailWindow = new Stage() ;
        final TextField emailUserInput = new TextField() ;
        emailUserInput.setLayoutX( 200 ) ;
        emailUserInput.setLayoutY( 250 ) ;
        this.textBox = emailUserInput ;
        final Scene miniScene = new Scene( this.rootOfStage, 500, 500 ) ;
        this.rootOfStage.getChildren()
                        .addAll( emailUserInput, this.instructionOnScreen ) ;
        this.stageOfEmailWindow.setScene( miniScene ) ;
        this.stageOfEmailWindow.setTitle( "Email Portal" ) ;
        this.stageOfEmailWindow.setResizable( false ) ;
        }

    }
// end class EmailPortalWindow