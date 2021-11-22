package de.fhg.iais.roberta.syntax.action.mbot2;

import java.util.List;

import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.blockly.generated.Field;
import de.fhg.iais.roberta.blockly.generated.Hide;
import de.fhg.iais.roberta.syntax.BlockType;
import de.fhg.iais.roberta.syntax.BlockTypeContainer;
import de.fhg.iais.roberta.syntax.BlocklyBlockProperties;
import de.fhg.iais.roberta.syntax.BlocklyComment;
import de.fhg.iais.roberta.syntax.BlocklyConstants;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.WithUserDefinedPort;
import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.transformer.Ast2Jaxb;
import de.fhg.iais.roberta.transformer.Jaxb2Ast;
import de.fhg.iais.roberta.transformer.Jaxb2ProgramAst;
import de.fhg.iais.roberta.transformer.NepoField;
import de.fhg.iais.roberta.transformer.NepoHide;
import de.fhg.iais.roberta.transformer.NepoPhrase;
import de.fhg.iais.roberta.util.dbc.Assert;

/**
 * This class represents the <b>robActions_play_recording</b> block from Blockly into the AST (abstract syntax tree). Object from this class will generate code for
 * stopping every movement of the robot.<br/>
 * <br/>
 */
@NepoPhrase(containerType = "PLAY_RECORDING_ACTION")
public class PlayRecordingAction<V> extends Action<V> implements WithUserDefinedPort<V> {
    @NepoField(name = BlocklyConstants.ACTORPORT, value = BlocklyConstants.EMPTY_PORT)
    public final String port;

    @NepoHide
    public final Hide hide;

    public PlayRecordingAction(BlockType kind, BlocklyBlockProperties properties, BlocklyComment comment, String port, Hide hide) {
        super(kind, properties, comment);
        Assert.nonEmptyString(port);
        this.hide = hide;
        this.port = port;
        setReadOnly();
    }

    /**
     * Creates instance of {@link PlayRecordingAction}. This instance is read only and can not be modified.
     *
     * @param properties of the block (see {@link BlocklyBlockProperties}),
     * @param comment added from the user,
     * @return read only object of class {@link PlayRecordingAction}
     */
    public static <V> PlayRecordingAction<V> make(BlocklyBlockProperties properties, BlocklyComment comment, String port, Hide hide) {
        return new PlayRecordingAction<>(BlockTypeContainer.getByName("DISPLAY_SET_COLOUR_ACTION"), properties, comment, port, hide);
    }

    @Override
    public String getUserDefinedPort() {
        return this.port;
    }
}
