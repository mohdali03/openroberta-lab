define(["require", "exports", "log", "guiState.controller", "neuralnetwork.playground", "jquery", "blockly", "jquery-validate"], function (require, exports, LOG, GUISTATE_C, PG, $, Blockly) {
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.resetView = exports.reloadView = exports.reloadNN = exports.getBricklyWorkspace = exports.showNN = exports.newNN = exports.showSaveAsModal = exports.loadFromListing = exports.saveAsToServer = exports.saveToServer = exports.initNNForms = exports.init = void 0;
    function init() {
        $('#tabNN').onWrap('show.bs.tab', function (e) {
            GUISTATE_C.setView('tabNN');
        }, 'show tabNN');
        $('#tabNN').onWrap('shown.bs.tab', function (e) {
            prepareNNfromNNstep();
        }, 'shown tabNN');
        $('#tabNN').onWrap('hide.bs.tab', function (e) {
            var nnstepBlock = getTheNNstepBlock();
            nnstepBlock.data = PG.getStateAsJSONString();
        }, 'hide tabNN');
        $('#tabNN').onWrap('hidden.bs.tab', function (e) { }, 'hidden tabNN');
    }
    exports.init = init;
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
        for (var _i = 0, _a = Blockly.Workspace.getByContainer('blocklyDiv').getAllBlocks(); _i < _a.length; _i++) {
            var block = _a[_i];
            if (block.type === 'robActions_NNstep') {
                if (nnstepBlock) {
                    LOG.error("more than one NNstep block makes no sense");
                }
                nnstepBlock = block;
            }
        }
        return nnstepBlock;
    }
    function extractInputOutputNeuronsFromNNstep(inputNeurons, outputNeurons) {
        extractInputOutputNeurons(getTheNNstepBlock().getChildren());
    }
    function extractInputOutputNeurons(inputNeurons, outputNeurons, neurons) {
        for (var _i = 0, neurons_1 = neurons; _i < neurons_1.length; _i++) {
            var block = neurons_1[_i];
            if (block.type === 'robActions_inputneuron') {
                inputNeurons.push(block.getFieldValue("NAME"));
            }
            else if (block.type === 'robActions_outputneuron') {
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
