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

@NepoPhrase(containerType = "ULTRASONIC2_LIGHT_ACTION")
public class Ultrasonic2LEDAction<V> extends Action<V> implements WithUserDefinedPort<V> {
    @NepoField(name = BlocklyConstants.ACTORPORT, value = BlocklyConstants.EMPTY_PORT)
    public final String port;
    @NepoField(name = BlocklyConstants.LED, value = BlocklyConstants.EMPTY_PORT)
    public final String led;
    @NepoValue(name = BlocklyConstants.BRIGHTNESS, type = BlocklyType.NUMBER_INT)
    public final Expr<V> brightness;

    public Ultrasonic2LEDAction(BlockType kind, BlocklyBlockProperties properties, BlocklyComment comment, String port, String led, Expr<V> brightness) {
        super(kind, properties, comment);
        Assert.notNull(brightness);
        Assert.nonEmptyString(port);
        Assert.nonEmptyString(led);
        this.brightness = brightness;
        this.port = port;
        this.led = led;
        setReadOnly();
    }

    /**
     * Creates instance of {@link Ultrasonic2LEDAction}. This instance is read only and can not be modified.
     *
     * @param properties of the block (see {@link BlocklyBlockProperties}),
     * @param comment added from the user,
     * @return read only object of class {@link Ultrasonic2LEDAction}
     */
    public static <V> Ultrasonic2LEDAction<V> make(BlocklyBlockProperties properties, BlocklyComment comment, String port, String led, Expr<V> brightness) {
        return new Ultrasonic2LEDAction<>(BlockTypeContainer.getByName("ULTRASONIC2_LIGHT_ACTION"), properties, comment, port, led, brightness);
    }

    public Expr<V> getBrightness() {
        return this.brightness;
    }

    @Override
    public String getUserDefinedPort() {
        return this.port;
    }

    public String getLed() {
        return this.led;
    }

    public String getLedNumber() {
        return led.replace("LED", "");
    }
}
