define(["require", "exports", "log", "guiState.controller", "neuralnetwork.playground", "jquery", "blockly", "jquery-validate"], function (require, exports, LOG, GUISTATE_C, PG, $, Blockly) {
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.resetView = exports.reloadView = exports.reloadNN = exports.getBricklyWorkspace = exports.showNN = exports.newNN = exports.showSaveAsModal = exports.loadFromListing = exports.saveAsToServer = exports.saveToServer = exports.initNNForms = exports.init = void 0;
    var inputNeurons;
    var outputNeurons;
    function init() {
        initEvents();
    }
    exports.init = init;
    function initEvents() {
        $('#tabNN').onWrap('show.bs.tab', function (e) {
            GUISTATE_C.setView('tabNN');
            extractInputOutputNeuronsFromNNstep();
            PG.runPlayground();
        }, 'show tabNN');
        $('#tabNN').onWrap('shown.bs.tab', function (e) {
            PG.reset();
        }, 'shown tabNN');
        $('#tabNN').onWrap('hide.bs.tab', function (e) { }, 'hide tabNN');
        $('#tabNN').onWrap('hidden.bs.tab', function (e) { }, 'hidden tabNN');
    }
    function extractInputOutputNeuronsFromNNstep() {
        inputNeurons = [];
        outputNeurons = [];
        var stepBlockFound = false;
        for (var block in Blockly.Workspace.getByContainer('blocklyDiv').getAllBlocks()) {
            if (block.type === 'robActions_NNstep') {
                if (stepBlockFound) {
                    LOG.error("more than one NNstep block makes no sense");
                }
                stepBlockFound = true;
                extractInputOutputNeurons(block.getChildren());
            }
        }
    }
    function extractInputOutputNeurons(neurons) {
        for (var block in neurons) {
            if (block.type === 'robActions_inputneuron') {
                inputNeurons.push(block.getFieldValue("NAME"));
            }
            else if (block.type === 'robActions_outputneuron') {
                outputNeurons.push(block.getFieldValue("NAME"));
            }
            else {
                LOG.error("in a NNstep block only input and output neurons are allowed");
            }
            var next = block.getChildren();
            if (next) {
                extractInputOutputNeurons(next);
            }
        }
    }
    function showNN(result) {
        if (result.rc == 'ok') {
            GUISTATE_C.setNN(result);
            LOG.info('show nn ' + GUISTATE_C.getNNName());
        }
    }
    exports.showNN = showNN;
    function reloadNN(opt_result) {
        var nn;
        if (opt_result) {
            nn = opt_result.nnXML;
        }
        else {
            nn = GUISTATE_C.getNNXML();
        }
        if (!seen) {
            nnToBricklyWorkspace(nn);
            var x, y;
            if ($(window).width() < 768) {
                x = $(window).width() / 50;
                y = 25;
            }
            else {
                x = $(window).width() / 5;
                y = 50;
            }
            var blocks = bricklyWorkspace.getTopBlocks(true);
            for (var i = 0; i < blocks.length; i++) {
                var coord = Blockly.getSvgXY_(blocks[i].svgGroup_, bricklyWorkspace);
                var coordBlock = blocks[i].getRelativeToSurfaceXY();
                blocks[i].moveBy(coordBlock.x - coord.x + x, coordBlock.y - coord.y + y);
            }
        }
        else {
            nnToBricklyWorkspace(nn);
        }
    }
    exports.reloadNN = reloadNN;
    function reloadView() {
        if (isVisible()) {
            var dom = Blockly.Xml.workspaceToDom(bricklyWorkspace);
            var xml = Blockly.Xml.domToText(dom);
            nnToBricklyWorkspace(xml);
        }
        else {
            seen = false;
        }
        var toolbox = GUISTATE_C.getNNToolbox();
        bricklyWorkspace.updateToolbox(toolbox);
    }
    exports.reloadView = reloadView;
    function resetView() {
        bricklyWorkspace.setDevice({
            group: GUISTATE_C.getRobotGroup(),
            robot: GUISTATE_C.getRobot(),
        });
        var toolbox = GUISTATE_C.getNNToolbox();
        bricklyWorkspace.updateToolbox(toolbox);
    }
    exports.resetView = resetView;
    function isVisible() {
        return GUISTATE_C.getView() == 'tabNN';
    }
});
