package gametheory.assignment2.students2021;

import gametheory.assignment2.Player;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Random;

/**
 * This player chooses the random field until
 * the moment when he/she and opponent chose different fields.
 * After that moment the player remembers the field,
 * this field will be "his/her" field(farmingField) where
 * he/she will eat the grass. In addition, the player
 * remembers the field(restField) where he/she will stand
 * and wait the moment when "his/her" field
 * will reach X value equal to 6.
 * restField is neither player's farmingField or
 * opponent's farmingField and logically
 * the same for both players.
 *
 * So, finally after the resolving the types of all fields,
 * player stands on restField until his/her
 * farmingField value reached the value of 6,
 * and then performs the following moves:
 * farmingField -> restField -> farmingField -> restField
 * until the end of the game.
 *
 * If the opponent went on player's farmingField,
 * then player offends and plays in WiseGreedyPlayer manner.
 *
 * @author rufusnufus
 */
public class RufinaTalalaevaCode implements Player {
    /** farming field of the player */
    private int definedFarmingField = 0;
    /** rest field of the player and opponent */
    private int definedRestField = 0;
    /** variable that tracks move of the player in the last round */
    private int myLastMove = 0;
    /** flag that keeps track, whether opponent cooperates or not */
    private boolean cooperative = true;

    /** empty constructor that needs to be here by the task */
    public RufinaTalalaevaCode(){}

    /**
     * This method is called to reset the agent before the match
     * with another player containing several rounds
     */
    @Override
    public void reset() {
        definedFarmingField = 0;
        definedRestField = 0;
        myLastMove = 0;
        cooperative = true;
    }

    /**
     * This method returns the move of the player based on
     * the last move of the opponent and X values of all fields.
     * Initially, X for all fields is equal to 1 and last opponent
     * move is equal to 0
     *
     * @param opponentLastMove the last move of the opponent
     *                         varies from 0 to 3
     *                         (0 – if this is the first move)
     * @param xA               the argument X for a field A
     * @param xB               the argument X for a field B
     * @param xC               the argument X for a field C
     * @return the move of the player can be 1 for A, 2 for B
     * and 3 for C fields
     */
    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        if (cooperative) {
            // if farmingField is not defined, we need to find it
            if (definedFarmingField == 0) {
                // if no collisions in last moves then we have
                // found our farming field
                if (myLastMove != opponentLastMove) {
                    definedFarmingField = myLastMove;
                    // Sum of all numbers of the fields is
                    // 1 + 2 + 3 = 6
                    // So, we can understand which field
                    // will be the restField for remaining rounds
                    definedRestField = 6 - opponentLastMove - myLastMove;
                } else {
                    // continue to search player's farming field
                    myLastMove = new Random().nextInt(3) + 1;
                    return myLastMove;
                }
            }
            // if opponent went to my player's field,
            // he/she is not a cooperator anymore, so play wiseGreedy
            if (opponentLastMove == definedFarmingField) {
                cooperative = false;
                // play WiseGreedy
                AbstractMap.SimpleEntry<Integer,Integer> xTopField = getTopField(xA, xB, xC);
                // if X value of Top Field is greater than 6, then there is at least 1
                // field that we need for randomisation
                boolean condition = (xTopField.getValue() >= 6) && (new Random().nextBoolean());
                return (condition ? getMidField(xA, xB, xC).getKey() : xTopField.getKey());
            }
            // current mappings of the fields' numbers with their X value
            HashMap<Integer, Integer> mappings = new HashMap<>();
            mappings.put(1, xA);
            mappings.put(2, xB);
            mappings.put(3, xC);
            // if farmingField reached 6, then go there, else stay on restField
            return ((mappings.get(definedFarmingField) < 6) ? definedRestField : definedFarmingField);
        } else {
            // play WiseGreedy
            AbstractMap.SimpleEntry<Integer,Integer> xTopField = getTopField(xA, xB, xC);
            // if X value of Top Field is greater than 6, then there is at least 1
            // field that we need for randomisation
            boolean condition = (xTopField.getValue() >= 6) && (new Random().nextBoolean());
            return (condition ? getMidField(xA, xB, xC).getKey() : xTopField.getKey());
        }
    }

    /**
     * This method returns my IU email
     *
     * @return my email
     */
    @Override
    public String getEmail() {
        return "r.talalaeva@innopolis.university";
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
