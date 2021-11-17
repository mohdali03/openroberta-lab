package de.fhg.iais.roberta.visitor;

import de.fhg.iais.roberta.bean.UsedMethodBean;
import de.fhg.iais.roberta.syntax.action.mbot2.DisplaySetColourAction;
import de.fhg.iais.roberta.syntax.action.mbot2.PlayRecordingAction;
import de.fhg.iais.roberta.syntax.action.serial.SerialWriteAction;
import de.fhg.iais.roberta.syntax.sensor.generic.EncoderSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.KeysSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.UltrasonicSensor;
import de.fhg.iais.roberta.syntax.sensor.mbot2.QuadRGBSensor;
import de.fhg.iais.roberta.visitor.collect.AbstractUsedMethodCollectorVisitor;

public class Mbot2UsedMethodCollectorVisitor extends AbstractUsedMethodCollectorVisitor implements IMbot2Visitor<Void> {
    public Mbot2UsedMethodCollectorVisitor(UsedMethodBean.Builder builder) {
        super(builder);
    }


    @Override
    public Void visitPlayRecordingAction(PlayRecordingAction<Void> playRecordingAction) {
        return null;
    }

    @Override
    public Void visitDisplaySetColourAction(DisplaySetColourAction<Void> displaySetColourAction) {
        return null;
    }

    @Override
    public Void visitSerialWriteAction(SerialWriteAction<Void> serialWriteAction) {
        return null;
    }

    @Override
    public Void visitKeysSensor(KeysSensor<Void> keysSensor){
        return null;
    }

    @Override
    public Void visitEncoderSensor(EncoderSensor<Void> encoderSensor){
        return null;
    }

    @Override
    public Void visitUltrasonicSensor(UltrasonicSensor<Void> ultrasonicSensor) {
        return null;
    }

    @Override
    public Void visitQuadRGBSensor(QuadRGBSensor<Void> quadRGBSensor) {
        return null;
    }
}
