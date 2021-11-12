package de.fhg.iais.roberta.worker;

import java.util.Collections;
import java.util.List;

import de.fhg.iais.roberta.bean.UsedMethodBean;
import de.fhg.iais.roberta.visitor.Mbot2Methods;
import de.fhg.iais.roberta.visitor.Mbot2UsedMethodCollectorVisitor;
import de.fhg.iais.roberta.visitor.collect.ICollectorVisitor;

public class Mbot2UsedMethodCollectorWorker extends AbstractUsedMethodCollectorWorker {
    @Override
    protected ICollectorVisitor getVisitor(UsedMethodBean.Builder builder) {
        return new Mbot2UsedMethodCollectorVisitor(builder);
    }

    /**
     * Edison has additional methods that need to be generated.
     *
     * @return the additional methods
     */
    @Override
    protected List<Class<? extends Enum<?>>> getAdditionalMethodEnums() {
        return Collections.singletonList(Mbot2Methods.class);
    }
}
