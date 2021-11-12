package de.fhg.iais.roberta.visitor;

import de.fhg.iais.roberta.bean.UsedMethodBean;
import de.fhg.iais.roberta.visitor.collect.AbstractUsedMethodCollectorVisitor;

public class Mbot2UsedMethodCollectorVisitor extends AbstractUsedMethodCollectorVisitor {
    public Mbot2UsedMethodCollectorVisitor(UsedMethodBean.Builder builder) {
        super(builder);
    }
}
