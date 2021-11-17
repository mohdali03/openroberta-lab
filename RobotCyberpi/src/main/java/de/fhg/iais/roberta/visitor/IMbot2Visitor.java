package de.fhg.iais.roberta.visitor;

import de.fhg.iais.roberta.syntax.action.mbot2.DisplaySetColourAction;
import de.fhg.iais.roberta.syntax.action.mbot2.PlayRecordingAction;
import de.fhg.iais.roberta.syntax.action.serial.SerialWriteAction;
import de.fhg.iais.roberta.syntax.sensor.generic.EncoderSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.KeysSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.UltrasonicSensor;
import de.fhg.iais.roberta.syntax.sensor.mbot2.QuadRGBSensor;
import de.fhg.iais.roberta.visitor.hardware.actor.IActors4AutonomousDriveRobots;
import de.fhg.iais.roberta.visitor.hardware.sensor.ISensorVisitor;

/**
 * Interface to be used with the visitor pattern to traverse an AST (and generate code, e.g.).
 */
public interface IMbot2Visitor<V> extends IActors4AutonomousDriveRobots<V>, ISensorVisitor<V> {

    V visitKeysSensor(KeysSensor<V> keysSensor);

    V visitEncoderSensor(EncoderSensor<V> encoderSensor);

    V visitPlayRecordingAction(PlayRecordingAction<Void> playRecordingAction);

    V visitDisplaySetColourAction(DisplaySetColourAction<Void> displaySetColourAction);

    V visitUltrasonicSensor(UltrasonicSensor<V> ultrasonicSensor);

    V visitQuadRGBSensor(QuadRGBSensor<V> quadRGBSensor);

    default V visitSerialWriteAction(SerialWriteAction<Void> serialWriteAction) {
        return null;
    }

}