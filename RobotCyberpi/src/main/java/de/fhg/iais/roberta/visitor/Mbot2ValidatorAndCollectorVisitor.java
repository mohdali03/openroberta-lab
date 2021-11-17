package de.fhg.iais.roberta.visitor;

import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.bean.UsedMethodBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.ConfigurationComponent;
import de.fhg.iais.roberta.syntax.MotionParam;
import de.fhg.iais.roberta.syntax.MotorDuration;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.syntax.action.display.ClearDisplayAction;
import de.fhg.iais.roberta.syntax.action.mbot2.DisplaySetColourAction;
import de.fhg.iais.roberta.syntax.action.mbot2.PlayRecordingAction;
import de.fhg.iais.roberta.syntax.action.display.ShowTextAction;
import de.fhg.iais.roberta.syntax.action.light.LightAction;
import de.fhg.iais.roberta.syntax.action.light.LightStatusAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorGetPowerAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorOnAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorSetPowerAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorStopAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.CurveAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.MotorDriveStopAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.TurnAction;
import de.fhg.iais.roberta.syntax.action.sound.PlayFileAction;
import de.fhg.iais.roberta.syntax.action.sound.PlayNoteAction;
import de.fhg.iais.roberta.syntax.action.sound.ToneAction;
import de.fhg.iais.roberta.syntax.action.sound.VolumeAction;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.lang.expr.NumConst;
import de.fhg.iais.roberta.syntax.sensor.generic.EncoderSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.KeysSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.UltrasonicSensor;
import de.fhg.iais.roberta.syntax.sensor.mbot2.QuadRGBSensor;
import de.fhg.iais.roberta.visitor.validate.CommonNepoValidatorAndCollectorVisitor;
import de.fhg.iais.roberta.syntax.action.motor.differential.DriveAction;


public class Mbot2ValidatorAndCollectorVisitor extends CommonNepoValidatorAndCollectorVisitor implements IMbot2Visitor<Void> {

    public Mbot2ValidatorAndCollectorVisitor(ConfigurationAst robotConfiguration, ClassToInstanceMap<IProjectBean.IBuilder<?>> beanBuilders) {
        super(robotConfiguration, beanBuilders);
    }

    @Override
    public Void visitClearDisplayAction(ClearDisplayAction<Void> clearDisplayAction) {
        return null;
    }

    @Override
    public Void visitKeysSensor(KeysSensor<Void> keysSensor) {
        return null;
    }

    @Override
    public Void visitEncoderSensor(EncoderSensor<Void> encoderSensor) {
        return null;
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
    public Void visitUltrasonicSensor(UltrasonicSensor<Void> ultrasonicSensor) {
        return null;
    }

    @Override
    public Void visitQuadRGBSensor(QuadRGBSensor<Void> quadRGBSensor) {
        return null;
    }

    @Override
    public Void visitLightAction(LightAction<Void> lightAction) {
        return null;
    }

    @Override
    public Void visitLightStatusAction(LightStatusAction<Void> lightStatusAction) {
        return null;
    }

    @Override
    public Void visitMotorGetPowerAction(MotorGetPowerAction<Void> motorGetPowerAction) {
        return null;
    }

    @Override
    public Void visitMotorOnAction(MotorOnAction<Void> motorOnAction) {
        return null;
    }

    @Override
    public Void visitMotorSetPowerAction(MotorSetPowerAction<Void> motorSetPowerAction) {
        return null;
    }

    @Override
    public Void visitMotorStopAction(MotorStopAction<Void> motorStopAction) {
        return null;
    }

    @Override
    public Void visitShowTextAction(ShowTextAction<Void> showTextAction) {
        return null;
    }

    @Override
    public Void visitDriveAction(DriveAction<Void> driveAction) {
        hasDifferentialDriveCheck(driveAction);
        hasEncodersOnDifferentialDriveCheck(driveAction);
        checkAndVisitMotionParam(driveAction, driveAction.getParam());
        return null;
    }

    private Void hasDifferentialDriveCheck(Phrase<Void> driveAction) {
        ConfigurationComponent differentialDrive = getDifferentialDrive();
        if ( differentialDrive == null || differentialDrive.getOptProperty("MOTOR_L").equals(differentialDrive.getOptProperty("MOTOR_R")) ) {
            //error has no differentialdrive
            addErrorToPhrase(driveAction, "");
        }
        return null;
    }

    private ConfigurationComponent getDifferentialDrive() {
        Map<String, ConfigurationComponent> configComponents = this.robotConfiguration.getConfigurationComponents();
        for ( ConfigurationComponent component : configComponents.values() ) {
            if ( component.getComponentType().equals("DIFFERENTIALDRIVE") ) {
                return component;
            }
        }
        return null;
    }

    private Void hasEncodersOnDifferentialDriveCheck(Phrase<Void> driveAction) {
        Map<String, ConfigurationComponent> configComponents = this.robotConfiguration.getConfigurationComponents();
        ConfigurationComponent differentialDrive = getDifferentialDrive();
        if(differentialDrive == null){
            //error has no differentialdrive
            addErrorToPhrase(driveAction, "");
            return null;
        }
        int numLeftMotors = 0;
        int numRightMotors = 0;
        for ( ConfigurationComponent component : configComponents.values() ) {
            if ( component.getComponentType().equals("ENCODER") ) {
                if ( (component.getOptProperty("PORT1").equals(differentialDrive.getOptProperty("MOTOR_R"))) ) {
                    numRightMotors++;
                } else if ( component.getOptProperty("PORT1").equals(differentialDrive.getOptProperty("MOTOR_L")) ) {
                    numLeftMotors++;
                }
            }
            if ( numRightMotors > 1 || numLeftMotors > 1 ) {
                addErrorToPhrase(driveAction, "");
                return null;
            }
        }
        if ( numRightMotors != 1 || numLeftMotors != 1 ) {
            addErrorToPhrase(driveAction, "CONFIGURATION_ERROR_MOTOR_MISSING");
        }
        return null;
    }

    protected void checkAndVisitMotionParam(Action<Void> action, MotionParam<Void> param) {
        MotorDuration<Void> duration = param.getDuration();
        Expr<Void> speed = param.getSpeed();

        requiredComponentVisited(action, speed);

        if ( duration != null ) {
            requiredComponentVisited(action, duration.getValue());
            checkForZeroSpeed(action, speed);
        }
    }


    protected void checkForZeroSpeed(Action<Void> action, Expr<Void> speed) {
        if ( speed.getKind().hasName("NUM_CONST") ) {
            if ( Math.abs(Double.parseDouble(((NumConst<Void>) speed).getValue())) < 1E-7 ) {
                addWarningToPhrase(action, "MOTOR_SPEED_0");
            }
        }
    }

    @Override
    public Void visitCurveAction(CurveAction<Void> curveAction) {
        if ( curveAction.getParamLeft().getDuration() != null ) {
            this.getBuilder(UsedMethodBean.Builder.class).addUsedMethod(Mbot2Methods.DIFFDRIVEFOR);
        }
        return null;
    }

    @Override
    public Void visitToneAction(ToneAction<Void> toneAction) {
        return null;
    }

    @Override
    public Void visitPlayNoteAction(PlayNoteAction<Void> playNoteAction) {
        return null;
    }

    @Override
    public Void visitTurnAction(TurnAction<Void> turnAction) {
        return null;
    }

    @Override
    public Void visitMotorDriveStopAction(MotorDriveStopAction<Void> stopAction) {
        return null;
    }

    @Override
    public Void visitTimerSensor(TimerSensor<Void> timerSensor) {
        return null;
    }

    @Override
    public Void visitVolumeAction(VolumeAction<Void> volumeAction) {
        return null;
    }

    @Override
    public Void visitPlayFileAction(PlayFileAction<Void> playFileAction) {
        return null;
    }
}
