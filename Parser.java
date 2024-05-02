import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;


public class Parser {
    private final TokenManager tokenManager;
    private StringBuilder binaryRepresentation;

    public Parser(LinkedList<Token> tokens) {
        this.tokenManager = new TokenManager(tokens);
    }

    public void parseProgram() {
        String output = "output.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
            // Parse all the statements in the program
            while (tokenManager.MoreTokens()) {
                binaryRepresentation = new StringBuilder();
                parseStatement(); // Add the statement's binary representation to the builder
                AcceptSeparators(); // Accept newlines between statements
                binaryRepresentation.append(System.lineSeparator());

                writer.write(binaryRepresentation.toString()); // Write to file
            }
            writer.flush(); // Ensure all data is written to the file
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    private void parseStatement() {
        Optional<Token> currentToken = tokenManager.Peek(0);
        if (!currentToken.isPresent()) return;

        Token.tokenType tokenType = currentToken.get().gettype();

        switch (tokenType) {
            case HALT:
                binaryRepresentation.append("00000000000000000000000000000000");
            case MATH:
                tokenManager.MatchAndRemove(Token.tokenType.MATH);
                parseMath();
                break;
            case SHIFT:
                tokenManager.MatchAndRemove(Token.tokenType.SHIFT);
                parseMath();
                break;
            case COPY:
                parseMath();
                break;
            case BRANCH:
                tokenManager.MatchAndRemove(Token.tokenType.BRANCH);
                parseBranch();
                break;
            case JUMP:
                parseBranch();
                break;
            case CALL:
                tokenManager.MatchAndRemove(Token.tokenType.CALL);
                parseCall();
                break;
            case LOAD, RETURN:
                parseLoad();
                break;
            case STORE:
                parseStore();
                break;
            case PUSH:
                tokenManager.MatchAndRemove(Token.tokenType.LOAD);
                parsePush();
                break;
            case POP, INTERRUPT, PEEK:
                parsePop();
                break;
            default:
                throw new RuntimeException("Invalid instruction");
        }
    }


    private void parseMath() {
        Optional<Token> nextToken = tokenManager.Peek(0);
        if (nextToken.isPresent()) {
            Token operationToken = nextToken.get();
            tokenManager.MatchAndRemove(operationToken.gettype()); // Remove operation token
            String function = getMathOperationCode(operationToken.gettype());

            // Now decide if it's twoR, threeR, or DestOnly
            if (tokenManager.Peek(0).isPresent()) {
                if (tokenManager.Peek(0).get().gettype().equals(Token.tokenType.REGISTER)) {
                    if (tokenManager.Peek(1).isPresent()) {
                        if (tokenManager.Peek(1).get().gettype().equals(Token.tokenType.REGISTER)) {
                            if (tokenManager.Peek(2).isPresent()) {
                                if (tokenManager.Peek(2).get().gettype().equals(Token.tokenType.REGISTER)) {
                                     parseThreeR(function, "000");
                                } else {
                                    parseTwoR(function, "000");
                                }
                            } else
                                throw new RuntimeException("Invalid instruction");
                        } else
                            parseDestOnly(function,"000");

                    } else
                        throw new RuntimeException("Invalid instruction");

                } else
                    throw new RuntimeException("Invalid instruction");
            }
        }
    }


    private void parseBranch() {
        Optional<Token> nextToken = tokenManager.Peek(0);
        if (nextToken.isPresent()) {
            Token operationToken = nextToken.get();
            tokenManager.MatchAndRemove(operationToken.gettype()); // Remove operation token
            String function = getBooleanOperationCode(operationToken.gettype());
            // Now decide if it's twoR, threeR, or DestOnly
            if (tokenManager.Peek(0).isPresent()) {
                if (tokenManager.Peek(0).get().gettype().equals(Token.tokenType.REGISTER)) {
                    if (tokenManager.Peek(1).isPresent()) {
                        if (tokenManager.Peek(1).get().gettype().equals(Token.tokenType.REGISTER)) {
                            if (tokenManager.Peek(2).isPresent()) {
                                if (tokenManager.Peek(2).get().gettype().equals(Token.tokenType.REGISTER)) {
                                    parseThreeR(function, "001");
                                } else {
                                    parseTwoR(function, "001");
                                }
                            } else
                                throw new RuntimeException("Invalid instruction");
                        } else
                            parseDestOnly(function,"001");

                    } else
                        throw new RuntimeException("Invalid instruction");

                } else
                    throw new RuntimeException("Invalid instruction");
            } else if (tokenManager.Peek(0).get().gettype().equals(Token.tokenType.NUMBER))
                parseNoR("001");
        }
    }


    private void parseCall() {
        Optional<Token> nextToken = tokenManager.Peek(0);
        if (nextToken.isPresent()) {
            Token operationToken = nextToken.get();
            tokenManager.MatchAndRemove(operationToken.gettype()); // Remove operation token
            String function = getBooleanOperationCode(operationToken.gettype());
            // Now decide if it's twoR, threeR, or DestOnly
            if (tokenManager.Peek(0).isPresent()) {
                if (tokenManager.Peek(0).get().gettype().equals(Token.tokenType.REGISTER)) {
                    if (tokenManager.Peek(1).isPresent()) {
                        if (tokenManager.Peek(1).get().gettype().equals(Token.tokenType.REGISTER)) {
                            if (tokenManager.Peek(2).isPresent()) {
                                if (tokenManager.Peek(2).get().gettype().equals(Token.tokenType.REGISTER)) {
                                    parseThreeR(function, "010");
                                } else {
                                    parseTwoR(function, "010");
                                }
                            } else
                                throw new RuntimeException("Invalid instruction");
                        } else
                            parseDestOnly(function,"010");

                    } else
                        throw new RuntimeException("Invalid instruction");

                } else
                    throw new RuntimeException("Invalid instruction");
            } else if (tokenManager.Peek(0).get().gettype().equals(Token.tokenType.NUMBER))
                parseNoR("010");
        }
    }

    private void parseLoad() {
        Optional<Token> nextToken = tokenManager.Peek(0);
        if (nextToken.isPresent()) {
            Token operationToken = nextToken.get();
            tokenManager.MatchAndRemove(operationToken.gettype()); // Remove statement token
            String function = "0000";
            // Now decide if it's twoR, threeR, or DestOnly
            if (tokenManager.Peek(0).isPresent()) {
                if (tokenManager.Peek(0).get().gettype().equals(Token.tokenType.REGISTER)) {
                    if (tokenManager.Peek(1).isPresent()) {
                        if (tokenManager.Peek(1).get().gettype().equals(Token.tokenType.REGISTER)) {
                            if (tokenManager.Peek(2).isPresent()) {
                                if (tokenManager.Peek(2).get().gettype().equals(Token.tokenType.REGISTER)) {
                                    parseThreeR(function, "100");
                                } else {
                                    parseTwoR(function, "100");
                                }
                            } else
                                throw new RuntimeException("Invalid instruction");
                        } else
                            parseDestOnly(function,"100");

                    } else
                        throw new RuntimeException("Invalid instruction");

                } else
                    throw new RuntimeException("Invalid instruction");
            } else if (tokenManager.Peek(0).get().gettype().equals(Token.tokenType.NUMBER))
                parseNoR("100");
        }
    }


    private void parseStore() {
        Optional<Token> nextToken = tokenManager.Peek(0);
        if (nextToken.isPresent()) {
            Token operationToken = nextToken.get();
            tokenManager.MatchAndRemove(operationToken.gettype()); // Remove state,emt token
            String function = "0000";
            // Now decide if it's twoR, threeR, or DestOnly
            if (tokenManager.Peek(0).isPresent()) {
                if (tokenManager.Peek(0).get().gettype().equals(Token.tokenType.REGISTER)) {
                    if (tokenManager.Peek(1).isPresent()) {
                        if (tokenManager.Peek(1).get().gettype().equals(Token.tokenType.REGISTER)) {
                            if (tokenManager.Peek(2).isPresent()) {
                                if (tokenManager.Peek(2).get().gettype().equals(Token.tokenType.REGISTER)) {
                                    parseThreeR(function, "101");
                                } else {
                                    parseTwoR(function, "101");
                                }
                            } else
                                throw new RuntimeException("Invalid instruction");
                        } else
                            parseDestOnly(function,"101");

                    } else
                        throw new RuntimeException("Invalid instruction");

                } else
                    throw new RuntimeException("Invalid instruction");
            } else if (tokenManager.Peek(0).get().gettype().equals(Token.tokenType.NUMBER))
                throw new RuntimeException("Invalid instruction");
        }
    }

    private void parsePush() {
        Optional<Token> nextToken = tokenManager.Peek(0);
        if (nextToken.isPresent()) {
            Token operationToken = nextToken.get();
            tokenManager.MatchAndRemove(operationToken.gettype()); // Remove operation token
            String function = getMathOperationCode(operationToken.gettype());
            // Now decide if it's twoR, threeR, or DestOnly
            if (tokenManager.Peek(0).isPresent()) {
                if (tokenManager.Peek(0).get().gettype().equals(Token.tokenType.REGISTER)) {
                    if (tokenManager.Peek(1).isPresent()) {
                        if (tokenManager.Peek(1).get().gettype().equals(Token.tokenType.REGISTER)) {
                            if (tokenManager.Peek(2).isPresent()) {
                                if (tokenManager.Peek(2).get().gettype().equals(Token.tokenType.REGISTER)) {
                                    parseThreeR(function, "011");
                                } else {
                                    parseTwoR(function, "011");
                                }
                            } else
                                throw new RuntimeException("Invalid instruction");
                        } else
                            parseDestOnly(function,"011");

                    } else
                        throw new RuntimeException("Invalid instruction");

                } else
                    throw new RuntimeException("Invalid instruction");
            } else if (tokenManager.Peek(0).get().gettype().equals(Token.tokenType.NUMBER))
                throw new RuntimeException("Invalid instruction");
        }
    }

    private void parsePop() {
        Optional<Token> nextToken = tokenManager.Peek(0);
        if (nextToken.isPresent()) {
            Token operationToken = nextToken.get();
            tokenManager.MatchAndRemove(operationToken.gettype()); // Remove statement token
            String function = "0000";
            // Now decide if it's twoR, threeR, or DestOnly
            if (tokenManager.Peek(0).isPresent()) {
                if (tokenManager.Peek(0).get().gettype().equals(Token.tokenType.REGISTER)) {
                    if (tokenManager.Peek(1).isPresent()) {
                        if (tokenManager.Peek(1).get().gettype().equals(Token.tokenType.REGISTER)) {
                            if (tokenManager.Peek(2).isPresent()) {
                                if (tokenManager.Peek(2).get().gettype().equals(Token.tokenType.REGISTER)) {
                                    parseThreeR(function, "110");
                                } else {
                                    parseTwoR(function, "110");
                                }
                            } else
                                throw new RuntimeException("Invalid instruction");
                        } else
                            parseDestOnly(function,"110");

                    } else
                        throw new RuntimeException("Invalid instruction");

                } else
                    throw new RuntimeException("Invalid instruction");
            } else if (tokenManager.Peek(0).get().gettype().equals(Token.tokenType.NUMBER))
                parseNoR("110");
        }
    }



    private String getMathOperationCode(Token.tokenType operationType) {
        switch (operationType) {
            case ADD:
                return "1110";
            case SUBTRACT:
                return "1111";
            case MULTIPLY:
                return "0111";
            case RIGHT: ;
                return "1101";
            case LEFT:
                return "1100";
            case NOT:
                return "1011";
            case XOR:
                return "1010";
            case OR:
                return "1001";
            case AND:
                return "1000";
            case COPY:
                tokenManager.MatchAndRemove(Token.tokenType.COPY);
                return "0000";
            default:
                return "";
        }
    }

    private String getBooleanOperationCode(Token.tokenType operationType) {
        switch (operationType) {
            case EQUAL:
                return "0000";
            case UNEQUAL:
                return "0001";
            case LESS:
                return "0010";
            case GREATEROREQUAL:
                return "0011";
            case GREATER:
                return  "0100";
            case LESSOREQUAL:
                return  "0101";
            case JUMP:
                tokenManager.MatchAndRemove(Token.tokenType.JUMP);
                return "0000";
            default:
                return "";
        }
    }

    private void parseTwoR(String function, String instruction) {
        binaryRepresentation.append("11");
        binaryRepresentation.append(instruction);
        String registerString = tokenManager.MatchAndRemove(Token.tokenType.REGISTER).get().getvalue();
        int val = Integer.parseInt(registerString); // parse the input to an integer
        String binaryString = Integer.toBinaryString(val);
        String paddedBinaryString = String.format("%05d", Integer.parseInt(binaryString));
        binaryRepresentation.append(paddedBinaryString);
        binaryRepresentation.append(function);
        registerString = tokenManager.MatchAndRemove(Token.tokenType.REGISTER).get().getvalue();
        val = Integer.parseInt(registerString); // parse the input to an integer
        binaryString = Integer.toBinaryString(val);
        paddedBinaryString = String.format("%05d", Integer.parseInt(binaryString));
        binaryRepresentation.append(paddedBinaryString);
        while (binaryRepresentation.length() < 32) {
            binaryRepresentation.append("0");
        }
    }

    private void parseThreeR(String function, String instruction) {
        binaryRepresentation.append("10");
        binaryRepresentation.append(instruction);
        String registerString = tokenManager.MatchAndRemove(Token.tokenType.REGISTER).get().getvalue();
        int val = Integer.parseInt(registerString); // parse the input to an integer
        String binaryString = Integer.toBinaryString(val);
        String paddedBinaryString = String.format("%05d", Integer.parseInt(binaryString));
        binaryRepresentation.append(paddedBinaryString);
        binaryRepresentation.append(function);
        registerString = tokenManager.MatchAndRemove(Token.tokenType.REGISTER).get().getvalue();
        val = Integer.parseInt(registerString); // parse the input to an integer
        binaryString = Integer.toBinaryString(val);
        paddedBinaryString = String.format("%05d", Integer.parseInt(binaryString));
        binaryRepresentation.append(paddedBinaryString);
        registerString = tokenManager.MatchAndRemove(Token.tokenType.REGISTER).get().getvalue();
        val = Integer.parseInt(registerString); // parse the input to an integer
        binaryString = Integer.toBinaryString(val);
        paddedBinaryString = String.format("%05d", Integer.parseInt(binaryString));
        binaryRepresentation.append(paddedBinaryString);
        while (binaryRepresentation.length() < 32) {
            binaryRepresentation.append("0");
        }

    }

    private void parseDestOnly(String function,  String instruction) {
        binaryRepresentation.append("01");
        binaryRepresentation.append(instruction);
        String registerString = tokenManager.MatchAndRemove(Token.tokenType.REGISTER).get().getvalue();
        int val = Integer.parseInt(registerString); // parse the input to an integer
        String binaryString = Integer.toBinaryString(val);
        String paddedBinaryString = String.format("%05d", Integer.parseInt(binaryString));
        binaryRepresentation.append(paddedBinaryString);
        binaryRepresentation.append(function);
        // Append the binary representation of the immediate NUMBER value
        String immediateString = tokenManager.MatchAndRemove(Token.tokenType.NUMBER).get().getvalue();
        val = Integer.parseInt(immediateString); // parse the input to an integer
        binaryString = Integer.toBinaryString(val);
        paddedBinaryString = String.format("%018d", Integer.parseInt(binaryString));
        binaryRepresentation.append(paddedBinaryString);
    }

    private void parseNoR(String instruction) {
        binaryRepresentation.append("00");
        binaryRepresentation.append(instruction);
        String immediateString = tokenManager.MatchAndRemove(Token.tokenType.NUMBER).get().getvalue();
        int val = Integer.parseInt(immediateString); // parse the input to an integer
        String binaryString = Integer.toBinaryString(val);
        String paddedBinaryString = String.format("%027d", Integer.parseInt(binaryString));
        binaryRepresentation.append(paddedBinaryString);
    }

    private void AcceptSeparators() {
        while (tokenManager.MoreTokens()) {
            Optional<Token> currentToken = tokenManager.Peek(0);
            if (currentToken.isPresent() && currentToken.get().gettype() == Token.tokenType.NEWLINE) {
                tokenManager.MatchAndRemove(Token.tokenType.NEWLINE);
            } else {
                break;
            }
        }
    }
}