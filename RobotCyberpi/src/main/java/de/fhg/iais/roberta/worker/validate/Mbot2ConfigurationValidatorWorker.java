package de.fhg.iais.roberta.worker.validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationComponent;
import de.fhg.iais.roberta.components.Project;
import de.fhg.iais.roberta.util.Key;
import de.fhg.iais.roberta.visitor.Mbot2ConfigurationValidatorVisitor;
import de.fhg.iais.roberta.visitor.validate.AbstractBrickValidatorVisitor;
import de.fhg.iais.roberta.worker.AbstractValidatorWorker;

public class Mbot2ConfigurationValidatorWorker extends AbstractValidatorWorker {

    @Override
    protected AbstractBrickValidatorVisitor getVisitor(Project project, ClassToInstanceMap<IProjectBean.IBuilder<?>> beanBuilders) {
        return new Mbot2ConfigurationValidatorVisitor(project.getConfigurationAst(), beanBuilders);
    }

    @Override
    public void execute(Project project) {
        List<String> takenPins = new ArrayList<>();
        project.getConfigurationAst().getConfigurationComponents().forEach((k, v) -> checkConfiguration(v, project, takenPins));

        super.execute(project);
    }

    private static final List<String> NON_BLOCKING_PROPERTIES = Collections.unmodifiableList(Arrays.asList(
        "MOTOR_L",
        "MOTOR_R",
        "BRICK_WHEEL_DIAMETER",
        "BRICK_TRACK_WIDTH"
    ));

    private void checkConfiguration(ConfigurationComponent configurationComponent, Project project, List<String> takenPins){
        checkIfPinTaken(configurationComponent,project,takenPins);
        if(configurationComponent.getComponentType().startsWith("MBUILD_")){
            //checkMBuildLimit(configurationComponent);
        }
    }
    private void checkIfPinTaken(ConfigurationComponent configurationComponent, Project project, List<String> takenPins) {
        Map<String, String> componentProperties = configurationComponent.getComponentProperties();
        for(Map.Entry<String,String> property : componentProperties.entrySet()){
            if( NON_BLOCKING_PROPERTIES.contains(property.getKey())){
                continue;
            }
            if(takenPins.contains(property.getValue())){
                project.setResult(Key.PROGRAM_INVALID_STATEMETNS);
                break;
            }
            takenPins.add(property.getValue());
        }
    }

    /*private void checkMBuildLimit(ConfigurationComponent mBuildPort){
        mBuildPort.getComponentProperties().size();
    }*/
}