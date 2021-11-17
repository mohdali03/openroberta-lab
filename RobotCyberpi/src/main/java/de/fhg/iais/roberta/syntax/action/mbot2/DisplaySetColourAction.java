package de.fhg.iais.roberta.syntax.action.mbot2;

import java.util.List;

import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.blockly.generated.Value;
import de.fhg.iais.roberta.syntax.BlockTypeContainer;
import de.fhg.iais.roberta.syntax.BlocklyBlockProperties;
import de.fhg.iais.roberta.syntax.BlocklyComment;
import de.fhg.iais.roberta.syntax.BlocklyConstants;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.transformer.Ast2Jaxb;
import de.fhg.iais.roberta.transformer.ExprParam;
import de.fhg.iais.roberta.transformer.Jaxb2Ast;
import de.fhg.iais.roberta.transformer.Jaxb2ProgramAst;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.dbc.Assert;

/**
 * This class represents the <b>robactions_display_set_colour</b> block from Blockly into the AST (abstract syntax tree). Object from this class will generate code for
 * stopping every movement of the robot.<br/>
 * <br/>
 */
public final class DisplaySetColourAction<V> extends Action<V> {
    private final Expr<V> color;
    private DisplaySetColourAction(Expr<V> color,BlocklyBlockProperties properties, BlocklyComment comment) {
        super(BlockTypeContainer.getByName("DISPLAY_SET_COLOUR_ACTION"), properties, comment);
        setReadOnly();
        Assert.notNull(color);
        this.color = color;

    }

    /**
     * Creates instance of {@link DisplaySetColourAction}. This instance is read only and can not be modified.
     *
     * @param properties of the block (see {@link BlocklyBlockProperties}),
     * @param comment added from the user,
     * @return read only object of class {@link DisplaySetColourAction}
     */
    private static <V> DisplaySetColourAction<V> make(Expr<V> color, BlocklyBlockProperties properties, BlocklyComment comment) {
        return new DisplaySetColourAction<>(color, properties, comment);
    }

    public Expr<V> getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        return "setDisplayColor [" + this.color + ", " + "]";
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
        Phrase<V> color = helper.extractValue(values, new ExprParam(BlocklyConstants.COLOR, BlocklyType.COLOR));

        return DisplaySetColourAction.make(Jaxb2Ast.convertPhraseToExpr(color), Jaxb2Ast.extractBlockProperties(block), Jaxb2Ast.extractComment(block));
    }

    @Override
    public Block astToBlock() {
        Block jaxbDestination = new Block();
        Ast2Jaxb.setBasicProperties(this, jaxbDestination);
        Ast2Jaxb.addValue(jaxbDestination, BlocklyConstants.COLOR, this.color);

        return jaxbDestination;
    }
}
