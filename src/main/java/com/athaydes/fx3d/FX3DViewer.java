package com.athaydes.fx3d;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 *
 */
public class FX3DViewer extends Group {

    private final PerspectiveCamera camera = new PerspectiveCamera( true );
    private final Xform cameraXform = new Xform();
    private final Xform cameraXform2 = new Xform();
    private final Xform cameraXform3 = new Xform();

    private final Xform axisGroup = new Xform();

    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    private static final double CAMERA_INITIAL_DISTANCE = -450;
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;

    private final Translate cameraPosition = new Translate( 0, 0, 0 );

    private final EventHandler<ZoomEvent> zoomEventHandler = event -> {
        if ( !Double.isNaN( event.getZoomFactor() ) && event.getZoomFactor() > 0.8 && event.getZoomFactor() < 1.2 ) {
            double z = cameraPosition.getZ() / event.getZoomFactor();
            z = Math.max( z, -1000 );
            z = Math.min( z, 0 );
            cameraPosition.setZ( z );
        }
    };

    private final EventHandler<ScrollEvent> scrollEventHandler = event -> {
        if ( event.getTouchCount() > 0 ) { // touch pad scroll
            cameraXform2.t.setX( cameraXform2.t.getX() - ( 0.01 * event.getDeltaX() ) );
            cameraXform2.t.setY( cameraXform2.t.getY() + ( 0.01 * event.getDeltaY() ) );
        } else {
            double z = cameraPosition.getZ() - ( event.getDeltaY() * 0.2 );
            z = Math.max( z, -1000 );
            z = Math.min( z, 0 );
            cameraPosition.setZ( z );
        }
    };

    private SimpleBooleanProperty showAxis = new SimpleBooleanProperty( false ) {
        @Override
        protected void invalidated() {
            if ( get() ) {
                getChildren().add( axisGroup );
            } else {
                getChildren().remove( axisGroup );
            }
        }
    };

    public FX3DViewer( Scene scene ) {
        buildCamera();
        createAxes();
        handleKeyboard( scene );
        handleMouse( scene );
        scene.setCamera( camera );
        scene.addEventHandler( ZoomEvent.ANY, zoomEventHandler );
        scene.addEventHandler( ScrollEvent.ANY, scrollEventHandler );
        showAxis.set( true );
    }

    public boolean isShowAxis() {
        return showAxis.get();
    }

    public SimpleBooleanProperty showAxisProperty() {
        return showAxis;
    }

    private void buildCamera() {
        getChildren().add( cameraXform );
        cameraXform.getChildren().add( cameraXform2 );
        cameraXform2.getChildren().add( cameraXform3 );
        cameraXform3.getChildren().add( camera );
        cameraXform3.setRotateZ( 180.0 );

        camera.setNearClip( CAMERA_NEAR_CLIP );
        camera.setFarClip( CAMERA_FAR_CLIP );
        camera.setTranslateZ( CAMERA_INITIAL_DISTANCE );
        cameraXform.ry.setAngle( CAMERA_INITIAL_Y_ANGLE );
        cameraXform.rx.setAngle( CAMERA_INITIAL_X_ANGLE );

        camera.getTransforms().add( cameraPosition );
    }

    private void createAxes() {
        axisGroup.getChildren().add( new Axis() );
    }

    private void handleMouse( Scene scene ) {
        scene.setOnMousePressed( me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        } );

        scene.setOnMouseDragged( me -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = ( mousePosX - mouseOldX );
            mouseDeltaY = ( mousePosY - mouseOldY );

            double modifier = 1.0;

            if ( me.isControlDown() ) {
                modifier = CONTROL_MULTIPLIER;
            }
            if ( me.isShiftDown() ) {
                modifier = SHIFT_MULTIPLIER;
            }
            if ( me.isPrimaryButtonDown() ) {
                cameraXform.ry.setAngle( cameraXform.ry.getAngle() -
                        mouseDeltaX * modifier * ROTATION_SPEED );
                cameraXform.rx.setAngle( cameraXform.rx.getAngle() +
                        mouseDeltaY * modifier * ROTATION_SPEED );
            } else if ( me.isSecondaryButtonDown() ) {
                double z = camera.getTranslateZ();
                double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier;
                camera.setTranslateZ( newZ );
            } else if ( me.isMiddleButtonDown() ) {
                cameraXform2.t.setX( cameraXform2.t.getX() +
                        mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED );
                cameraXform2.t.setY( cameraXform2.t.getY() +
                        mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED );
            }
        } );
    }

    private void handleKeyboard( Scene scene ) {
        scene.setOnKeyPressed( event -> {
            switch ( event.getCode() ) {
                case Z:
                    cameraXform2.t.setX( 0.0 );
                    cameraXform2.t.setY( 0.0 );
                    cameraXform.ry.setAngle( CAMERA_INITIAL_Y_ANGLE );
                    cameraXform.rx.setAngle( CAMERA_INITIAL_X_ANGLE );
                    break;
                case X:
                    axisGroup.setVisible( !axisGroup.isVisible() );
                    break;
            }
        } );
    }

    private static class Axis extends Group {
        private static final double RADIUS = 1.0;
        private static final int LENGTH = 1_000;

        Axis() {
            Cylinder axisX = new Cylinder( RADIUS, LENGTH );
            axisX.getTransforms().addAll( new Rotate( 90, Rotate.Z_AXIS ), new Translate( 0, 30, 0 ) );
            axisX.setMaterial( new PhongMaterial( Color.RED ) );
            Cylinder axisY = new Cylinder( RADIUS, LENGTH );
            axisY.getTransforms().add( new Translate( 0, 30, 0 ) );
            axisY.setMaterial( new PhongMaterial( Color.GREEN ) );
            Cylinder axisZ = new Cylinder( RADIUS, LENGTH );
            axisZ.setMaterial( new PhongMaterial( Color.BLUE ) );
            axisZ.getTransforms().addAll( new Rotate( 90, Rotate.X_AXIS ), new Translate( 0, 30, 0 ) );
            getChildren().addAll( axisX, axisY, axisZ );
        }
    }

}
