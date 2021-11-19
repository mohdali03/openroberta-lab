package de.fhg.iais.roberta.syntax.action.mbot2;

import java.util.List;

import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.blockly.generated.Field;
import de.fhg.iais.roberta.blockly.generated.Value;
import de.fhg.iais.roberta.syntax.BlockTypeContainer;
import de.fhg.iais.roberta.syntax.BlocklyBlockProperties;
import de.fhg.iais.roberta.syntax.BlocklyComment;
import de.fhg.iais.roberta.syntax.BlocklyConstants;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.WithUserDefinedPort;
import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.transformer.Ast2Jaxb;
import de.fhg.iais.roberta.transformer.ExprParam;
import de.fhg.iais.roberta.transformer.Jaxb2Ast;
import de.fhg.iais.roberta.transformer.Jaxb2ProgramAst;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.dbc.Assert;

public class CyberpiLedBrightnessAction<V> extends Action<V> implements WithUserDefinedPort<V> {
    private final Expr<V> brightness;
    private final String port;

    private CyberpiLedBrightnessAction(Expr<V> brightness, BlocklyBlockProperties properties, BlocklyComment comment, String port) {
        super(BlockTypeContainer.getByName("CYBERPI_SET_BRIGHTNESS_ACTION"), properties, comment);
        Assert.notNull(brightness);
        Assert.notNull(port);
        this.brightness = brightness;
        this.port = port;
        setReadOnly();
    }

    /**
     * Creates instance of {@link CyberpiLedBrightnessAction}. This instance is read only and can not be modified.
     *
     * @param properties of the block (see {@link BlocklyBlockProperties}),
     * @param comment added from the user,
     * @return read only object of class {@link CyberpiLedBrightnessAction}
     */
    private static <V> CyberpiLedBrightnessAction<V> make(Expr<V> brightness, BlocklyBlockProperties properties, BlocklyComment comment, String port) {
        return new CyberpiLedBrightnessAction<>(brightness, properties, comment, port);
    }

    public Expr<V> getBrightness() {
        return this.brightness;
    }

    @Override
    public String getUserDefinedPort() {
        return this.port;
    }

    @Override
    public String toString() {
        return "Ultrasonic2Led [" + this.brightness + ", " + "]";
    }

    /**
     * Transformation from JAXB object to corresponding AST object.
     *
     * @param block for transformation
     * @param helper class for making the transformation
     * @return corresponding AST object
     */
    public static <V> Phrase<V> jaxbToAst(Block block, Jaxb2ProgramAst<V> helper) {
        List<Value> values = Jaxb2Ast.extractValues(block, (short) 1);
        List<Field> fields = Jaxb2Ast.extractFields(block, (short) 2);

        Phrase<V> brightness = helper.extractValue(values, new ExprParam(BlocklyConstants.BRIGHTNESS, BlocklyType.NUMBER_INT));
        String port = Jaxb2Ast.extractField(fields, BlocklyConstants.ACTORPORT);
        return CyberpiLedBrightnessAction.make(Jaxb2Ast.convertPhraseToExpr(brightness), Jaxb2Ast.extractBlockProperties(block), Jaxb2Ast.extractComment(block), port);
    }

    @Override
    public Block astToBlock() {
        Block jaxbDestination = new Block();
        Ast2Jaxb.setBasicProperties(this, jaxbDestination);
        Ast2Jaxb.addValue(jaxbDestination, BlocklyConstants.BRIGHTNESS, this.brightness);
        Ast2Jaxb.addField(jaxbDestination, BlocklyConstants.ACTORPORT, this.port);

        return jaxbDestination;
    }
}
