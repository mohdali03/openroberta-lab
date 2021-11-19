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

public class CyberpiLedOnAction<V> extends Action<V> implements WithUserDefinedPort<V> {
    private final Expr<V> color;
    private final String port;
    private final String led;

    private CyberpiLedOnAction(Expr<V> color, BlocklyBlockProperties properties, BlocklyComment comment, String port, String led) {
        super(BlockTypeContainer.getByName("CYBERPI_LED_ON_ACTION"), properties, comment);
        Assert.notNull(color);
        Assert.notNull(port);
        this.color = color;
        this.port = port;
        this.led = led;
        setReadOnly();
    }

    /**
     * Creates instance of {@link CyberpiLedOnAction}. This instance is read only and can not be modified.
     *
     * @param properties of the block (see {@link BlocklyBlockProperties}),
     * @param comment added from the user,
     * @return read only object of class {@link CyberpiLedOnAction}
     */
    private static <V> CyberpiLedOnAction<V> make(Expr<V> color, BlocklyBlockProperties properties, BlocklyComment comment, String port, String led) {
        return new CyberpiLedOnAction<>(color, properties, comment, port, led);
    }

    public Expr<V> getColor() {
        return this.color;
    }

    public String getLed(){
        return this.led;
    }

    @Override
    public String getUserDefinedPort() {
        return this.port;
    }

    @Override
    public String toString() {
        return "CyberpiLedOn [" + this.color + ", " + this.led + "]";
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

        Phrase<V> color = helper.extractValue(values, new ExprParam(BlocklyConstants.COLOR, BlocklyType.COLOR));
        String port = Jaxb2Ast.extractField(fields, BlocklyConstants.ACTORPORT);
        String led = Jaxb2Ast.extractField(fields, BlocklyConstants.LED);
        return CyberpiLedOnAction.make(Jaxb2Ast.convertPhraseToExpr(color), Jaxb2Ast.extractBlockProperties(block), Jaxb2Ast.extractComment(block), port, led);
    }

    @Override
    public Block astToBlock() {
        Block jaxbDestination = new Block();
        Ast2Jaxb.setBasicProperties(this, jaxbDestination);
        Ast2Jaxb.addValue(jaxbDestination, BlocklyConstants.COLOR, this.color);
        Ast2Jaxb.addField(jaxbDestination, BlocklyConstants.ACTORPORT, this.port);
        Ast2Jaxb.addField(jaxbDestination, BlocklyConstants.LED, this.led);

        return jaxbDestination;
    }
}
