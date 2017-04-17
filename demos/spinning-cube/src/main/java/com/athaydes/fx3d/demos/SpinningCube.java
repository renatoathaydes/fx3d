package com.athaydes.fx3d.demos;

import com.athaydes.fx3d.FX3DViewer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.function.Function;

public class SpinningCube extends Application {
    private FX3DViewer viewer;

    @Override
    public void start( Stage primaryStage ) throws Exception {
        Group root = new Group();
        Scene scene = new Scene( root, 600, 600 );

        this.viewer = new FX3DViewer( 600, 600 );

        Box box = new Box( 50, 50, 50 );

        PhongMaterial material = new PhongMaterial( Color.RED );
        box.setMaterial( material );
        viewer.setContent( box );

        Rotate rotateX = new Rotate( -20, Rotate.X_AXIS );
        Rotate rotateY = new Rotate( -20, Rotate.Y_AXIS );
        box.getTransforms().addAll( rotateX, rotateY );

        Timeline timeline = new Timeline();
        timeline.setCycleCount( Timeline.INDEFINITE );
        KeyValue kv = new KeyValue( rotateX.angleProperty(), 340 );
        KeyValue kv2 = new KeyValue( rotateY.angleProperty(), 340 );
        KeyFrame kf = new KeyFrame( Duration.millis( 3000 ), kv, kv2 );
        timeline.getKeyFrames().add( kf );

        SubScene scene3d = viewer.getScene();
        scene3d.setFill( Color.LIGHTBLUE );

        Function<Translate, String> positionText = pos ->
                "x: " + pos.getX() + ", y: " + pos.getY() + ", z: " + pos.getZ();

        VBox status = new VBox( 10 );

        status.setOpacity( 0.8 );
        status.setBackground( new Background( new BackgroundFill( Color.DARKGREEN, new CornerRadii( 5 ), new Insets( 10 ) ) ) );
        status.setMinWidth( 300 );

        Label title = new Label( "JavaFX 3D Demo" );
        Label statusLabel = new Label( positionText.apply( viewer.getCameraPosition() ) );

        status.getChildren().addAll( title, statusLabel );

        InvalidationListener statusListener = observable -> {
            Translate pos = viewer.getCameraPosition();
            statusLabel.setText( positionText.apply( pos ) );
        };

        viewer.getCameraPosition().xProperty().addListener( statusListener );
        viewer.getCameraPosition().yProperty().addListener( statusListener );
        viewer.getCameraPosition().zProperty().addListener( statusListener );

        root.getChildren().addAll( scene3d, status );

        status.setLayoutX( 10 );
        status.setLayoutY( 500 );

        primaryStage.setScene( scene );
        primaryStage.setTitle( "FX3D Viewer" );
        primaryStage.show();
        timeline.play();

    }

}