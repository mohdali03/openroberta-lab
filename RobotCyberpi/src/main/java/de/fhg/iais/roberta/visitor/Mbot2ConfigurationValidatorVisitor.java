package de.fhg.iais.roberta.visitor;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.syntax.action.Action;

import de.fhg.iais.roberta.visitor.validate.AbstractBrickValidatorVisitor;

public final class Mbot2ConfigurationValidatorVisitor extends AbstractBrickValidatorVisitor implements IMbot2Visitor<Void> {

    private boolean main = false;

    public Mbot2ConfigurationValidatorVisitor(ConfigurationAst brickConfiguration, ClassToInstanceMap<IProjectBean.IBuilder<?>> beanBuilders) {
        super(brickConfiguration, beanBuilders);
    }

}
