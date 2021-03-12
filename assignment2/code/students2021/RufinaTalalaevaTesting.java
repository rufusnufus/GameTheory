package gametheory.assignment2.students2021;
import gametheory.assignment2.Player;

import java.util.*;

public class RufinaTalalaevaTesting {
    public static void main(String[] args){
        // list of players participating in tournament
        ArrayList<Player> players = new ArrayList<>();
        for(int i=0; i < 20; i++){
            players.add(new RufinaTalalaevaCode());
        }
        for(int i=0; i < 20; i++){
            players.add(new CooperativePlayer());
        }
        for (int i=0; i < 30; i++){
            players.add(new WiseGreedyPlayer());
        }
        for(int i=0; i < 30; i++){
            players.add(new GreedyPlayer());
        }
        for(int i=0; i < 0; i++){
            players.add(new AltruisticPlayer());
        }
        for(int i=0; i < 0; i++){
            players.add(new SwappingPlayer());
        }
        for(int i=0; i < 0; i++){
            players.add(new RandomPlayer());
        }
        for(int i=0; i < 12; i++){
            players.add(new CircularPlayer());
        }

        // creating tournament
        Tournament tournament = new Tournament(100, players);
        // running the tournament
        tournament.run();
        // print the statistics
        tournament.statisticsByClass("RufinaTalalaevaCode");
        tournament.statisticsByClass("CooperativePlayer");
        tournament.statisticsByClass("WiseGreedyPlayer");
        tournament.statisticsByClass("GreedyPlayer");
        tournament.statisticsByClass("AltruisticPlayer");
        tournament.statisticsByClass("SwappingPlayer");
        tournament.statisticsByClass("RandomPlayer");
        tournament.statisticsByClass("CircularPlayer");
    }

    /**
     * This method returns field with the best payoff
     *
     * @param xA               the argument X for a field A
     * @param xB               the argument X for a field B
     * @param xC               the argument X for a field C
     * @return pair (the move of the player, the argument X for a field)
     */
    public static AbstractMap.SimpleEntry<Integer,Integer> getTopField(int xA, int xB, int xC) {
        if(xA == xB && xA == xC) {
            int field = new Random().nextInt(3) + 1;
            return new AbstractMap.SimpleEntry<>(field, xA);
        } else if(xA == xB && xA > xC){
            int field = new Random().nextInt(2) + 1;
            return new AbstractMap.SimpleEntry<>(field, xA);
        } else if(xB == xC && xB > xA){
            int field = new Random().nextInt(2) + 2;
            return new AbstractMap.SimpleEntry<>(field, xB);
        } else if(xA == xC && xA > xB){
            int field = new Random().nextInt(2) + 1;
            if(field == 2){
                return new AbstractMap.SimpleEntry<>(field + 1, xC);
            }
            return new AbstractMap.SimpleEntry<>(field, xA);
        } else if(xA > xB && xA > xC) {
            return new AbstractMap.SimpleEntry<>(1, xA);
        } else if(xB > xA && xB > xC) {
            return new AbstractMap.SimpleEntry<>(2, xB);
        } else {
            return new AbstractMap.SimpleEntry<>(3, xC);
        }
    }

    /**
     * This method returns field with middle payoff, neither the best and the worst payoff
     *
     * @param xA               the argument X for a field A
     * @param xB               the argument X for a field B
     * @param xC               the argument X for a field C
     * @return pair (the move of the player, the argument X for a field)
     */
    public static AbstractMap.SimpleEntry<Integer,Integer> getMidField(int xA, int xB, int xC) {
        if(xA == xB && xA == xC) {
            int field = new Random().nextInt(3) + 1;
            return new AbstractMap.SimpleEntry<>(field, xA);
        } else if((xB > xA && xA > xC) || (xC > xA && xA > xB)) {
            return new AbstractMap.SimpleEntry<>(1, xA);
        } else if((xA > xB && xB > xC) || (xC > xB && xB > xA)){
            return new AbstractMap.SimpleEntry<>(2, xB);
        } else {
            return new AbstractMap.SimpleEntry<>(3, xC);
        }
    }
}

/**
 * Class that is responsible for running the tournament with
 * number of rounds and specified players.
 * Each player will play with others players only one game.
 * One game is defined by number of rounds.
 * Before each game players will be reset.
 *
 * @author rufusnufus
 */
class Tournament {
    /** Number of rounds for the tournament */
    private final int rounds;
    /** Fields numbers */
    private final int A = 1;
    private final int B = 2;
    private final int C = 3;
    /** Array of players */
    private final ArrayList<Player> players;
    /** Mapping of fields number with the arguments X for the fields */
    private HashMap<Integer, Integer> fields = new HashMap<>();
    /** Mapping of players with their scores obtained in tournament */
    private HashMap<Player, Double> scores = new HashMap<>();

    /**
     * Initializes the tournament
     *
     * @param rounds Number of rounds for the tournament
     * @param players Array of players
     */
    public Tournament(int rounds, ArrayList<Player> players) {
        this.rounds = rounds;
        this.players = players;
    }

    /**
     * Initializes the mapping of fields number with the
     * arguments 1 for the fields.
     * Needed to be done before each game in the tournament.
     */
    public void fieldsInitialize() {
        fields.put(A, 1);
        fields.put(B, 1);
        fields.put(C, 1);
    }

    /**
     * Function that is responsible for running the tournament
     * among all players.
     */
    public void run() {
        // Initializes the scores of all players with 0
        for (Player player : players) {
            scores.put(player, 0.0);
        }

        // Each player is put against each another player in the players list
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                // reset of the players before the game
                players.get(i).reset();
                players.get(j).reset();

                // initializing the fields before the game
                fieldsInitialize();

                // initializing the previous moves of
                // the players before the game
                int p1LastMove = 0;
                int p2LastMove = 0;

                // run the game with defined number of rounds
                for (int n = 0; n < rounds; n++) {
                    // get the current arguments X for each field
                    int xA = fields.get(A);
                    int xB = fields.get(B);
                    int xC = fields.get(C);

                    // players perform the moves in the current round
                    int p1Move = players.get(i).move(p2LastMove, xA, xB, xC);
                    int p2Move = players.get(j).move(p1LastMove, xA, xB, xC);

                    // if both of the Moose are in the same field, then they will fight.
                    // Fighting is exhausting and prevents eating and damages the local area,
                    // and causes of a score of 0 to be given to both Moose.
                    // However, other fields increase their X by 1.
                    if ((p1Move == p2Move) && (p1Move == A)) {
                        // minimum of xA field is 0 always
                        if (xA > 0) {
                            fields.put(A, xA - 1);
                        }
                        fields.put(B, xB + 1);
                        fields.put(C, xC + 1);
                    } else if ((p1Move == p2Move) && (p1Move == B)) {
                        // minimum of xB field is 0 always
                        if (xB > 0) {
                            fields.put(B, xB - 1);
                        }
                        fields.put(A, xA + 1);
                        fields.put(C, xC + 1);
                    } else if ((p1Move == p2Move) && (p1Move == C)) {
                        // minimum of xC field is 0 always
                        if (xC > 0) {
                            fields.put(C, xC - 1);
                        }
                        fields.put(A, xA + 1);
                        fields.put(B, xB + 1);
                    } else {
                        // if each Moose chose different fields,
                        // they get payoff f(X) - f(0)
                        // and the field’s X is decreased by 1
                        updatePlayerScore(players.get(i), p1Move);
                        updatePlayerScore(players.get(j), p2Move);

                        // Sum of all numbers of the fields is
                        // 1 + 2 + 3 = 6
                        // So, we can understand which field was not
                        // visited by any Moose, so the field
                        // increases it's X by 1
                        int remainingField = 6 - p1Move - p2Move;
                        int xRemainingField = fields.get(remainingField);
                        fields.put(remainingField, xRemainingField + 1);
                    }
                    // update last moves of the players for the next round
                    p1LastMove = p1Move;
                    p2LastMove = p2Move;
                }
            }
        }
    }

    /**
     * Function that updates the score of the given player
     * based on the move he/she performed
     *
     * @param player player that needs update of the score
     * @param move the move of the player can be 1 for A, 2 for B
     * and 3 for C fields
     */
    private void updatePlayerScore(Player player, int move) {
        // get the X value of the field
        // where Moose went this round
        int xField = fields.get(move);

        // obtain new score of the player
        double newScore = scores.get(player) + getPayoff(xField) - getPayoff(0);

        // update the score of the player
        scores.put(player, newScore);

        // the field’s X is decreased by 1,
        // to a minimum of 0.
        if (xField > 0) {
            fields.put(move, xField - 1);
        }
    }

    /**
     * Returns the vegetation amount that player can gain
     * if steps on the field with value X.
     *
     * @param X Field's X value
     * @return vegetation amount that player gains
     * when eats the grass with X valued field
     */
    private double getPayoff(int X) {
        return (10 * Math.exp(X))/(1.0 + Math.exp(X));
    }

    /**
     * Prints the current Scores of all Players in the tournament
     * in the following manner:
     *
     * Player's name : score
     */
    public void printScoresTable() {
        for (HashMap.Entry iter : scores.entrySet()) {
            // prints the name of the player and it's score
            System.out.println(iter.getKey().getClass().getSimpleName()
                    + " : " + iter.getValue());
        }
    }

    /**
     * Prints the average score of players of
     * the same specified class in the tournament
     *
     * @param className SimpleName of the class
     */
    public void statisticsByClass(String className){
        double score = 0.0;
        int number = 0;
        for (HashMap.Entry iter :  scores.entrySet()){
            if(iter.getKey().getClass().getSimpleName().equals(className)){
                score += Double.parseDouble(iter.getValue().toString());
                number += 1;
            }
        }
        System.out.println(className + "sAverage: " + score / number);
    }
}

/**
 * This player tries to cooperate,
 * if opponent doesn't perform the same,
 * player starts to play greedy.
 * To understand whether the opponent is a friend,
 * both of the players need to stand on the 1st field(A) 5 first moves.
 * It is done for getting the maximum payoff on other fields.
 *
 * After first 5 cooperative moves players perform the random move
 * on the 2nd(B) or 3rd(C) field.
 * If players will choose different fields,
 * it determines the direction, where to move in the next rounds.
 * If they chose the same field, then they return to
 * the 1st field(A) and repeat until determination of the direction
 *
 * @author rufusnufus
 */
class CooperativePlayer implements Player {
    /** flag that keeps track, whether opponent cooperates or not */
    private boolean cooperative = true;
    /** variable that keeps track whether direction is selected or not */
    private int direction = 0;
    /**
     * variable that keeps track player's
     * last move after direction is known
     */
    private int myLastEvenMove = 0;
    /**
     * variable that keeps track opponent's
     * last move after direction is known
     */
    private int opponentLastEvenMove = 0;
    /** variable that keeps track how many rounds have passed */
    private int roundsCounter = 0;

    @Override
    public void reset() {
        this.cooperative = true;
        this.direction = 0;
        this.myLastEvenMove = 0;
        this.opponentLastEvenMove = 0;
        this.roundsCounter = 0;
    }

    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        roundsCounter += 1;
        if (cooperative) {
            // first 5 rounds player and opponent wait
            // until fields are full of grass(their value is equal to 5)
            if (roundsCounter <= 5) {
                // check whether opponent is cooperator
                if ((opponentLastMove != 0) && (opponentLastMove != 1)) {
                    cooperative = false;
                    // play Greedy
                    return RufinaTalalaevaTesting.getTopField(xA, xB, xC).getKey();
                }
                // if opponent still cooperates, returning 1
                // and waiting other fields to grow
                return 1;
            } else {
                if (roundsCounter % 2 == 0) {
                    if (direction == 0) {
                        // to understand in which direction player
                        // needs to move in the next rounds
                        direction = myLastEvenMove - opponentLastEvenMove;
                    }
                    // if still not chosen the direction
                    if (direction == 0) {
                        // chooses randomly between B and C fields
                        int move = new Random().nextInt(2) + 2;
                        myLastEvenMove = move;
                        return move;
                    } else if (direction > 0) {
                        return 3;
                    } else {
                        return 2;
                    }
                } else {
                    opponentLastEvenMove = opponentLastMove;
                    return 1;
                }
            }
        } else {
            // Play Greedy
            return RufinaTalalaevaTesting.getTopField(xA, xB, xC).getKey();
        }
    }

    @Override
    public String getEmail() {
        return null;
    }
}

/**
 * Player that always takes the field with
 * the Top X value
 */
class GreedyPlayer implements Player {

    @Override
    public void reset() {}

    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        return RufinaTalalaevaTesting.getTopField(xA, xB, xC).getKey();
    }

    @Override
    public String getEmail() { return null; }
}

/**
 * Player that plays greedy until there is some field
 * with value 6 or greater, then he randomly chooses
 * between middle field and top field
 */
class WiseGreedyPlayer implements Player {

    @Override
    public void reset() {
    }

    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        AbstractMap.SimpleEntry<Integer,Integer> xTopField = RufinaTalalaevaTesting.getTopField(xA, xB, xC);
        // if X value of Top Field is greater than 6, then there is at least 1
        // field that we need for randomisation
        boolean condition = (xTopField.getValue() >= 6) && (new Random().nextBoolean());
        return (condition ? RufinaTalalaevaTesting.getMidField(xA, xB, xC).getKey() : xTopField.getKey());
    }

    @Override
    public String getEmail() {
        return null;
    }
}

/**
 * Player that takes always middle field.
 * Neither the best and the worst.
 *
 * @author rufusnufus
 */
class AltruisticPlayer implements Player {

    @Override
    public void reset() {}

    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        return RufinaTalalaevaTesting.getMidField(xA, xB, xC).getKey();
    }

    @Override
    public String getEmail() {
        return null;
    }
}

/**
 * Player that swaps the strategies each move.
 * There are 2 strategies he follows:
 * take the first best field(greedy)
 * and take the second best field.
 * Firstly, he/she takes random strategy among two
 * and then swaps them.
 *
 * @author rufusnufus
 */
class SwappingPlayer implements Player {
    /** flag that determines current strategy,
     * whether it's greedy or not */
    private boolean greedy = true;

    @Override
    public void reset() {
        greedy = new Random().nextBoolean();
    }

    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        // swaps strategy
        greedy = !greedy;

        int topField = RufinaTalalaevaTesting.getTopField(xA, xB, xC).getKey();
        int midField = RufinaTalalaevaTesting.getMidField(xA, xB, xC).getKey();
        // returns top or mid field number according to the current strategy
        return (greedy ? topField : midField);
    }

    @Override
    public String getEmail() {
        return null;
    }
}

/**
 * Player that performs the sequence of
 * random moves.
 *
 * @author rufusnufus
 */
class RandomPlayer implements Player {

    @Override
    public void reset() {}

    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        return new Random().nextInt(3) + 1;
    }

    @Override
    public String getEmail() {
        return null;
    }
}

/**
 * Player that performs the sequence of moves
 * in a circular manner. Begins with 1, continues with 2,
 * then 3, after that again moves to 1 and continues
 * moving like that until the end of the game.
 *
 * @author rufusnufus
 */
class CircularPlayer implements Player {
    /** Variable for monitoring which round goes now */
    private int roundCounter = 0;

    @Override
    public void reset() {
        roundCounter = 0;
    }

    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        // increments roundCounter on each move
        roundCounter += 1;
        // returns 1, 2 or 3 according to current roundCounter
        int move = roundCounter % 3;
        if (move == 0) {
            move += 3;
        }
        return move;
    }

    @Override
    public String getEmail() {
        return null;
    }
}

