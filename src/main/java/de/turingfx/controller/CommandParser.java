package de.turingfx.controller;

import de.turingfx.model.exceptions.CmdException;
import de.turingfx.model.CmdLine;
import de.turingfx.model.state.State;
import de.turingfx.model.state.StateType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
@Data
public class CommandParser {

    private int numberOfTapes = 1;
    public static final String START_STATE_NAME = "START";
    public static final String END_STATE_NAME = "END";

    CmdLine parse(String cmdLine, int line) throws CmdException {
        if (StringUtils.isEmpty(cmdLine)) {
            throw new CmdException("Line " + line + "Command line should not be empty!");
        }

        String[] delta = cmdLine.split("->");
        if (delta.length < 2 || delta.length > 2) {
            throw new CmdException("Line " + line
                    + "Could not determine the transitions \"->\" between the states!");
        }

        CmdLine cmd = CmdLine.builder().build();
        cmd.setLine(line);
        String currentState = delta[0];
        String nextState = delta[1];

        parseCurrentState(currentState, cmd);
        parseNextState(nextState, cmd);
        return cmd;
    }

    /**
     * Parse string in for of State:alphabet
     * e.g. B1ab
     * @param stateStr
     * @param cmdLine
    */
    private void parseCurrentState(String stateStr, CmdLine cmdLine) throws CmdException {
        int lengthOfStateStr = stateStr.length() - numberOfTapes;
        String alphabet = StringUtils.substring(stateStr, lengthOfStateStr, stateStr.length());
        String state = StringUtils.substring(stateStr, 0, lengthOfStateStr);

        checkAlphabet(alphabet, cmdLine);
        checkState(state, cmdLine);

        StateType stateType = StateType.NORMAL;

        if (state.equalsIgnoreCase(START_STATE_NAME)) {
            stateType = StateType.START;
        }

        cmdLine.setCurrentState(State.builder()
                .name(state)
                .stateType(stateType)
                .transitionsActions(new HashMap<>())
                .transitionsWrite(new HashMap<>())
                .transitionsState(new HashMap<>()).build());
        cmdLine.setReadAlphabet(alphabet);
    }

    /**
     * Parse string in form of alphabetStateDirection.
     * e.g. aaA1LR
     * @param nextCmdPart
     * @param cmdLine
     */
    private void parseNextState(String nextCmdPart, CmdLine cmdLine) throws CmdException {
        String alphabet = StringUtils.substring(nextCmdPart, 0, numberOfTapes);
        checkAlphabet(alphabet, cmdLine);

        String headDirections = StringUtils.substring(nextCmdPart,
                nextCmdPart.length() - numberOfTapes,
                nextCmdPart.length());
        String state = StringUtils.substring(nextCmdPart, numberOfTapes,
                nextCmdPart.length() - headDirections.length());

        checkState(state, cmdLine);

        StateType stateType = StateType.NORMAL;
        if (state.equalsIgnoreCase(END_STATE_NAME)) {
            stateType = StateType.END;
        }

        cmdLine.setWriteAlphabet(alphabet);
        cmdLine.setNextState(State.builder()
                .name(state)
                .stateType(stateType)
                .transitionsActions(new HashMap<>())
                .transitionsWrite(new HashMap<>())
                .transitionsState(new HashMap<>()).build());
        cmdLine.determineAndSetMovement(headDirections);
    }

    private void checkAlphabet(String alphabets, CmdLine cmdLine) throws CmdException {
        if (StringUtils.isEmpty(alphabets)) {
            throw new CmdException("Line " + cmdLine.getLine()
                    + " The alphabet should not be empty");
        }
        if (alphabets.length() < numberOfTapes || alphabets.length() > numberOfTapes) {
            throw new CmdException("Line " + cmdLine.getLine()
                    + " The number of input alphabet does not match the number of the tapes! "
                    + alphabets + " for number of tapes " + numberOfTapes);
        }
    }

    private void checkState(String stateStr, CmdLine cmdLine) throws CmdException {
        if (StringUtils.isEmpty(stateStr)) {
            throw new CmdException("Line " + cmdLine.getLine()
                    + " The state identifier should not be empty");
        }
        if (!stateStr.matches("[A-Z0-9]+")) {
            throw new CmdException("Line " + cmdLine.getLine()
                    + " The state should be written as upper case or does not matched with the expression [A-Z0-9]: "
                    + stateStr
                    + "\nthis also be possible that the number of tapes does not correspond \n"
                    + "with the number of tapes defined in the code!");
        }
    }

    public List<CmdLine> parseCommandLines(String rawInstructionsStr) throws CmdException {
        List<CmdLine> parsedLines = new ArrayList<>();
        String[] lines = rawInstructionsStr.split("\\n");
        if (lines.length == 0) {
            throw new CmdException("Could not determine states");
        }

        int lineIndex = 0;
        for (String line: lines) {
            lineIndex++;
            if (StringUtils.isBlank(line)) {
                continue;
            }
            CmdLine cmdLine = parse(StringUtils.deleteWhitespace(line), lineIndex);
            parsedLines.add(cmdLine);
        }
        return parsedLines;
    }
}