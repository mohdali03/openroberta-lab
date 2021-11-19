package de.fhg.iais.roberta.worker;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.Project;
import de.fhg.iais.roberta.visitor.Mbot2Methods;
import de.fhg.iais.roberta.visitor.Mbot2ValidatorAndCollectorVisitor;
import de.fhg.iais.roberta.visitor.validate.CommonNepoValidatorAndCollectorVisitor;

public class Mbot2ValidatorAndCollectorWorker extends AbstractValidatorAndCollectorWorker {

    @Override
    protected CommonNepoValidatorAndCollectorVisitor getVisitor(
        Project project, ClassToInstanceMap<IProjectBean.IBuilder<?>> beanBuilders) {
        return new Mbot2ValidatorAndCollectorVisitor(project.getConfigurationAst(), beanBuilders);
    }
    @Override
    protected List<Class<? extends Enum<?>>> getAdditionalMethodEnums() {
        return Collections.singletonList(Mbot2Methods.class);
    }

}
