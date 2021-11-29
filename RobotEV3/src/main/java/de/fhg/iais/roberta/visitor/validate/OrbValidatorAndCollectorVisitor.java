package de.fhg.iais.roberta.visitor.validate;

import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.ConfigurationComponent;
import de.fhg.iais.roberta.components.UsedActor;
import de.fhg.iais.roberta.components.UsedSensor;
import de.fhg.iais.roberta.syntax.BlocklyConstants;
import de.fhg.iais.roberta.syntax.SC;
import de.fhg.iais.roberta.syntax.action.display.ClearDisplayAction;
import de.fhg.iais.roberta.syntax.action.display.ShowTextAction;
import de.fhg.iais.roberta.syntax.action.light.LightAction;
import de.fhg.iais.roberta.syntax.action.light.LightStatusAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorGetPowerAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorOnAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorSetPowerAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorStopAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.CurveAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.DriveAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.MotorDriveStopAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.TurnAction;
import de.fhg.iais.roberta.syntax.action.sound.PlayFileAction;
import de.fhg.iais.roberta.syntax.action.sound.PlayNoteAction;
import de.fhg.iais.roberta.syntax.action.sound.ToneAction;
import de.fhg.iais.roberta.syntax.action.sound.VolumeAction;
import de.fhg.iais.roberta.syntax.lang.expr.NumConst;
import de.fhg.iais.roberta.syntax.sensor.ExternalSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.ColorSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.CompassSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.EncoderSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.GyroSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.InfraredSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.KeysSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TouchSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.UltrasonicSensor;
import de.fhg.iais.roberta.visitor.IOrbVisitor;
import de.fhg.iais.roberta.visitor.hardware.actor.DifferentialMotorValidatorAndCollectorVisitor;

public class OrbValidatorAndCollectorVisitor extends CommonNepoValidatorAndCollectorVisitor implements IOrbVisitor<Void> {

	private final DifferentialMotorValidatorAndCollectorVisitor differentialMotorValidatorAndCollectorVisitor;

	public OrbValidatorAndCollectorVisitor(ConfigurationAst robotConfiguration, ClassToInstanceMap<IProjectBean.IBuilder<?>> beanBuilders) {
		super(robotConfiguration, beanBuilders);
		this.differentialMotorValidatorAndCollectorVisitor = new DifferentialMotorValidatorAndCollectorVisitor(this, robotConfiguration, beanBuilders);
	}

	@Override
	public Void visitClearDisplayAction(ClearDisplayAction<Void> clearDisplayAction) {
		usedHardwareBuilder.addUsedActor(new UsedActor(BlocklyConstants.EMPTY_PORT, SC.DISPLAY));
		return null;
	}

	@Override
	public Void visitColorSensor(ColorSensor<Void> colorSensor) {
		checkSensorPort(colorSensor);
		usedHardwareBuilder.addUsedSensor(new UsedSensor(colorSensor.getUserDefinedPort(), SC.COLOR, colorSensor.getMode()));
		return null;
	}

	@Override
	public Void visitCompassSensor(CompassSensor<Void> compassSensor) {
		checkSensorPort(compassSensor);
		usedHardwareBuilder.addUsedSensor(new UsedSensor(compassSensor.getUserDefinedPort(), SC.COMPASS, compassSensor.getMode()));
		return null;
	}

	@Override
	public Void visitCurveAction(CurveAction<Void> curveAction) {
		return differentialMotorValidatorAndCollectorVisitor.visitCurveActionForDiff(curveAction);
	}

	private ConfigurationComponent getDifferentialDrive() {//TODO:
		Map<String, ConfigurationComponent> configComponents = this.robotConfiguration.getConfigurationComponents();
		for ( ConfigurationComponent component : configComponents.values() ) {
			if ( component.getComponentType().equals("DIFFERENTIALDRIVE") ) {
				return component;
			}
		}
		return null;
	}

	@Override
	public Void visitDriveAction(DriveAction<Void> driveAction) {
		return differentialMotorValidatorAndCollectorVisitor.visitDriveActionForDiff(driveAction);
	}

	@Override
	public Void visitEncoderSensor(EncoderSensor<Void> encoderSensor) {
		ConfigurationComponent configurationComponent = this.robotConfiguration.optConfigurationComponent(encoderSensor.getUserDefinedPort());
		if ( configurationComponent == null ) {
			addErrorToPhrase(encoderSensor, "CONFIGURATION_ERROR_MOTOR_MISSING");
		} else {
			usedHardwareBuilder.addUsedActor(new UsedActor(encoderSensor.getUserDefinedPort(), configurationComponent.getComponentType()));
		}
		return null;
	}

	@Override
	public Void visitGyroSensor(GyroSensor<Void> gyroSensor) {
		checkSensorPort(gyroSensor);
		if ( !gyroSensor.getMode().equals(SC.RESET) ) {
			usedHardwareBuilder.addUsedSensor(new UsedSensor(gyroSensor.getUserDefinedPort(), SC.GYRO, gyroSensor.getMode()));
		}
		return null;
	}

	@Override
	public Void visitInfraredSensor(InfraredSensor<Void> infraredSensor) {
		checkSensorPort(infraredSensor);
		String mode = infraredSensor.getMode();
		if ( infraredSensor.getMode().equals(SC.PRESENCE) ) {
			// TODO Why do we do this ?????
			mode = SC.SEEK;
		}
		usedHardwareBuilder.addUsedSensor(new UsedSensor(infraredSensor.getUserDefinedPort(), SC.INFRARED, mode));
		return null;
	}

	@Override
	public Void visitKeysSensor(KeysSensor<Void> keysSensor) {
		// TODO Shouldn't we do this: checkSensorPort(keysSensor);
		return null;
	}

	@Override
	public Void visitLightAction(LightAction<Void> lightAction) {
		optionalComponentVisited(lightAction.getRgbLedColor());
		usedHardwareBuilder.addUsedActor(new UsedActor(lightAction.getPort(), SC.LIGHT));
		return null;
	}

	@Override
	public Void visitLightStatusAction(LightStatusAction<Void> lightStatusAction) {
		usedHardwareBuilder.addUsedActor(new UsedActor(lightStatusAction.getUserDefinedPort(), SC.LIGHT));
		return null;
	}

	@Override
	public Void visitMotorDriveStopAction(MotorDriveStopAction<Void> stopAction) {
		return differentialMotorValidatorAndCollectorVisitor.visitMotorDriveStopAction(stopAction);
	}

	@Override
	public Void visitMotorGetPowerAction(MotorGetPowerAction<Void> motorGetPowerAction) {
		return differentialMotorValidatorAndCollectorVisitor.visitMotorGetPowerAction(motorGetPowerAction);
	}

	@Override
	public Void visitMotorOnAction(MotorOnAction<Void> motorOnAction) {
		return differentialMotorValidatorAndCollectorVisitor.visitMotorOnAction(motorOnAction);
	}

	@Override
	public Void visitMotorSetPowerAction(MotorSetPowerAction<Void> motorSetPowerAction) {
		return differentialMotorValidatorAndCollectorVisitor.visitMotorSetPowerAction(motorSetPowerAction);
	}

	@Override
	public Void visitMotorStopAction(MotorStopAction<Void> motorStopAction) {
		return differentialMotorValidatorAndCollectorVisitor.visitMotorStopAction(motorStopAction);
	}

	@Override
	public Void visitPlayFileAction(PlayFileAction<Void> playFileAction) {
		usedHardwareBuilder.addUsedActor(new UsedActor(BlocklyConstants.EMPTY_PORT, SC.SOUND));
		return null;
	}

	@Override
	public Void visitPlayNoteAction(PlayNoteAction<Void> playNoteAction) {
		usedHardwareBuilder.addUsedActor(new UsedActor(playNoteAction.getPort(), SC.SOUND));
		return null;
	}


	@Override
	public Void visitShowTextAction(ShowTextAction<Void> showTextAction) {
		requiredComponentVisited(showTextAction, showTextAction.msg, showTextAction.x, showTextAction.y);
		usedHardwareBuilder.addUsedActor(new UsedActor(showTextAction.port, SC.DISPLAY));
		return null;
	}


	@Override
	public Void visitTimerSensor(TimerSensor<Void> timerSensor) {
		usedHardwareBuilder.addUsedSensor(new UsedSensor(timerSensor.getUserDefinedPort(), SC.TIMER, timerSensor.getMode()));
		return null;
	}

	@Override
	public Void visitToneAction(ToneAction<Void> toneAction) {
		requiredComponentVisited(toneAction, toneAction.getDuration(), toneAction.getFrequency());

		if ( toneAction.getDuration().getKind().hasName("NUM_CONST") ) {
			double toneActionConst = Double.parseDouble(((NumConst<Void>) toneAction.getDuration()).getValue());
			if ( toneActionConst <= 0 ) {
				addWarningToPhrase(toneAction, "BLOCK_NOT_EXECUTED");
			}
		}
		usedHardwareBuilder.addUsedActor(new UsedActor(toneAction.getPort(), SC.SOUND));
		return null;
	}

	@Override
	public Void visitTouchSensor(TouchSensor<Void> touchSensor) {
		checkSensorPort(touchSensor);
		usedHardwareBuilder.addUsedSensor(new UsedSensor(touchSensor.getUserDefinedPort(), SC.TOUCH, touchSensor.getMode()));
		return null;
	}

	@Override
	public Void visitTurnAction(TurnAction<Void> turnAction) {
		return differentialMotorValidatorAndCollectorVisitor.visitTurnActionForDiff(turnAction);
	}

	@Override
	public Void visitUltrasonicSensor(UltrasonicSensor<Void> ultrasonicSensor) {
		checkSensorPort(ultrasonicSensor);
		usedHardwareBuilder.addUsedSensor(new UsedSensor(ultrasonicSensor.getUserDefinedPort(), SC.ULTRASONIC, ultrasonicSensor.getMode()));
		return null;
	}

	@Override
	public Void visitVolumeAction(VolumeAction<Void> volumeAction) {
		if ( volumeAction.getMode() == VolumeAction.Mode.SET ) {
			requiredComponentVisited(volumeAction, volumeAction.getVolume());
		}
		usedHardwareBuilder.addUsedActor(new UsedActor(BlocklyConstants.EMPTY_PORT, SC.SOUND));
		return null;
	}

	protected void checkSensorPort(ExternalSensor<Void> sensor) {
		ConfigurationComponent usedSensor = this.robotConfiguration.optConfigurationComponent(sensor.getUserDefinedPort());
		if ( usedSensor == null ) {
			addErrorToPhrase(sensor, "CONFIGURATION_ERROR_SENSOR_MISSING");
		} else {
			String type = usedSensor.getComponentType();
			switch ( sensor.getKind().getName() ) {
				case "COLOR_SENSING":
					if ( !type.equals("COLOR") ) {
						addErrorToPhrase(sensor, "CONFIGURATION_ERROR_SENSOR_WRONG");
					}
					break;
				case "TOUCH_SENSING":
					if ( !type.equals("TOUCH") ) {
						addErrorToPhrase(sensor, "CONFIGURATION_ERROR_SENSOR_WRONG");
					}
					break;
				case "ULTRASONIC_SENSING":
					if ( !type.equals("ULTRASONIC") ) {
						addErrorToPhrase(sensor, "CONFIGURATION_ERROR_SENSOR_WRONG");
					}
					break;
				case "INFRARED_SENSING":
					if ( !type.equals("INFRARED") ) {
						addErrorToPhrase(sensor, "CONFIGURATION_ERROR_SENSOR_WRONG");
					}
					break;
				case "GYRO_SENSING":
					if ( !type.equals("GYRO") ) {
						addErrorToPhrase(sensor, "CONFIGURATION_ERROR_SENSOR_WRONG");
					}
					break;
				case "SOUND_SENSING":
					if ( !type.equals("SOUND") ) {
						addErrorToPhrase(sensor, "CONFIGURATION_ERROR_SENSOR_WRONG");
					}
					break;
				case "LIGHT_SENSING":
					if ( !type.equals("LIGHT") ) {
						addErrorToPhrase(sensor, "CONFIGURATION_ERROR_SENSOR_WRONG");
					}
					break;
				case "COMPASS_SENSING":
					if ( !type.equals("COMPASS") ) {
						addErrorToPhrase(sensor, "CONFIGURATION_ERROR_SENSOR_WRONG");
					}
					break;
				case "IRSEEKER_SENSING":
					if ( !type.equals("IRSEEKER") ) {
						addErrorToPhrase(sensor, "CONFIGURATION_ERROR_SENSOR_WRONG");
					}
					break;
				case "HTCOLOR_SENSING":
					if ( !type.equals("HT_COLOR") ) {
						addErrorToPhrase(sensor, "CONFIGURATION_ERROR_SENSOR_WRONG");
					}
					break;
				default:
					break;
			}
		}
	}

}
