package de.fhg.iais.roberta.syntax.action.mbot2;

import java.util.List;

import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.blockly.generated.Field;
import de.fhg.iais.roberta.blockly.generated.Value;
import de.fhg.iais.roberta.syntax.BlockType;
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
import de.fhg.iais.roberta.transformer.NepoField;
import de.fhg.iais.roberta.transformer.NepoPhrase;
import de.fhg.iais.roberta.transformer.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.dbc.Assert;

@NepoPhrase(containerType = "QUADRGB_LIGHT_ACTION")
public class QuadRGBLightOnAction<V> extends Action<V> implements WithUserDefinedPort<V> {
    @NepoValue(name = BlocklyConstants.COLOR, type = BlocklyType.COLOR)
    public final Expr<V> color;
    @NepoField(name = BlocklyConstants.ACTORPORT, value = BlocklyConstants.EMPTY_PORT)
    public final String port;

    public QuadRGBLightOnAction(BlockType kind, BlocklyBlockProperties properties, BlocklyComment comment, Expr<V> color, String port) {
        super(kind, properties, comment);
        Assert.notNull(color);
        Assert.nonEmptyString(port);
        this.color = color;
        this.port = port;
        setReadOnly();
    }

    /**
     * Creates instance of {@link QuadRGBLightOnAction}. This instance is read only and can not be modified.
     *
     * @param properties of the block (see {@link BlocklyBlockProperties}),
     * @param comment added from the user,
     * @return read only object of class {@link QuadRGBLightOnAction}
     */
    public static <V> QuadRGBLightOnAction<V> make(BlocklyBlockProperties properties, BlocklyComment comment, Expr<V> color, String port) {
        return new QuadRGBLightOnAction<>(BlockTypeContainer.getByName("QUADRGB_LIGHT_ACTION"), properties, comment, color, port);
    }

    public Expr<V> getColor() {
        return this.color;
    }

    @Override
    public String getUserDefinedPort() {
        return this.port;
    }
}
