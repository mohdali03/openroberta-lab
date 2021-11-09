package de.fhg.iais.roberta.visitor;

import java.util.List;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.CodeGeneratorSetupBean;
import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.ConfigurationComponent;
import de.fhg.iais.roberta.inter.mode.action.ILanguage;
import de.fhg.iais.roberta.syntax.MotorDuration;
import de.fhg.iais.roberta.syntax.Phrase;
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
import de.fhg.iais.roberta.syntax.lang.blocksequence.MainTask;
import de.fhg.iais.roberta.syntax.lang.expr.ConnectConst;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.lang.expr.NumConst;
import de.fhg.iais.roberta.syntax.lang.expr.StringConst;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtList;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitTimeStmt;
import de.fhg.iais.roberta.syntax.sensor.generic.EncoderSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.KeysSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.visitor.lang.codegen.prog.AbstractPythonVisitor;
import de.fhg.iais.roberta.syntax.SC;

/**
 * This class is implementing {@link IVisitor}. All methods are implemented and they append a human-readable Python code representation of a phrase to a
 * StringBuilder. <b>This representation is correct Python code.</b> <br>
 */
public final class Mbot2PythonVisitor extends AbstractPythonVisitor implements IMbot2Visitor<Void> {

    private ILanguage language;
    private ConfigurationAst configurationAst;
    private String rightMotorPort;

    /**
     * initialize the Python code generator visitor.
     *
     * @param programPhrases to generate the code from
     */
    public Mbot2PythonVisitor(
        List<List<Phrase<Void>>> programPhrases,
        ILanguage language,
        ClassToInstanceMap<IProjectBean> beans,
        ConfigurationAst configurationAst) {
        super(programPhrases, beans);
        this.configurationAst = configurationAst;
        this.language = language;
        setRightMotorPort();
    }


    @Override
    public Void visitKeysSensor(KeysSensor<Void> keysSensor) {
        this.sb.append("cyberpi.controller.is_press(");
        String port = keysSensor.getUserDefinedPort();
        ConfigurationComponent configurationComponent = this.configurationAst.getConfigurationComponent(port);
        String pin1 = configurationComponent.getProperty("PIN1");

        this.sb.append("\"").append(pin1).append("\"");
        this.sb.append(")");
        return null;
    }

    @Override
    public Void visitEncoderSensor(EncoderSensor<Void> encoderSensor) {
        String port = getPortFromConfig(encoderSensor.getUserDefinedPort());
        if ( encoderSensor.getSensorMetaDataBean().getMode().equals("RESET") ) {
            this.sb.append("mbot2.EM_reset_angle(\"").append(port).append("\") ");
        } else {
            this.sb.append("mbot2.EM_get_angle(\"").append(port).append("\")");
        }
        return null;
    }

    @Override
    protected void generateProgramPrefix(boolean withWrapping) {
        if ( !withWrapping ) {
            return;
        }
        this.sb.append("import cyberpi, mbot2");
        nlIndent();
        this.sb.append("import math");
        nlIndent();
        this.sb.append("import time"); //TODO maybe only if time is required
    }

    @Override
    public Void visitMainTask(MainTask<Void> mainTask) {
        StmtList<Void> variables = mainTask.getVariables();
        variables.accept(this);
        generateUserDefinedMethods();
        nlIndent();
        this.sb.append("def run():");
        incrIndentation();
        if ( !this.usedGlobalVarInFunctions.isEmpty() ) {
            this.sb.append("global ").append(String.join(", ", this.usedGlobalVarInFunctions));
        }

        return null;
    }

    @Override
    protected void generateProgramSuffix(boolean withWrapping) {
        if ( !withWrapping ) {
            return;
        }
        decrIndentation(); // everything is still indented from main program
        nlIndent();
        nlIndent();
        this.sb.append("def main():");
        incrIndentation();
        nlIndent();
        this.sb.append("try:");
        incrIndentation();
        nlIndent();
        this.sb.append("run()");
        decrIndentation();
        nlIndent();
        this.sb.append("except Exception as e:");
        incrIndentation();
        nlIndent();
        this.sb.append("raise");
        decrIndentation();
        decrIndentation();
        nlIndent();

        if ( !this.getBean(CodeGeneratorSetupBean.class).getUsedMethods().isEmpty() ) {
            String helperMethodImpls =
                this
                    .getBean(CodeGeneratorSetupBean.class)
                    .getHelperMethodGenerator()
                    .getHelperMethodDefinitions(this.getBean(CodeGeneratorSetupBean.class).getUsedMethods());
            this.sb.append(helperMethodImpls);
        }

        nlIndent();
        this.sb.append("main()");
    }

    @Override
    public Void visitClearDisplayAction(ClearDisplayAction<Void> clearDisplayAction) {
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

    private String getPortFromConfig(String name) {
        ConfigurationComponent block = configurationAst.getConfigurationComponent(name);
        return block.getComponentProperties().get("PORT1");
    }

    @Override
    public Void visitMotorOnAction(MotorOnAction<Void> motorOnAction) {
        float speed = getSpeed(motorOnAction.getParam().getSpeed());
        MotorDuration<Void> distance = motorOnAction.getParam().getDuration();
        String port = getPortFromConfig(motorOnAction.getUserDefinedPort());
        if ( distance == null ) {
            sb.append("mbot2.EM_set_speed(");
            sb.append(speed);
            sb.append(", \"").append(port).append("\")");
        } else {
            float angle = Float.parseFloat(((NumConst) distance.getValue()).getValue());
            if ( distance.getType().toString().equals("ROTATIONS") ) {
                angle *= 360;
            }
            sb.append("mbot2.EM_turn(");
            sb.append(angle).append(", ").append(speed).append(", \"").append(port).append("\")");
        }
        return null;
    }

    @Override
    public Void visitMotorSetPowerAction(MotorSetPowerAction<Void> motorSetPowerAction) {
        return null;
    }

    @Override
    public Void visitMotorStopAction(MotorStopAction<Void> motorStopAction) {
        String port = getPortFromConfig(motorStopAction.getUserDefinedPort());
        this.sb.append("mbot2.EM_stop(\"").append(port).append("\")");
        return null;
    }

    @Override
    public Void visitShowTextAction(ShowTextAction<Void> showTextAction) {

        String text = ((StringConst) showTextAction.msg).getValue();
        int lines = Integer.parseInt(((NumConst) showTextAction.y).getValue());
        int rows = Integer.parseInt(((NumConst) showTextAction.x).getValue());

        int xPixel = 8 * rows + 5;
        int yPixel = 17* lines;
        this.sb.append("cyberpi.display.show_label(");
        this.sb.append("\"").append(text).append("\", ");
        this.sb.append("16, ");
        this.sb.append("int(").append(xPixel).append("), ");
        this.sb.append("int(").append(yPixel).append("))");

        return null;
    }

    private void appendDriveAction(float speedEm1, float speedEm2) {
        sb.append("mbot2.drive_speed(");
        if ( rightMotorPort.equals("EM1") ) { // the right motor needs to be inverted due to construction of the mbot2
            sb.append(-speedEm1).append(", ").append(speedEm2).append(")");
        } else {
            sb.append(speedEm1).append(", ").append(-speedEm2).append(")");
        }
    }

    private void appendDriveAction(float speedEm1, float speedEm2, float distance) {
        appendDriveAction(speedEm1, speedEm2);
        nlIndent();
        sb.append("time.sleep(");
        sb.append(calculateWaitTime(speedEm1, speedEm2, distance));
        sb.append(")");
        nlIndent();
        sb.append("mbot2.EM_stop()");
    }

    private double calculateWaitTime(float speedEm1, float speedEm2, float distance) {
        ConfigurationComponent diffDrive = getDiffDrive();
        double circumference = Double.parseDouble(diffDrive.getComponentProperties().get("BRICK_WHEEL_DIAMETER")) * Math.PI;
        double trackWidth = Double.parseDouble(diffDrive.getComponentProperties().get("BRICK_TRACK_WIDTH"));
        double speedL;
        double speedR;
        if ( rightMotorPort.equals("EM1") ) {
            speedR = speedEm1 * circumference / 60;
            speedL = speedEm2 * circumference / 60;
        } else {
            speedL = speedEm1 * circumference / 60;
            speedR = speedEm2 * circumference / 60;
        }
        double r = ((speedL + speedR) * trackWidth) / 2;
        double w = (speedR / speedL) / trackWidth;
        return Math.abs(distance / (r * w));
    }

    private ConfigurationComponent getDiffDrive() {
        for ( ConfigurationComponent component : this.configurationAst.getConfigurationComponents().values() ) {
            if ( component.getComponentType().equals("DIFFERENTIALDRIVE") ) {
                return component;
            }
        }
        return null;
    }

    private void setRightMotorPort() {
        if ( rightMotorPort == null ) {
            ConfigurationComponent diffDrive = getDiffDrive();
            if ( diffDrive != null ) {
                rightMotorPort = diffDrive.getOptProperty("MOTOR_R");
            } else {
                rightMotorPort = "EM2"; //default if something in validation went wrong this should never happen
            }
        }
    }

    @Override
    public Void visitDriveAction(DriveAction<Void> driveAction) {
        String direction = driveAction.getDirection().toString().equals(SC.FOREWARD) ? "forward" : "backward";
        if ( driveAction.getParam().getDuration() != null ) {
            appendMotorOnForAction(driveAction, direction);
        } else {
            appendMotorOnAction(driveAction, direction);
        }

        return null;
    }

    private void appendMotorOnAction(DriveAction<Void> driveAction, String direction) {
        float speed = getSpeed(driveAction.getParam().getSpeed());
        if ( direction.equals("backward") ) {
            speed = -speed;
        }
        appendDriveAction(speed, speed);
    }

    private void appendMotorOnForAction(DriveAction<Void> driveAction, String direction) {
        this.sb.append("mbot2.");
        this.sb.append("straight(");
        if ( (direction.equals("backward") && rightMotorPort.equals("EM2")) || (direction.equals("forward") && rightMotorPort.equals("EM1")) ) {
            this.sb.append("-");
        }
        driveAction.getParam().getDuration().getValue().accept(this);
        this.sb.append(", ");
        this.sb.append(getSpeed(driveAction.getParam().getSpeed()));
        this.sb.append(")");
    }

    private <V> float getSpeed(Expr<V> speedExpression) {
        return Float.parseFloat(((NumConst<Void>) speedExpression).getValue()) * 2;
    }

    @Override
    public Void visitMotorDriveStopAction(MotorDriveStopAction<Void> stopAction) {
        this.sb.append("mbot2.EM_stop()");
        return null;
    }

    @Override
    public Void visitTurnAction(TurnAction<Void> turnAction) {
        String direction = turnAction.getDirection().toString().toLowerCase();

        if ( turnAction.getParam().getDuration() != null ) {
            appendTurnForAction(turnAction, direction);
        } else {
            appendTurnAction(turnAction, direction);
        }
        return null;
    }

    private void appendTurnAction(TurnAction<Void> turnAction, String direction) {
        float speed = getSpeed(turnAction.getParam().getSpeed());
        if ( direction.equals("left") ) {
            speed = -speed;
        }
        sb.append("mbot2.drive_speed(").append(speed).append(", ").append(speed).append(")");
    }

    private void appendTurnForAction(TurnAction<Void> turnAction, String direction) {
        this.sb.append("mbot2.turn(");
        if ( direction.equals("left") ) {
            this.sb.append("-");
        }
        turnAction.getParam().getDuration().getValue().accept(this);
        this.sb.append(", ");
        this.sb.append(getSpeed(turnAction.getParam().getSpeed()));
        this.sb.append(")");
    }

    @Override
    public Void visitCurveAction(CurveAction<Void> curveAction) {
        int direction = curveAction.getDirection().toString().equals(SC.FOREWARD) ? 1 : -1;
        float speedL = direction * getSpeed(curveAction.getParamLeft().getSpeed());
        float speedR = direction * getSpeed(curveAction.getParamRight().getSpeed());
        float speedEm1;
        float speedEm2;
        if ( rightMotorPort.equals("EM1") ) {
            speedEm1 = speedR;
            speedEm2 = speedL;
        } else {
            speedEm1 = speedL;
            speedEm2 = speedR;
        }
        MotorDuration duration = curveAction.getParamLeft().getDuration();
        float distance;
        if ( duration != null ) {
            distance = Float.parseFloat(((NumConst) duration.getValue()).getValue());
            appendDriveAction(speedEm1, speedEm2, distance);
        } else {
            appendDriveAction(speedEm1, speedEm2);
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
    public Void visitConnectConst(ConnectConst<Void> connectConst) {
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

    @Override
    public Void visitWaitStmt(WaitStmt<Void> waitStmt) {
        this.sb.append("while True:");
        incrIndentation();
        visitStmtList(waitStmt.getStatements());
        decrIndentation();
        return null;
    }

    @Override
    public Void visitWaitTimeStmt(WaitTimeStmt<Void> waitTimeStmt) {
        this.sb.append("time.sleep(");
        double time = Double.parseDouble(((NumConst) waitTimeStmt.getTime()).getValue()) / 100;
        this.sb.append(time);

        this.sb.append(")");
        return null;
    }


}