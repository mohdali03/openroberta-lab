import * as LOG from 'log';
import * as UTIL from 'util';
import * as COMM from 'comm';
import * as MSG from 'message';
import * as GUISTATE_C from 'guiState.controller';
import * as LIB from 'neuralnetwork-lib';
import * as d3 from 'd3';
import * as PG from 'neuralnetwork.playground';
import * as $ from 'jquery';
import * as Blockly from 'blockly';
import 'jquery-validate';

function init() {
    $('#tabNN').onWrap(
        'show.bs.tab',
        function (e) {
            GUISTATE_C.setView('tabNN');
        },
        'show tabNN'
    );

    $('#tabNN').onWrap(
        'shown.bs.tab',
        function (e) {
            prepareNNfromNNstep();
        },
        'shown tabNN'
    );

    $('#tabNN').onWrap('hide.bs.tab', function (e) {
        var nnstepBlock = getTheNNstepBlock();
        nnstepBlock.data = PG.getStateAsJSONString();
    }, 'hide tabNN');

    $('#tabNN').onWrap('hidden.bs.tab', function (e) {}, 'hidden tabNN');
}

function prepareNNfromNNstep() {
    var inputNeurons = [];
    var outputNeurons = [];
    var nnStepBlock = getTheNNstepBlock();
    var state = nnStepBlock.data === undefined ? undefined : JSON.parse(nnStepBlock.data);
    extractInputOutputNeurons(inputNeurons, outputNeurons, nnStepBlock.getChildren());
    PG.runPlayground(state, inputNeurons, outputNeurons);
}

function getTheNNstepBlock() {
    var nnstepBlock = null;
    for (const block of Blockly.Workspace.getByContainer('blocklyDiv').getAllBlocks()) {
        if (block.type === 'robActions_NNstep') {
            if (nnstepBlock) {
                LOG.error("more than one NNstep block makes no sense");
            }
            nnstepBlock = block;
        }
    }
    return nnstepBlock;
}

function extractInputOutputNeuronsFromNNstep(inputNeurons, outputNeurons, ) {
    extractInputOutputNeurons(getTheNNstepBlock().getChildren());
}

function extractInputOutputNeurons(inputNeurons, outputNeurons, neurons) {
    for (const block of neurons) {
        if (block.type === 'robActions_inputneuron') {
            inputNeurons.push(block.getFieldValue("NAME"));
        } else if (block.type === 'robActions_outputneuron') {
            outputNeurons.push(block.getFieldValue("NAME"));
        }
        var next = block.getChildren();
        if (next) {
            extractInputOutputNeurons(inputNeurons, outputNeurons, next);
        }
    }
}

function showNN(result) {
    if (result.rc == 'ok') {
        GUISTATE_C.setNN(result);
        LOG.info('show nn ' + GUISTATE_C.getNNName());
    }
}

function reloadNN(opt_result) {
    var nn;
    if (opt_result) {
        nn = opt_result.nnXML;
    } else {
        nn = GUISTATE_C.getNNXML();
    }
    if (!seen) {
        nnToBricklyWorkspace(nn);
        var x, y;
        if ($(window).width() < 768) {
            x = $(window).width() / 50;
            y = 25;
        } else {
            x = $(window).width() / 5;
            y = 50;
        }
        var blocks = bricklyWorkspace.getTopBlocks(true);
        for (var i = 0; i < blocks.length; i++) {
            var coord = Blockly.getSvgXY_(blocks[i].svgGroup_, bricklyWorkspace);
            var coordBlock = blocks[i].getRelativeToSurfaceXY();
            blocks[i].moveBy(coordBlock.x - coord.x + x, coordBlock.y - coord.y + y);
        }
    } else {
        nnToBricklyWorkspace(nn);
    }
}

function reloadView() {
    if (isVisible()) {
        var dom = Blockly.Xml.workspaceToDom(bricklyWorkspace);
        var xml = Blockly.Xml.domToText(dom);
        nnToBricklyWorkspace(xml);
    } else {
        seen = false;
    }
    var toolbox = GUISTATE_C.getNNToolbox();
    bricklyWorkspace.updateToolbox(toolbox);
}

function resetView() {
    bricklyWorkspace.setDevice({
        group: GUISTATE_C.getRobotGroup(),
        robot: GUISTATE_C.getRobot(),
    });
    var toolbox = GUISTATE_C.getNNToolbox();
    bricklyWorkspace.updateToolbox(toolbox);
}
export {
    init,
    initNNForms,
    saveToServer,
    saveAsToServer,
    loadFromListing,
    showSaveAsModal,
    newNN,
    showNN,
    getBricklyWorkspace,
    reloadNN,
    reloadView,
    resetView,
};

function isVisible() {
    return GUISTATE_C.getView() == 'tabNN';
}
