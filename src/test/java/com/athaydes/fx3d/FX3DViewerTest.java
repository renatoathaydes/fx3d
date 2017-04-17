package com.athaydes.fx3d;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.junit.Test;

/**
 *
 */
public class FX3DViewerTest {

    @Test
    public void canShowSphere() throws Exception {

    }

    public static class TestApp extends Application {
        private FX3DViewer viewer;

        @Override
        public void start( Stage primaryStage ) throws Exception {
            Group root = new Group();

            Scene scene = new Scene( root, 500, 700 );

            this.viewer = new FX3DViewer( scene );

            Box box = new Box( 50, 50, 50 );

            PhongMaterial material = new PhongMaterial( Color.RED );
            box.setMaterial( material );
            viewer.getChildren().add( box );

            final Rotate rotateX = new Rotate( -20, Rotate.X_AXIS );
            final Rotate rotateY = new Rotate( -20, Rotate.Y_AXIS );
            box.getTransforms().addAll( rotateX, rotateY );

            Timeline timeline = new Timeline();
            timeline.setCycleCount( Timeline.INDEFINITE );
            final KeyValue kv = new KeyValue( rotateX.angleProperty(), 340 );
            final KeyValue kv2 = new KeyValue( rotateY.angleProperty(), 340 );
            final KeyFrame kf = new KeyFrame( Duration.millis( 3000 ),kv, kv2 );
            timeline.getKeyFrames().add( kf );

            root.getChildren().add( this.viewer );

            primaryStage.setScene( scene );
            primaryStage.setTitle( "FX3D Viewer" );
            primaryStage.show();
            timeline.play();

        }

        public static void main( String[] args ) {
            launch( TestApp.class );

        }
    }

}
